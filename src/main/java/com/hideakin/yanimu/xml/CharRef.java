package com.hideakin.yanimu.xml;

public class CharRef extends Node {

	public final int codepoint;

	public CharRef(byte[] sequence) {
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
