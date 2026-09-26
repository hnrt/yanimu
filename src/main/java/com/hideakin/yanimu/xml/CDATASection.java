package com.hideakin.yanimu.xml;

public class CDATASection extends ImmutableNode {

	public static final String START = "<![CDATA[";
	public static final String END = "]]>";

	public static final byte[] START_BYTES = START.getBytes();
	public static final byte[] END_BYTES = END.getBytes();

	public static CDATASection of(byte[] sequence) {
		return new CDATASection(sequence);
	}

	public static CDATASection of(String sequence) {
		return new CDATASection(sequence);
	}

	private CDATASection(byte[] sequence) {
		super(CD_SECT, sequence, START_BYTES, END_BYTES);
	}

	private CDATASection(String sequence) {
		super(CD_SECT, sequence, START, END);
	}

	public String innerText() {
		return innerText(START_BYTES.length, END_BYTES.length);
	}

}
