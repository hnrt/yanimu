package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class CDATASection extends TerminalNode {

	public static final String START = "<![CDATA[";
	public static final String END = "]]>";

	private static final int START_LENGTH = START.length();
	private static final int START_END_LENGTH = START.length() + END.length();

	public static CDATASection of(byte[] sequence) {
		return new CDATASection(sequence);
	}

	public static CDATASection of(String source) {
		byte[] sequence;
		if (source.startsWith(START)) {
			if (source.endsWith(END)) {
				sequence = source.getBytes(StandardCharsets.UTF_8);
			} else {
				String processed = source + END;
				sequence = processed.getBytes(StandardCharsets.UTF_8);
			}
		} else if (source.endsWith(END)) {
			String processed = START + source;
			sequence = processed.getBytes(StandardCharsets.UTF_8);
		} else {
			String processed = START + source + END;
			sequence = processed.getBytes(StandardCharsets.UTF_8);
		}
		return new CDATASection(sequence);
	}

	private CDATASection(byte[] sequence) {
		super(CD_SECT, sequence);
	}

	public String innerText() {
		byte[] s = sequence();
		return new String(s, START_LENGTH, s.length - START_END_LENGTH, StandardCharsets.UTF_8);
	}

}
