package com.hideakin.yanimu.xml;

import java.util.Arrays;

public class CDATASection extends Token {

	public final String innerText;

	public CDATASection(int start, int end, byte[] sequence) {
		super(CD_SECT, start, end, sequence);
		this.innerText = new String(Arrays.copyOfRange(sequence, 9, sequence.length - 3));
	}

}
