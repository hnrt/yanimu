package com.hideakin.yanimu.xml;

/**
 * An immutable node for the XML CDATA Section.
 */
public class CDATASection extends ImmutableNode {

	/**
	 * The sequence of open delimiter for CDATA section. ({@code String} representation)
	 */
	public static final String OPEN_DELIMITER_STRING = "<![CDATA[";

	/**
	 * The sequence of close delimiter for CDATA section. ({@code String} representation)
	 */
	public static final String CLOSE_DELIMITER_STRING = "]]>";

	/**
	 * The sequence of open delimiter for CDATA section. (byte representation)
	 */
	public static final byte[] OPEN_DELIMITER_BYTES = OPEN_DELIMITER_STRING.getBytes();

	/**
	 * The sequence of close delimiter for CDATA section. (byte representation)
	 */
	public static final byte[] CLOSE_DELIMITER_BYTES = CLOSE_DELIMITER_STRING.getBytes();

	/**
	 * Creates a new node of type {@code CD_SECT} with a UTF-8 encoded byte representation of text.
	 * @param sequence a UTF-8 encoded byte sequence of text
	 * @return a newly created node
	 */
	public static CDATASection of(byte[] sequence) {
		return new CDATASection(sequence);
	}

	/**
	 * Creates a new node of type {@code CD_SECT} with a {@code String} representation of text.
	 * @param sequence a {@code String} representation of text
	 * @return a newly created node
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
	 * Returns text in this node.
	 * @return a {@code String} representation of the text in this node,
	 * excluding the start sequence "&lt;![CDATA[" and the end sequence "]]&gt;".
	 */
	public String innerText() {
		return innerText(OPEN_DELIMITER_BYTES.length, CLOSE_DELIMITER_BYTES.length);
	}

}
