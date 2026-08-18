package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.Node;

import static com.hideakin.yanimu.xml.Node.*;

public class Lexer {

	public static final int MODE_XML = 1;
	public static final int MODE_PI = 2;
	public static final int MODE_STAG = 3;
	public static final int MODE_ETAG = 4;
	public static final int MODE_DOCTYPE = 500;
	public static final int MODE_DOCTYPE_ELEMENT = 501;
	public static final int MODE_DOCTYPE_ATTLIST = 502;
	public static final int MODE_DOCTYPE_ENTITY = 503;
	public static final int MODE_DOCTYPE_NOTATION = 504;
	public static final int MODE_DOCTYPE_PI = 510;
	public static final int MODE_EXTERNAL = 600;
	public static final int MODE_EXTERNAL_ELEMENT = 601;
	public static final int MODE_EXTERNAL_ATTLIST = 602;
	public static final int MODE_EXTERNAL_ENTITY = 603;
	public static final int MODE_EXTERNAL_NOTATION = 604;
	public static final int MODE_EXTERNAL_IGNORE = 605;
	public static final int MODE_EXTERNAL_PI = 610;
	public static final int MODE_EXTERNAL_XML = 620;

	public static final int EOF = -1;
	public static final int PREMATURE_EOF = Node.PREMATURE_EOF;
	public static final int ILLEGAL_ENCODING = Node.ILLEGAL_ENCODING;

	private static final int HT = 9;
	private static final int LF = 10;
	private static final int CR = 13;
	private static final int SP = 32;

	private static final ReaderFactory _readerFactory = new ReaderFactory();

	private final NodeFactory _nodeFactory;
	private final Reader _reader;
	private int _c; // current UNICODE codepoint
	private int _d; // depth of the element structure
	private int _m; // read mode

	public Lexer(byte[] content) {
		this(content, 0, 0);
	}

	public Lexer(byte[] content, int mode) {
		this(content, mode, 0);
	}

	public Lexer(byte[] content, int mode, int offset) {
		_nodeFactory = new NodeFactory();
		_reader = _readerFactory.create(content, _nodeFactory);
		_d = 0;
		_m = mode;
		readChar();
	}

	public Lexer(String content) {
		this(content.getBytes(), 0, 0);
	}

	public Lexer(String content, int mode) {
		this(content.getBytes(), mode, 0);
	}

	public Lexer(String content, int mode, int offset) {
		this(content.getBytes(), mode, offset);
	}

	public int mode() {
		return _m;
	}

	public void setMode(int mode) {
		_m = mode;
	}

	public Node read() {
		return read(0);
	}

	public Node read(int preferred) {
		if (_c == EOF) {
			return nodeOf(EOF);
		}
		switch (_m) {
		case MODE_STAG:
		case MODE_ETAG:
		case MODE_XML:
		case MODE_EXTERNAL_XML:
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
				return nodeOf(TAG_END);
			case '/':
				if (next('>')) {
					readChar();
					_m = 0;
					return nodeOf(EETAG_END);
				}
				break;
			case '?':
				if ((_m == MODE_XML || _m == MODE_EXTERNAL_XML) && next('>')) {
					readChar();
					_m = _m == MODE_EXTERNAL_XML ? MODE_EXTERNAL : 0;
					return nodeOf(XML_END);
				}
				break;
			case '=':
				readChar();
				return nodeOf(EQ);
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
			break;
		case MODE_PI:
		case MODE_DOCTYPE_PI:
		case MODE_EXTERNAL_PI:
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
					_m = _m == MODE_EXTERNAL_PI ? MODE_EXTERNAL : _m == MODE_DOCTYPE_PI ? MODE_DOCTYPE : 0;
					return nodeOf(PI_END);
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
		default:
			if (_d > 0) {
				switch (_c) {
				case '<':
					if (next('!', '-', '-')) {
						readChar();
						return parseComment();
					} else if (next('?')) {
						readChar();
						_m = MODE_PI;
						return nodeOf(PI_START);
					} else if (next('!', '[', 'C', 'D', 'A', 'T', 'A', '[')) {
						readChar();
						return parseCDSect();
					} else if (next('/')) {
						readChar();
						_m = MODE_ETAG;
						return nodeOf(ETAG_START);
					} else {
						readChar();
						_m = MODE_STAG;
						return nodeOf(TAG_START);
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
			} else {
				switch (_m) {
				case 0:
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
							readChar();
							_m = MODE_PI;
							return nodeOf(PI_START);
						} else if (next('!', 'D', 'O', 'C', 'T', 'Y', 'P', 'E')) {
							readChar();
							_m = MODE_DOCTYPE;
							return nodeOf(DOCTYPE_DECL_START);
						} else {
							readChar();
							_m = MODE_STAG;
							return nodeOf(TAG_START);
						}
					default:
						break;
					}
					break;
				case MODE_DOCTYPE:
				case MODE_DOCTYPE_ELEMENT:
				case MODE_DOCTYPE_ATTLIST:
				case MODE_DOCTYPE_ENTITY:
				case MODE_DOCTYPE_NOTATION:
				case MODE_EXTERNAL:
				case MODE_EXTERNAL_ELEMENT:
				case MODE_EXTERNAL_ATTLIST:
				case MODE_EXTERNAL_ENTITY:
				case MODE_EXTERNAL_NOTATION:
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
								_m = _m == MODE_EXTERNAL ? MODE_EXTERNAL_ELEMENT : MODE_DOCTYPE_ELEMENT;
								return nodeOf(ELEMENT_DECL_START);
							} else if (next('A', 'T', 'T', 'L', 'I', 'S', 'T')) {
								readChar();
								_m = _m == MODE_EXTERNAL ? MODE_EXTERNAL_ATTLIST : MODE_DOCTYPE_ATTLIST;
								return nodeOf(ATTLIST_DECL_START);
							} else if (next('E', 'N', 'T', 'I', 'T', 'Y')) {
								readChar();
								_m = _m == MODE_EXTERNAL ? MODE_EXTERNAL_ENTITY : MODE_DOCTYPE_ENTITY;
								return nodeOf(ENTITY_DECL_START);
							} else if (next('N', 'O', 'T', 'A', 'T', 'I', 'O', 'N')) {
								readChar();
								_m = _m == MODE_EXTERNAL ? MODE_EXTERNAL_NOTATION : MODE_DOCTYPE_NOTATION;
								return nodeOf(NOTATION_DECL_START);
							} else if (_m == MODE_EXTERNAL && next('[')) {
								readChar();
								return nodeOf(SECTION_START);
							}
						} else if (next('?')) {
							_m = _m == MODE_EXTERNAL ? MODE_EXTERNAL_PI : MODE_DOCTYPE_PI;
							return nodeOf(PI_START);
						}
						break;
					case '>':
						readChar();
						_m = _m > MODE_EXTERNAL ? MODE_EXTERNAL : _m > MODE_DOCTYPE ? MODE_DOCTYPE : 0;
						return nodeOf(TAG_END);
					case ']':
						if (_m == MODE_EXTERNAL && next(']', '>')) {
							readChar();
							return nodeOf(SECTION_END);
						}
						//FALLTHOUGH
					case '[':
					case '(':
					case '|':
					case ',':
					case '?':
					case '*':
					case '+':
						int c = _c;
						readChar();
						return nodeOf(c);
					case ')':
						if (preferred == PCDATA_END && next('*')) {
							readChar();
							return nodeOf(PCDATA_END);
						} else {
							readChar();
							return nodeOf(')');
						}
					case '#':
						if (next('P', 'C', 'D', 'A', 'T', 'A')) {
							readChar();
							return nodeOf(PCDATA);
						} else if (next('R', 'E', 'Q', 'U', 'I', 'R', 'E', 'D')) {
							readChar();
							return nodeOf(REQUIRED);
						} else if (next('I', 'M', 'P', 'L', 'I', 'E', 'D')) {
							readChar();
							return nodeOf(IMPLIED);
						} else if (next('F', 'I', 'X', 'E', 'D')) {
							readChar();
							return nodeOf(FIXED);
						}
						break;
					case '%':
						readChar();
						if (isNameStartChar(_c)) {
							readChar();
							return parsePEReference();
						} else {
							return nodeOf('%');
						}
					case 'A':
						if (_m == MODE_DOCTYPE_ELEMENT || _m == MODE_EXTERNAL_ELEMENT) {
							if (next('N', 'Y')) {
								readChar();
								return nodeOf(ANY);
							}
						}
						readChar();
						return parseName();
					case 'C':
						if (_m == MODE_DOCTYPE_ATTLIST || _m == MODE_EXTERNAL_ATTLIST) {
							if (next('D', 'A', 'T', 'A')) {
								readChar();
								return nodeOf(TYPE_CDATA);
							}
						}
						readChar();
						return parseName();
					case 'E':
						if (_m == MODE_DOCTYPE_ATTLIST || _m == MODE_EXTERNAL_ATTLIST) {
							if (next('N', 'T', 'I', 'T', 'I', 'E', 'S')) {
								readChar();
								return nodeOf(TYPE_ENTITIES);
							} else if (next('N', 'T', 'I', 'T', 'Y')) {
								readChar();
								return nodeOf(TYPE_ENTITY);
							}
						} else if (_m == MODE_DOCTYPE_ELEMENT || _m == MODE_EXTERNAL_ELEMENT) {
							if (next('M', 'P', 'T', 'Y')) {
								readChar();
								return nodeOf(EMPTY);
							}
						}
						readChar();
						return parseName();
					case 'I':
						if (_m == MODE_DOCTYPE_ATTLIST || _m == MODE_EXTERNAL_ATTLIST) {
							if (next('D', 'R', 'E', 'F', 'S')) {
								readChar();
								return nodeOf(TYPE_IDREFS);
							} else if (next('D', 'R', 'E', 'F')) {
								readChar();
								return nodeOf(TYPE_IDREF);
							} else if (next('D')) {
								readChar();
								return nodeOf(TYPE_ID);
							}
						} else if (_m == MODE_EXTERNAL) {
							if (next('G', 'N', 'O', 'R', 'E')) {
								_m = MODE_EXTERNAL_IGNORE;
								readChar();
								return nodeOf(IGNORE);
							} else if (next('N', 'C', 'L', 'U', 'D', 'E')) {
								readChar();
								return nodeOf(INCLUDE);
							} 					
						}
						readChar();
						return parseName();
					case 'N':
						if (_m == MODE_DOCTYPE_ATTLIST || _m == MODE_EXTERNAL_ATTLIST) {
							if (next('M', 'T', 'O', 'K', 'E', 'N', 'S')) {
								readChar();
								return nodeOf(TYPE_NMTOKENS);
							} else if (next('M', 'T', 'O', 'K', 'E', 'N')) {
								readChar();
								return nodeOf(TYPE_NMTOKEN);
							} else if (next('O', 'T', 'A', 'T', 'I', 'O', 'N')) {
								readChar();
								return nodeOf(TYPE_NOTATION);
							}
						} else if (_m == MODE_DOCTYPE_ENTITY || _m == MODE_EXTERNAL_ENTITY) {
							if (next('D', 'A', 'T', 'A')) {
								readChar();
								return nodeOf(NDATA);
							}
						}
						readChar();
						return parseName();
					case 'P':
						if (_m == MODE_DOCTYPE
							|| _m == MODE_DOCTYPE_ENTITY
							|| _m == MODE_DOCTYPE_NOTATION
							|| _m == MODE_EXTERNAL_ENTITY
							|| _m == MODE_EXTERNAL_NOTATION) {
							if (next('U', 'B', 'L', 'I', 'C')) {
								readChar();
								return nodeOf(PUBLIC);
							}
						}
						readChar();
						return parseName();
					case 'S':
						if (_m == MODE_DOCTYPE
							|| _m == MODE_DOCTYPE_ENTITY
							|| _m == MODE_DOCTYPE_NOTATION
							|| _m == MODE_EXTERNAL_ENTITY
							|| _m == MODE_EXTERNAL_NOTATION) {
							if (next('Y', 'S', 'T', 'E', 'M')) {
								readChar();
								return nodeOf(SYSTEM);
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
						} else if (_m == MODE_DOCTYPE_ATTLIST || _m == MODE_EXTERNAL_ATTLIST) {
							int q = _c;
							readChar();
							return parseAttValue(q);
						} else if (_m == MODE_DOCTYPE_ENTITY || _m == MODE_EXTERNAL_ENTITY) {
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
					break;
				case MODE_EXTERNAL_IGNORE:
					if (preferred == '[') {
						switch (_c) {
						case HT:
						case LF:
						case CR:
						case SP:
							readChar();
							return parseWhiteSpace();
						case '[':
							readChar();
							return nodeOf('[');
						default:
							readChar();
							return nodeOf(ILLEGAL_CHARACTER);
								
						}
					}
					return parseIgnoreSectionContents();
				default:
					throw new RuntimeException("Lexer::read: BUG!");
				}
			}
		}
		readChar();
		return nodeOf(ILLEGAL_CHARACTER);
	}

	private Node parseWhiteSpace() {
		while (isWhiteSpace(_c)) {
			readChar();
		}
		return nodeOf(S);
	}

	private Node parseComment() {
		while (true) {
			if (_c == EOF) {
				return nodeOf(PREMATURE_EOF);
			} else if (_c == '-') {
				readChar();
				if (_c == '-') {
					readChar();
					if (_c == '>') {
						readChar();
						return nodeOf(COMMENT);
					} else {
						readChar();
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				}
			} else {
				readChar();
			}
		}
	}

	private Node parsePI() {
		while (true) {
			if (_c == EOF) {
				return nodeOf(PREMATURE_EOF);
			} else if (_c == '?' && peek('>')) {
				return nodeOf(PI_BODY);
			} else {
				readChar();
			}
		}
	}

	private Node parseName() {
		while (isNameChar(_c)) {
			readChar();
		}
		return nodeOf(NAME);
	}

	private Node parseNmtoken() {
		while (isNameChar(_c)) {
			readChar();
		}
		return nodeOf(NMTOKEN);
	}

	private Node parseAttValue(int q) {
		while (_c != q) {
			switch (_c) {
			case EOF:
				return nodeOf(PREMATURE_EOF);
			case '<':
				readChar();
				return nodeOf(ILLEGAL_CHARACTER);
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
							return nodeOf(MALFORMED_CHARREF);
						}
					} else if (isDigit(_c)) {
						readChar();
						while (isDigit(_c)) {
							readChar();
						}
					} else {
						readChar();
						return nodeOf(MALFORMED_CHARREF);
					}
					if (_c == ';') {
						readChar();
					} else if (_c == EOF) {
						return nodeOf(PREMATURE_EOF);
					} else {
						readChar();
						return nodeOf(MALFORMED_CHARREF);
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
						return nodeOf(PREMATURE_EOF);
					} else {
						readChar();
						return nodeOf(MALFORMED_ENTITYREF);
					}
				} else {
					readChar();
					return nodeOf(MALFORMED_REFERENCE);
				}
				break;
			default:
				readChar();
				break;
			}
		}
		readChar();
		return nodeOf(ATT_VALUE);
	}

	private Node parseSystemLiteral(int q) {
		while (_c != q) {
			if (_c == EOF) {
				return nodeOf(PREMATURE_EOF);
			} else {
				readChar();
			}
		}
		readChar();
		return nodeOf(SYSTEM_LITERAL);
	}

	private Node parsePubidLiteral(int q) {
		readChar();
		while (_c != q) {
			if (_c == EOF) {
				return nodeOf(PREMATURE_EOF);
			} else if (isPubidChar(_c)) {
				readChar();
			} else {
				return nodeOf(ILLEGAL_CHARACTER);
			}
		}
		readChar();
		return nodeOf(PUBID_LITERAL);
	}

	private Node parseEntityValue(int q) {
		while (_c != q) {
			switch (_c) {
			case EOF:
				return nodeOf(PREMATURE_EOF);
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
						return nodeOf(PREMATURE_EOF);
					} else {
						readChar();
						return nodeOf(MALFORMED_PEREFERENCE);
					}
				} else {
					readChar();
					return nodeOf(MALFORMED_PEREFERENCE);
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
							return nodeOf(MALFORMED_CHARREF);
						}
					} else if (isDigit(_c)) {
						readChar();
						while (isDigit(_c)) {
							readChar();
						}
					} else {
						readChar();
						return nodeOf(MALFORMED_CHARREF);
					}
					if (_c == ';') {
						readChar();
					} else if (_c == EOF) {
						return nodeOf(PREMATURE_EOF);
					} else {
						readChar();
						return nodeOf(MALFORMED_CHARREF);
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
						return nodeOf(PREMATURE_EOF);
					} else {
						readChar();
						return nodeOf(MALFORMED_ENTITYREF);
					}
				} else {
					readChar();
					return nodeOf(MALFORMED_REFERENCE);
				}
				break;
			default:
				readChar();
				break;
			}
		}
		readChar();
		return nodeOf(ENTITY_VALUE);
	}

	private Node parsePEReference() {
		readChar();
		while (isNameChar(_c)) {
			readChar();
		}
		if (_c == ';') {
			readChar();
			return nodeOf(PEREFERENCE);
		} else if (_c == EOF) {
			return nodeOf(PREMATURE_EOF);
		} else {
			readChar();
			return nodeOf(MALFORMED_PEREFERENCE);
		}
	}

	private Node parseReference() {
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
						return nodeOf(CHAR_REF);
					} else {
						readChar();
						return nodeOf(MALFORMED_CHARREF);
					}
				} else {
					return nodeOf(MALFORMED_CHARREF);
				}
			} else if (Character.isDigit(_c)) {
				readChar();
				while (Character.isDigit(_c)) {
					readChar();
				}
				if (_c == ';') {
					readChar();
					return nodeOf(CHAR_REF);
				} else {
					readChar();
					return nodeOf(MALFORMED_CHARREF);
				}
			} else {
				readChar();
				return nodeOf(MALFORMED_CHARREF);
			}
		} else if (isNameStartChar(_c)) {
			// EntityRef
			readChar();
			while (isNameChar(_c)) {
				readChar();
			}
			if (_c == ';') {
				readChar();
				return nodeOf(ENTITY_REF);
			} else {
				readChar();
				return nodeOf(MALFORMED_ENTITYREF);
			}
		} else {
			return nodeOf(MALFORMED_REFERENCE);
		}
	}

	private Node parseCDSect() {
		while (true) {
			if (_c == EOF) {
				return nodeOf(PREMATURE_EOF);
			} else if (_c == ']' && next(']', '>')) {
				readChar();
				return nodeOf(CD_SECT);
			} else {
				readChar();
			}
		}
	}

	private Node parseCharData() {
		while (_c != EOF && _c != '<' && _c != '&' && (_c != ']' || !peek(']', '>'))) {
			readChar();
		}
		return nodeOf(CHAR_DATA);
	}

	private Node parseIgnoreSectionContents() {
		int depth = 1;
		while (true) {
			if (_c == EOF) {
				return nodeOf(PREMATURE_EOF);
			} else if (_c == ']' && peek(']', '>')) {
				if (--depth == 0) {
					break;
				}
				readChar();
				readChar();
			} else if (_c == '<' && next('!', '[')) {
				depth++;
			} else if (!isChar(_c)) {
				return nodeOf(ILLEGAL_CHARACTER);
			}
			readChar();
		}
		_m = MODE_EXTERNAL;
		return nodeOf(IGNORE_SECTION_CONTENTS);
	}

	private int readChar() {
		_c = _reader.readChar();
		_reader.from();
		return _c;
	}

	private boolean next(int... cc) {
		return _reader.next(cc);
	}

	private boolean peek(int... cc) {
		return _reader.peek(cc);
	}

	private Node nodeOf(int code) {
		return _nodeFactory.nodeOf(code);
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
