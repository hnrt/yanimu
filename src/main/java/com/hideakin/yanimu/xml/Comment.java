package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class Comment extends Node {

	public static final String START = "<!--";
	public static final String END = "-->";

	private static final int START_LENGTH = START.length();
	private static final int START_END_LENGTH = START.length() + END.length();

	public static Comment of(byte[] sequence) {
		return new Comment(sequence);
	}

	public static Comment of(String sequence) {
		return new Comment(sequence.getBytes(StandardCharsets.UTF_8));
	}

	private Comment(byte[] sequence) {
		super(COMMENT, sequence);
	}

	public String innerText() {
		byte[] s = sequence();
		return new String(s, START_LENGTH, s.length - START_END_LENGTH, StandardCharsets.UTF_8);
	}

}
