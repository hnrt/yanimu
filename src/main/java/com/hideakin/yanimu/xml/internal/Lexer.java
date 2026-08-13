package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.Token;

import static com.hideakin.yanimu.xml.Token.*;

public class Lexer {

	public final static int MODE_XML = 1;
	public final static int MODE_PI = 2;
	public final static int MODE_STAG = 3;
	public final static int MODE_ETAG = 4;
	public final static int MODE_DOCTYPE = 500;
	public final static int MODE_DOCTYPE_ELEMENT = 501;
	public final static int MODE_DOCTYPE_ATTLIST = 502;
	public final static int MODE_DOCTYPE_ENTITY = 503;
	public final static int MODE_DOCTYPE_NOTATION = 504;
	public final static int MODE_DOCTYPE_PI = 505;

	private final Reader _reader;
	private int _g; // head index of the character sequence
	private int _h; // head index of the byte sequence
	private int _c; // current UNICODE codepoint
	private int _d; // depth of the element structure
	private int _m; // read mode

	public Lexer(byte[] content) {
		this(content, 0);
	}

	public Lexer(byte[] content, int mode) {
		_reader = ReaderFactory.create(content);
		_d = 0;
		_m = mode;
		readChar();
	}

	public Lexer(String content) {
		this(content.getBytes(), 0);
	}

	public Lexer(String content, int mode) {
		this(content.getBytes(), mode);
	}

	public int mode() {
		return _m;
	}

	public Token read() {
		return read(0);
	}

	public Token read(int preferred) {
		_g = _h;
		if (_c == EOF) {
			return Token.of(EOF, _g, _h, text());
		} else if (_m == MODE_STAG || _m == MODE_ETAG || _m == MODE_XML) {
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '>':
				readChar();
				_d += _m == MODE_STAG ? +1 : -1;
				_m = 0;
				return Token.of(TAG_END, _g, _h, text());
			case '/':
				if (next('>')) {
					readChar();
					_m = 0;
					return Token.of(EETAG_END, _g, _h, text());
				}
				break;
			case '?':
				if (_m == MODE_XML && next('>')) {
					readChar();
					_m = 0;
					return Token.of(XML_END, _g, _h, text());
				}
				break;
			case '=':
				readChar();
				return Token.of(EQ, _g, _h, text());
			case '\"':
			case '\'':
				int q = _c;
				readChar();
				return parseAttValue(q);
			default:
				if (isNameStartChar(_c)) {
					readChar();
					return parseName();
				}
				break;
			}
		} else if (_m == MODE_PI || _m == MODE_DOCTYPE_PI) {
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '?':
				if (next('>')) {
					readChar();
					_m = _m == MODE_DOCTYPE_PI ? MODE_DOCTYPE : 0;
					return Token.of(PI_END, _g, _h, text());
				}
				//FALLTHROUGH
			default:
				if (preferred == NAME) {
					if (isNameStartChar(_c)) {
						readChar();
						return parseName();
					}
				}
				readChar();
				return parsePI();
			}
		} else if (_d > 0) {
			switch (_c) {
			case '<':
				if (next('!', '-', '-')) {
					readChar();
					return parseComment();
				} else if (next('?')) {
					readChar();
					_m = MODE_PI;
					return Token.of(PI_START, _g, _h, text());
				} else if (next('!', '[', 'C', 'D', 'A', 'T', 'A', '[')) {
					readChar();
					return parseCDSect();
				} else if (next('/')) {
					readChar();
					_m = MODE_ETAG;
					return Token.of(ETAG_START, _g, _h, text());
				} else {
					readChar();
					_m = MODE_STAG;
					return Token.of(STAG_START, _g, _h, text());
				}
			case '&':
				readChar();
				return parseReference();
			case ']':
				if (next(']', '>')) {
					break;
				}
				//FALLTHROUGH
			default:
				readChar();
				return parseCharData();
			}
		} else if (_m == 0) {
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '<':
				if (next('!', '-', '-')) {
					readChar();
					return parseComment();
				} else if (next('?')) {
					int i = _reader.to();
					if (next('x', 'm', 'l')) {
						readChar();
						if (isNameChar(_c)) {
							_reader.reset(i);
							readChar();
							_m = MODE_PI;
							return Token.of(PI_START, _g, _h, "<?");
						} else {
							_m = MODE_XML;
							return Token.of(XML_START, _g, _h, text());
						}
					} else {
						readChar();
						_m = MODE_PI;
						return Token.of(PI_START, _g, _h, text());
					}
				} else if (next('!', 'D', 'O', 'C', 'T', 'Y', 'P', 'E')) {
					readChar();
					_m = MODE_DOCTYPE;
					return Token.of(DOCTYPE_DECL, _g, _h, text());
				} else {
					readChar();
					_m = MODE_STAG;
					return Token.of(STAG_START, _g, _h, text());
				}
			default:
				break;
			}
		} else if (_m == MODE_DOCTYPE
				|| _m == MODE_DOCTYPE_ELEMENT
				|| _m == MODE_DOCTYPE_ATTLIST
				|| _m == MODE_DOCTYPE_ENTITY
				|| _m == MODE_DOCTYPE_NOTATION) {
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '<':
				if (next('!')) {
					if (next('-', '-')) {
						readChar();
						return parseComment();
					} else if (next('E', 'L', 'E', 'M', 'E', 'N', 'T')) {
						readChar();
						_m = MODE_DOCTYPE_ELEMENT;
						return Token.of(ELEMENT_DECL, _g, _h, text());
					} else if (next('A', 'T', 'T', 'L', 'I', 'S', 'T')) {
						readChar();
						_m = MODE_DOCTYPE_ATTLIST;
						return Token.of(ATTLIST_DECL, _g, _h, text());
					} else if (next('E', 'N', 'T', 'I', 'T', 'Y')) {
						readChar();
						_m = MODE_DOCTYPE_ENTITY;
						return Token.of(ENTITY_DECL, _g, _h, text());
					} else if (next('N', 'O', 'T', 'A', 'T', 'I', 'O', 'N')) {
						readChar();
						_m = MODE_DOCTYPE_NOTATION;
						return Token.of(NOTATION_DECL, _g, _h, text());
					}
				} else if (next('?')) {
					readChar();
					_m = MODE_DOCTYPE_PI;
					return Token.of(PI_START, _g, _h, text());
				}
				break;
			case '>':
				readChar();
				_m = _m == MODE_DOCTYPE ? 0 : MODE_DOCTYPE;
				return Token.of(TAG_END, _g, _h, text());
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
				return Token.of(c, _g, _h, text());
			case ')':
				if (next('*')) {
					readChar();
					return Token.of(PCDATA_END, _g, _h, text());
				} else {
					readChar();
					return Token.of(')', _g, _h, text());
				}
			case '#':
				if (next('P', 'C', 'D', 'A', 'T', 'A')) {
					readChar();
					return Token.of(PCDATA, _g, _h, text());
				} else if (next('R', 'E', 'Q', 'U', 'I', 'R', 'E', 'D')) {
					readChar();
					return Token.of(REQUIRED, _g, _h, text());
				} else if (next('I', 'M', 'P', 'L', 'I', 'E', 'D')) {
					readChar();
					return Token.of(IMPLIED, _g, _h, text());
				} else if (next('F', 'I', 'X', 'E', 'D')) {
					readChar();
					return Token.of(FIXED, _g, _h, text());
				}
				break;
			case '%':
				readChar();
				if (isNameStartChar(_c)) {
					readChar();
					return parsePEReference();
				} else {
					return Token.of('%', _g, _h, text());
				}
			case 'A':
				if (_m == MODE_DOCTYPE_ELEMENT) {
					if (next('N', 'Y')) {
						readChar();
						return Token.of(ANY, _g, _h, text());
					}
				}
				readChar();
				return parseName();
			case 'C':
				if (_m == MODE_DOCTYPE_ATTLIST) {
					if (next('D', 'A', 'T', 'A')) {
						readChar();
						return Token.of(TYPE_CDATA, _g, _h, text());
					}
				}
				readChar();
				return parseName();
			case 'E':
				if (_m == MODE_DOCTYPE_ATTLIST) {
					if (next('N', 'T', 'I', 'T', 'I', 'E', 'S')) {
						readChar();
						return Token.of(TYPE_ENTITIES, _g, _h, text());
					} else if (next('N', 'T', 'I', 'T', 'Y')) {
						readChar();
						return Token.of(TYPE_ENTITY, _g, _h, text());
					}
				} else if (_m == MODE_DOCTYPE_ELEMENT) {
					if (next('M', 'P', 'T', 'Y')) {
						readChar();
						return Token.of(EMPTY, _g, _h, text());
					}
				}
				readChar();
				return parseName();
			case 'I':
				if (_m == MODE_DOCTYPE_ATTLIST) {
					if (next('D', 'R', 'E', 'F', 'S')) {
						readChar();
						return Token.of(TYPE_IDREFS, _g, _h, text());
					} else if (next('D', 'R', 'E', 'F')) {
						readChar();
						return Token.of(TYPE_IDREF, _g, _h, text());
					} else if (next('D')) {
						readChar();
						return Token.of(TYPE_ID, _g, _h, text());
					}
				}
				readChar();
				return parseName();
			case 'N':
				if (_m == MODE_DOCTYPE_ATTLIST) {
					if (next('M', 'T', 'O', 'K', 'E', 'N', 'S')) {
						readChar();
						return Token.of(TYPE_NMTOKENS, _g, _h, text());
					} else if (next('M', 'T', 'O', 'K', 'E', 'N')) {
						readChar();
						return Token.of(TYPE_NMTOKEN, _g, _h, text());
					} else if (next('O', 'T', 'A', 'T', 'I', 'O', 'N')) {
						readChar();
						return Token.of(TYPE_NOTATION, _g, _h, text());
					}
				} else if (_m == MODE_DOCTYPE_ENTITY) {
					if (next('D', 'A', 'T', 'A')) {
						readChar();
						return Token.of(NDATA, _g, _h, text());
					}
				}
				readChar();
				return parseName();
			case 'P':
				if (_m == MODE_DOCTYPE || _m == MODE_DOCTYPE_NOTATION) {
					if (next('U', 'B', 'L', 'I', 'C')) {
						readChar();
						return Token.of(PUBLIC, _g, _h, text());
					}
				}
				readChar();
				return parseName();
			case 'S':
				if (_m == MODE_DOCTYPE || _m == MODE_DOCTYPE_NOTATION) {
					if (next('Y', 'S', 'T', 'E', 'M')) {
						readChar();
						return Token.of(SYSTEM, _g, _h, text());
					}
				}
				readChar();
				return parseName();
			case '\"':
			case '\'':
				if (preferred == SYSTEM_LITERAL) {
					int q = _c;
					readChar();
					return parseSystemLiteral(q);
				} else if (preferred == PUBID_LITERAL) {
					int q = _c;
					readChar();
					return parsePubidLiteral(q);
				} else if (_m == MODE_DOCTYPE_ATTLIST) {
					int q = _c;
					readChar();
					return parseAttValue(q);
				} else if (_m == MODE_DOCTYPE_ENTITY) {
					int q = _c;
					readChar();
					return parseEntityValue(q);
				}
				break;
			default:
				if (preferred == NMTOKEN && isNameChar(_c)) {
					readChar();
					return parseNmtoken();
				} else if (isNameStartChar(_c)) {
					readChar();
					return parseName();
				}
				break;
			}
		} else {
			throw new RuntimeException("Lexer::read: BUG!");
		}
		readChar();
		return Token.of(ILLEGAL_CHARACTER, _g, _h, text());
	}

	private Token parseWhiteSpace() {
		while (isWhiteSpace(_c)) {
			readChar();
		}
		return Token.of(SP, _g, _h, text());
	}

	private Token parseComment() {
		while (true) {
			if (_c == EOF) {
				return Token.of(PREMATURE_EOF, _g, _h, text());
			} else if (_c == '-') {
				readChar();
				if (_c == '-') {
					readChar();
					if (_c == '>') {
						readChar();
						return Token.of(COMMENT, _g, _h, text());
					} else {
						readChar();
						return Token.of(ILLEGAL_SEQUENCE, _g, _h, text());
					}
				}
			} else {
				readChar();
			}
		}
	}

	private Token parsePI() {
		while (true) {
			if (_c == EOF) {
				return Token.of(PREMATURE_EOF, _g, _h, text());
			} else if (_c == '?' && peek('>')) {
				return Token.of(PI_BODY, _g, _h, text());
			} else {
				readChar();
			}
		}
	}

	private Token parseName() {
		while (isNameChar(_c)) {
			readChar();
		}
		return Token.of(NAME, _g, _h, text());
	}

	private Token parseNmtoken() {
		while (isNameChar(_c)) {
			readChar();
		}
		return Token.of(NMTOKEN, _g, _h, text());
	}

	private Token parseAttValue(int q) {
		while (_c != q) {
			switch (_c) {
			case EOF:
				return Token.of(PREMATURE_EOF, _g, _h, text());
			case '<':
				readChar();
				return Token.of(ILLEGAL_CHARACTER, _g, _h, text());
			case '&':
				readChar();
				if (_c == '#') {
					// CharRef
					readChar();
					if (_c == 'x') {
						readChar();
						if (isHexadecimal(_c)) {
							readChar();
							while (isHexadecimal(_c)) {
								readChar();
							}
						} else {
							readChar();
							return Token.of(MALFORMED_CHARREF, _g, _h, text());
						}
					} else if (isDigit(_c)) {
						readChar();
						while (isDigit(_c)) {
							readChar();
						}
					} else {
						readChar();
						return Token.of(MALFORMED_CHARREF, _g, _h, text());
					}
					if (_c == ';') {
						readChar();
					} else if (_c == EOF) {
						return Token.of(PREMATURE_EOF, _g, _h, text());
					} else {
						readChar();
						return Token.of(MALFORMED_CHARREF, _g, _h, text());
					}
				} else if (isNameStartChar(_c)) {
					// EntityRef
					readChar();
					while (isNameChar(_c)) {
						readChar();
					}
					if (_c == ';') {
						readChar();
					} else if (_c == EOF) {
						return Token.of(PREMATURE_EOF, _g, _h, text());
					} else {
						readChar();
						return Token.of(MALFORMED_ENTITYREF, _g, _h, text());
					}
				} else {
					readChar();
					return Token.of(MALFORMED_REFERENCE, _g, _h, text());
				}
				break;
			default:
				readChar();
				break;
			}
		}
		readChar();
		return Token.of(ATT_VALUE, _g, _h, text());
	}

	private Token parseSystemLiteral(int q) {
		while (_c != q) {
			if (_c == EOF) {
				return Token.of(PREMATURE_EOF, _g, _h, text());
			} else {
				readChar();
			}
		}
		readChar();
		return Token.of(SYSTEM_LITERAL, _g, _h, text());
	}

	private Token parsePubidLiteral(int q) {
		readChar();
		while (_c != q) {
			if (_c == EOF) {
				return Token.of(PREMATURE_EOF, _g, _h, text());
			} else if (isPubidChar(_c)) {
				readChar();
			} else {
				return Token.of(ILLEGAL_CHARACTER, _g, _h, text());
			}
		}
		readChar();
		return Token.of(PUBID_LITERAL, _g, _h, text());
	}

	private Token parseEntityValue(int q) {
		while (_c != q) {
			switch (_c) {
			case EOF:
				return Token.of(PREMATURE_EOF, _g, _h, text());
			case '%':
				readChar();
				if (isNameStartChar(_c)) {
					// PEReference
					readChar();
					while (isNameChar(_c)) {
						readChar();
					}
					if (_c == ';') {
						readChar();
					} else if (_c == EOF) {
						return Token.of(PREMATURE_EOF, _g, _h, text());
					} else {
						readChar();
						return Token.of(MALFORMED_PEREFERENCE, _g, _h, text());
					}
				} else {
					readChar();
					return Token.of(MALFORMED_PEREFERENCE, _g, _h, text());
				}
				break;
			case '&':
				readChar();
				if (_c == '#') {
					// CharRef
					readChar();
					if (_c == 'x') {
						readChar();
						if (isHexadecimal(_c)) {
							readChar();
							while (isHexadecimal(_c)) {
								readChar();
							}
						} else {
							readChar();
							return Token.of(MALFORMED_CHARREF, _g, _h, text());
						}
					} else if (isDigit(_c)) {
						readChar();
						while (isDigit(_c)) {
							readChar();
						}
					} else {
						readChar();
						return Token.of(MALFORMED_CHARREF, _g, _h, text());
					}
					if (_c == ';') {
						readChar();
					} else if (_c == EOF) {
						return Token.of(PREMATURE_EOF, _g, _h, text());
					} else {
						readChar();
						return Token.of(MALFORMED_CHARREF, _g, _h, text());
					}
				} else if (isNameStartChar(_c)) {
					// EntityRef
					readChar();
					while (isNameChar(_c)) {
						readChar();
					}
					if (_c == ';') {
						readChar();
					} else if (_c == EOF) {
						return Token.of(PREMATURE_EOF, _g, _h, text());
					} else {
						readChar();
						return Token.of(MALFORMED_ENTITYREF, _g, _h, text());
					}
				} else {
					readChar();
					return Token.of(MALFORMED_REFERENCE, _g, _h, text());
				}
				break;
			default:
				readChar();
				break;
			}
		}
		readChar();
		return Token.of(ENTITY_VALUE, _g, _h, text());
	}

	private Token parsePEReference() {
		readChar();
		while (isNameChar(_c)) {
			readChar();
		}
		if (_c == ';') {
			readChar();
			return Token.of(PEREFERENCE, _g, _h, text());
		} else if (_c == EOF) {
			return Token.of(PREMATURE_EOF, _g, _h, text());
		} else {
			readChar();
			return Token.of(MALFORMED_PEREFERENCE, _g, _h, text());
		}
	}

	private Token parseReference() {
		if (_c == '#') {
			// CharRef
			readChar();
			if (_c == 'x') {
				readChar();
				if (isHexadecimal(_c)) {
					readChar();
					while (isHexadecimal(_c)) {
						readChar();
					}
					if (_c == ';') {
						readChar();
						return Token.of(CHAR_REF, _g, _h, text());
					} else {
						readChar();
						return Token.of(MALFORMED_CHARREF, _g, _h, text());
					}
				} else {
					return Token.of(MALFORMED_CHARREF, _g, _h, text());
				}
			} else if (Character.isDigit(_c)) {
				readChar();
				while (Character.isDigit(_c)) {
					readChar();
				}
				if (_c == ';') {
					readChar();
					return Token.of(CHAR_REF, _g, _h, text());
				} else {
					readChar();
					return Token.of(MALFORMED_CHARREF, _g, _h, text());
				}
			} else {
				readChar();
				return Token.of(MALFORMED_CHARREF, _g, _h, text());
			}
		} else if (isNameStartChar(_c)) {
			// EntityRef
			readChar();
			while (isNameChar(_c)) {
				readChar();
			}
			if (_c == ';') {
				readChar();
				return Token.of(ENTITY_REF, _g, _h, text());
			} else {
				readChar();
				return Token.of(MALFORMED_ENTITYREF, _g, _h, text());
			}
		} else {
			return Token.of(MALFORMED_REFERENCE, _g, _h, text());
		}
	}

	private Token parseCDSect() {
		while (true) {
			if (_c == EOF) {
				return Token.of(PREMATURE_EOF, _g, _h, text());
			} else if (_c == ']' && next(']', '>')) {
				readChar();
				return Token.of(CD_SECT, _g, _h, text());
			} else {
				readChar();
			}
		}
	}

	private Token parseCharData() {
		while (_c != EOF && _c != '<' && _c != '&' && (_c != ']' || !peek(']', '>'))) {
			readChar();
		}
		return Token.of(CHAR_DATA, _g, _h, text());
	}

	private int readChar() {
		_c = _reader.readChar();
		_h = _reader.from();
		return _c;
	}

	private boolean next(int... cc) {
		return _reader.next(cc);
	}

	private boolean peek(int... cc) {
		return _reader.peek(cc);
	}

	private String text() {
		return _reader.text();
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
