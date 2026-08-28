package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class CDATASection extends Node {

	public static final String START = "<![CDATA[";
	public static final String END = "]]>";

	private static final int START_LENGTH = START.length();
	private static final int START_END_LENGTH = START.length() + END.length();

	public static CDATASection of(byte[] sequence) {
		return new CDATASection(sequence);
	}

	public static CDATASection of(String sequence) {
		return new CDATASection(sequence.getBytes(StandardCharsets.UTF_8));
	}

	private CDATASection(byte[] sequence) {
		super(CD_SECT, sequence);
	}

	public String innerText() {
		byte[] s = sequence();
		return new String(s, START_LENGTH, s.length - START_END_LENGTH, StandardCharsets.UTF_8);
	}

}
