package com.fhaidary.xmlcmd.util;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public final class EasyNamespaceContext implements NamespaceContext {

	private String defaultNamespace;

	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix.isEmpty())
			return defaultNamespace;
		throw new UnsupportedOperationException("Prefix '" + prefix + "' not found!");
	}

	@Override
	public String getPrefix(String ns) {
		throw new UnsupportedOperationException(ns);
	}

	@Override
	public Iterator<?> getPrefixes(String ns) {
		throw new UnsupportedOperationException(ns);
	}

	public void setDefaultNamespace(String namespace) {
		String defNs = namespace.trim();
		if (defNs.length() >= 1)
			defaultNamespace = defNs;
	}
}