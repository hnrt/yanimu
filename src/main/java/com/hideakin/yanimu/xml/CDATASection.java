package com.hideakin.yanimu.xml;

public class CDATASection extends Node {

	public final String innerText;

	public CDATASection(int offset, byte[] sequence) {
		super(CD_SECT, offset, sequence);
		this.innerText = new String(sequence, 9, sequence.length - 12);
	}

}
