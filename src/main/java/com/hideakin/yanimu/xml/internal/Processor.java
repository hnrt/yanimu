package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.Attribute;
import com.hideakin.yanimu.xml.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.ExternalEntityDefinition;
import com.hideakin.yanimu.xml.ExternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.ExternalIdentifiers;
import com.hideakin.yanimu.xml.InternalEntityDefinition;
import com.hideakin.yanimu.xml.InternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.ParseException;
import com.hideakin.yanimu.xml.ProcessingInstruction;
import com.hideakin.yanimu.xml.QuotedString;
import com.hideakin.yanimu.xml.Token;
import com.hideakin.yanimu.xml.XmlDeclaration;
import com.hideakin.yanimu.xml.ParameterEntityReference;

import static com.hideakin.yanimu.xml.Token.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processor {

	private final byte[] _content;
	private final Deque<Lexer> _lexers = new ArrayDeque<>();
	private Lexer _lexer;
	private final Deque<List<Token>> _ttt = new ArrayDeque<>();
	private List<Token> _tt;
	private boolean _ttEnabled;
	private Token _t;
	private final Map<String, Object> _entities = new HashMap<>();
	private final List<String> _warnings = new ArrayList<>();

	public Processor(byte[] content) {
		_content = content;
	}

	public String[] warnings() {
		return _warnings.toArray(new String[_warnings.size()]);
	}

	public Token[] parse() throws Exception {
		_lexer = new Lexer(_content);
		_tt = new ArrayList<>();
		_ttEnabled = true;
		_t = _lexer.read();
		installPredefinedEntities();
		parseProlog();
		Element root = parseElement(null);
		_tt.add(root);
		Token t;
		while ((t = parseMisc()) != null) {
			_tt.add(t);
		}
		if (_t.code == EOF) {
			return _tt.toArray(new Token[_tt.size()]);
		} else {
			throw new ParseException("Extra data exist.", _t.start);
		}
	}

	private void parseProlog() throws Exception {
		Token t;
		if (_t.code == XML_START) {
			t = parseXmlDeclaration();
			_tt.add(t);
		}
		while ((t = parseMisc()) != null) {
			_tt.add(t);
		}
		if (_t.code == DOCTYPE_DECL) {
			t = parseDoctypeDeclaration();
			_tt.add(t);
			while ((t = parseMisc()) != null) {
				_tt.add(t);
			}
		}
	}

	private XmlDeclaration parseXmlDeclaration() throws Exception {
		String name;
		String version;
		String encoding = null;
		String standalone = null;
		push();
		read();
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = _t.toString();
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
		if (_t.code == ATT_VALUE) {
			version = stripQuote(_t.toString());
			if (!version.matches("1\\.[0-9]+")) {
				throw new ParseException("Malformed version number.", _t.start);
			}
			read();
		} else {
			throw new ParseException("version number is expected.", _t.start);
		}
		name = null;
		if (_t.code == SP) {
			read();
			if (_t.code == NAME) {
				name = _t.toString();
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
					if (_t.code == ATT_VALUE) {
						encoding = stripQuote(_t.toString());
						read();
					} else {
						throw new ParseException("encoding name is expected.", _t.start);
					}
					name = null;
					if (_t.code == SP) {
						read();
						if (_t.code == NAME) {
							name = _t.toString();
						}
					}
				}
			}
			if (name != null) {
				if (name.equals("standalone")) {
					read();
				} else {
					throw new ParseException("standalone is expected.", _t.start);
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
				if (_t.code == ATT_VALUE) {
					standalone = stripQuote(_t.toString());
					if (!standalone.equals("yes") && !standalone.equals("no")) {
						throw new ParseException("Malformed standalone value.", _t.start);
					}
					read();
				} else {
					throw new ParseException("standalone value is expected.", _t.start);
				}
				if (_t.code == SP) {
					read();
				}
			}
		}
		if (_t.code == XML_END) {
			read();
		} else {
			throw new ParseException("?> is expected.", _t.start);
		}
		return new XmlDeclaration(pop(), version, encoding, standalone);
	}

	private Token parseMisc() throws Exception {
		Token t;
		if (_t.code == COMMENT) {
			t = _t;
			suspend();
			read();
			resume();
		} else if (_t.code == PI_START) {
			t = parseProcessingInstruction();
		} else if (_t.code == SP) {
			t = _t;
			suspend();
			read();
			resume();
		} else {
			t = null;
		}
		return t;
	}

	private DocumentTypeDeclaration parseDoctypeDeclaration() throws Exception {
		String name;
		push();
		read();
		if (_t.code == SP) {
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = _t.toString();
			read();
		} else {
			throw new ParseException("Name is expected.", _t.start);
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
		if (_t.code == TAG_END) {
			read();
		} else {
			throw new ParseException("> is expected.", _t.start);
		}
		return new DocumentTypeDeclaration(pop(), name);
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

	private void parseAttlistDecl() throws Exception {
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
			read();
		} else {
			throw new ParseException("White space is expected.", _t.start);
		}
		if (_t.code == SP) {
			read();
			while (_t.code == NAME) {
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
			read(NMTOKEN);
			if (_t.code == SP) {
				read(NMTOKEN);
			}
			if (_t.code == NMTOKEN) {
				read();
			} else {
				throw new ParseException("NMTOKEN is expected.", _t.start);
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
			if (_t.code == ATT_VALUE) {
				read();
			} else {
				throw new ParseException("AttValue is expected.", _t.start);
			}
			break;
		}
	}

	private void parseEntityDecl() throws Exception {
		String key;
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
			key = _t.toString();
			read();
			if (_t.code == SP) {
				read();
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == ENTITY_VALUE) {
				putEntity(key, new InternalEntityDefinition(key, ((QuotedString)_t).innerText));
				read();
			} else if (_t.code == SYSTEM || _t.code == PUBLIC) {
				ExternalIdentifiers extid = parseExternalId();
				String ndata = null;
				if (_t.code == SP) {
					read();
					if (_t.code == NDATA) {
						read();
						if (_t.code == SP) {
							read();
						} else {
							throw new ParseException("White space is expected.", _t.start);
						}
						if (_t.code == NAME) {
							ndata = _t.toString();
							read();
						} else {
							throw new ParseException("Name is expected.", _t.start);
						}
					}
				}
				putEntity(key, new ExternalEntityDefinition(key, extid, ndata));
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
				key = _t.toString();
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
				putEntity(key, new InternalParameterEntityDefinition(key, ((QuotedString)_t).innerText));
				read();
			} else if (_t.code == SYSTEM || _t.code == PUBLIC) {
				ExternalIdentifiers extid = parseExternalId();
				putEntity(key, new ExternalParameterEntityDefinition(key, extid));
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

	private void parseNotationDecl() throws Exception {
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
			parseExternalId(false);
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

	private ExternalIdentifiers parseExternalId() throws Exception {
		return parseExternalId(true);
	}

	private ExternalIdentifiers parseExternalId(boolean systemLiteralIsMandatory) throws Exception {
		String sysValue = null;
		String pubValue = null;
		if (_t.code == SYSTEM) {
			read();
			if (_t.code == SP) {
				read(SYSTEM_LITERAL);
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == SYSTEM_LITERAL) {
				sysValue = ((QuotedString)_t).innerText;
				read();
			} else {
				throw new ParseException("System literal is expected.", _t.start);
			}
			return new ExternalIdentifiers(sysValue);
		} else if (_t.code == PUBLIC) {
			read();
			if (_t.code == SP) {
				read(PUBID_LITERAL);
			} else {
				throw new ParseException("White space is expected.", _t.start);
			}
			if (_t.code == PUBID_LITERAL) {
				pubValue = ((QuotedString)_t).innerText;
				read();
			} else {
				throw new ParseException("Pubid literal is expected.", _t.start);
			}
			if (systemLiteralIsMandatory) {
				if (_t.code == SP) {
					read(SYSTEM_LITERAL);
				} else {
					throw new ParseException("White space is expected.", _t.start);
				}
				if (_t.code == SYSTEM_LITERAL) {
					sysValue = ((QuotedString)_t).innerText;
					read();
				} else {
					throw new ParseException("System literal is expected.", _t.start);
				}
			} else if (_t.code == SP) {
				read(SYSTEM_LITERAL);
				if (_t.code == SYSTEM_LITERAL) {
					read();
				}
			}
			return new ExternalIdentifiers(pubValue, sysValue);
		} else {
			return null;
		}
	}

	private ProcessingInstruction parseProcessingInstruction() throws Exception {
		String name;
		String body;
		push();
		read(NAME);
		if (_t.code == NAME) {
			name = _t.toString();
			if (name.toLowerCase().equals("xml")) {
				throw new ParseException("xml is now allowed for PI.", _t.start);
			} else {
				read();
			}
		} else {
			throw new ParseException("Name is expected.", _t.start);
		}
		if (_t.code == PI_BODY) {
			body = _t.toString();
			read();
		} else {
			body = null;
		}
		if (_t.code == PI_END) {
			read();
		} else {
			throw new ParseException("PI end is expected.", _t.start);
		}
		return new ProcessingInstruction(pop(), name, body);
	}

	private Element parseElement(Element parent) throws Exception {
		String name;
		List<Attribute> attributes = new ArrayList<>();
		if (_t.code == STAG_START) {
			push();
			read();
		} else {
			throw new ParseException("Tag start is expected.", _t.start);
		}
		if (_t.code == NAME) {
			name = _t.toString();
			read();
		} else {
			throw new ParseException("Name is expected.", _t.start);
		}
		while (_t.code == SP) {
			read();
			String key;
			if (_t.code == NAME) {
				key = _t.toString();
				push();
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
			if (_t.code == ATT_VALUE) {
				QuotedString qs = (QuotedString)_t;
				String value = translate(qs.innerText);
				read();
				Attribute attribute = new Attribute(pop(), key, value);
				attributes.add(attribute);
				_tt.add(attribute);
			} else {
				throw new ParseException("Attribute value is expected.", _t.start);
			}
		}
		if (_t.code == EETAG_END) {
			read();
			return new Element(pop(), name, attributes, parent);
		}
		if (_t.code == TAG_END) {
			read();
		} else {
			throw new ParseException("Tag end is expected.", _t.start);
		}
		Element element = new Element(pop(), name, attributes, parent);
		List<Token> children = parseContent(element);
		if (_t.code == ETAG_START) {
			push();
			read();
		} else {
			throw new ParseException("ETag start is expected.", _t.start);
		}
		if (_t.code == NAME) {
			if (!_t.toString().equals(name)) {
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
			read();
		} else {
			throw new ParseException("ETag end is expected.", _t.start);
		}
		return new Element(element, children, pop());
	}

	private List<Token> parseContent(Element parent) throws Exception {
		push();
		if (_t.code == CHAR_DATA) {
			read();
		}
		while (true) {
			if (_t.code == STAG_START) {
				_tt.add(parseElement(parent));
			} else if (_t.code == ENTITY_REF) {
				read();
			} else if (_t.code == CHAR_REF) {
				read();
			} else if (_t.code == CD_SECT) {
				read();
			} else if (_t.code == PI_START) {
				_tt.add(parseProcessingInstruction());
			} else if (_t.code == COMMENT) {
				read();
			} else {
				break;
			}
			if (_t.code == CHAR_DATA) {
				read();
			}
		}
		return pop();
	}

	private List<Token> push() {
		_ttt.push(_tt);
		_tt = new ArrayList<>();
		return _tt;
	}

	private List<Token> pop() {
		List<Token> last = _tt;
		_tt = _ttt.pop();
		return last;
	}

	private void suspend() {
		_ttEnabled = false;
	}

	private void resume() {
		_ttEnabled = true;
	}

	private Token read() {
		return read(0);
	}

	private Token read(int preferred) {
		if (_ttEnabled) {
			_tt.add(_t);
		}
		boolean sp = _t.code == SP;
		_t = _lexer.read(preferred);
		while (_t.code == EOF && !_lexers.isEmpty()) {
			_lexer = _lexers.pop();
			_t = _lexer.read(preferred);
			if (sp && _t.code == SP) {
				_t = _lexer.read(preferred);
			}
		}
		if (_t.code == PEREFERENCE) {
			int mode = _lexer.mode();
			if (Lexer.MODE_DOCTYPE <= mode && mode <= Lexer.MODE_DOCTYPE_NOTATION) {
				String value = getParameterEntity(((ParameterEntityReference)_t).name);
				if (value != null) {
					_lexers.push(_lexer);
					_lexer = new Lexer(value, mode);
					Token t = _lexer.read(preferred);
					if (sp && t.code == SP) {
						t = _lexer.read(preferred);
					}
					if (t.code == EOF) {
						_lexer = _lexers.pop();
					} else {
						_t = t;
					}
				}
			}
		}
		return _t;
	}

	private void installPredefinedEntities() {
		putEntity("lt", new InternalEntityDefinition("lt", translate("&#38;#60;")));
		putEntity("gt", new InternalEntityDefinition("gt", translate("&#62;")));
		putEntity("amp", new InternalEntityDefinition("amp", translate("&#38;#38;")));
		putEntity("apos", new InternalEntityDefinition("apos", translate("&#39;")));
		putEntity("quot", new InternalEntityDefinition("quot", translate("&#34;")));
	}

	private String getEntity(String key) {
		Object value = _entities.get(key);
		if (value instanceof InternalEntityDefinition ie) {
			return ie.value;
		} else if (value instanceof ExternalEntityDefinition) {
			addWarning("External entity reference %s cannot be used as it is not supported.", key);
			return null;
		} else {
			return null;
		}
	}

	private void putEntity(String key, Object value) {
		if ((value instanceof InternalParameterEntityDefinition) || (value instanceof ExternalParameterEntityDefinition)) {
			String peKey = "%" + key;
			if (_entities.containsKey(peKey)) {
				addWarning("Parameter entity reference %s is declared multiple times.", key);
			} else {
				_entities.put(peKey, value);
			}
		} else if (_entities.containsKey(key)) {
			addWarning("Entity reference %s is declared multiple times.", key);
		} else {
			_entities.put(key, value);
		}
	}

	private String getParameterEntity(String key) {
		Object value = _entities.get("%" + key);
		if (value instanceof InternalParameterEntityDefinition ipe) {
			return ipe.value;
		} else if (value instanceof ExternalParameterEntityDefinition) {
			addWarning("External parameter entity reference %s cannot be used as it is not supported.", key);
			return null;
		} else {
			return null;
		}
	}

	private static String stripQuote(String text) {
		return text != null && text.length() >= 2 ? text.substring(1, text.length() - 1) : null;
	}

	private String translate(String text) {
		if (text == null) {
			return null;
		}
		int replaced = 0;
		StringBuilder buf = new StringBuilder();
		int n = text.length();
		int i = 0;
		int c = i < n ? text.codePointAt(i++) : -1;
		while (c >= 0) {
			if (c == '&') {
				int h = i;
				c = i < n ? text.codePointAt(i++) : -1;
				if (c == '#') {
					boolean successful = false;
					int d = 0;
					c = i < n ? text.codePointAt(i++) : -1;
					if (c == 'x') {
						c = i < n ? text.codePointAt(i++) : -1;
						if (Lexer.isHexadecimal(c)) {
							do {
								d = d * 16 + (c < 'A' ? c - '0' : c < 'a' ? c - 'A' + 10 : c - 'a' + 10);
								c = i < n ? text.codePointAt(i++) : -1;
							} while (Lexer.isHexadecimal(c));
							if (c == ';') {
								successful = true;
							}
						}
					} else if (Lexer.isDigit(c)) {
						do {
							d = d * 10 + c - '0';
							c = i < n ? text.codePointAt(i++) : -1;
						} while (Lexer.isDigit(c));
						if (c == ';') {
							successful = true;
						}
					}
					if (successful) {
						buf.appendCodePoint(d);
						replaced++;
					} else {
						buf.append('&');
						i = h;
					}
				} else if (Lexer.isNameStartChar(c)) {
					StringBuilder buf2 = new StringBuilder();
					buf2.appendCodePoint(c);
					c = i < n ? text.codePointAt(i++) : -1;
					while (Lexer.isNameChar(c)) {
						buf2.appendCodePoint(c);
						c = i < n ? text.codePointAt(i++) : -1;
					}
					if (c == ';') {
						String key = buf2.toString();
						String value = getEntity(key);
						if (value != null) {
							buf.append(value);
							replaced++;
						} else {
							buf.append('&');
							buf.append(key);
							buf.append(';');
						}
					} else {
						buf.append('&');
						i = h;
					}
				} else {
					buf.append('&');
					i = h;
				}
			} else {
				buf.appendCodePoint(c);
			}
			c = i < n ? text.codePointAt(i++) : -1;
		}
		if (replaced > 0) {
			return translate(buf.toString());
		} else {
			return text;
		}
	}

	private void addWarning(String format, Object...args) {
		String message = String.format(format, args);
		if (!_warnings.contains(message)) {
			_warnings.add(message);
		}
	}

}
