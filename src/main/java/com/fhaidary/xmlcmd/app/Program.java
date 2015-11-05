package com.fhaidary.xmlcmd.app;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import com.fhaidary.xmlcmd.util.EasyNamespaceContext;

public class Program {

	public static void main(String[] args) throws Exception {
		// Get parameters
		String file = args[0];
		String path = args[1];
		// Decide which to run
		if (path.contains(".xslt.xml"))
			processXslt(file, path);
		else
			processXpath(file, path);
	}

	private static void processXslt(String file, String style) throws Exception {
		// Get new transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		// Get stylesheet's source
		Source xslt = new StreamSource(new File(style));
		Transformer trans = factory.newTransformer(xslt);
		// Get XML's source
		Source xml = new StreamSource(new File(file));
		StringWriter str = new StringWriter();
		trans.transform(xml, new StreamResult(str));
		System.out.println(str.toString());
	}

	private static void processXpath(String file, String path) throws Exception {
		// Set up input
		InputSource source = new InputSource(file);
		// Create new factory for XPath
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		// Find default name space
		EasyNamespaceContext ctx = new EasyNamespaceContext();
		ctx.setDefaultNamespace(xpath.evaluate("namespace-uri(/*)", source));
		xpath.setNamespaceContext(ctx);
		// Evaluate XPath expression
		XPathExpression expr = xpath.compile(path);
		String result = expr.evaluate(source);
		// Output it!
		System.out.println("<result>" + result + "</result>");
	}
}