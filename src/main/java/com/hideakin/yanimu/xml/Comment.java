package com.hideakin.yanimu.xml;

public class Comment extends ImmutableNode {

	public static final String START = "<!--";
	public static final String END = "-->";

	public static final byte[] START_BYTES = START.getBytes();
	public static final byte[] END_BYTES = END.getBytes();

	public static Comment of(byte[] sequence) {
		return new Comment(sequence);
	}

	public static Comment of(String sequence) {
		return new Comment(sequence);
	}

	private Comment(byte[] sequence) {
		super(COMMENT, sequence, START_BYTES, END_BYTES);
	}

	private Comment(String sequence) {
		super(COMMENT, sequence, START, END);
	}

	public String innerText() {
		return innerText(START_BYTES.length, END_BYTES.length);
	}

}
