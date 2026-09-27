package com.hideakin.yanimu.xml;

public class CDATASection extends ImmutableNode {

	public static final String OPEN_DELIMITER_STRING = "<![CDATA[";
	public static final String CLOSE_DELIMITER_STRING = "]]>";

	public static final byte[] OPEN_DELIMITER_BYTES = OPEN_DELIMITER_STRING.getBytes();
	public static final byte[] CLOSE_DELIMITER_BYTES = CLOSE_DELIMITER_STRING.getBytes();

	public static CDATASection of(byte[] sequence) {
		return new CDATASection(sequence);
	}

	public static CDATASection of(String sequence) {
		return new CDATASection(sequence);
	}

	private CDATASection(byte[] sequence) {
		super(CD_SECT, sequence, OPEN_DELIMITER_BYTES, CLOSE_DELIMITER_BYTES);
	}

	private CDATASection(String sequence) {
		super(CD_SECT, sequence, OPEN_DELIMITER_STRING, CLOSE_DELIMITER_STRING);
	}

	public String innerText() {
		return innerText(OPEN_DELIMITER_BYTES.length, CLOSE_DELIMITER_BYTES.length);
	}

}
