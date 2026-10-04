package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

/**
 * An immutable node object for XML Character Reference.
 */
public class CharRef extends ImmutableNode {

	public static final String START = "&#";
	public static final String START_HEX = "&#x";
	public static final String END = ";";

	/**
	 * Creates a new immutable node of {@code CHAR_REF}.
	 * @param codepoint the code point of a character reference to format in this node
	 * @return a newly created node of {@code CHAR_REF}
	 */
	public static CharRef of(int codepoint) {
		return new CharRef(codepoint);
	}

	/**
	 * Creates a new immutable node of {@code CHAR_REF}.
	 * @param sequence the UTF-8 representation of a character reference to be stored in this node
	 * @return a newly created node of {@code CHAR_REF}
	 */
	public static CharRef of(byte[] sequence) {
		return new CharRef(sequence);
	}

	/**
	 * Creates a new immutable node of {@code CHAR_REF}.
	 * @param sequence the String representation of a character reference to be stored in this node
	 * @return a newly created node of {@code CHAR_REF}
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
