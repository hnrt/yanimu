package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

/**
 * An immutable node for the XML Character Reference.
 */
public class CharRef extends ImmutableNode {

	/**
	 * The start sequence of character reference for the decimal representation of code point.
	 */
	public static final String START = "&#";

	/**
	 * The start sequence of character reference for the hexadecimal representation of code point.
	 */
	public static final String START_HEX = "&#x";

	/**
	 * The end sequence of character reference.
	 */
	public static final String END = ";";

	/**
	 * Creates a new node of type {@code CHAR_REF}.
	 * @param codepoint a code point for a character reference to assign to this node
	 * @return a newly created node
	 */
	public static CharRef of(int codepoint) {
		return new CharRef(codepoint);
	}

	/**
	 * Creates a new node of type {@code CHAR_REF}.
	 * @param sequence a UTF-8 encoded byte representation of a character reference to assign to this node
	 * @return a newly created node
	 */
	public static CharRef of(byte[] sequence) {
		return new CharRef(sequence);
	}

	/**
	 * Creates a new node of type {@code CHAR_REF}.
	 * @param sequence a {@code String} representation of a character reference to assign to this node
	 * @return a newly created node
	 */
	public static CharRef of(String sequence) {
		return new CharRef(sequence.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * The code point of the character reference.
	 */
	public final int codepoint;

	private CharRef(int codepoint) {
		super(CHAR_REF, String.format("%s%d%s", START, codepoint, END).getBytes(StandardCharsets.UTF_8));
		this.codepoint = codepoint;
	}

	private CharRef(byte[] sequence) {
		super(CHAR_REF, sequence);
		int i = 2;
		int c = sequence[i++];
		int d = 0;
		if (c == 'x') {
			c = sequence[i++];
			do {
				d = d * 16 + (c < 'A' ? c - '0' : c < 'a' ? c - 'A' + 10 : c - 'a' + 10);
			} while ((c = sequence[i++]) != ';');
		} else {
			do {
				d = d * 10 + (c - '0');
			} while ((c = sequence[i++]) != ';');
		}
		this.codepoint = d;
	}

}
