package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * An immutable node containing an immutable sequence of bytes.
 */
public class ImmutableNode extends Node {

	public static ImmutableNode of(int type, byte[] sequence) {
		return new ImmutableNode(type, sequence);
	}

	public static ImmutableNode of(int type, String sequence) {
		return new ImmutableNode(type, sequence);
	}

	public static ImmutableNode of(int type, char...characters) {
		return new ImmutableNode(type, new String(characters));
	}

	protected final byte[] _sequence;

	protected ImmutableNode(int type, byte[] sequence) {
		super(type);
		_sequence = sequence != null ? sequence : new byte[0];
	}

	protected ImmutableNode(int type, String sequence) {
		super(type);
		_sequence = sequence != null ? sequence.getBytes(StandardCharsets.UTF_8) : new byte[0];
	}

	protected ImmutableNode(int type, byte[] source, byte[] start, byte[] end) {
		super(type);
		if (source == null) {
			_sequence = new byte[0];
		} else if (source.length >= start.length && Arrays.mismatch(source, 0, start.length, start, 0, start.length) == -1) {
			if (Arrays.mismatch(source, source.length - end.length, source.length, end, 0, end.length) == -1) {
				_sequence = source;
			} else {
				_sequence = Arrays.copyOf(source, source.length + end.length);
				System.arraycopy(_sequence, source.length, end, 0, end.length);
			}
		} else if (source.length >= end.length && Arrays.mismatch(source, source.length - end.length, source.length, end, 0, end.length) == -1) {
			_sequence = Arrays.copyOf(start, start.length + source.length);
			System.arraycopy(_sequence, start.length, source, 0, source.length);
		} else {
			_sequence = Arrays.copyOf(start, start.length + source.length + end.length);
			System.arraycopy(_sequence, start.length, source, 0, source.length);
			System.arraycopy(_sequence, start.length + source.length, end, 0, end.length);
		}
	}

	protected ImmutableNode(int type, String source, String start, String end) {
		super(type);
		if (source == null) {
			_sequence = new byte[0];
		} else if (source.startsWith(start)) {
			if (source.endsWith(end)) {
				_sequence = source.getBytes(StandardCharsets.UTF_8);
			} else {
				_sequence = (source + end).getBytes(StandardCharsets.UTF_8);
			}
		} else if (source.endsWith(end)) {
			_sequence = (start + source).getBytes(StandardCharsets.UTF_8);
		} else {
			_sequence = (start + source + end).getBytes(StandardCharsets.UTF_8);
		}
	}

	@Override
	public byte[] sequence() {
		return _sequence.clone();
	}

	@Override
	public int length() {
		return _sequence.length;
	}

	/**
	 * Returns a String containing the text of this node within the specified range.
	 * @param clippingLengthAtStart the starting byte offset of the range
	 * @param clippingLengthAtEnd the ending byte offset of the range, from the end of the byte sequence 
	 * @return a String containing the specified portion of the text
	 */
	protected String innerText(int clippingLengthAtStart, int clippingLengthAtEnd) {
		int offset = clippingLengthAtStart;
		int length = _sequence.length - clippingLengthAtStart - clippingLengthAtEnd;
		return new String(_sequence, offset, length, StandardCharsets.UTF_8);
	}

}
