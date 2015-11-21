package com.fhaidary.xmlcmd.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Terms implements Collection<TermEntry> {

	private final AtomicInteger all;
	private final Map<String, AtomicLong> occurs;

	public Terms(AtomicInteger all) {
		this.all = all;
		this.occurs = new TreeMap<String, AtomicLong>();
	}

	public void push(String text) {
		AtomicLong number;
		if (occurs.containsKey(text))
			number = occurs.get(text);
		else
			occurs.put(text, number = new AtomicLong(0L));
		number.incrementAndGet();
	}

	@Override
	public Iterator<TermEntry> iterator() {
		List<TermEntry> list = new LinkedList<TermEntry>();
		for (Entry<String, AtomicLong> e : occurs.entrySet())
			list.add(new TermEntry(e.getKey(), e.getValue(), all));
		return list.iterator();
	}

	@Override
	public boolean add(TermEntry e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends TermEntry> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}
}