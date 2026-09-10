package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class TerminalNode extends Node {

	public static TerminalNode of(int type, byte[] sequence) {
		return new TerminalNode(type, sequence);
	}

	public static TerminalNode of(int type, String sequence) {
		return new TerminalNode(type, sequence.getBytes(StandardCharsets.UTF_8));
	}

	public static TerminalNode of(int type, char...characters) {
		return new TerminalNode(type, new String(characters).getBytes(StandardCharsets.UTF_8));
	}

	protected final byte[] _sequence;

	protected TerminalNode(int type, byte[] sequence) {
		super(type);
		_sequence = sequence;
	}

	@Override
	public byte[] sequence() {
		return _sequence;
	}

	@Override
	public int length() {
		return _sequence.length;
	}

}
