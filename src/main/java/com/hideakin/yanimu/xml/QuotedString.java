package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class QuotedString extends Node {

	public QuotedString(int type, byte[] sequence) {
		super(type, sequence);
	}

	public String innerText() {
		byte[] s = sequence();
		return new String(s, 1, s.length - 2, StandardCharsets.UTF_8);
	}

}
