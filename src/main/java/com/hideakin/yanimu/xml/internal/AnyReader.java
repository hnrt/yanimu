package com.hideakin.yanimu.xml.internal;

import static com.hideakin.yanimu.xml.internal.Lexer.EOF;

public class AnyReader implements Reader {

	protected final byte[] _content;
	protected final NodeFactory _nodeFactory;
	protected int _i; // index of the next byte to read
	protected int _c; // current UNICODE codepoint

	protected AnyReader(byte[] content, NodeFactory nodeFactory) {
		_content = content;
		_nodeFactory = nodeFactory;
		_i = 0;
		_c = Character.MAX_CODE_POINT + 1;
	}

	@Override
	public int readChar() {
		return EOF;
	}

	@Override
	public boolean next(int... cc) {
		int i = _i;
		int c = _c;
		int j = _nodeFactory.getLength();
		int n = cc.length;
		for (int k = 0; k < n; k++) {
			if (readChar() != cc[k]) {
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
		int i = _i;
		int c = _c;
		int j = _nodeFactory.getLength();
		int n = cc.length;
		for (int k = 0; k < n && (result = readChar() == cc[k]); k++) continue;
		_i = i;
		_c = c;
		_nodeFactory.setLength(j);
		return result;
	}

	@Override
	public int codepoint() {
		return _c;
	}

	protected void storeChar(int c) {
		_nodeFactory.push(c);
	}

}
