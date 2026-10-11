package com.hideakin.yanimu.xml.internal;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A utility class of StringBuilder with a snapshot functionality.
 */
public class CodePointBuffer {

	private final StringBuilder _buffer = new StringBuilder();
	private String _snapshot = null;

	/**
	 * Initializes a newly created object.
	 */
	public CodePointBuffer() {
	}

	/**
	 * Appends the string representation of the codePoint argument to the buffer
	 * and clears the snapshot.
	 * @param c a UNICODE code point
	 */
	public void append(int c) {
		_buffer.appendCodePoint(c);
		_snapshot = null;
	}

	/**
	 * Takes the snapshot from the buffer if it is cleared,
	 * clears the buffer,
	 * and returns a UTF-8 encoded byte representation of the content.
	 * @return a UTF-8 encoded byte representation of the content
	 */
	public byte[] getBytes() {
		if (_snapshot == null) {
			_snapshot = _buffer.toString();
		}
		byte[] sequence = _snapshot.getBytes(StandardCharsets.UTF_8);
		_buffer.setLength(0);
		_snapshot = null;
		return sequence;
	}

	/**
	 * Returns the number of UCS2 characters stored in the buffer.
	 * @return the number of UCS2 characters stored in the buffer
	 */
	public int getLength() {
		return _buffer.length();
	}

	/**
	 * Sets the number of UCS2 characters of the buffer to the specified one.
	 * @param length the number of UCS2 characters to set
	 */
	public void setLength(int length) {
		_buffer.setLength(length);
	}

	/**
	 * Takes the snapshot from the buffer if it is cleared,
	 * and matches it with the specified regular expression.
	 * @param regex a regular expression to match
	 * @return true if match, or false otherwise
	 */
	public boolean matches(String regex) {
		if (_snapshot == null) {
			_snapshot = _buffer.toString();
		}
		return _snapshot.matches(regex);
	}

	/**
	 * Takes the snapshot from the buffer if it is cleared,
	 * and look up it for the specified map.
	 * @param map a map to look up the snapshot
	 * @param defaultValue the return value on look up error
	 * @return the value of the matched entry, or {@code defaultValue} on error
	 */
	public int lookup(Map<String, Integer> map, int defaultValue) {
		if (_snapshot == null) {
			_snapshot = _buffer.toString();
		}
		Integer value = map.get(_snapshot);
		return value != null ? value.intValue() : defaultValue;
	}

}
