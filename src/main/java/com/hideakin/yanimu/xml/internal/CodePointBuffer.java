package com.hideakin.yanimu.xml.internal;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CodePointBuffer {

	private final StringBuilder _buffer = new StringBuilder();
	private String _snapshot = null;

	public CodePointBuffer() {
	}

	public void append(int c) {
		_buffer.appendCodePoint(c);
		_snapshot = null;
	}

	public byte[] getBytes() {
		if (_snapshot == null) {
			_snapshot = _buffer.toString();
		}
		byte[] sequence = _snapshot.getBytes(StandardCharsets.UTF_8);
		_buffer.setLength(0);
		_snapshot = null;
		return sequence;
	}

	public int getLength() {
		return _buffer.length();
	}

	public void setLength(int length) {
		_buffer.setLength(length);
	}

	public boolean matches(String regex) {
		if (_snapshot == null) {
			_snapshot = _buffer.toString();
		}
		return _snapshot.matches(regex);
	}

	public int lookup(Map<String, Integer> map, int defaultValue) {
		if (_snapshot == null) {
			_snapshot = _buffer.toString();
		}
		Integer value = map.get(_snapshot);
		return value != null ? value.intValue() : defaultValue;
	}

}
