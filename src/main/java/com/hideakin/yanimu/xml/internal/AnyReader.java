package com.hideakin.yanimu.xml.internal;

import static com.hideakin.yanimu.xml.Token.EOF;

public class AnyReader implements Reader {

	protected final byte[] _content;
	protected final StringBuilder _buffer;
	protected int _h; // head index of the byte sequence
	protected int _i; // index of the next byte to read
	protected int _c; // current UNICODE codepoint

	protected AnyReader(byte[] content) {
		_content = content;
		_buffer = new StringBuilder();
		_h = 0;
		_i = 0;
		_c = Character.MAX_CODE_POINT + 1;
	}

	@Override
	public int readChar() {
		return EOF;
	}

	@Override
	public boolean next(int... cc) {
		int h = _h;
		int i = _i;
		int c = _c;
		int j = _buffer.length();
		int n = cc.length;
		for (int k = 0; k < n; k++) {
			if (readChar() != cc[k]) {
				_h = h;
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
		int h = _h;
		int i = _i;
		int c = _c;
		int j = _buffer.length();
		int n = cc.length;
		for (int k = 0; k < n && (result = readChar() == cc[k]); k++) continue;
		_h = h;
		_i = i;
		_c = c;
		_buffer.setLength(j);
		return result;
	}

	@Override
	public int from() {
		return _h;
	}

	@Override
	public int to() {
		return _i;
	}

	@Override
	public int codepoint() {
		return _c;
	}

	@Override
	public String text() {
		String buffered = _buffer.toString();
		_buffer.setLength(0);
		return buffered;
	}

	@Override
	public void reset(int i) {
		_buffer.setLength(0);
		_h = i;
		_i = i;
		_c = Character.MAX_CODE_POINT + 1;
	}

}
