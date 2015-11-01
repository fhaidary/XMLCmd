package com.fhaidary.xmlcmd.app;

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