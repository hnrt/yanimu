package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.Attribute;
import com.hideakin.yanimu.xml.CharData;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.ParseException;
import com.hideakin.yanimu.xml.ProcessingInstruction;
import com.hideakin.yanimu.xml.Token;
import com.hideakin.yanimu.xml.XmlDeclaration;

import static com.hideakin.yanimu.xml.Token.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

	private final byte[] _contents;
	private final List<Object> _list = new ArrayList<>();
	private Element _root;
	private Lexer _lexer;
	private Token _t;

	public Parser(byte[] contents) {
		_contents = contents;
	}

	public Element root() {
		return _root;
	}

	public void run() throws Exception {
		_lexer = new Lexer(_contents);
		_t = _lexer.read();
		parseProlog();
		_root = parseElement();
		_list.add(_root);
		parseMisc();
		if (_t.code != EOF) {
			throw new ParseException("Extra data exist.", _t.start);
		}
	}

	private void parseProlog() throws Exception {
		if (_t.code == XML) {
			XmlDeclaration xml = parseXmlDeclaration();
			_list.add(xml);
		}
		while (parseMisc()) {
			continue;
		}
		if (_t.code == DOCTYPE) {
			parseDoctypeDeclaration();
			while (parseMisc()) {
				continue;
			}
		}
	}

	private XmlDeclaration parseXmlDeclaration() throws Exception {
		XmlDeclaration xml;
		int start = _t.start;
		String name;
		String version;
		String encoding = null;
		String standalone = null;
		read();
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = text();
		} else {
			throw new ParseException("version is expected.", _t.start);
		}
		if (name.equals("version")) {
			read();
		} else {
			throw new ParseException("version is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		if (_t.code == EQ) {
			read();
		} else {
			throw new ParseException("Equal sign is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		if (_t.code == ATTVALUE) {
			version = text();
			if (!version.substring(1, version.length() - 1).matches("1\\.[0-9]+")) {
				throw new ParseException("Malformed version number.", _t.start);
			}
			read();
		} else {
			throw new ParseException("version number is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
			if (_t.code == NAME) {
				name = text();
				if (name.equals("encoding")) {
					read();
					if (_t.code == SP) {
						read();
					}
					if (_t.code == EQ) {
						read();
					} else {
						throw new ParseException("Equal sign is expected.", _t.start);
					}
					if (_t.code == SP) {
						read();
					}
					if (_t.code == ATTVALUE) {
						encoding = text();
						read();
					} else {
						throw new ParseException("encoding name is expected.", _t.start);
					}
					if (_t.code == SP) {
						read();
						if (_t.code == NAME) {
							name = text();
							if (name.equals("standalone")) {
								read();
								if (_t.code == SP) {
									read();
								}
								if (_t.code == EQ) {
									read();
								} else {
									throw new ParseException("Equal sign is expected.", _t.start);
								}
								if (_t.code == SP) {
									read();
								}
								if (_t.code == ATTVALUE) {
									standalone = text();
									if (!standalone.substring(1, standalone.length() - 1).equals("yes") && !standalone.equals("no")) {
										throw new ParseException("Malformed standalone value.", _t.start);
									}
									read();
								} else {
									throw new ParseException("standalone value is expected.", _t.start);
								}
								if (_t.code == SP) {
									read();
								}
							} else {
								throw new ParseException("encoding or standalone is expected.", _t.start);
							}
						}
					}
				} else if (name.equals("standalone")) {
					read();
					if (_t.code == SP) {
						read();
					}
					if (_t.code == EQ) {
						read();
					} else {
						throw new ParseException("Equal sign is expected.", _t.start);
					}
					if (_t.code == SP) {
						read();
					}
					if (_t.code == ATTVALUE) {
						standalone = text();
						if (!standalone.substring(1, standalone.length() - 1).equals("yes") && !standalone.equals("no")) {
							throw new ParseException("Malformed standalone value.", _t.start);
						}
						read();
					} else {
						throw new ParseException("standalone value is expected.", _t.start);
					}
					if (_t.code == SP) {
						read();
					}
				} else {
					throw new ParseException("encoding or standalone is expected.", _t.start);
				}
			}
		}
		if (_t.code == PI_END) {
			xml = new XmlDeclaration(start, _t.end, version, encoding, standalone);
			read();
		} else {
			throw new ParseException("?> is expected.", _t.start);
		}
		return xml;
	}

	private boolean parseMisc() throws Exception {
		if (_t.code == COMMENT) {
			_list.add(_t);
			read();
			return true;
		} else if (_t.code == PI_START) {
			ProcessingInstruction pi = parseProcessingInstruction();
			_list.add(pi);
			return true;
		} else if (_t.code == SP) {
			_list.add(_t);
			read();
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unused")
	private void parseDoctypeDeclaration() throws Exception {
		int start = _t.start;
		String name;
		if (_t.code == DOCTYPE) {
			read();
		} else {
			throw new ParseException("<!DOCTYPE is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = text();
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
			if (_t.code == SYSTEM || _t.code == PUBLIC) {
				parseExternalId();
				if (_t.code == SP) {
					read();
				}
			}
		}
		if (_t.code == '[') {
			read();
			while (_t.code != ']') {
				if (_t.code == ELEMENT_DECL) {
					parseElementDecl();
				} else if (_t.code == ATTLIST_DECL) {
					parseAttlistDecl();
				} else if (_t.code == ENTITY_DECL) {
					parseEntityDecl();
				} else if (_t.code == NOTATION_DECL) {
					parseNotationDecl();
				} else if (_t.code == PI_START) {
					parseProcessingInstruction();
				} else if (_t.code == COMMENT) {
					read();
				} else if (_t.code == PEREFERENCE) {
					read();
				} else if (_t.code == SP) {
					read();
				} else {
					throw new ParseException("Internal subset is expected.", _t.start);
				}
			}
			read();
			if (_t.code == SP) {
				read();
			}
		}
		if (_t.code == '>') {
			read();
		} else {
			throw new ParseException("> is expected.", _t.start);
		}
	}

	private void parseElementDecl() throws Exception {
		read();
		if (_t.code == SP) {
			read(NAME);
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == NAME) {
			read();
		} else {
			throw new ParseException("Name is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == EMPTY) {
			read();
		} else if (_t.code == ANY) {
			read();
		} else if (_t.code == '(') {
			parseMixedOrChildren();
		} else {
			throw new ParseException("EMPTY, ANY or ( is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		if (_t.code == '>') {
			read();
		} else {
			throw new ParseException("End of element declaration is expected.", _t.start);
		}
	}

	private void parseMixedOrChildren() throws Exception {
		if (_t.code == '(') {
			read();
		} else {
			throw new ParseException("( is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		if (_t.code == PCDATA) {
			read();
			if (_t.code == SP) {
				read();
			}
			if (_t.code == ')') {
				read();
			} else if (_t.code == PCDATA_END) {
				read();
			} else if (_t.code == '|') {
				do {
					read();
					if (_t.code == SP) {
						read();
					}
					if (_t.code == NAME) {
						read();
					} else {
						throw new ParseException("Name is expected.", _t.start);
					}
					if (_t.code == SP) {
						read();
					}
				} while (_t.code == '|');
				if (_t.code == PCDATA_END) {
					read();
				} else {
					throw new ParseException(")* is expected.", _t.start);
				}
			} else {
				throw new ParseException(") or )* is expected.", _t.start);
			}
		} else {
			parseCP();
			if (_t.code == SP) {
				read();
			}
			if (_t.code == '|') {
				do {
					read();
					if (_t.code == SP) {
						read();
					}
					parseCP();
					if (_t.code == SP) {
						read();
					}
				} while (_t.code == '|');
			} else if (_t.code == ',') {
				do {
					read();
					if (_t.code == SP) {
						read();
					}
					parseCP();
					if (_t.code == SP) {
						read();
					}
				} while (_t.code == ',');
			}
			if (_t.code == ')') {
				read();
			} else {
				throw new ParseException(") is expected.", _t.start);
			}
			if (_t.code == '?') {
				read();
			} else if (_t.code == '*') {
				read();
			} else if (_t.code == '+') {
				read();
			}
		}
	}

	private void parseChoiceOrSequence() throws Exception {
		if (_t.code == '(') {
			read();
		} else {
			throw new ParseException("( is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		parseCP();
		if (_t.code == SP) {
			read();
		}
		if (_t.code == '|') {
			do {
				read();
				if (_t.code == SP) {
					read();
				}
				parseCP();
				if (_t.code == SP) {
					read();
				}
			} while (_t.code == '|');
		} else if (_t.code == ',') {
			do {
				read();
				if (_t.code == SP) {
					read();
				}
				parseCP();
				if (_t.code == SP) {
					read();
				}
			} while (_t.code == ',');
		}
		if (_t.code == ')') {
			read();
		} else {
			throw new ParseException(") is expected.", _t.start);
		}
	}

	private void parseCP() throws Exception {
		if (_t.code == NAME) {
			read();
		} else {
			parseChoiceOrSequence();
		}
		if (_t.code == '?') {
			read();
		} else if (_t.code == '*') {
			read();
		} else if (_t.code == '+') {
			read();
		}
	}

	@SuppressWarnings("unused")
	private void parseAttlistDecl() throws Exception {
		int start = _t.start;
		String name;
		String aname;
		if (_t.code == ATTLIST_DECL) {
			read();
		} else {
			throw new ParseException("<!ATTLIST is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = text();
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
			while (_t.code == NAME) {
				aname = text();
				read();
				if (_t.code == SP) {
					read();
				} else {
					throw new ParseException("White space is expected.", _t.start);
				}
				parseAttType();
				if (_t.code == SP) {
					read();
				} else {
					throw new ParseException("White space is expected.", _t.start);
				}
				parseDefaultDecl();
				if (_t.code == SP) {
					read();
				} else {
					break;
				}
			}
		}
		if (_t.code == '>') {
			read();
		} else {
			throw new ParseException("End of attlist declaration is expected.", _t.start);
		}
	}

	private void parseAttType() throws Exception {
		switch (_t.code) {
		case TYPE_CDATA:
		case TYPE_ID:
		case TYPE_IDREF:
		case TYPE_IDREFS:
		case TYPE_ENTITY:
		case TYPE_ENTITIES:
		case TYPE_NMTOKEN:
		case TYPE_NMTOKENS:
			read();
			break;
		case TYPE_NOTATION:
			parseNotationType();
			break;
		case '(':
			parseEnumeration();
			break;
		default:
			throw new ParseException("AttType is expected.", _t.start);
		}
	}

	private void parseNotationType() throws Exception {
		if (_t.code == TYPE_NOTATION) {
			read();
		} else {
			throw new ParseException("NOTATION is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == '(') {
			read();
		} else {
			throw new ParseException("( is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		if (_t.code == NAME) {
			read();
		} else {
			throw new ParseException("Name is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		while (_t.code == '|') {
			read();
			if (_t.code == SP) {
				read();
			}
			if (_t.code == NAME) {
				read();
			} else {
				throw new ParseException("Name is expected.", _t.start);
			}
			if (_t.code == SP) {
				read();
			}
		}
		if (_t.code == ')') {
			read();
		} else {
			throw new ParseException(") is expected.", _t.start);
		}
	}

	private void parseEnumeration() throws Exception {
		if (_t.code == '(') {
			read(NMTOKEN);
		} else {
			throw new ParseException("( is expected.", _t.start);
		}
		if (_t.code == SP) {
			read(NMTOKEN);
		}
		if (_t.code == NMTOKEN) {
			read();
		} else {
			throw new ParseException("Nmtoken is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		while (_t.code == '|') {
			read();
			if (_t.code == SP) {
				read();
			}
			if (_t.code == NMTOKEN) {
				read();
			} else {
				throw new ParseException("Name is expected.", _t.start);
			}
			if (_t.code == SP) {
				read();
			}
		}
		if (_t.code == ')') {
			read();
		} else {
			throw new ParseException(") is expected.", _t.start);
		}
	}

	private void parseDefaultDecl() throws Exception {
		switch (_t.code) {
		case REQUIRED:
		case IMPLIED:
			read();
			break;
		case FIXED:
			read();
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("white space is expected.", _t.start);
			}
			//FALLTHROUGH
		default:
			if (_t.code == ATTVALUE) {
				read();
			} else {
				throw new ParseException("AttValue is expected.", _t.start);
			}
			break;
		}
	}

	@SuppressWarnings("unused")
	private void parseEntityDecl() throws Exception {
		int start = _t.start;
		String name;
		String aname;
		if (_t.code == ENTITY_DECL) {
			read();
		} else {
			throw new ParseException("<!ENTITY is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = text();
			read();
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == ENTITY_VALUE) {
				read();
			} else if (_t.code == SYSTEM || _t.code == PUBLIC) {
				parseExternalId();
				if (_t.code == SP) {
					read();
				} else {
					throw new ParseException("White space is expected.", _t.start);
				}
				if (_t.code == NDATA) {
					read();
				} else {
					throw new ParseException("NDATA is expected.", _t.start);
				}
				if (_t.code == SP) {
					read();
				} else {
					throw new ParseException("White space is expected.", _t.start);
				}
				if (_t.code == NAME) {
					read();
				} else {
					throw new ParseException("Name is expected.", _t.start);
				}
			} else {
				throw new ParseException("Entity value or external ID is expected.", _t.start);
			}
		} else if (_t.code == '%') {
			read();
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == NAME) {
				name = text();
				read();
			} else {
				throw new ParseException("Name is expected.", _t.start);
			}
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == ENTITY_VALUE) {
				read();
			} else if (_t.code == SYSTEM || _t.code == PUBLIC) {
				parseExternalId();
			} else {
				throw new ParseException("Entity value or external ID is expected.", _t.start);
			}
		} else {
			throw new ParseException("Name or % is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		if (_t.code == '>') {
			read();
		} else {
			throw new ParseException("End of entity declaration is expected.", _t.start);
		}
	}

	@SuppressWarnings("unused")
	private void parseNotationDecl() throws Exception {
		int start = _t.start;
		String name;
		String aname;
		if (_t.code == NOTATION_DECL) {
			read();
		} else {
			throw new ParseException("<!NOTATION is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = text();
			read();
		} else {
			throw new ParseException("Name is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == SYSTEM || _t.code == PUBLIC) {
			parseExternalIdOrPublicId();
		} else {
			throw new ParseException("External ID or Public ID is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		if (_t.code == '>') {
			read();
		} else {
			throw new ParseException("End of notation declaration is expected.", _t.start);
		}
	}

	private void parseExternalId() throws Exception {
		if (_t.code == SYSTEM) {
			read();
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == SYSTEM_LITERAL) {
				read();
			} else {
				throw new ParseException("System literal is expected.", _t.start);
			}
		} else if (_t.code == PUBLIC) {
			read();
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == PUBID_LITERAL) {
				read();
			} else {
				throw new ParseException("Pubid literal is expected.", _t.start);
			}
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == SYSTEM_LITERAL) {
				read();
			} else {
				throw new ParseException("System literal is expected.", _t.start);
			}
		}
	}

	private void parseExternalIdOrPublicId() throws Exception {
		if (_t.code == SYSTEM) {
			read();
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == SYSTEM_LITERAL) {
				read();
			} else {
				throw new ParseException("System literal is expected.", _t.start);
			}
		} else if (_t.code == PUBLIC) {
			read();
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == PUBID_LITERAL) {
				read();
			} else {
				throw new ParseException("Pubid literal is expected.", _t.start);
			}
			if (_t.code == SP) {
				read();
				if (_t.code == SYSTEM_LITERAL) {
					read();
				}
			}
		}
	}

	private ProcessingInstruction parseProcessingInstruction() throws Exception {
		ProcessingInstruction pi;
		int start = _t.start;
		String name;
		String body;
		read(NAME);
		if (_t.code == NAME) {
			name = text();
			if (name.toLowerCase().equals("xml")) {
				throw new ParseException("xml is now allowed for PI.", _t.start);
			} else {
				read();
			}
		} else {
			throw new ParseException("Name is expected.", _t.start);
		}
		if (_t.code == PI_BODY) {
			body = text();
			read();
		} else {
			body = null;
		}
		if (_t.code == PI_END) {
			pi = new ProcessingInstruction(start, _t.end, name, body);
			read();
		} else {
			throw new ParseException("PI end is expected.", _t.start);
		}
		return pi;
	}

	private Element parseElement() throws Exception {
		Element element;
		int start = _t.start;
		String name;
		List<Attribute> attributes = new ArrayList<>();
		if (_t.code == STAG_START) {
			read();
		} else {
			throw new ParseException("Tag start is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = text();
			read();
		} else {
			throw new ParseException("Name is expected.", _t.start);
		}
		while (_t.code == SP) {
			read();
			int astart;
			String aname;
			if (_t.code == NAME) {
				astart = _t.start;
				aname = text();
				read();
			} else {
				break;
			}
			if (_t.code == SP) {
				read();
			}
			if (_t.code == EQ) {
				read();
			} else {
				throw new ParseException("Equal sign is expected.", _t.start);
			}
			if (_t.code == SP) {
				read();
			}
			if (_t.code == ATTVALUE) {
				attributes.add(new Attribute(astart, _t.end, aname, text()));
				read();
			} else {
				throw new ParseException("Attribute value is expected.", _t.start);
			}
		}
		if (_t.code == EETAG_END) {
			element = new Element(start, _t.end, name, attributes);
			read();
			return element;
		} else if (_t.code == TAG_END) {
			element = new Element(start, _t.end, name, attributes);
			read();
		} else {
			throw new ParseException("Tag end is expected.", _t.start);
		}
		parseContent(element);
		if (_t.code == ETAG_START) {
			read();
		} else {
			throw new ParseException("ETag start is expected.", _t.start);
		}
		if (_t.code == NAME) {
			if (!text().equals(name)) {
				throw new ParseException("Tags mismatch.", _t.start);
			}
			read();
		} else {
			throw new ParseException("Name is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
		}
		if (_t.code == TAG_END) {
			element = new Element(element, _t.end);
			read();
		} else {
			throw new ParseException("ETag end is expected.", _t.start);
		}
		return element;
	}

	private void parseContent(Element parent) throws Exception {
		if (_t.code == CHAR_DATA) {
			CharData cd = new CharData(_t.start, _t.end, text());
			parent.addChild(cd);
			read();
		}
		while (true) {
			if (_t.code == STAG_START) {
				Element child = parseElement();
				parent.addChild(child);
			} else if (_t.code == ENTITYREF) {
				parent.addChild(_t);
				read();
			} else if (_t.code == CHARREF) {
				parent.addChild(_t);
				read();
			} else if (_t.code == CDSECT) {
				parent.addChild(_t);
				read();
			} else if (_t.code == PI_START) {
				ProcessingInstruction pi = parseProcessingInstruction();
				parent.addChild(pi);
			} else if (_t.code == COMMENT) {
				parent.addChild(_t);
				read();
			} else {
				break;
			}
			if (_t.code == CHAR_DATA) {
				CharData cd = new CharData(_t.start, _t.end, text());
				parent.addChild(cd);
				read();
			}
		}
	}

	private Token read() {
		_t = _lexer.read();
		return _t;
	}

	private Token read(int preferred) {
		_t = _lexer.read(preferred);
		return _t;
	}

	private String text() {
		return _t.text(_contents);
	}

}
