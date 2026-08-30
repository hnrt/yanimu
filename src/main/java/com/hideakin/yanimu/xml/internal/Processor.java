package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.Attribute;
import com.hideakin.yanimu.xml.Content;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.EmptyElementTag;
import com.hideakin.yanimu.xml.EndTag;
import com.hideakin.yanimu.xml.EntityMap;
import com.hideakin.yanimu.xml.EntityRef;
import com.hideakin.yanimu.xml.ParseException;
import com.hideakin.yanimu.xml.ParseResult;
import com.hideakin.yanimu.xml.ProcessingInstruction;
import com.hideakin.yanimu.xml.QuotedString;
import com.hideakin.yanimu.xml.StartTag;
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

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

//
// Notes:
// HTTP(S) access to external DTDs are not supported.
// XML catalog is not supported.
//
public class Processor {

	private final byte[] _content;
	private final Deque<Lexer> _lexerStack = new ArrayDeque<>();
	private Lexer _lexer;
	private final Deque<List<Node>> _nnn = new ArrayDeque<>();
	private List<Node> _nn;
	private Node _n;
	private final EntityMap _entityMap;
	private final ParseResultWriter _result;
	private final List<Object> _markupDeclarationList;

	public Processor(byte[] content) {
		_content = content;
		_result = ParseResultWriter.of(new ParseResult());
		_entityMap = new EntityMap();
		_markupDeclarationList = new ArrayList<>();
	}

	public Processor(byte[] content, ParseResult result) {
		_content = content;
		_result = ParseResultWriter.of(result);
		_entityMap = new EntityMap();
		_markupDeclarationList = new ArrayList<>();
	}

	private Processor(byte[] content, EntityMap entityMap, ParseResultWriter result, List<Object> markupDeclarationList) {
		_content = content;
		_result = result;
		_entityMap = entityMap;
		_markupDeclarationList = markupDeclarationList;
	}

	public List<Node> parse() throws Exception {
		_lexer = new Lexer(_content);
		DebugHelper.printLexerContext(_lexer.getContext());
		_nn = new ArrayList<>();
		_n = _lexer.read();
		DebugHelper.print(_n);
		parseProlog();
		parseElement(null);
		while (parseMisc()) {
			continue;
		}
		if (_n.type == EOF) {
			return _nn;
		} else {
			throw new ParseException(_result.error(offset(_n), "Extra data exist."));
		}
	}

	private void parseProlog() throws Exception {
		if (_n.type == PI_START) {
			push();
			read(PI_TARGET);
			switch (_n.type) {
			case NAME:
				String name = _n.toString();
				if (name.equals("xml")) {
					_lexer.setContext(LexerContext.XML);
					read();
					parseXmlDeclaration();
				} else {
					throw new ParseException(_result.error(offset(_n), "Either xml or PI target is expected."));
				}
				break;
			case PI_TARGET:
				String target = _n.toString();
				read();
				String body = null;
				if (_n.type == S) {
					read();
					if (_n.type == PI_BODY) {
						body = _n.toString();
						read();
					}
				}
				if (_n.type == PI_END) {
					read();
				} else {
					throw new ParseException(_result.error(offset(_n), "PI end is expected."));
				}
				ProcessingInstruction pi = new ProcessingInstruction(pop(), target, body); 
				store(pi);
				break;
			default:
				throw new ParseException(_result.error(offset(_n), "Either xml or PI target is expected."));
			}
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
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == NAME) {
			name = _n.toString();
		} else {
			throw new ParseException(_result.error(offset(_n), "version is expected."));
		}
		if (name.equals("version")) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "version is expected."));
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == EQ) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Equal sign is expected."));
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == ATT_VALUE) {
			version = ((QuotedString)_n).innerText();
			if (!version.matches("1\\.[0-9]+")) {
				throw new ParseException(_result.error(offset(_n), "Malformed version number."));
			}
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "version number is expected."));
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
						throw new ParseException(_result.error(offset(_n), "Equal sign is expected."));
					}
					if (_n.type == S) {
						read();
					}
					if (_n.type == ATT_VALUE) {
						encoding = ((QuotedString)_n).innerText();
						read();
					} else {
						throw new ParseException(_result.error(offset(_n), "encoding name is expected."));
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
					throw new ParseException(_result.error(offset(_n), "standalone is expected."));
				}
				if (_n.type == S) {
					read();
				}
				if (_n.type == EQ) {
					read();
				} else {
					throw new ParseException(_result.error(offset(_n), "Equal sign is expected."));
				}
				if (_n.type == S) {
					read();
				}
				if (_n.type == ATT_VALUE) {
					standalone = ((QuotedString)_n).innerText();
					if (!standalone.equals("yes") && !standalone.equals("no")) {
						throw new ParseException(_result.error(offset(_n), "Malformed standalone value."));
					}
					read();
				} else {
					throw new ParseException(_result.error(offset(_n), "standalone value is expected."));
				}
				if (_n.type == S) {
					read();
				}
			}
		}
		if (_n.type == XML_END) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "?> is expected."));
		}
		XmlDeclaration t = new XmlDeclaration(pop(), version, encoding, standalone); 
		store(t);
	}

	private boolean parseMisc() throws Exception {
		switch (_n.type) {
		case S:
			read();
			return true;
		case COMMENT:
			read();
			return true;
		case PI_START:
			read();
			parseProcessingInstruction();
			return true;
		default:
			return false;
		}
	}

	private void parseDoctypeDeclaration() throws Exception {
		String name;
		ExternalIdentifiers extid = null;
		push();
		read();
		if (_n.type == S) {
			read(NAME);
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Name is expected."));
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
		if (extid != null) {
			processExternalDocument(name, extid);
		}
		if (_n.type == '[') {
			read();
			while (_n.type != ']') {
				switch (_n.type) {
				case ELEMENT_DECL_START:
					ElementTypeDeclaration etd = parseElementDecl();
					_markupDeclarationList.add(etd);
					break;
				case ATTLIST_DECL_START:
					AttributeListDeclaration ald = parseAttlistDecl();
					_markupDeclarationList.add(ald);
					break;
				case ENTITY_DECL_START:
					EntityDeclaration ed = parseEntityDecl();
					_markupDeclarationList.add(ed);
					break;
				case NOTATION_DECL_START:
					NotationDeclaration nd = parseNotationDecl();
					_markupDeclarationList.add(nd);
					break;
				case PI_START:
					ProcessingInstruction pi = parseProcessingInstruction();
					_markupDeclarationList.add(pi);
					break;
				case COMMENT:
					_markupDeclarationList.add(_n);
					read();
					break;
				case S:
					read();
					break;
				default:
					throw new ParseException(_result.error(offset(_n), "Internal subset is expected."));
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
			throw new ParseException(_result.error(offset(_n), "> is expected."));
		}
		DocumentTypeDeclaration dtd = new DocumentTypeDeclaration(pop(), name, extid, _markupDeclarationList);
		store(dtd);
	}

	private ElementTypeDeclaration parseElementDecl() throws Exception {
		String name;
		push();
		read();
		if (_n.type == S) {
			read(NAME);
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Name is expected."));
		}
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		ContentSpec cs = parseContentSpec();
		if (_n.type == S) {
			read();
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "End of element declaration is expected."));
		}
		ElementTypeDeclaration etd = new ElementTypeDeclaration(pop(), name, cs);
		store(etd);
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
			throw new ParseException(_result.error(offset(_n), "EMPTY, ANY or ( is expected."));
		}
	}

	private ContentSpec parseMixedOrChildren() throws Exception {
		ContentSpec cs;
		if (_n.type == '(') {
			read(NAME);
		} else {
			throw new ParseException(_result.error(offset(_n), "( is expected."));
		}
		if (_n.type == S) {
			read(NAME);
		}
		if (_n.type == PCDATA) {
			read(PCDATA_END);
			if (_n.type == S) {
				read(PCDATA_END);
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
					read(NAME);
					if (_n.type == S) {
						read(NAME);
					}
					if (_n.type == NAME) {
						choiceList.add(_n.toString());
						read(PCDATA_END);
					} else {
						throw new ParseException(_result.error(offset(_n), "Name is expected."));
					}
					if (_n.type == S) {
						read(PCDATA_END);
					}
				} while (_n.type == '|');
				if (_n.type == PCDATA_END) {
					read();
				} else {
					throw new ParseException(_result.error(offset(_n), ")* is expected."));
				}
				cs = new ContentSpec(choiceList);
			} else {
				throw new ParseException(_result.error(offset(_n), ") or )* is expected."));
			}
		} else {
			Object choiceOrSequence;
			ContentParticle cp = parseCP();
			if (_n.type == S) {
				read();
			}
			if (_n.type == '|') {
				ContentChoice choice = new ContentChoice(cp);
				do {
					read(NAME);
					if (_n.type == S) {
						read(NAME);
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
					read(NAME);
					if (_n.type == S) {
						read(NAME);
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
				throw new ParseException(_result.error(offset(_n), ") is expected."));
			}
			if (_n.type == '?') {
				read();
				cs = new ContentSpec(new ContentParticle(choiceOrSequence, '?'));
			} else if (_n.type == '*') {
				read();
				cs = new ContentSpec(new ContentParticle(choiceOrSequence, '*'));
			} else if (_n.type == '+') {
				read();
				cs = new ContentSpec(new ContentParticle(choiceOrSequence, '+'));
			} else {
				cs = new ContentSpec(new ContentParticle(choiceOrSequence));
			}
		}
		return cs;
	}

	private Object parseChoiceOrSequence() throws Exception {
		Object choiceOrSequence;
		if (_n.type == '(') {
			read(NAME);
		} else {
			throw new ParseException(_result.error(offset(_n), "( is expected."));
		}
		if (_n.type == S) {
			read(NAME);
		}
		ContentParticle cp = parseCP();
		if (_n.type == S) {
			read();
		}
		if (_n.type == '|') {
			ContentChoice choice = new ContentChoice(cp);
			do {
				read(NAME);
				if (_n.type == S) {
					read(NAME);
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
				read(NAME);
				if (_n.type == S) {
					read(NAME);
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
			throw new ParseException(_result.error(offset(_n), ") is expected."));
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
			read(NAME);
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == S) {
			read(NAME);
			while (_n.type == NAME) {
				String key = _n.toString();
				read();
				if (_n.type == S) {
					read();
				} else {
					throw new ParseException(_result.error(offset(_n), "White space is expected."));
				}
				Object type = parseAttType();
				if (_n.type == S) {
					read();
				} else {
					throw new ParseException(_result.error(offset(_n), "White space is expected."));
				}
				AttributeDefault value = parseDefaultDecl();
				AttributeDefinition adef = new AttributeDefinition(key, type, value);
				defList.add(adef);
				if (_n.type == S) {
					read(NAME);
				} else {
					break;
				}
			}
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "End of attlist declaration is expected."));
		}
		AttributeListDeclaration ald = new AttributeListDeclaration(pop(), name, defList);
		store(ald);
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
			throw new ParseException(_result.error(offset(_n), "AttType is expected."));
		}
		return type;
	}

	private NotationType parseNotationType() throws Exception {
		NotationType nt;
		if (_n.type == TYPE_NOTATION) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "NOTATION is expected."));
		}
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == '(') {
			read(NAME);
		} else {
			throw new ParseException(_result.error(offset(_n), "( is expected."));
		}
		if (_n.type == S) {
			read(NAME);
		}
		if (_n.type == NAME) {
			nt = new NotationType(_n.toString());
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Name is expected."));
		}
		if (_n.type == S) {
			read();
		}
		while (_n.type == '|') {
			read(NAME);
			if (_n.type == S) {
				read(NAME);
			}
			if (_n.type == NAME) {
				nt.add(_n.toString());
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "Name is expected."));
			}
			if (_n.type == S) {
				read();
			}
		}
		if (_n.type == ')') {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), ") is expected."));
		}
		return nt;
	}

	private EnumerationType parseEnumeration() throws Exception {
		EnumerationType et;
		if (_n.type == '(') {
			read(NMTOKEN);
		} else {
			throw new ParseException(_result.error(offset(_n), "( is expected."));
		}
		if (_n.type == S) {
			read(NMTOKEN);
		}
		if (_n.type == NMTOKEN) {
			et = new EnumerationType(_n.toString());
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Nmtoken is expected."));
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
				throw new ParseException(_result.error(offset(_n), "NMTOKEN is expected."));
			}
			if (_n.type == S) {
				read();
			}
		}
		if (_n.type == ')') {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), ") is expected."));
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
				throw new ParseException(_result.error(offset(_n), "white space is expected."));
			}
			if (_n.type == ATT_VALUE) {
				value = ((QuotedString)_n).innerText();
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "AttValue is expected."));
			}
			ad = new AttributeDefault(AttributeDefault.DEFAULT_FIXED, value);
			break;
		default:
			if (_n.type == ATT_VALUE) {
				value = ((QuotedString)_n).innerText();
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "AttValue is expected."));
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
			read(NAME);
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == NAME) {
			key = _n.toString();
			read();
			if (_n.type == S) {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "White space is expected."));
			}
			if (_n.type == ENTITY_VALUE) {
				definition = new InternalEntityDefinition(key, ((QuotedString)_n).innerText());
				read();
			} else if (_n.type == SYSTEM || _n.type == PUBLIC) {
				ExternalIdentifiers extid = parseExternalId();
				String ndata = null;
				if (_n.type == S) {
					read();
					if (_n.type == NDATA) {
						read();
						if (_n.type == S) {
							read(NAME);
						} else {
							throw new ParseException(_result.error(offset(_n), "White space is expected."));
						}
						if (_n.type == NAME) {
							ndata = _n.toString();
							read();
						} else {
							throw new ParseException(_result.error(offset(_n), "Name is expected."));
						}
					}
				}
				definition = new ExternalEntityDefinition(key, extid, ndata);
			} else {
				throw new ParseException(_result.error(offset(_n), "Entity value or external ID is expected."));
			}
		} else if (_n.type == '%') {
			read();
			if (_n.type == S) {
				read(NAME);
			} else {
				throw new ParseException(_result.error(offset(_n), "White space is expected."));
			}
			if (_n.type == NAME) {
				key = _n.toString();
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "Name is expected."));
			}
			if (_n.type == S) {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "White space is expected."));
			}
			if (_n.type == ENTITY_VALUE) {
				definition = new InternalParameterEntityDefinition(key, ((QuotedString)_n).innerText());
				read();
			} else if (_n.type == SYSTEM || _n.type == PUBLIC) {
				ExternalIdentifiers extid = parseExternalId();
				definition = new ExternalParameterEntityDefinition(key, extid);
			} else {
				throw new ParseException(_result.error(offset(_n), "Entity value or external ID is expected."));
			}
		} else {
			throw new ParseException(_result.error(offset(_n), "Name or % is expected."));
		}
		_entityMap.put(key, definition);
		if (_n.type == S) {
			read();
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "End of entity declaration is expected."));
		}
		EntityDeclaration ed = new EntityDeclaration(pop(), definition);
		store(ed);
		return ed;
	}

	private NotationDeclaration parseNotationDecl() throws Exception {
		String name;
		ExternalIdentifiers extid;
		push();
		read();
		if (_n.type == S) {
			read(NAME);
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == NAME) {
			name = _n.toString();
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Name is expected."));
		}
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == SYSTEM || _n.type == PUBLIC) {
			extid = parseExternalId(false);
		} else {
			throw new ParseException(_result.error(offset(_n), "External ID or Public ID is expected."));
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == TAG_END) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "End of notation declaration is expected."));
		}
		NotationDeclaration nd = new NotationDeclaration(pop(), name, extid);
		store(nd);
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
				throw new ParseException(_result.error(offset(_n), "White space is expected."));
			}
			if (_n.type == SYSTEM_LITERAL) {
				sysValue = ((QuotedString)_n).innerText();
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "System literal is expected."));
			}
			return new ExternalIdentifiers(sysValue);
		} else if (_n.type == PUBLIC) {
			read();
			if (_n.type == S) {
				read(PUBID_LITERAL);
			} else {
				throw new ParseException(_result.error(offset(_n), "White space is expected."));
			}
			if (_n.type == PUBID_LITERAL) {
				pubValue = ((QuotedString)_n).innerText();
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "Pubid literal is expected."));
			}
			if (systemLiteralIsMandatory) {
				if (_n.type == S) {
					read(SYSTEM_LITERAL);
				} else {
					throw new ParseException(_result.error(offset(_n), "White space is expected."));
				}
				if (_n.type == SYSTEM_LITERAL) {
					sysValue = ((QuotedString)_n).innerText();
					read();
				} else {
					throw new ParseException(_result.error(offset(_n), "System literal is expected."));
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

	private void parseElement(Element parent) throws Exception {
		push();
		StartTag tag = parseStartTag();
		Element element = new Element(tag.name, parent);
		if (tag.type == STAG) {
			parseContent(element);
			parseEndTag(tag.name);
		}
		element.set(pop());
		store(element);
	}

	private StartTag parseStartTag() throws Exception {
		StartTag tag;
		List<Attribute> attributeList = new ArrayList<>();
		if (_n.type == STAG_START) {
			push();
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Tag start is expected."));
		}
		if (_n.type == NAME) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Name is expected."));
		}
		if (_n.type == S) {
			read();
			while (_n.type == NAME) {
				String key = _n.toString();
				push();
				read();
				if (_n.type == S) {
					read();
				}
				if (_n.type == EQ) {
					read();
				} else {
					throw new ParseException(_result.error(offset(_n), "Equal sign is expected."));
				}
				if (_n.type == S) {
					read();
				}
				if (_n.type == ATT_VALUE) {
					QuotedString qs = (QuotedString)_n;
					String value = _entityMap.translate(qs.innerText());
					read();
					Attribute attribute = new Attribute(pop(), key, value);
					attributeList.add(attribute);
					store(attribute);
				} else {
					throw new ParseException(_result.error(offset(_n), "Attribute value is expected."));
				}
				if (_n.type == S) {
					read();
				} else {
					break;
				}
			}
		}
		if (_n.type == STAG_END) {
			read();
			tag = StartTag.of(pop(), attributeList);
			return store(tag);
		} else if (_n.type == EETAG_END) {
			read();
			tag = EmptyElementTag.of(pop(), attributeList);
			return store(tag);
		} else {
			throw new ParseException(_result.error(offset(_n), "Tag end is expected."));
		}
	}

	private void parseContent(Element parent) throws Exception {
		push();
		while (true) {
			if (_n.type == CHAR_DATA) {
				read();
			}
			switch (_n.type) {
			case STAG_START:
				parseElement(parent);
				break;
			case ENTITY_REF:
				EntityRef er = (EntityRef)_n;
				String value = _entityMap.getEntity(er.name);
				if (value != null) {
					_n = er.with(value);
				} else if (_entityMap.get(er.name) instanceof ExternalEntityDefinition) {
					_result.warning(offset(_n), "External entity reference %s cannot be expanded as it is not supported.", er.name);
				} else {
					_result.warning(offset(_n), "Entity reference %s is not defined.", er.name);
				}
				read();
				break;
			case CHAR_REF:
				read();
				break;
			case CD_SECT:
				read();
				break;
			case PI_START:
				parseProcessingInstruction();
				break;
			case COMMENT:
				read();
				break;
			default:
				Content content = Content.of(pop());
				store(content);
				return;
			}
		}
	}

	private void parseEndTag(String name) throws Exception {
		if (_n.type == ETAG_START) {
			push();
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "End tag start is expected."));
		}
		if (_n.type == NAME) {
			if (!_n.toString().equals(name)) {
				throw new ParseException(_result.error(offset(_n), "Tags mismatch. expected=%s actual=%s", name, _n.toString()));
			}
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Name is expected."));
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == ETAG_END) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "ETag end is expected."));
		}
		EndTag tag = EndTag.of(pop());
		store(tag);
	}

	private void processExternalDocument(String name, ExternalIdentifiers extid) {
		Path path = null;
		if (extid.pubidLiteral != null) {
			String[] parts = extid.pubidLiteral.split("//");
			if (parts.length == 4) {
				String organization = parts[0];
				String dtdName = parts[1];
				String version = parts[2];
				String language = parts[3];
				_result.information(-1, "DTD %s PUBID org=\"%s\" dtd=\"%s\" ver=\"%s\" lang=\"%s\"", name, organization, dtdName, version, language);
				_result.information(-1, "DTD %s Catalog not supported.", name, organization, dtdName, version, language);
			} else {
				_result.warning(-1, "DTD %s PUBID %s cannot be parsed.", name, extid.pubidLiteral);
			}
		}
		if (extid.systemLiteral != null) {
			if (extid.systemLiteral.matches("^(?i)https?://.*")) {
				_result.information(-1, "DTD %s Processor doesn't load document from %s.", name, extid.systemLiteral);
			} else if (extid.systemLiteral.matches("^(?i)file://.*")) {
				path = Path.of(URI.create(extid.systemLiteral));
			} else {
				path = Path.of(extid.systemLiteral);
			}
		}
		if (path != null) {
			try {
				byte[] content = Files.readAllBytes(path);
				Processor processor = new Processor(content, _entityMap, _result, _markupDeclarationList);
				processor.parseExternalSubset();
				_result.information(-1, "DTD %s Processor loaded external document from %s.", name, extid.systemLiteral);
			} catch (NoSuchFileException e) {
				_result.warning(-1, "DTD %s Processor failed to load external document from %s: No such file.", name, extid.systemLiteral);
			} catch (Exception e) {
				_result.warning(-1, "DTD %s Processor failed to load external document from %s: %s", name, extid.systemLiteral, e.getMessage());
			}
		}
	}

	public List<Node> parseExternalSubset() throws Exception {
		_lexer = new Lexer(_content, LexerContext.EXTERNAL);
		_nn = new ArrayList<>();
		_n = _lexer.read();
		if (_n.type == PI_START) {
			push();
			read(PI_TARGET);
			if (_n.type == NAME) {
				String name = _n.toString();
				if (name.equals("xml")) {
					_lexer.setContext(LexerContext.XML);
					read();
					parseTextDeclaration();
				} else {
					read();
					ProcessingInstruction pi = parseProcessingInstruction(name);
					_markupDeclarationList.add(pi);
				}
			} else {
				throw new ParseException(_result.error(offset(_n), "Name is expected."));
			}
		}
		while (parseExternalSubsetDeclaration()) {
			continue;
		}
		if (_n.type != EOF) {
			throw new ParseException(_result.error(offset(_n), "EOF is expected."));
		}
		return _nn;
	}

	private void parseTextDeclaration() throws Exception {
		String name;
		String version = null;
		String encoding = null;
		if (_n.type == S) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "White space is expected."));
		}
		if (_n.type == NAME) {
			name = _n.toString();
		} else {
			throw new ParseException(_result.error(offset(_n), "encoding is expected."));
		}
		if (name.equals("version")) {
			read();
			if (_n.type == S) {
				read();
			}
			if (_n.type == EQ) {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "Equal sign is expected."));
			}
			if (_n.type == S) {
				read();
			}
			if (_n.type == ATT_VALUE) {
				version = ((QuotedString)_n).innerText();
				if (!version.matches("1\\.[0-9]+")) {
					throw new ParseException(_result.error(offset(_n), "Malformed version number."));
				}
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "version number is expected."));
			}
			if (_n.type == S) {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "White space is expected."));
			}
			if (_n.type == NAME) {
				name = _n.toString();
			} else {
				throw new ParseException(_result.error(offset(_n), "encoding is expected."));
			}
		}
		if (name.equals("encoding")) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "encoding is expected."));
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == EQ) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "Equal sign is expected."));
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == ATT_VALUE) {
			encoding = ((QuotedString)_n).innerText();
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "encoding name is expected."));
		}
		if (_n.type == S) {
			read();
		}
		if (_n.type == XML_END) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "?> is expected."));
		}
		XmlDeclaration t = new XmlDeclaration(pop(), version, encoding, null);
		store(t);
	}

	private boolean parseExternalSubsetDeclaration() throws Exception {
		switch (_n.type) {
		case ELEMENT_DECL_START:
			ElementTypeDeclaration etd = parseElementDecl();
			_markupDeclarationList.add(etd);
			return true;
		case ATTLIST_DECL_START:
			AttributeListDeclaration ald = parseAttlistDecl();
			_markupDeclarationList.add(ald);
			return true;
		case ENTITY_DECL_START:
			EntityDeclaration ed = parseEntityDecl();
			_markupDeclarationList.add(ed);
			return true;
		case NOTATION_DECL_START:
			NotationDeclaration nd = parseNotationDecl();
			_markupDeclarationList.add(nd);
			return true;
		case PI_START:
			ProcessingInstruction pi = parseProcessingInstruction();
			_markupDeclarationList.add(pi);
			return true;
		case COMMENT:
			_markupDeclarationList.add(_n);
			read();
			return true;
		case S:
			read();
			return true;
		case SECTION_START:
			parseConditionalSection();
			return true;
		default:
			return false;
		}
	}

	private void parseConditionalSection() throws Exception {
		read();
		if (_n.type == S) {
			read();
		}
		switch (_n.type) {
		case INCLUDE:
			read();
			if (_n.type == S) {
				read();
			}
			if (_n.type == '[') {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "[ is expected."));
			}
			while (parseExternalSubsetDeclaration()) {
				continue;
			}
			if (_n.type == SECTION_END) {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "]]> is expected."));
			}
			break;
		case IGNORE:
			read('[');
			if (_n.type == S) {
				read('[');
			}
			if (_n.type == '[') {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "[ is expected."));
			}
			if (_n.type == IGNORE_SECTION_CONTENTS) {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "ignore section contents are expected."));
			}
			if (_n.type == SECTION_END) {
				read();
			} else {
				throw new ParseException(_result.error(offset(_n), "]]> is expected."));
			}
			break;
		default:
			throw new ParseException(_result.error(offset(_n), "INCLUDE or IGNORE is expected."));
		}
	}

	private ProcessingInstruction parseProcessingInstruction() throws Exception {
		return parseProcessingInstruction(null);
	}

	private ProcessingInstruction parseProcessingInstruction(String name) throws Exception {
		String body = null;
		if (name == null) {
			push();
			read(NAME);
			if (_n.type == NAME) {
				name = _n.toString();
				if (name.toLowerCase().equals("xml")) {
					throw new ParseException(_result.error(offset(_n), "xml is now allowed for PI."));
				} else {
					read();
				}
			} else {
				throw new ParseException(_result.error(offset(_n), "Name is expected."));
			}
		}
		if (_n.type == S) {
			read();
			if (_n.type == PI_BODY) {
				body = _n.toString();
				read();
			}
		}
		if (_n.type == PI_END) {
			read();
		} else {
			throw new ParseException(_result.error(offset(_n), "PI end is expected."));
		}
		ProcessingInstruction pi = new ProcessingInstruction(pop(), name, body); 
		return store(pi);
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

	private <T extends Node> T store(T node) {
		_nn.add(node);
		return node;
	}

	private Node read() {
		return read(0);
	}

	private Node read(int preferred) {
		if (_lexerStack.isEmpty()) {
			store(_n);
		}
		boolean sp = _n.type == S;
		DebugHelper.printLexerContext(_lexer.getContext());
		_n = _lexer.read(preferred);
		while (true) {
			if (_n.type == EOF) {
				if (_lexerStack.isEmpty()) {
					break;
				}
				_lexer = _lexerStack.pop();
				_n = _lexer.read(preferred);
				if (sp && _n.type == S) {
					if (_lexerStack.isEmpty()) {
						store(_n);
					}
					_n = _lexer.read(preferred);
				}
			} else if (_n.type == PEREFERENCE && _lexer.getContext() >= LexerContext.DOCTYPE) {
				ParameterEntityReference per = (ParameterEntityReference)_n;
				String value = _entityMap.getParameterEntity(per.name);
				if (value != null) {
					if (_lexerStack.isEmpty()) {
						store(_n);
					}
					_lexerStack.push(_lexer);
					_lexer = new Lexer(value, _lexer);
					_n = _lexer.read(preferred);
					if (sp && _n.type == S) {
						_n = _lexer.read(preferred);
					}
				} else if (_entityMap.get(EntityMap.peKey(per.name)) instanceof ExternalParameterEntityDefinition) {
					_result.warning(offset(_n), "External parameter entity %s cannot be expanded as it is not supported.", per.name);
					break;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		DebugHelper.print(_n);
		return _n;
	}

	private int offset(Node target) {
		int length = 0;
		int size = _nn.size();
		for (int i = 0; i < size; i++) {
			Node node = _nn.get(i);
			int delta = node.length(target);
			if (delta >= 0) {
				return length + delta;
			}
			length += node.length();
		}
		return length;
	}

}
