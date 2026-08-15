package com.hideakin.yanimu.xml;

public class QuotedString extends Node {

	public final String innerText;

	public QuotedString(int type, int offset, byte[] sequence) {
		super(type, offset, sequence);
		this.innerText = new String(sequence, 1, sequence.length - 2);
	}

}
