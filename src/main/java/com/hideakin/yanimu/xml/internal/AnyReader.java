package com.hideakin.yanimu.xml.internal;

import static com.hideakin.yanimu.xml.Character.*;

public class AnyReader implements Reader {

	protected final byte[] _content;
	protected final CodePointBuffer _buffer;
	protected int _i; // index of the next byte to read
	protected int _c; // current UNICODE code point

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

	protected void storeCodePoint(int c) {
		_buffer.append(c);
	}

}
