package com.hideakin.yanimu.xml;

import java.util.Arrays;

public class Comment extends Node {

	public final String innerText;

	public Comment(int start, int end, byte[] sequence) {
		super(COMMENT, start, end, sequence);
		this.innerText = new String(Arrays.copyOfRange(sequence, 4, sequence.length - 3));
	}

}
