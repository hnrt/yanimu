package com.hideakin.yanimu.xml.internal;

import static com.hideakin.yanimu.xml.internal.Lexer.EOF;

public class AnyReader implements Reader {

	protected final byte[] _content;
	protected final NodeFactory _nodeFactory;
	protected int _h; // head index of the byte sequence
	protected int _i; // index of the next byte to read
	protected int _c; // current UNICODE codepoint

	protected AnyReader(byte[] content, NodeFactory nodeFactory) {
		_content = content;
		_nodeFactory = nodeFactory;
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
		int j = _nodeFactory.getLength();
		int n = cc.length;
		for (int k = 0; k < n; k++) {
			if (readChar() != cc[k]) {
				_h = h;
				_i = i;
				_c = c;
				_nodeFactory.setLength(j);
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
		int j = _nodeFactory.getLength();
		int n = cc.length;
		for (int k = 0; k < n && (result = readChar() == cc[k]); k++) continue;
		_h = h;
		_i = i;
		_c = c;
		_nodeFactory.setLength(j);
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
	public void reset(int i) {
		_nodeFactory.setLength(0);
		_h = i;
		_i = i;
		_c = Character.MAX_CODE_POINT + 1;
	}

	protected void storeChar(int c) {
		_nodeFactory.push(c);
	}

}
