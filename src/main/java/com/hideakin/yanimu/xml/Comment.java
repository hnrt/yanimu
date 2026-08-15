package com.hideakin.yanimu.xml;

public class Comment extends Node {

	public final String innerText;

	public Comment(int offset, byte[] sequence) {
		super(COMMENT, offset, sequence);
		this.innerText = new String(sequence, 4, sequence.length - 7);
	}

}
