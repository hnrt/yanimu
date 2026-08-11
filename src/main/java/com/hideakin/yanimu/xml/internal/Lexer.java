package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.Token;

import static com.hideakin.yanimu.xml.Token.*;

public class Lexer {

	private final static int MODE_XML = 1;
	private final static int MODE_PI = 2;
	private final static int MODE_STAG = 3;
	private final static int MODE_ETAG = 4;
	private final static int MODE_DOCTYPE = 500;
	private final static int MODE_DOCTYPE_SYSTEM = 501;
	private final static int MODE_DOCTYPE_PUBLIC = 502;
	private final static int MODE_DOCTYPE_ELEMENT = 510;
	private final static int MODE_DOCTYPE_ATTLIST = 520;
	private final static int MODE_DOCTYPE_ENTITY = 530;
	private final static int MODE_DOCTYPE_NOTATION = 540;
	private final static int MODE_DOCTYPE_NOTATION_SYSTEM = 541;
	private final static int MODE_DOCTYPE_NOTATION_PUBLIC = 542;
	private final static int MODE_DOCTYPE_PI = 550;

	private final byte[] _contents;
	private int _i; // index to read next
	private int _h; // head index of the character sequence
	private int _c; // current unicode codepoint
	private int _d; // depth of the element structure
	private int _m; // mode of the tokenization 

	public Lexer(byte[] contents) throws Exception {
		_contents = contents;
		_i = 0;
		_d = 0;
		_m = 0;
		readChar();
	}

	public Token read() {
		return read(0);
	}

	public Token read(int preferred) {
		int h = _h;
		if (_c == EOF) {
			return new Token(EOF, h, h);
		} else if (_m == MODE_STAG || _m == MODE_ETAG || _m == MODE_XML) {
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				return parseWhiteSpace();
			case '>':
				readChar();
				_d += _m == MODE_STAG ? +1 : -1;
				_m = 0;
				return new Token(TAG_END, h, _h);
			case '/':
				if (next('>')) {
					readChar();
					_m = 0;
					return new Token(EETAG_END, h, _h);
				} else {
					readChar();
					return new Token(ILLEGAL_CHARACTER, h, _h);
				}
			case '?':
				if (_m == MODE_XML && next('>')) {
					readChar();
					_m = 0;
					return new Token(PI_END, h, _h);
				} else {
					readChar();
					return new Token(ILLEGAL_CHARACTER, h, _h);
				}
			case '=':
				readChar();
				return new Token(EQ, h, _h);
			case '\"':
			case '\'':
				return parseAttValue();
			default:
				return parseName();
			}
		} else if (_m == MODE_PI || _m == MODE_DOCTYPE_PI) {
			if (preferred == NAME) {
				return parseName();
			} else if (isWhiteSpace(_c)) {
				readChar();
				while (true) {
					if (_c == EOF) {
						return new Token(PREMATURE_EOF, h, _h);
					} else if (_c == '?' && peek('>')) {
						return new Token(PI_BODY, h, _h);
					} else {
						readChar();
					}
				}
			} else if (_c == '?' && next('>')) {
				readChar();
				_m = _m == MODE_DOCTYPE_PI ? MODE_DOCTYPE : 0;
				return new Token(PI_END, h, _h);
			} else {
				readChar();
				return new Token(ILLEGAL_CHARACTER, h, _h);
			}
		} else if (_d > 0) {
			switch (_c) {
			case '<':
				if (next('!', '-', '-')) {
					return parseComment();
				} else if (next('?')) {
					readChar();
					_m = MODE_PI;
					return new Token(PI_START, h, _h);
				} else if (next('!', '[', 'C', 'D', 'A', 'T', 'A', '[')) {
					readChar();
					while (true) {
						if (_c == EOF) {
							return new Token(PREMATURE_EOF, h, _h);
						} else if (_c == ']' && next(']', '>')) {
							readChar();
							return new Token(CDSECT, h, _h);
						}
					}
				} else if (next('/')) {
					readChar();
					_m = MODE_ETAG;
					return new Token(ETAG_START, h, _h);
				} else {
					readChar();
					_m = MODE_STAG;
					return new Token(STAG_START, h, _h);
				}
			case '&':
				if (next('#', 'x')) {
					readChar();
					if (isHexadecimal(_c)) {
						readChar();
					} else {
						readChar();
						return new Token(MALFORMED_CHARREF, h, _h);
					}
					while (isHexadecimal(_c)) {
						readChar();
					}
					if (_c == ';') {
						readChar();
						return new Token(CHARREF, h, _h);
					} else {
						readChar();
						return new Token(MALFORMED_CHARREF, h, _h);
					}
				} else if (next('#')) {
					readChar();
					if (isDigit(_c)) {
						readChar();
					} else {
						readChar();
						return new Token(MALFORMED_CHARREF, h, _h);
					}
					while (isDigit(_c)) {
						readChar();
					}
					if (_c == ';') {
						readChar();
						return new Token(CHARREF, h, _h);
					} else {
						readChar();
						return new Token(MALFORMED_CHARREF, h, _h);
					}
				} else {
					readChar();
					if (isNameStartChar(_c)) {
						readChar();
					} else {
						readChar();
						return new Token(MALFORMED_ENTITYREF, h, _h);
					}
					while (isNameChar(_c)) {
						readChar();
					}
					if (_c == ';') {
						readChar();
						return new Token(ENTITYREF, h, _h);
					} else {
						readChar();
						return new Token(MALFORMED_ENTITYREF, h, _h);
					}
				}
			case ']':
				if (next(']', '>')) {
					readChar();
					return new Token(ILLEGAL_SEQUENCE, h, _h);
				}
				//FALLTHROUGH
			default:
				readChar();
				while (_c != EOF && _c != '<' && _c != '&') {
					if (_c == ']' && next(']', '>')) {
						_i -= 2;
						break;
					}
					readChar();
				}
				return new Token(CHAR_DATA, h, _h);
			}
		} else if (_m == 0) {
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				return parseWhiteSpace();
			case '<':
				if (next('!', '-', '-')) {
					return parseComment();
				} else if (next('?')) {
					if (next('x', 'm', 'l')) {
						readChar();
						if (isNameChar(_c)) {
							_i = h + 2;
							readChar();
							_m = MODE_PI;
							return new Token(PI_START, h, _h);
						} else {
							_m = MODE_XML;
							return new Token(XML, h, _h);
						}
					} else {
						readChar();
						_m = MODE_PI;
						return new Token(PI_START, h, _h);
					}
				} else if (next('!', 'D', 'O', 'C', 'T', 'Y', 'P', 'E')) {
					readChar();
					_m = MODE_DOCTYPE;
					return new Token(DOCTYPE, h, _h);
				} else {
					readChar();
					_m = MODE_STAG;
					return new Token(STAG_START, h, _h);
				}
			default:
				readChar();
				return new Token(ILLEGAL_CHARACTER, h, _h);
			}
		} else if (_m == MODE_DOCTYPE
				|| _m == MODE_DOCTYPE_SYSTEM
				|| _m == MODE_DOCTYPE_PUBLIC
				|| _m == MODE_DOCTYPE_ELEMENT
				|| _m == MODE_DOCTYPE_ATTLIST
				|| _m == MODE_DOCTYPE_ENTITY
				|| _m == MODE_DOCTYPE_NOTATION) {
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				return parseWhiteSpace();
			case '<':
				if (next('!')) {
					if (next('-', '-')) {
						return parseComment();
					} else if (next('E', 'L', 'E', 'M', 'E', 'N', 'T')) {
						readChar();
						_m = MODE_DOCTYPE_ELEMENT;
						return new Token(ELEMENT_DECL, h, _h);
					} else if (next('A', 'T', 'T', 'L', 'I', 'S', 'T')) {
						readChar();
						_m = MODE_DOCTYPE_ATTLIST;
						return new Token(ATTLIST_DECL, h, _h);
					} else if (next('E', 'N', 'T', 'I', 'T', 'Y')) {
						readChar();
						_m = MODE_DOCTYPE_ENTITY;
						return new Token(ENTITY_DECL, h, _h);
					} else if (next('N', 'O', 'T', 'A', 'T', 'I', 'O', 'N')) {
						readChar();
						_m = MODE_DOCTYPE_NOTATION;
						return new Token(NOTATION_DECL, h, _h);
					}
				} else if (next('?')) {
					readChar();
					_m = MODE_DOCTYPE_PI;
					return new Token(PI_START, h, _h);
				}
				readChar();
				return new Token(ILLEGAL_CHARACTER, h, _h);
			case '>':
				readChar();
				_m = _m == MODE_DOCTYPE ? 0 : MODE_DOCTYPE;
				return new Token('>', h, _h);
			case '[':
			case ']':
			case '(':
			case '|':
			case ',':
			case '?':
			case '*':
			case '+':
				int c = _c;
				readChar();
				return new Token(c, h, _h);
			case ')':
				if (next('*')) {
					readChar();
					return new Token(PCDATA_END, h, _h);
				} else {
					readChar();
					return new Token(')', h, _h);
				}
			case '#':
				if (next('P', 'C', 'D', 'A', 'T', 'A')) {
					readChar();
					return new Token(PCDATA, h, _h);
				} else if (next('R', 'E', 'Q', 'U', 'I', 'R', 'E', 'D')) {
					readChar();
					return new Token(REQUIRED, h, _h);
				} else if (next('I', 'M', 'P', 'L', 'I', 'E', 'D')) {
					readChar();
					return new Token(IMPLIED, h, _h);
				} else if (next('F', 'I', 'X', 'E', 'D')) {
					readChar();
					return new Token(FIXED, h, _h);
				}
				readChar();
				return new Token(ILLEGAL_CHARACTER, h, _h);
			case '%':
				return parsePEReference();
			case 'A':
				if (next('N', 'Y')) {
					readChar();
					return new Token(ANY, h, _h);
				} else {
					return parseName();
				}
			case 'C':
				if (next('D', 'A', 'T', 'A')) {
					readChar();
					return new Token(TYPE_CDATA, h, _h);
				} else {
					return parseName();
				}
			case 'E':
				if (next('M', 'P', 'T', 'Y')) {
					readChar();
					return new Token(EMPTY, h, _h);
				} else if (next('N', 'T', 'I', 'T', 'I', 'E', 'S')) {
					readChar();
					return new Token(TYPE_ENTITIES, h, _h);
				} else if (next('N', 'T', 'I', 'T', 'Y')) {
					readChar();
					return new Token(TYPE_ENTITY, h, _h);
				} else {
					return parseName();
				}
			case 'I':
				if (next('D', 'R', 'E', 'F', 'S')) {
					readChar();
					return new Token(TYPE_IDREFS, h, _h);
				} else if (next('D', 'R', 'E', 'F')) {
					readChar();
					return new Token(TYPE_IDREF, h, _h);
				} else if (next('D')) {
					readChar();
					return new Token(TYPE_ID, h, _h);
				} else {
					return parseName();
				}
			case 'N':
				if (next('M', 'T', 'O', 'K', 'E', 'N', 'S')) {
					readChar();
					return new Token(TYPE_NMTOKENS, h, _h);
				} else if (next('M', 'T', 'O', 'K', 'E', 'N')) {
					readChar();
					return new Token(TYPE_NMTOKEN, h, _h);
				} else if (next('O', 'T', 'A', 'T', 'I', 'O', 'N')) {
					readChar();
					return new Token(TYPE_NOTATION, h, _h);
				} else if (next('D', 'A', 'T', 'A')) {
					readChar();
					return new Token(TYPE_NOTATION, h, _h);
				} else {
					return parseName();
				}
			case 'P':
				if ((_m == MODE_DOCTYPE || _m == MODE_DOCTYPE_NOTATION)
						&& next('U', 'B', 'L', 'I', 'C')) {
					readChar();
					_m = _m == MODE_DOCTYPE_NOTATION ? MODE_DOCTYPE_NOTATION_PUBLIC : MODE_DOCTYPE_PUBLIC;
					return new Token(PUBLIC, h, _h);
				} else {
					readChar();
					return new Token(ILLEGAL_CHARACTER, h, _h);
				}
			case 'S':
				if ((_m == MODE_DOCTYPE || _m == MODE_DOCTYPE_NOTATION)
						&& next('Y', 'S', 'T', 'E', 'M')) {
					readChar();
					_m = _m == MODE_DOCTYPE_NOTATION ? MODE_DOCTYPE_NOTATION_SYSTEM : MODE_DOCTYPE_SYSTEM;
					return new Token(SYSTEM, h, _h);
				} else {
					readChar();
					return new Token(ILLEGAL_CHARACTER, h, _h);
				}
			case '\"':
			case '\'':
				if (_m == MODE_DOCTYPE_SYSTEM || _m == MODE_DOCTYPE_NOTATION_SYSTEM) {
					return parseSystemLiteral();
				} else if (_m == MODE_DOCTYPE_PUBLIC || _m == MODE_DOCTYPE_NOTATION_PUBLIC) {
					return parsePubidLiteral();
				} else if (_m == MODE_DOCTYPE_ATTLIST) {
					return parseAttValue();
				} else if (_m == MODE_DOCTYPE_ENTITY) {
					return parseEntityValue();
				} else {
					readChar();
					return new Token(ILLEGAL_CHARACTER, h, _h);
				}
			default:
				if (preferred == NMTOKEN) {
					return parseNmtoken();
				} else if (isNameStartChar(_c)) {
					return parseName();
				} else {
					readChar();
					return new Token(ILLEGAL_CHARACTER, h, _h);
				}
			}
		} else {
			throw new RuntimeException("Lexer::read: BUG!");
		}
	}

	private Token parseWhiteSpace() {
		int h = _h;
		readChar();
		while (isWhiteSpace(_c)) {
			readChar();
		}
		return new Token(SP, h, _h);
	}

	private Token parseComment() {
		int h = _h;
		readChar();
		int b = 0;
		while (_c != '-' || b != '-') {
			if (_c == EOF) {
				return new Token(PREMATURE_EOF, h, _h);
			}
			b = _c;
			readChar();
		}
		readChar();
		if (_c == '>') {
			readChar();
			return new Token(COMMENT, h, _h);
		} else {
			return new Token(ILLEGAL_SEQUENCE, h, _h);
		}
	}

	private Token parseName() {
		int h = _h;
		if (isNameStartChar(_c)) {
			readChar();
			while (isNameChar(_c)) {
				readChar();
			}
			return new Token(NAME, h, _h);
		} else if (_c == EOF) {
			return new Token(EOF, h, h);
		} else {
			readChar();
			return new Token(ILLEGAL_CHARACTER, h, _h);
		}
	}

	private Token parseAttValue() {
		int h = _h;
		int q = _c;
		if (_c == '\"' || _c == '\'') {
			readChar();
		} else {
			return new Token(ILLEGAL_CHARACTER, h, _h);
		}
		Token t;
		while (_c != q) {
			switch (_c) {
			case EOF:
				return new Token(PREMATURE_EOF, h, _h);
			case '<':
				readChar();
				return new Token(ILLEGAL_CHARACTER, h, _h);
			case '&':
				t = parseReference();
				if (t.code != CHARREF && t.code != ENTITYREF) {
					return new Token(t.code, h, _h);
				}
				break;
			default:
				readChar();
				break;
			}
		}
		readChar();
		return new Token(ATTVALUE, h, _h);
	}

	private Token parseSystemLiteral() {
		int h = _h;
		int q = _c;
		readChar();
		while (_c != q) {
			if (_c == EOF) {
				return new Token(PREMATURE_EOF, h, _h);
			} else {
				readChar();
			}
		}
		readChar();
		_m = _m == MODE_DOCTYPE_NOTATION_SYSTEM ? MODE_DOCTYPE_NOTATION : MODE_DOCTYPE;
		return new Token(SYSTEM_LITERAL, h, _h);
	}

	private Token parsePubidLiteral() {
		int h = _h;
		int q = _c;
		readChar();
		while (_c != q) {
			if (_c == EOF) {
				return new Token(PREMATURE_EOF, h, _h);
			} else if (isPubidChar(_c)) {
				readChar();
			} else {
				return new Token(ILLEGAL_CHARACTER, h, _h);
			}
		}
		readChar();
		_m = _m == MODE_DOCTYPE_NOTATION_PUBLIC ? MODE_DOCTYPE_NOTATION_SYSTEM :  MODE_DOCTYPE_SYSTEM;
		return new Token(PUBID_LITERAL, h, _h);
	}

	private Token parseNmtoken() {
		int h = _h;
		if (isNameChar(_c)) {
			readChar();
			while (isNameChar(_c)) {
				readChar();
			}
			return new Token(NMTOKEN, h, _h);
		} else if (_c == EOF) {
			return new Token(EOF, h, h);
		} else {
			readChar();
			return new Token(ILLEGAL_CHARACTER, h, _h);
		}
	}

	private Token parseEntityValue() {
		int h = _h;
		int q = _c;
		if (_c == '\"' || _c == '\'') {
			readChar();
		} else {
			return new Token(ILLEGAL_CHARACTER, h, _h);
		}
		Token t;
		while (_c != q) {
			switch (_c) {
			case EOF:
				return new Token(PREMATURE_EOF, h, _h);
			case '%': // PEReference
				t = parsePEReference();
				if (t.code != PEREFERENCE) {
					return new Token(t.code, h, t.end);
				}
				break;
			case '&':
				t = parseReference();
				if (t.code != CHARREF && t.code != ENTITYREF) {
					return new Token(t.code, h, _h);
				}
				break;
			default:
				readChar();
				break;
			}
		}
		readChar();
		return new Token(ENTITY_VALUE, h, _h);
	}

	private Token parsePEReference() {
		int h = _h;
		if (_c == '%') {
			readChar();
		} else {
			readChar();
			return new Token(MALFORMED_PEREFERENCE, h, _h);
		}
		if (isNameStartChar(_c)) {
			readChar();
			while (isNameChar(_c)) {
				readChar();
			}
			if (_c == ';') {
				readChar();
				return new Token(PEREFERENCE, h, _h);
			} else if (_c == EOF) {
				return new Token(PREMATURE_EOF, h, _h);
			} else {
				readChar();
				return new Token(MALFORMED_PEREFERENCE, h, _h);
			}
		} else if (_c == EOF) {
			return new Token(EOF, h, h);
		} else {
			readChar();
			return new Token(MALFORMED_PEREFERENCE, h, _h);
		}
	}

	private Token parseReference() {
		int h = _h;
		if (_c == '&') {
			readChar();
		} else {
			readChar();
			return new Token(MALFORMED_REFERENCE, h, _h);
		}
		if (next('#')) {
			// CharRef
			if (next('x')) {
				readChar();
				if (isHexadecimal(_c)) {
					readChar();
				} else {
					return new Token(MALFORMED_CHARREF, h, _h);
				}
				while (isHexadecimal(_c)) {
					readChar();
				}
				if (_c == ';') {
					readChar();
					return new Token(CHARREF, h, _h);
				} else {
					return new Token(MALFORMED_CHARREF, h, _h);
				}
			} else {
				readChar();
				if (Character.isDigit(_c)) {
					readChar();
				} else {
					return new Token(MALFORMED_CHARREF, h, _h);
				}
				while (Character.isDigit(_c)) {
					readChar();
				}
				if (_c == ';') {
					readChar();
					return new Token(CHARREF, h, _h);
				} else {
					return new Token(MALFORMED_CHARREF, h, _h);
				}
			}
		} else {
			// EntityRef
			readChar();
			if (isNameStartChar(_c)) {
				readChar();
			} else {
				return new Token(MALFORMED_ENTITYREF, h, _h);
			}
			while (isNameChar(_c)) {
				readChar();
			}
			if (_c == ';') {
				readChar();
				return new Token(ENTITYREF, h, _h);
			} else {
				return new Token(MALFORMED_ENTITYREF, h, _h);
			}
		}
	}

	public int readChar() {
		_h = _i;
		int b1 = readByte();
		if (b1 < 0x80) {
			_c = b1;
		} else if (b1 < 0xC2) {
			_c = ILLEGAL_SEQUENCE;
		} else if (b1 < 0xE0) {
			int b2 = readByte();
			if (b2 < 0x80) {
				_c = ILLEGAL_SEQUENCE;
			} else if (b2 < 0xC0) {
				_c = ((b1 & 0x1F) << (6 * 1)) | ((b2 & 0x3F) << (6 * 0));
				if (_c < 0x80) {
					_c = OUT_OF_RANGE;
				}
			} else {
				_c = ILLEGAL_SEQUENCE;
			}
		} else if (b1 < 0xF0) {
			int b2 = readByte();
			if (b2 < 0x80) {
				_c = ILLEGAL_SEQUENCE;
			} else if (b2 < 0xC0) {
				int b3 = readByte();
				if (b3 < 0x80) {
					_c = ILLEGAL_SEQUENCE;
				} else if (b3 < 0xC0) {
					_c = ((b1 & 0x0F) << (6 * 2)) | ((b2 & 0x3F) << (6 * 1)) | ((b3 & 0x3F) << (6 * 0));
					if (_c < 0x800 || (0xD800 <= _c && _c <= 0xDFFF)) {
						_c = OUT_OF_RANGE;
					}
				} else {
					_c = ILLEGAL_SEQUENCE;
				}
			} else {
				_c = ILLEGAL_SEQUENCE;
			}
		} else if (b1 < 0xF8) {
			int b2 = readByte();
			if (b2 < 0x80) {
				_c = ILLEGAL_SEQUENCE;
			} else if (b2 < 0xC0) {
				int b3 = readByte();
				if (b3 < 0x80) {
					_c = ILLEGAL_SEQUENCE;
				} else if (b3 < 0xC0) {
					int b4 = readByte();
					if (b4 < 0x80) {
						_c = ILLEGAL_SEQUENCE;
					} else if (b4 < 0xC0) {
						_c = ((b1 & 0x0F) << (6 * 3)) | ((b2 & 0x3F) << (6 * 2)) | ((b3 & 0x3F) << (6 * 1)) | ((b4 & 0x3F) << (6 * 0));
						if (_c < 0x10000 || 0x10FFFF < _c) {
							_c = OUT_OF_RANGE;
						}
					} else {
						_c = ILLEGAL_SEQUENCE;
					}
				} else {
					_c = ILLEGAL_SEQUENCE;
				}
			} else {
				_c = ILLEGAL_SEQUENCE;
			}
		} else {
			_c = ILLEGAL_SEQUENCE;
		}
		return _c;
	}

	private int readByte() {
		return _i < _contents.length ? ((int)_contents[_i++] + 0x100) & 0xFF : EOF;
	}

	private boolean peek(int c) {
		return _i < _contents.length && _contents[_i] == c;
	}

	private boolean next(int... cc) {
		int n = cc.length;
		if (_i + n <= _contents.length) {
			for (int j = 0; j < n; j++) {
				if (_contents[_i + j] != cc[j]) {
					return false;
				}
			}
			_i += n;
			return true;
		} else {
			return false;
		}
	}

	public static boolean isWhiteSpace(int c) {
		return c == SP || c == HT || c == LF || c == CR;
	}

	public static boolean isNameStartChar(int c) {
		return isNameStartCharL(c) || isNameStartCharH(c);
	}

	private static boolean isNameStartCharL(int c) {
		return isAlphabetic(c)
				|| c == ':'
				|| c == '_';
	}

	private static boolean isNameStartCharH(int c) {
		return (0xC0 <= c && c <= 0xD6)
				|| (0xD8 <= c && c <= 0xF6)
				|| (0xF8 <= c && c <= 0x2FF)
				|| (0x370 <= c && c <= 0x37D)
				|| (0x37F <= c && c <= 0x1FFF)
				|| (0x200C <= c && c <= 0x200D)
				|| (0x2070 <= c && c <= 0x218F)
				|| (0x2C00 <= c && c <= 0x2FEF)
				|| (0x3001 <= c && c <= 0xD7FF)
				|| (0xF900 <= c && c <= 0xFDCF)
				|| (0xFDF0 <= c && c <= 0xFFFD)
				|| (0x10000 <= c && c <= 0xEFFFF);
	}

	public static boolean isNameChar(int c) {
		return isNameStartCharL(c)
				|| isDigit(c)
				|| c == '-'
				|| c == '.'
				|| c == 0xB7
				|| (0x0300 <= c && c <= 0x036F)
				|| (0x203F <= c && c <= 0x2040)
				|| isNameStartCharH(c);
	}

	public static boolean isChar(int c) {
		return c == HT
				|| c == LF
				|| c == CR
				|| (0x20 <= c && c <= 0xD7FF)
				|| (0xE000 <= c && c <= 0xFFFD)
				|| (0x10000 <= c && c <= 0x10FFFF);
	}

	public static boolean isPubidChar(int c) {
		switch (c) {
		case SP:
		case CR:
		case LF:
		case '-':
		case '\'':
		case '(':
		case ')':
		case '+':
		case ',':
		case '.':
		case '/':
		case ':':
		case '=':
		case '?':
		case ';':
		case '!':
		case '*':
		case '#':
		case '@':
		case '$':
		case '_':
		case '%':
			return true;
		default:
			return isAlphabetic(c) || isDigit(c);
		}
	}

	public static boolean isAlphabetic(int c) {
		switch (c) {
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return true;
		default:
			return false;
		}
	}

	public static boolean isDigit(int c) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return true;
		default:
			return false;
		}
	}

	public static boolean isHexadecimal(int c) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
			return true;
		default:
			return false;
		}
	}

}
