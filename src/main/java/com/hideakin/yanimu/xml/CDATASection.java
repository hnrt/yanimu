package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class CDATASection extends Node {

	public CDATASection(byte[] sequence) {
		super(CD_SECT, sequence);
	}

	public String innerText() {
		byte[] s = sequence();
		return new String(s, 9, s.length - 12, StandardCharsets.UTF_8);
	}

}
