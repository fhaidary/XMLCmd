package com.fhaidary.xmlcmd.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.InputSource;

import com.fhaidary.xmlcmd.util.Terms;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExtractTool {

	private static String buildXPath(Deque<String> names) {
		StringBuilder bld = new StringBuilder();
		for (String name : names)
			bld.insert(0, '/' + name);
		return bld.toString();
	}

	private static Collection<String> extractXPaths(File file) throws IOException, XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newFactory();
		XMLEventReader xml = factory.createXMLEventReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		return extractXPaths(xml);
	}

	private static Collection<String> extractXPaths(XMLEventReader xml) throws XMLStreamException {
		Set<String> xpaths = new LinkedHashSet<String>();
		Deque<String> names = new ArrayDeque<String>();
		while (xml.hasNext()) {
			XMLEvent evt = xml.nextEvent();
			switch (evt.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				break;
			case XMLEvent.START_ELEMENT:
				StartElement start = evt.asStartElement();
				int index = 1;
				String local = start.getName().getLocalPart();
				// Patch for name-spaces
				String ns = start.getName().getNamespaceURI();
				if (ns != null && !ns.isEmpty())
					local = ':' + local;
				// Proceed...
				String parent = buildXPath(names);
				String xpath;
				while (true) {
					String newName = local + "[" + index + "]";
					xpath = parent + '/' + newName;
					if (xpaths.contains(xpath)) {
						index++;
					} else {
						names.push(newName);
						break;
					}
				}
				xpaths.add(xpath);
				@SuppressWarnings("unchecked")
				Iterator<Attribute> attrs = start.getAttributes();
				for (Attribute attr = null; attrs.hasNext();) {
					attr = attrs.next();
					String apath = xpath + "/@" + attr.getName().getLocalPart();
					xpaths.add(apath);
				}
				break;
			case XMLEvent.CHARACTERS:
				break;
			case XMLEvent.END_ELEMENT:
				// EndElement end = evt.asEndElement();
				names.poll();
				break;
			case XMLEvent.END_DOCUMENT:
				xml.close();
				break;
			default:
				throw new UnsupportedOperationException("Unknown? " + evt);
			}
		}
		return xpaths;
	}

	public static void extractXPaths(String path, File output) throws IOException, XMLStreamException {
		Set<String> xpaths = new TreeSet<String>();
		File handle = new File(path);
		if (handle.isFile())
			extractXPaths(handle, xpaths);
		else
			for (File file : handle.listFiles()) {
				if (!file.getName().toLowerCase().endsWith(".xml"))
					continue;
				extractXPaths(file, xpaths);
			}
		FileUtils.writeLines(output, xpaths);
	}

	private static void extractXPaths(File file, Set<String> xpaths) throws IOException, XMLStreamException {
		System.out.printf("Processing '%s'...", file);
		Collection<String> currents = extractXPaths(file);
		System.out.println(" => " + currents.size() + " expressions extracted!");
		xpaths.addAll(currents);
	}

	public static void compareXPaths(String srcPath, File cmpFile)
			throws IOException, XPathExpressionException, InterruptedException {
		// Set up...
		final List<String> xpaths = FileUtils.readLines(cmpFile);
		final Map<String, Terms> memory = new ConcurrentHashMap<String, Terms>();
		final AtomicInteger counter = new AtomicInteger(0);
		File dir = new File(srcPath);
		// Get in pool...
		int cpuCount = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(cpuCount);
		// Do the work...
		for (final File file : dir.listFiles()) {
			if (!file.getName().toLowerCase().endsWith(".xml"))
				continue;
			pool.submit(new Runnable() {
				public void run() {
					try {
						compareXPaths(file, xpaths, memory, counter);
						counter.incrementAndGet();
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			});
		}
		// Get out of pool...
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.DAYS);
		// Finish and save!
		Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
		Map<String, Terms> copy = new TreeMap<String, Terms>(memory);
		gson.toJson(copy, System.out);
	}

	private static void compareXPaths(File file, List<String> xpaths, Map<String, Terms> memory, AtomicInteger countAll)
			throws XPathExpressionException, IOException {
		// Set up input
		String content = FileUtils.readFileToString(file, "UTF8");
		// Initialize XPath
		XPath xpath = XPathTool.createXPath(toSource(content));
		// Go for all XPaths
		for (String expr : xpaths) {
			// Check if killed...
			if (Thread.currentThread().isInterrupted())
				return;
			// Evaluate XPath expression
			String result = XPathTool.evaluate(xpath, expr, toSource(content));
			result = result.replaceAll("\\s+", " ").trim();
			// If whitespace, ignore that!
			if (result.isEmpty())
				continue;
			// Store values...
			Terms list;
			if (memory.containsKey(expr))
				list = memory.get(expr);
			else
				memory.put(expr, list = new Terms(countAll));
			list.push(result);
		}
	}

	private static InputSource toSource(String content) {
		return new InputSource(new StringReader(content));
	}
}