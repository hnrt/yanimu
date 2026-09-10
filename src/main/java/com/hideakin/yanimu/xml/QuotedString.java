package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class QuotedString extends TerminalNode {

	public static QuotedString of(int type, byte[] sequence) {
		return new QuotedString(type, sequence);
	}

	public static QuotedString of(int type, String source) {
		return new QuotedString(type, source.getBytes(StandardCharsets.UTF_8));
	}

	protected QuotedString(int type, byte[] sequence) {
		super(type, sequence);
	}

	public String innerText() {
		byte[] s = sequence();
		return new String(s, 1, s.length - 2, StandardCharsets.UTF_8);
	}

}
