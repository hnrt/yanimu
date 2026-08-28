package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class CharRef extends Node {

	public static final String START = "&#";
	public static final String START_HEX = "&#x";
	public static final String END = ";";

	public static CharRef of(int codepoint) {
		return new CharRef(codepoint);
	}

	public static CharRef of(byte[] sequence) {
		return new CharRef(sequence);
	}

	public static CharRef of(String sequence) {
		return new CharRef(sequence.getBytes(StandardCharsets.UTF_8));
	}

	public final int codepoint;

	private CharRef(int codepoint) {
		super(CHAR_REF, String.format("%s%d%s", START, codepoint, END));
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
