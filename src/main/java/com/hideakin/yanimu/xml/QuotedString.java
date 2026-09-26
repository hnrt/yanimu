package com.hideakin.yanimu.xml;

public class QuotedString extends ImmutableNode {

	public static QuotedString of(int type, byte[] sequence) {
		return new QuotedString(type, sequence);
	}

	public static QuotedString of(int type, String sequence) {
		return new QuotedString(type, sequence);
	}

	protected QuotedString(int type, byte[] sequence) {
		super(type, sequence);
	}

	protected QuotedString(int type, String sequence) {
		super(type, sequence);
	}

	public String innerText() {
		return innerText(1, 1);
	}

}
