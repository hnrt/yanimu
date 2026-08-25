package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.Node;

import static com.hideakin.yanimu.xml.Node.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Lexer {

	public static final int EOF = Node.EOF;
	public static final int PREMATURE_EOF = Node.PREMATURE_EOF;
	public static final int ILLEGAL_ENCODING = Node.ILLEGAL_ENCODING;

	public static final int HT = 9;
	public static final int LF = 10;
	public static final int CR = 13;
	public static final int SP = 32;

	public static final Map<String, Integer> RESERVED_WORDS;

	private static final ReaderFactory _readerFactory = new ReaderFactory();

	static {
		Map<String, Integer> rw = new HashMap<>();
		rw.put("SYSTEM", Integer.valueOf(SYSTEM));
		rw.put("PUBLIC", Integer.valueOf(PUBLIC));
		rw.put("<!ELEMENT", Integer.valueOf(ELEMENT_DECL_START));
		rw.put("<!ATTLIST", Integer.valueOf(ATTLIST_DECL_START));
		rw.put("<!ENTITY", Integer.valueOf(ENTITY_DECL_START));
		rw.put("<!NOTATION", Integer.valueOf(NOTATION_DECL_START));
		rw.put("#PCDATA", Integer.valueOf(PCDATA));
		rw.put("#REQUIRED", Integer.valueOf(REQUIRED));
		rw.put("#IMPLIED", Integer.valueOf(IMPLIED));
		rw.put("#FIXED", Integer.valueOf(FIXED));
		rw.put("ANY", Integer.valueOf(ANY));
		rw.put("CDATA", Integer.valueOf(TYPE_CDATA));
		rw.put("ENTITY", Integer.valueOf(TYPE_ENTITY));
		rw.put("ENTITIES", Integer.valueOf(TYPE_ENTITIES));
		rw.put("EMPTY", Integer.valueOf(EMPTY));
		rw.put("ID", Integer.valueOf(TYPE_ID));
		rw.put("IDREF", Integer.valueOf(TYPE_IDREF));
		rw.put("IDREFS", Integer.valueOf(TYPE_IDREFS));
		rw.put("IGNORE", Integer.valueOf(IGNORE));
		rw.put("INCLUDE", Integer.valueOf(INCLUDE));
		rw.put("NMTOKEN", Integer.valueOf(TYPE_NMTOKEN));
		rw.put("NMTOKENS", Integer.valueOf(TYPE_NMTOKENS));
		rw.put("NOTATION", Integer.valueOf(TYPE_NOTATION));
		rw.put("NDATA", Integer.valueOf(NDATA));
		RESERVED_WORDS = Map.copyOf(rw);
	}

	private final LexerContext _context;
	private final NodeFactory _nodeFactory;
	private Reader _reader;
	private int _c; // current UNICODE codepoint

	public Lexer(byte[] content) {
		this(content, LexerContext.BASE);
	}

	public Lexer(byte[] content, int context) {
		_context = LexerContext.of(context);
		_nodeFactory = new NodeFactory();
		_reader = _readerFactory.create(content, _nodeFactory);
		readChar();
	}

	public Lexer(byte[] content, Lexer parent) {
		_context = parent._context;
		_nodeFactory = parent._nodeFactory;
		_reader = _readerFactory.create(content, _nodeFactory);
		readChar();
	}

	public Lexer(String content) {
		this(content.getBytes(StandardCharsets.UTF_8), LexerContext.BASE);
	}

	public Lexer(String content, int context) {
		this(content.getBytes(StandardCharsets.UTF_8), context);
	}

	public Lexer(String content, Lexer parent) {
		this(content.getBytes(StandardCharsets.UTF_8), parent);
	}

	public int getContext() {
		return _context.get();
	}

	public void setContext(int context) {
		_context.set(context);
	}

	public int pushContext(int context) {
		return _context.push(context);
	}

	public int popContext() {
		return _context.pop();
	}

	public Node read() {
		return read(0);
	}

	public Node read(int preferred) {
		if (_c == EOF) {
			return nodeOf(EOF);
		}
		switch (_context.get()) {
		case LexerContext.BASE:
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
					pushContext(LexerContext.PI);
					return nodeOf(PI_START);
				} else if (next('!', 'D', 'O', 'C', 'T', 'Y', 'P', 'E')) {
					readChar();
					pushContext(LexerContext.DOCTYPE);
					return nodeOf(DOCTYPE_DECL_START);
				} else {
					readChar();
					pushContext(LexerContext.STAG);
					return nodeOf(STAG_START);
				}
			default:
				break;
			}
			break;
		case LexerContext.PI:
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
					popContext();
					return nodeOf(PI_END);
				}
				//FALLTHROUGH
			default:
				if (preferred == PI_TARGET && isNameStartChar(_c)) {
					readChar();
					while (isNameChar(_c)) {
						readChar();
					}
					return nodeOf("(?i)xml", NAME, PI_TARGET);
				}
				readChar();
				return parsePI();
			}
		case LexerContext.XML:
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
					popContext();
					return nodeOf(XML_END);
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
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
		case LexerContext.STAG:
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '>':
				readChar();
				setContext(LexerContext.CONTENT);
				return nodeOf(STAG_END);
			case '/':
				if (next('>')) {
					readChar();
					popContext();
					return nodeOf(EETAG_END);
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
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
		case LexerContext.CONTENT:
			switch (_c) {
			case '<':
				if (next('!', '-', '-')) {
					readChar();
					return parseComment();
				} else if (next('?')) {
					readChar();
					pushContext(LexerContext.PI);
					return nodeOf(PI_START);
				} else if (next('!', '[', 'C', 'D', 'A', 'T', 'A', '[')) {
					readChar();
					return parseCDSect();
				} else if (next('/')) {
					readChar();
					setContext(LexerContext.ETAG);
					return nodeOf(ETAG_START);
				} else {
					readChar();
					pushContext(LexerContext.STAG);
					return nodeOf(STAG_START);
				}
			case '&':
				readChar();
				return parseReference();
			case ']':
				if (next(']', '>')) {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
				//FALLTHROUGH
			default:
				readChar();
				return parseCharData();
			}
		case LexerContext.ETAG:
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '>':
				readChar();
				popContext();
				return nodeOf(ETAG_END);
			default:
				if (isNameStartChar(_c)) {
					readChar();
					return parseName();
				}
				break;
			}
			break;
		case LexerContext.DOCTYPE:
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
					} else {
						readChar();
						if (isAlphabeticUppercase(_c)) {
							readChar();
							while (isAlphabeticUppercase(_c)) {
								readChar();
							}
							int type = lookup();
							switch(type) {
							case ELEMENT_DECL_START:
								pushContext(LexerContext.ELEMENT);
								return nodeOf(ELEMENT_DECL_START);
							case ATTLIST_DECL_START:
								pushContext(LexerContext.ATTLIST);
								return nodeOf(ATTLIST_DECL_START);
							case ENTITY_DECL_START:
								pushContext(LexerContext.ENTITY);
								return nodeOf(ENTITY_DECL_START);
							case NOTATION_DECL_START:
								pushContext(LexerContext.NOTATION);
								return nodeOf(NOTATION_DECL_START);
							default:
								break;
							}
						}
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				} else if (next('?')) {
					readChar();
					pushContext(LexerContext.PI);
					return nodeOf(PI_START);
				}
				break;
			case '>':
				readChar();
				popContext();
				return nodeOf(TAG_END);
			case '[':
			case ']':
				int c = _c;
				readChar();
				return nodeOf(c);
			case '%':
				readChar();
				if (isNameStartChar(_c)) {
					readChar();
					return parsePEReference();
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
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
				}
				break;
			default:
				if (preferred == NAME && isNameStartChar(_c)) {
					readChar();
					return parseName();
				} else if (isAlphabeticUppercase(_c)) {
					readChar();
					while (isAlphabeticUppercase(_c)) {
						readChar();
					}
					int type = lookup();
					switch (type) {
					case SYSTEM:
					case PUBLIC:
						return nodeOf(type);
					default:
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				}
				break;
			}
			break;
		case LexerContext.EXTERNAL:
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
					} else if (next('[')) {
						readChar();
						pushContext(LexerContext.CONDITIONAL);
						return nodeOf(SECTION_START);
					} else {
						readChar();
						if (isAlphabeticUppercase(_c)) {
							readChar();
							while (isAlphabeticUppercase(_c)) {
								readChar();
							}
							int type = lookup();
							switch(type) {
							case ELEMENT_DECL_START:
								pushContext(LexerContext.ELEMENT);
								return nodeOf(ELEMENT_DECL_START);
							case ATTLIST_DECL_START:
								pushContext(LexerContext.ATTLIST);
								return nodeOf(ATTLIST_DECL_START);
							case ENTITY_DECL_START:
								pushContext(LexerContext.ENTITY);
								return nodeOf(ENTITY_DECL_START);
							case NOTATION_DECL_START:
								pushContext(LexerContext.NOTATION);
								return nodeOf(NOTATION_DECL_START);
							default:
								break;
							}
						}
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				} else if (next('?')) {
					readChar();
					pushContext(LexerContext.PI);
					return nodeOf(PI_START);
				}
				break;
			case '%':
				readChar();
				if (isNameStartChar(_c)) {
					readChar();
					return parsePEReference();
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
			default:
				break;
			}
			break;
		case LexerContext.CONDITIONAL:
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
					} else {
						readChar();
						if (isAlphabeticUppercase(_c)) {
							readChar();
							while (isAlphabeticUppercase(_c)) {
								readChar();
							}
							int type = lookup();
							switch(type) {
							case ELEMENT_DECL_START:
								pushContext(LexerContext.ELEMENT);
								return nodeOf(ELEMENT_DECL_START);
							case ATTLIST_DECL_START:
								pushContext(LexerContext.ATTLIST);
								return nodeOf(ATTLIST_DECL_START);
							case ENTITY_DECL_START:
								pushContext(LexerContext.ENTITY);
								return nodeOf(ENTITY_DECL_START);
							case NOTATION_DECL_START:
								pushContext(LexerContext.NOTATION);
								return nodeOf(NOTATION_DECL_START);
							default:
								break;
							}
						}
					}
					return nodeOf(ILLEGAL_SEQUENCE);
				} else if (next('?')) {
					readChar();
					pushContext(LexerContext.PI);
					return nodeOf(PI_START);
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
			case '[':
				readChar();
				return nodeOf('[');
			case ']':
				if (next(']', '>')) {
					readChar();
					popContext();
					return nodeOf(SECTION_END);
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
			default:
				if (isAlphabeticUppercase(_c)) {
					readChar();
					while (isAlphabeticUppercase(_c)) {
						readChar();
					}
					int type = lookup();
					switch (type) {
					case IGNORE:
						pushContext(LexerContext.IGNORE);
						return nodeOf(type);
					case INCLUDE:
						return nodeOf(type);
					default:
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				}
				break;
			}
			break;
		case LexerContext.ELEMENT:
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '>':
				readChar();
				popContext();
				return nodeOf(TAG_END);
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
				readChar();
				if (isAlphabeticUppercase(_c)) {
					readChar();
					while (isAlphabeticUppercase(_c)) {
						readChar();
					}
					int type = lookup();
					switch (type) {
					case PCDATA:
						return nodeOf(type);
					default:
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
			case '%':
				readChar();
				if (isNameStartChar(_c)) {
					readChar();
					return parsePEReference();
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
			default:
				if (preferred == NAME && isNameStartChar(_c)) {
					readChar();
					return parseName();
				} else if (isAlphabeticUppercase(_c)) {
					readChar();
					while (isAlphabeticUppercase(_c)) {
						readChar();
					}
					int type = lookup();
					switch (type) {
					case EMPTY:
					case ANY:
						return nodeOf(type);
					default:
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				}
				break;
			}
			break;
		case LexerContext.ATTLIST:
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '>':
				readChar();
				popContext();
				return nodeOf(TAG_END);
			case '(':
			case ')':
			case '|':
				int c = _c;
				readChar();
				return nodeOf(c);
			case '#':
				readChar();
				if (isAlphabeticUppercase(_c)) {
					readChar();
					while (isAlphabeticUppercase(_c)) {
						readChar();
					}
					int type = lookup();
					switch (type) {
					case REQUIRED:
					case IMPLIED:
					case FIXED:
						return nodeOf(type);
					default:
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				}
				break;
			case '%':
				readChar();
				if (isNameStartChar(_c)) {
					readChar();
					return parsePEReference();
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
			case '\"':
			case '\'':
				int q = _c;
				readChar();
				return parseAttValue(q);
			default:
				if (preferred == NAME && isNameStartChar(_c)) {
					readChar();
					return parseName();
				} else if (preferred == NMTOKEN && isNameChar(_c)) {
					readChar();
					return parseNmtoken();
				} else if (isAlphabeticUppercase(_c)) {
					readChar();
					while (isAlphabeticUppercase(_c)) {
						readChar();
					}
					int type = lookup();
					switch (type) {
					case TYPE_CDATA:
					case TYPE_ID:
					case TYPE_IDREF:
					case TYPE_IDREFS:
					case TYPE_ENTITY:
					case TYPE_ENTITIES:
					case TYPE_NMTOKEN:
					case TYPE_NMTOKENS:
					case TYPE_NOTATION:
						return nodeOf(type);
					default:
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				}
				break;
			}
			break;
		case LexerContext.ENTITY:
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '>':
				readChar();
				popContext();
				return nodeOf(TAG_END);
			case '%':
				readChar();
				if (isNameStartChar(_c)) {
					readChar();
					return parsePEReference();
				} else {
					return nodeOf('%');
				}
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
				} else {
					int q = _c;
					readChar();
					return parseEntityValue(q);
				}
			default:
				if (preferred == NAME && isNameStartChar(_c)) {
					readChar();
					return parseName();
				} else if (isAlphabeticUppercase(_c)) {
					readChar();
					while (isAlphabeticUppercase(_c)) {
						readChar();
					}
					int type = lookup();
					switch (type) {
					case SYSTEM:
					case PUBLIC:
					case NDATA:
						return nodeOf(type);
					default:
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				}
				break;
			}
			break;
		case LexerContext.NOTATION:
			switch (_c) {
			case HT:
			case LF:
			case CR:
			case SP:
				readChar();
				return parseWhiteSpace();
			case '>':
				readChar();
				popContext();
				return nodeOf(TAG_END);
			case '%':
				readChar();
				if (isNameStartChar(_c)) {
					readChar();
					return parsePEReference();
				} else {
					readChar();
					return nodeOf(ILLEGAL_SEQUENCE);
				}
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
				}
				break;
			default:
				if (preferred == NAME && isNameStartChar(_c)) {
					readChar();
					return parseName();
				} else if (isAlphabeticUppercase(_c)) {
					readChar();
					while (isAlphabeticUppercase(_c)) {
						readChar();
					}
					int type = lookup();
					switch (type) {
					case SYSTEM:
					case PUBLIC:
						return nodeOf(type);
					default:
						return nodeOf(ILLEGAL_SEQUENCE);
					}
				}
			}
			break;
		case LexerContext.IGNORE:
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
			switch (_c) {
			case EOF:
				return nodeOf(PREMATURE_EOF);
			case '-':
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
				break;
			default:
				readChar();
				break;
			}
		}
	}

	private Node parsePI() {
		while (true) {
			switch (_c) {
			case EOF:
				return nodeOf(PREMATURE_EOF);
			case '?':
				if (peek('>')) {
					return nodeOf(PI_BODY);
				}
				//FALLTHROUGH
			default:
				readChar();
				break;
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
				int type = doParseReference();
				if (type == CHAR_REF || type == ENTITY_REF) {
					break;
				} else {
					return nodeOf(type);
				}
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
					readChar();
					int type = doParsePEReference();
					if (type == PEREFERENCE) {
						break;
					} else {
						return nodeOf(type);
					}
				} else {
					readChar();
					return nodeOf(MALFORMED_PEREFERENCE);
				}
			case '&':
				readChar();
				int type = doParseReference();
				if (type == CHAR_REF || type == ENTITY_REF) {
					break;
				} else {
					return nodeOf(type);
				}
			default:
				readChar();
				break;
			}
		}
		readChar();
		return nodeOf(ENTITY_VALUE);
	}

	private Node parsePEReference() {
		int type = doParsePEReference();
		return nodeOf(type);
	}

	private int doParsePEReference() {
		while (isNameChar(_c)) {
			readChar();
		}
		if (_c == ';') {
			readChar();
			return PEREFERENCE;
		} else if (_c == EOF) {
			return PREMATURE_EOF;
		} else {
			readChar();
			return MALFORMED_PEREFERENCE;
		}
	}

	private Node parseReference() {
		int type = doParseReference();
		return nodeOf(type);
	}

	private int doParseReference() {
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
						return CHAR_REF;
					} else if (_c == EOF) {
						return PREMATURE_EOF;
					} else {
						readChar();
						return MALFORMED_CHARREF;
					}
				} else {
					return MALFORMED_CHARREF;
				}
			} else if (isDigit(_c)) {
				readChar();
				while (isDigit(_c)) {
					readChar();
				}
				if (_c == ';') {
					readChar();
					return CHAR_REF;
				} else if (_c == EOF) {
					return PREMATURE_EOF;
				} else {
					readChar();
					return MALFORMED_CHARREF;
				}
			} else {
				readChar();
				return MALFORMED_CHARREF;
			}
		} else if (isNameStartChar(_c)) {
			// EntityRef
			readChar();
			while (isNameChar(_c)) {
				readChar();
			}
			if (_c == ';') {
				readChar();
				return ENTITY_REF;
			} else if (_c == EOF) {
				return PREMATURE_EOF;
			} else {
				readChar();
				return MALFORMED_ENTITYREF;
			}
		} else {
			readChar();
			return MALFORMED_REFERENCE;
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
		while (true) {
			switch (_c) {
			case EOF:
			case '<':
			case '&':
				return nodeOf(CHAR_DATA);
			case ']':
				if (peek(']', '>')) {
					return nodeOf(CHAR_DATA);
				}
				//FALLTHROUGH
			default:
				readChar();
				break;
			}
		}
	}

	private Node parseIgnoreSectionContents() {
		int depth = 1;
		while (true) {
			switch (_c) {
			case EOF:
				return nodeOf(PREMATURE_EOF);
			case ']':
				if (peek(']', '>')) {
					if (--depth == 0) {
						popContext();
						return nodeOf(IGNORE_SECTION_CONTENTS);
					}
					readChar();
					readChar();
					readChar();
				} else {
					readChar();
				}
				break;
			case '<':
				if (next('!', '[')) {
					depth++;
				}
				readChar();
				break;
			default:
				if (isChar(_c)) {
					readChar();
					break;
				} else {
					return nodeOf(ILLEGAL_CHARACTER);
				}
			}
		}
	}

	private int readChar() {
		_c = _reader.readChar();
		return _c;
	}

	private boolean next(int... cc) {
		return _reader.next(cc);
	}

	private boolean peek(int... cc) {
		return _reader.peek(cc);
	}

	private int lookup() {
		return _nodeFactory.lookup(RESERVED_WORDS, -1);
	}

	private Node nodeOf(int type) {
		return _nodeFactory.nodeOf(type);
	}

	private Node nodeOf(String regex, int typeIfTrue, int typeOtherwise) {
		return _nodeFactory.nodeOf(_nodeFactory.matches(regex) ? typeIfTrue : typeOtherwise);
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
		return isAlphabeticUppercase(c) || isAlphabeticLowercase(c);
	}

	public static boolean isAlphabeticUppercase(int c) {
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
			return true;
		default:
			return false;
		}
	}

	public static boolean isAlphabeticLowercase(int c) {
		switch (c) {
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
