package com.hideakin.yanimu.xml.internal;

import static com.hideakin.yanimu.xml.Node.EOF;
import static com.hideakin.yanimu.xml.Node.PREMATURE_EOF;
import static com.hideakin.yanimu.xml.Node.ILLEGAL_ENCODING;

public class UTF16LEReader extends AnyReader {

	public UTF16LEReader(byte[] content, NodeFactory nodeFactory) {
		super(content, nodeFactory);
		if (2 <= content.length && content[0] == -1 && content[1] == -2) {
			// FF FE (BOM)
			_i = 2;
		}
	}

	@Override
	public int readChar() {
		if (Character.MIN_CODE_POINT <= _c && _c <= Character.MAX_CODE_POINT) {
			storeChar(_c);
		}
		_h = _i;
		int w1 = readWord();
		if (w1 < Character.MIN_HIGH_SURROGATE) {
			_c = w1;
		} else if (w1 <= Character.MAX_HIGH_SURROGATE) {
			int w2 = readWord();
			if (Character.MIN_LOW_SURROGATE <= w1 && w1 <= Character.MAX_LOW_SURROGATE) {
				_c = 0x10000 + (w1 - Character.MIN_HIGH_SURROGATE) * 0x400 + (w2 - Character.MIN_LOW_SURROGATE);
			} else {
				_c = ILLEGAL_ENCODING;
			}
		} else if (w1 <= Character.MAX_LOW_SURROGATE) {
			_c = ILLEGAL_ENCODING;
		} else {
			_c = w1;
		}
		return _c;
	}

	private int readWord() {
		if (_i + 1 < _content.length) {
			int w = ((((int)_content[_i + 0] + 0x100) & 0xFF) << 0) | ((((int)_content[_i + 1] + 0x100) & 0xFF) << 8);
			_i += 2;
			return w;
		} else if (_i < _content.length) {
			return PREMATURE_EOF;
		} else {
			return EOF;
		}
	}

}
