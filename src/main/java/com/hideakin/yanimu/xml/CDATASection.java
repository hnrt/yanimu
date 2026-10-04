package com.hideakin.yanimu.xml;

/**
 * An immutable node object for XML CDATA section.
 */
public class CDATASection extends ImmutableNode {

	public static final String OPEN_DELIMITER_STRING = "<![CDATA[";
	public static final String CLOSE_DELIMITER_STRING = "]]>";

	public static final byte[] OPEN_DELIMITER_BYTES = OPEN_DELIMITER_STRING.getBytes();
	public static final byte[] CLOSE_DELIMITER_BYTES = CLOSE_DELIMITER_STRING.getBytes();

	/**
	 * Creates a new CDATA section node with a UTF-8 representation of text.
	 * @param sequence a byte sequence of the CDATA section text encoded in UTF-8
	 * @return a newly created CDATA section node
	 */
	public static CDATASection of(byte[] sequence) {
		return new CDATASection(sequence);
	}

	/**
	 * Creates a new CDATA section node with a String representation of text.
	 * @param sequence a String of the CDATA section text
	 * @return a newly created CDATA section node
	 */
	public static CDATASection of(String sequence) {
		return new CDATASection(sequence);
	}

	private CDATASection(byte[] sequence) {
		super(CD_SECT, sequence, OPEN_DELIMITER_BYTES, CLOSE_DELIMITER_BYTES);
	}

	private CDATASection(String sequence) {
		super(CD_SECT, sequence, OPEN_DELIMITER_STRING, CLOSE_DELIMITER_STRING);
	}

	/**
	 * Returns the text contained in this CDATA section.
	 * @return the text from which the leading "&lt;![CDATA[" and the trailing "]]&gt;" are excluded.
	 */
	public String innerText() {
		return innerText(OPEN_DELIMITER_BYTES.length, CLOSE_DELIMITER_BYTES.length);
	}

}
