package com.fhaidary.xmlcmd.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TermEntry {

	public final String Text;
	public final String Frequency;

	public TermEntry(String text, AtomicLong number, AtomicInteger all) {
		Text = text;
		double fraction = (number.get() * 1.0) / (all.get() * 1.0);
		Frequency = String.format("%.2f %% (%s of %s)", fraction * 100.0, number, all);
	}
}