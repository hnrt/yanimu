package com.hideakin.yanimu.xml.internal;

import static com.hideakin.yanimu.xml.Token.EOF;
import static com.hideakin.yanimu.xml.Token.ILLEGAL_ENCODING;

public class UTF8Reader extends AnyReader {

	public UTF8Reader(byte[] content) {
		super(content);
		if (3 <= content.length && content[0] == 0xEF && content[1] == 0xBB && content[2] == 0xBF) {
			_i = 3;
		}
	}

	@Override
	public int readChar() {
		if (Character.MIN_CODE_POINT <= _c && _c <= Character.MAX_CODE_POINT) {
			_buffer.appendCodePoint(_c);
		}
		_h = _i;
		int b1 = readByte();
		if (b1 < 0x80) {
			_c = b1;
		} else if (b1 < 0xC2) {
			_c = ILLEGAL_ENCODING;
		} else if (b1 < 0xE0) {
			int b2 = readByte();
			if (b2 < 0x80) {
				_c = ILLEGAL_ENCODING;
			} else if (b2 < 0xC0) {
				_c = ((b1 & 0x1F) << (6 * 1)) | ((b2 & 0x3F) << (6 * 0));
				if (_c < 0x80) {
					_c = ILLEGAL_ENCODING;
				}
			} else {
				_c = ILLEGAL_ENCODING;
			}
		} else if (b1 < 0xF0) {
			int b2 = readByte();
			if (b2 < 0x80) {
				_c = ILLEGAL_ENCODING;
			} else if (b2 < 0xC0) {
				int b3 = readByte();
				if (b3 < 0x80) {
					_c = ILLEGAL_ENCODING;
				} else if (b3 < 0xC0) {
					_c = ((b1 & 0x0F) << (6 * 2)) | ((b2 & 0x3F) << (6 * 1)) | ((b3 & 0x3F) << (6 * 0));
					if (_c < 0x800 || (0xD800 <= _c && _c <= 0xDFFF)) {
						_c = ILLEGAL_ENCODING;
					}
				} else {
					_c = ILLEGAL_ENCODING;
				}
			} else {
				_c = ILLEGAL_ENCODING;
			}
		} else if (b1 < 0xF8) {
			int b2 = readByte();
			if (b2 < 0x80) {
				_c = ILLEGAL_ENCODING;
			} else if (b2 < 0xC0) {
				int b3 = readByte();
				if (b3 < 0x80) {
					_c = ILLEGAL_ENCODING;
				} else if (b3 < 0xC0) {
					int b4 = readByte();
					if (b4 < 0x80) {
						_c = ILLEGAL_ENCODING;
					} else if (b4 < 0xC0) {
						_c = ((b1 & 0x0F) << (6 * 3)) | ((b2 & 0x3F) << (6 * 2)) | ((b3 & 0x3F) << (6 * 1)) | ((b4 & 0x3F) << (6 * 0));
						if (_c < 0x10000 || 0x10FFFF < _c) {
							_c = ILLEGAL_ENCODING;
						}
					} else {
						_c = ILLEGAL_ENCODING;
					}
				} else {
					_c = ILLEGAL_ENCODING;
				}
			} else {
				_c = ILLEGAL_ENCODING;
			}
		} else {
			_c = ILLEGAL_ENCODING;
		}
		return _c;
	}

	private int readByte() {
		return _i < _content.length ? ((int)_content[_i++] + 0x100) & 0xFF : EOF;
	}

}
