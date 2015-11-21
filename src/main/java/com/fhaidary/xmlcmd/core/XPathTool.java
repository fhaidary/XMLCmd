package com.fhaidary.xmlcmd.core;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import com.fhaidary.xmlcmd.util.EasyNamespaceContext;

public final class XPathTool {

	static {
		final String DTM_MANAGER_NAME = "com.sun.org.apache.xml.internal.dtm.DTMManager";
		final String DTM_MANAGER_VALUE = "com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault";
		System.setProperty(DTM_MANAGER_NAME, DTM_MANAGER_VALUE);
	}

	private static final ThreadLocal<XPathFactory> xpathFactory = new ThreadLocal<XPathFactory>() {
		@Override
		protected XPathFactory initialValue() {
			return XPathFactory.newInstance();
		}
	};

	static XPath createXPath(InputSource source) throws XPathExpressionException {
		// Create new factory for XPath
		XPath xpath = xpathFactory.get().newXPath();
		// Find default name space
		EasyNamespaceContext ctx = new EasyNamespaceContext();
		ctx.setDefaultNamespace(xpath.evaluate("namespace-uri(/*)", source));
		xpath.setNamespaceContext(ctx);
		return xpath;
	}

	static String evaluate(XPath xpath, String path, InputSource source) throws XPathExpressionException {
		// XPathExpression expr = xpath.compile(path);
		// return expr.evaluate(source);
		return xpath.evaluate(path, source);
	}

	public static void evaluate(String file, String path) throws XPathExpressionException {
		// Set up input
		InputSource source = new InputSource(file);
		// Initialize XPath
		XPath xpath = createXPath(source);
		// Evaluate XPath expression
		String result = evaluate(xpath, path, source);
		// Output it!
		System.out.println(result.trim());
	}
}