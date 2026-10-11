package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * An immutable XML node containing a sequence of bytes.
 */
public class ImmutableNode extends Node {

	/**
	 * Creates a new immutable node with a node type and a byte sequence.
	 * @param type a node type to assign to this node
	 * @param sequence a UTF-8 encoded byte representation of this node
	 * @return a newly created node
	 */
	public static ImmutableNode of(int type, byte[] sequence) {
		return new ImmutableNode(type, sequence);
	}

	/**
	 * Creates a new immutable node with a node type and a {@code String} sequence.
	 * @param type a node type to assign to this node
	 * @param sequence a {@code String} representation of this node
	 * @return a newly created node
	 */
	public static ImmutableNode of(int type, String sequence) {
		return new ImmutableNode(type, sequence);
	}

	/**
	 * Creates a new immutable node with a node type and a character sequence.
	 * @param type a node type to assign to this node
	 * @param characters a {@code char} sequence representation of this node 
	 * @return a newly created node
	 */
	public static ImmutableNode of(int type, char...characters) {
		return new ImmutableNode(type, new String(characters));
	}

	/**
	 * The UTF-8 encoded byte representation of this node.
	 */
	protected final byte[] _sequence;

	/**
	 * Initializes a newly created node with a node type and a byte sequence.
	 * @param type a node type to assign to this node
	 * @param sequence a UTF-8 encoded byte representation of this node
	 */
	protected ImmutableNode(int type, byte[] sequence) {
		super(type);
		_sequence = sequence != null ? sequence : new byte[0];
	}

	/**
	 * Initializes a newly created node with a node type and a {@code String} sequence.
	 * @param type a node type to assign to this node
	 * @param sequence a {@code String} representation of this node
	 */
	protected ImmutableNode(int type, String sequence) {
		super(type);
		_sequence = sequence != null ? sequence.getBytes(StandardCharsets.UTF_8) : new byte[0];
	}

	/**
	 * Initializes a newly created node with a node type, a byte sequence, a start sequence and an end sequence.
	 * <p>
	 * Checks if the given byte sequence starts and ends with the given start and end sequence, respectively.
	 * If not, the start and end sequence are added to the byte sequence.
	 * @param type a node type to assign to this node
	 * @param source a UTF-8 encoded byte representation of this node
	 * @param start a UTF-8 encoded byte representation of the start sequence for this node
	 * @param end a UTF-8 encoded byte representation of the end sequence for this node
	 */
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

	/**
	 * Initializes a newly created node with a node type, a {@code String} sequence, a start sequence and an end sequence.
	 * <p>
	 * Checks if the given byte sequence starts and ends with the given start and end sequence, respectively.
	 * If not, the start and end sequence are added to the {@code String} sequence.
	 * @param type a node type to assign to this node
	 * @param source a {@code String} representation of this node
	 * @param start a {@code String} representation of the start sequence for this node
	 * @param end a {@code String} representation of the end sequence for this node
	 */
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
	 * Returns a {@code String} containing the text of this node within the specified range.
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
