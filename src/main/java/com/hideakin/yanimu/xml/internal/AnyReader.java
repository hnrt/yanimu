package com.hideakin.yanimu.xml.internal;

import static com.hideakin.yanimu.xml.Character.*;

/**
 * The base class implements the Reader interface.
 */
public class AnyReader implements Reader {

	/**
	 * The byte representation of the content.
	 */
	protected final byte[] _content;

	/**
	 * The code point buffer.
	 */
	protected final CodePointBuffer _buffer;

	/**
	 * The index of the next byte to read.
	 */
	protected int _i;

	/**
	 * The UNICODE code point currently read.
	 */
	protected int _c;

	/**
	 * Initializes a newly created object with a byte sequence of the content and a code point buffer.
	 * @param content a byte sequence of the content to read
	 * @param buffer a code point buffer
	 */
	protected AnyReader(byte[] content, CodePointBuffer buffer) {
		_content = content;
		_buffer = buffer;
		_i = 0;
		_c = Character.MAX_CODE_POINT + 1;
	}

	@Override
	public int readCodePoint() {
		return EOF;
	}

	@Override
	public boolean next(int... cc) {
		int i = _i;
		int c = _c;
		int j = _buffer.getLength();
		int n = cc.length;
		for (int k = 0; k < n; k++) {
			if (readCodePoint() != cc[k]) {
				_i = i;
				_c = c;
				_buffer.setLength(j);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean peek(int... cc) {
		boolean result = true;
		int i = _i;
		int c = _c;
		int j = _buffer.getLength();
		int n = cc.length;
		for (int k = 0; k < n && (result = readCodePoint() == cc[k]); k++) continue;
		_i = i;
		_c = c;
		_buffer.setLength(j);
		return result;
	}

	@Override
	public int codepoint() {
		return _c;
	}

	/**
	 * Stores a code point in the buffer.
	 * @param c a code point to store
	 */
	protected void storeCodePoint(int c) {
		_buffer.append(c);
	}

}
