package com.hideakin.yanimu.xml;

import java.util.Arrays;

public class QuotedString extends Token {

	public final String innerText;

	public QuotedString(int code, int start, int end, byte[] sequence) {
		super(code, start, end, sequence);
		this.innerText = new String(Arrays.copyOfRange(sequence, 1, sequence.length - 1));
	}

}
