package com.fhaidary.xmlcmd.core;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class XsltTool {

	public static void transform(String file, String style) throws TransformerException {
		// Get new transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		// Get stylesheet's source
		Source xslt = new StreamSource(new File(style));
		Transformer trans = factory.newTransformer(xslt);
		// Get XML's source
		Source xml = new StreamSource(new File(file));
		StringWriter str = new StringWriter();
		trans.transform(xml, new StreamResult(str));
		System.out.println(str.toString().trim());
	}
}