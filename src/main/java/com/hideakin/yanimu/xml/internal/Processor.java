package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.Attribute;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.EntityRef;
import com.hideakin.yanimu.xml.ParseException;
import com.hideakin.yanimu.xml.ProcessingInstruction;
import com.hideakin.yanimu.xml.QuotedString;
import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.XmlDeclaration;
import com.hideakin.yanimu.xml.doctype.AttributeDefault;
import com.hideakin.yanimu.xml.doctype.AttributeDefinition;
import com.hideakin.yanimu.xml.doctype.AttributeListDeclaration;
import com.hideakin.yanimu.xml.doctype.ContentChoice;
import com.hideakin.yanimu.xml.doctype.ContentParticle;
import com.hideakin.yanimu.xml.doctype.ContentSequence;
import com.hideakin.yanimu.xml.doctype.ContentSpec;
import com.hideakin.yanimu.xml.doctype.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.ElementTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.EntityDeclaration;
import com.hideakin.yanimu.xml.doctype.EnumerationType;
import com.hideakin.yanimu.xml.doctype.ExternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.ExternalIdentifiers;
import com.hideakin.yanimu.xml.doctype.ExternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.NotationDeclaration;
import com.hideakin.yanimu.xml.doctype.NotationType;
import com.hideakin.yanimu.xml.ParameterEntityReference;

import static com.hideakin.yanimu.xml.Node.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Processor {

	private final byte[] _content;
	private final Deque<Lexer> _lexers = new ArrayDeque<>();
	private Lexer _lexer;
	private final Deque<List<Node>> _nnn = new ArrayDeque<>();
	private List<Node> _nn;
	private Node _n;
	private final EntityManager _em;
	private final TextMessageManager _tm;

	public Processor(byte[] content) {
		_content = content;
		_tm = new TextMessageManager();
		_em = new EntityManager(_tm);
	}

	public String[] warnings() {
		return _tm.warnings();
	}

	public List<Node> parse() throws Exception {
		_lexer = new Lexer(_content);
		_nn = new ArrayList<>();
		_n = _lexer.read();
		parseProlog();
		parseElement(null);
		while (parseMisc()) {
			continue;
		}
		if (_n.type == EOF) {
			return _nn;
		} else {
			throw new ParseException("Extra data exist.", _n.start);
		}
	}

	private void parseProlog() throws Exception {
		if (_n.type == XML_START) {
			parseXmlDeclaration();
		}
		while (parseMisc()) {
			continue;
		}
		if (_n.type == DOCTYPE_DECL_START) {
			parseDoctypeDeclaration();
			while (parseMisc()) {
				continue;
			}
		}
	}

	private void parseXmlDeclaration() throws Exception {
		String name;
		String version;
		String encoding = null;
		String standalone = null;
		push();
		read();
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == NAME) {
			name = _n.toString();
		} else {
			throw new ParseException("version is expected.", _n.start);
		}
		if (name.equals("version")) {
			read();
		} else {
			throw new ParseException("version is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == EQ) {
			read();
		} else {
			throw new ParseException("Equal sign is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == ATT_VALUE) {
			version = ((QuotedString)_n).innerText;
			if (!version.matches("1\\.[0-9]+")) {
				throw new ParseException("Malformed version number.", _n.start);
			}
			read();
		} else {
			throw new ParseException("version number is expected.", _n.start);
		}
		name = null;
		if (_n.type == S) {
			read();
			if (_n.type == NAME) {
				name = _n.toString();
				if (name.equals("encoding")) {
					read();
					if (_n.type == S) {
						read();
					}
					if (_n.type == EQ) {
						read();
					} else {
						throw new ParseException("Equal sign is expected.", _n.start);
					}
					if (_n.type == S) {
						read();
					}
					if (_n.type == ATT_VALUE) {
						encoding = ((QuotedString)_n).innerText;
						read();
					} else {
						throw new ParseException("encoding name is expected.", _n.start);
					}
					name = null;
					if (_n.type == S) {
						read();
						if (_n.type == NAME) {
							name = _n.toString();
						}
					}
				}
			}
			if (name != null) {
				if (name.equals("standalone")) {
					read();
				} else {
					throw new ParseException("standalone is expected.", _n.start);
				}
				if (_n.type == S) {
					read();
				}
				if (_n.type == EQ) {
					read();
				} else {
					throw new ParseException("Equal sign is expected.", _n.start);
				}
				if (_n.type == S) {
					read();
				}
				if (_n.type == ATT_VALUE) {
					standalone = ((QuotedString)_n).innerText;
					if (!standalone.equals("yes") && !standalone.equals("no")) {
						throw new ParseException("Malformed standalone value.", _n.start);
					}
					read();
				} else {
					throw new ParseException("standalone value is expected.", _n.start);
				}
				if (_n.type == S) {
					read();
				}
			}
		}
		if (_n.type == XML_END) {
			read();
		} else {
			throw new ParseException("?> is expected.", _n.start);
		}
		XmlDeclaration t = new XmlDeclaration(pop(), version, encoding, standalone); 
		_nn.add(t);
	}

	private boolean parseMisc() throws Exception {
		if (_n.type == COMMENT) {
			read();
		} else if (_n.type == PI_START) {
			parseProcessingInstruction();
		} else if (_n.type == S) {
			read();
		} else {
			return false;
		}
		return true;
	}

	private void parseDoctypeDeclaration() throws Exception {
		String name;
		ExternalIdentifiers extid = null;
		List<Object> declarations = new ArrayList<>();
		push();
		read();
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException("Name is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
			if (_n.type == SYSTEM || _n.type == PUBLIC) {
				extid = parseExternalId();
				if (_n.type == S) {
					read();
				}
			}
		}
		if (_n.type == '[') {
			read();
			while (_n.type != ']') {
				if (_n.type == ELEMENT_DECL_START) {
					ElementTypeDeclaration etd = parseElementDecl();
					declarations.add(etd);
				} else if (_n.type == ATTLIST_DECL_START) {
					AttributeListDeclaration ald = parseAttlistDecl();
					declarations.add(ald);
				} else if (_n.type == ENTITY_DECL_START) {
					EntityDeclaration ed = parseEntityDecl();
					declarations.add(ed);
				} else if (_n.type == NOTATION_DECL_START) {
					NotationDeclaration nd = parseNotationDecl();
					declarations.add(nd);
				} else if (_n.type == PI_START) {
					parseProcessingInstruction();
				} else if (_n.type == COMMENT) {
					read();
				} else if (_n.type == S) {
					read();
				} else {
					throw new ParseException("Internal subset is expected.", _n.start);
				}
			}
			read();
			if (_n.type == S) {
				read();
			}
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException("> is expected.", _n.start);
		}
		DocumentTypeDeclaration dtd = new DocumentTypeDeclaration(pop(), name, extid, declarations);
		_nn.add(dtd);
	}

	private ElementTypeDeclaration parseElementDecl() throws Exception {
		String name;
		push();
		read();
		if (_n.type == S) {
			read(NAME);
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException("Name is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		ContentSpec cs = parseContentSpec();
		if (_n.type == S) {
			read();
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException("End of element declaration is expected.", _n.start);
		}
		ElementTypeDeclaration etd = new ElementTypeDeclaration(pop(), name, cs);
		_nn.add(etd);
		return etd;
	}

	private ContentSpec parseContentSpec() throws Exception {
		if (_n.type == EMPTY) {
			read();
			return new ContentSpec(EMPTY);
		} else if (_n.type == ANY) {
			read();
			return new ContentSpec(ANY);
		} else if (_n.type == '(') {
			return parseMixedOrChildren();
		} else {
			throw new ParseException("EMPTY, ANY or ( is expected.", _n.start);
		}
	}

	private ContentSpec parseMixedOrChildren() throws Exception {
		ContentSpec cs;
		if (_n.type == '(') {
			read();
		} else {
			throw new ParseException("( is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == PCDATA) {
			read();
			if (_n.type == S) {
				read();
			}
			if (_n.type == ')') {
				read();
				cs = new ContentSpec(PCDATA);
			} else if (_n.type == PCDATA_END) {
				read();
				cs = new ContentSpec(Arrays.asList("#PCDATA"));
			} else if (_n.type == '|') {
				List<String> choiceList = new ArrayList<>();
				choiceList.add("#PCDATA");
				do {
					read();
					if (_n.type == S) {
						read();
					}
					if (_n.type == NAME) {
						choiceList.add(_n.toString());
						read();
					} else {
						throw new ParseException("Name is expected.", _n.start);
					}
					if (_n.type == S) {
						read();
					}
				} while (_n.type == '|');
				if (_n.type == PCDATA_END) {
					read();
				} else {
					throw new ParseException(")* is expected.", _n.start);
				}
				cs = new ContentSpec(choiceList);
			} else {
				throw new ParseException(") or )* is expected.", _n.start);
			}
		} else {
			ContentParticle cp = parseChildren();
			cs = new ContentSpec(cp);
		}
		return cs;
	}

	private ContentParticle parseChildren() throws Exception {
		Object target = parseChoiceOrSequence();
		if (_n.type == '?') {
			read();
			return new ContentParticle(target, '?');
		} else if (_n.type == '*') {
			read();
			return new ContentParticle(target, '*');
		} else if (_n.type == '+') {
			read();
			return new ContentParticle(target, '+');
		} else {
			return new ContentParticle(target);
		}
	}

	private Object parseChoiceOrSequence() throws Exception {
		Object choiceOrSequence;
		if (_n.type == '(') {
			read();
		} else {
			throw new ParseException("( is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		ContentParticle cp = parseCP();
		if (_n.type == S) {
			read();
		}
		if (_n.type == '|') {
			ContentChoice choice = new ContentChoice(cp);
			do {
				read();
				if (_n.type == S) {
					read();
				}
				cp = parseCP();
				choice.add(cp);
				if (_n.type == S) {
					read();
				}
			} while (_n.type == '|');
			choiceOrSequence = choice;
		} else if (_n.type == ',') {
			ContentSequence seq = new ContentSequence(cp);
			do {
				read();
				if (_n.type == S) {
					read();
				}
				cp = parseCP();
				seq.add(cp);
				if (_n.type == S) {
					read();
				}
			} while (_n.type == ',');
			choiceOrSequence = seq;
		} else {
			choiceOrSequence = new ContentSequence(cp);
		}
		if (_n.type == ')') {
			read();
		} else {
			throw new ParseException(") is expected.", _n.start);
		}
		return choiceOrSequence;
	}

	private ContentParticle parseCP() throws Exception {
		Object target;
		if (_n.type == NAME) {
			target = _n.toString();
			read();
		} else {
			target = parseChoiceOrSequence();
		}
		if (_n.type == '?') {
			read();
			return new ContentParticle(target, '?');
		} else if (_n.type == '*') {
			read();
			return new ContentParticle(target, '*');
		} else if (_n.type == '+') {
			read();
			return new ContentParticle(target, '+');
		} else {
			return new ContentParticle(target);
		}
	}

	private AttributeListDeclaration parseAttlistDecl() throws Exception {
		String name;
		List<AttributeDefinition> defList = new ArrayList<>();
		push();
		read();
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
			while (_n.type == NAME) {
				String key = _n.toString();
				read();
				if (_n.type == S) {
					read();
				} else {
					throw new ParseException("White space is expected.", _n.start);
				}
				Object type = parseAttType();
				if (_n.type == S) {
					read();
				} else {
					throw new ParseException("White space is expected.", _n.start);
				}
				AttributeDefault value = parseDefaultDecl();
				AttributeDefinition adef = new AttributeDefinition(key, type, value);
				defList.add(adef);
				if (_n.type == S) {
					read();
				} else {
					break;
				}
			}
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException("End of attlist declaration is expected.", _n.start);
		}
		AttributeListDeclaration ald = new AttributeListDeclaration(pop(), name, defList);
		_nn.add(ald);
		return ald;
	}

	private Object parseAttType() throws Exception {
		Object type;
		switch (_n.type) {
		case TYPE_CDATA:
		case TYPE_ID:
		case TYPE_IDREF:
		case TYPE_IDREFS:
		case TYPE_ENTITY:
		case TYPE_ENTITIES:
		case TYPE_NMTOKEN:
		case TYPE_NMTOKENS:
			type = _n.toString();
			read();
			break;
		case TYPE_NOTATION:
			type = parseNotationType();
			break;
		case '(':
			type = parseEnumeration();
			break;
		default:
			throw new ParseException("AttType is expected.", _n.start);
		}
		return type;
	}

	private NotationType parseNotationType() throws Exception {
		NotationType nt;
		if (_n.type == TYPE_NOTATION) {
			read();
		} else {
			throw new ParseException("NOTATION is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == '(') {
			read();
		} else {
			throw new ParseException("( is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == NAME) {
			nt = new NotationType(_n.toString());
			read();
		} else {
			throw new ParseException("Name is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		while (_n.type == '|') {
			read();
			if (_n.type == S) {
				read();
			}
			if (_n.type == NAME) {
				nt.add(_n.toString());
				read();
			} else {
				throw new ParseException("Name is expected.", _n.start);
			}
			if (_n.type == S) {
				read();
			}
		}
		if (_n.type == ')') {
			read();
		} else {
			throw new ParseException(") is expected.", _n.start);
		}
		return nt;
	}

	private EnumerationType parseEnumeration() throws Exception {
		EnumerationType et;
		if (_n.type == '(') {
			read(NMTOKEN);
		} else {
			throw new ParseException("( is expected.", _n.start);
		}
		if (_n.type == S) {
			read(NMTOKEN);
		}
		if (_n.type == NMTOKEN) {
			et = new EnumerationType(_n.toString());
			read();
		} else {
			throw new ParseException("Nmtoken is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		while (_n.type == '|') {
			read(NMTOKEN);
			if (_n.type == S) {
				read(NMTOKEN);
			}
			if (_n.type == NMTOKEN) {
				et.add(_n.toString());
				read();
			} else {
				throw new ParseException("NMTOKEN is expected.", _n.start);
			}
			if (_n.type == S) {
				read();
			}
		}
		if (_n.type == ')') {
			read();
		} else {
			throw new ParseException(") is expected.", _n.start);
		}
		return et;
	}

	private AttributeDefault parseDefaultDecl() throws Exception {
		AttributeDefault ad;
		String value;
		switch (_n.type) {
		case REQUIRED:
			read();
			ad = new AttributeDefault(AttributeDefault.DEFAULT_REQUIRED);
			break;
		case IMPLIED:
			read();
			ad = new AttributeDefault(AttributeDefault.DEFAULT_IMPLIED);
			break;
		case FIXED:
			read();
			if (_n.type == S) {
				read();
			} else {
				throw new ParseException("white space is expected.", _n.start);
			}
			if (_n.type == ATT_VALUE) {
				value = ((QuotedString)_n).innerText;
				read();
			} else {
				throw new ParseException("AttValue is expected.", _n.start);
			}
			ad = new AttributeDefault(AttributeDefault.DEFAULT_FIXED, value);
			break;
		default:
			if (_n.type == ATT_VALUE) {
				value = ((QuotedString)_n).innerText;
				read();
			} else {
				throw new ParseException("AttValue is expected.", _n.start);
			}
			ad = new AttributeDefault(value);
			break;
		}
		return ad;
	}

	private EntityDeclaration parseEntityDecl() throws Exception {
		String key;
		Object definition;
		push();
		read();
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == NAME) {
			key = _n.toString();
			read();
			if (_n.type == S) {
				read();
			} else {
				throw new ParseException("White space is expected.", _n.start);
			}
			if (_n.type == ENTITY_VALUE) {
				definition = new InternalEntityDefinition(key, ((QuotedString)_n).innerText);
				read();
			} else if (_n.type == SYSTEM || _n.type == PUBLIC) {
				ExternalIdentifiers extid = parseExternalId();
				String ndata = null;
				if (_n.type == S) {
					read();
					if (_n.type == NDATA) {
						read();
						if (_n.type == S) {
							read();
						} else {
							throw new ParseException("White space is expected.", _n.start);
						}
						if (_n.type == NAME) {
							ndata = _n.toString();
							read();
						} else {
							throw new ParseException("Name is expected.", _n.start);
						}
					}
				}
				definition = new ExternalEntityDefinition(key, extid, ndata);
			} else {
				throw new ParseException("Entity value or external ID is expected.", _n.start);
			}
		} else if (_n.type == '%') {
			read();
			if (_n.type == S) {
				read();
			} else {
				throw new ParseException("White space is expected.", _n.start);
			}
			if (_n.type == NAME) {
				key = _n.toString();
				read();
			} else {
				throw new ParseException("Name is expected.", _n.start);
			}
			if (_n.type == S) {
				read();
			} else {
				throw new ParseException("White space is expected.", _n.start);
			}
			if (_n.type == ENTITY_VALUE) {
				definition = new InternalParameterEntityDefinition(key, ((QuotedString)_n).innerText);
				read();
			} else if (_n.type == SYSTEM || _n.type == PUBLIC) {
				ExternalIdentifiers extid = parseExternalId();
				definition = new ExternalParameterEntityDefinition(key, extid);
			} else {
				throw new ParseException("Entity value or external ID is expected.", _n.start);
			}
		} else {
			throw new ParseException("Name or % is expected.", _n.start);
		}
		_em.putEntity(key, definition);
		if (_n.type == S) {
			read();
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException("End of entity declaration is expected.", _n.start);
		}
		EntityDeclaration ed = new EntityDeclaration(pop(), definition);
		_nn.add(ed);
		return ed;
	}

	private NotationDeclaration parseNotationDecl() throws Exception {
		String name;
		ExternalIdentifiers extid;
		push();
		read();
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException("Name is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException("White space is expected.", _n.start);
		}
		if (_n.type == SYSTEM || _n.type == PUBLIC) {
			extid = parseExternalId(false);
		} else {
			throw new ParseException("External ID or Public ID is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException("End of notation declaration is expected.", _n.start);
		}
		NotationDeclaration nd = new NotationDeclaration(pop(), name, extid);
		_nn.add(nd);
		return nd;
	}

	private ExternalIdentifiers parseExternalId() throws Exception {
		return parseExternalId(true);
	}

	private ExternalIdentifiers parseExternalId(boolean systemLiteralIsMandatory) throws Exception {
		String sysValue = null;
		String pubValue = null;
		if (_n.type == SYSTEM) {
			read();
			if (_n.type == S) {
				read(SYSTEM_LITERAL);
			} else {
				throw new ParseException("White space is expected.", _n.start);
			}
			if (_n.type == SYSTEM_LITERAL) {
				sysValue = ((QuotedString)_n).innerText;
				read();
			} else {
				throw new ParseException("System literal is expected.", _n.start);
			}
			return new ExternalIdentifiers(sysValue);
		} else if (_n.type == PUBLIC) {
			read();
			if (_n.type == S) {
				read(PUBID_LITERAL);
			} else {
				throw new ParseException("White space is expected.", _n.start);
			}
			if (_n.type == PUBID_LITERAL) {
				pubValue = ((QuotedString)_n).innerText;
				read();
			} else {
				throw new ParseException("Pubid literal is expected.", _n.start);
			}
			if (systemLiteralIsMandatory) {
				if (_n.type == S) {
					read(SYSTEM_LITERAL);
				} else {
					throw new ParseException("White space is expected.", _n.start);
				}
				if (_n.type == SYSTEM_LITERAL) {
					sysValue = ((QuotedString)_n).innerText;
					read();
				} else {
					throw new ParseException("System literal is expected.", _n.start);
				}
			} else if (_n.type == S) {
				read(SYSTEM_LITERAL);
				if (_n.type == SYSTEM_LITERAL) {
					read();
				}
			}
			return new ExternalIdentifiers(pubValue, sysValue);
		} else {
			return null;
		}
	}

	private void parseProcessingInstruction() throws Exception {
		String name;
		String body;
		push();
		read(NAME);
		if (_n.type == NAME) {
			name = _n.toString();
			if (name.toLowerCase().equals("xml")) {
				throw new ParseException("xml is now allowed for PI.", _n.start);
			} else {
				read();
			}
		} else {
			throw new ParseException("Name is expected.", _n.start);
		}
		if (_n.type == PI_BODY) {
			body = _n.toString();
			read();
		} else {
			body = null;
		}
		if (_n.type == PI_END) {
			read();
		} else {
			throw new ParseException("PI end is expected.", _n.start);
		}
		ProcessingInstruction pi = new ProcessingInstruction(pop(), name, body); 
		_nn.add(pi);
	}

	private void parseElement(Element parent) throws Exception {
		String name;
		List<Attribute> attributes = new ArrayList<>();
		if (_n.type == STAG_START) {
			push();
			read();
		} else {
			throw new ParseException("Tag start is expected.", _n.start);
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException("Name is expected.", _n.start);
		}
		while (_n.type == S) {
			read();
			String key;
			if (_n.type == NAME) {
				key = _n.toString();
				push();
				read();
			} else {
				break;
			}
			if (_n.type == S) {
				read();
			}
			if (_n.type == EQ) {
				read();
			} else {
				throw new ParseException("Equal sign is expected.", _n.start);
			}
			if (_n.type == S) {
				read();
			}
			if (_n.type == ATT_VALUE) {
				QuotedString qs = (QuotedString)_n;
				String value = _em.translate(qs.innerText);
				read();
				Attribute attribute = new Attribute(pop(), key, value);
				attributes.add(attribute);
				_nn.add(attribute);
			} else {
				throw new ParseException("Attribute value is expected.", _n.start);
			}
		}
		if (_n.type == EETAG_END) {
			read();
			Element t = new Element(pop(), name, attributes, parent);
			_nn.add(t);
			return;
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException("Tag end is expected.", _n.start);
		}
		Element element = new Element(pop(), name, attributes, parent);
		List<Node> children = parseContent(element);
		if (_n.type == ETAG_START) {
			push();
			read();
		} else {
			throw new ParseException("ETag start is expected.", _n.start);
		}
		if (_n.type == NAME) {
			if (!_n.toString().equals(name)) {
				throw new ParseException("Tags mismatch.", _n.start);
			}
			read();
		} else {
			throw new ParseException("Name is expected.", _n.start);
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException("ETag end is expected.", _n.start);
		}
		Element t = new Element(element, children, pop());
		_nn.add(t);
	}

	private List<Node> parseContent(Element parent) throws Exception {
		push();
		if (_n.type == CHAR_DATA) {
			read();
		}
		while (true) {
			if (_n.type == STAG_START) {
				parseElement(parent);
			} else if (_n.type == ENTITY_REF) {
				EntityRef er = (EntityRef)_n;
				String value = _em.getEntity(er.name);
				_n = new EntityRef(er, value != null ? value : er.toString());
				read();
			} else if (_n.type == CHAR_REF) {
				read();
			} else if (_n.type == CD_SECT) {
				read();
			} else if (_n.type == PI_START) {
				parseProcessingInstruction();
			} else if (_n.type == COMMENT) {
				read();
			} else {
				break;
			}
			if (_n.type == CHAR_DATA) {
				read();
			}
		}
		return pop();
	}

	private List<Node> push() {
		_nnn.push(_nn);
		_nn = new ArrayList<>();
		return _nn;
	}

	private List<Node> pop() {
		List<Node> last = _nn;
		_nn = _nnn.pop();
		return last;
	}

	private Node read() {
		return read(0);
	}

	private Node read(int preferred) {
		if (_lexers.isEmpty()) {
			_nn.add(_n);
		}
		boolean sp = _n.type == S;
		_n = _lexer.read(preferred);
		while (true) {
			if (_n.type == EOF) {
				if (_lexers.isEmpty()) {
					break;
				}
				_lexer = _lexers.pop();
				_n = _lexer.read(preferred);
				if (sp && _n.type == S) {
					if (_lexers.isEmpty()) {
						_nn.add(_n);
					}
					_n = _lexer.read(preferred);
				}
			} else if (_n.type == PEREFERENCE) {
				int mode = _lexer.mode();
				if (Lexer.MODE_DOCTYPE <= mode && mode <= Lexer.MODE_DOCTYPE_NOTATION) {
					String value = _em.getParameterEntity(((ParameterEntityReference)_n).name);
					if (value != null) {
						if (_lexers.isEmpty()) {
							_nn.add(_n);
						}
						int offset = _n.start;
						_lexers.push(_lexer);
						_lexer = new Lexer(value, mode, offset);
						_n = _lexer.read(preferred);
						if (sp && _n.type == S) {
							_lexer.setOffset(offset);
							_n = _lexer.read(preferred);
						}
					} else {
						break;
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}
		return _n;
	}

}
