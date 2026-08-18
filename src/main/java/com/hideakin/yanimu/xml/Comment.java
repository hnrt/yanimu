package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class Comment extends Node {

	public Comment(byte[] sequence) {
		super(COMMENT, sequence);
	}

	public String innerText() {
		byte[] s = sequence();
		return new String(s, 4, s.length - 7, StandardCharsets.UTF_8);
	}

}
