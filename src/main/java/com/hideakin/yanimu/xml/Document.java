package com.hideakin.yanimu.xml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.hideakin.yanimu.xml.doctype.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.internal.Processor;

public class Document extends NodeList {

	public static final int INDENTATION_DEFAULT = 2;

	public static final String LF = "\n";
	public static final String CRLF = "\r\n";
	public static final byte[] LF_SEQUENCE = { 10 };
	public static final byte[] CRLF_SEQUENCE = { 13, 10 };

	protected Path _path;
	protected XmlDeclaration _xml;
	protected DocumentTypeDeclaration _dtd;
	protected Element _root;
	protected String[] _warnings;
	protected String[] _information;
	protected int _indentation = INDENTATION_DEFAULT;

	public Document() {
		super(DOCUMENT);
	}

	public Document(Path path) {
		super(DOCUMENT);
		_path = path;
	}

	public Path path() {
		return _path;
	}

	public void setPath(Path path) {
		_path = path;
	}

	public XmlDeclaration xml() {
		return _xml;
	}

	public void setXml(XmlDeclaration xml) {
		if (get(0).type == XML_DECL) {
			if (xml != null) {
				set(0, xml);
			} else {
				remove(0);
				if (get(0).type == S) {
					remove(0);
				}
			}
		}
		_xml = xml;
	}

	public DocumentTypeDeclaration dtd() {
		return _dtd;
	}

	public void setDtd(DocumentTypeDeclaration dtd) {
		for (int i = 0; ; i++) {
			switch (get(i).type) {
			case DOCTYPE_DECL:
				if (dtd != null) {
					set(i, dtd);
				} else {
					remove(i);
					if (get(i).type == S) {
						remove(i);
					}
				}
				_dtd = dtd;
				return;
			case ELEMENT:
			case NULL:
				if (dtd != null) {
					add(i, dtd);
					add(i + 1, Node.of(S, endOfLineSequence()));
				}
				_dtd = dtd;
				return;
			default:
				break;
			}
		}
	}

	public Element root() {
		return _root;
	}

	public void setRoot(Element root) {
		for (int i = 0; ; i++) {
			switch (get(i).type) {
			case ELEMENT:
				if (root != null) {
					set(i, root);
				} else {
					remove(i);
					if (get(i).type == S) {
						remove(i);
					}
				}
				_root = root;
				return;
			case NULL:
				if (root != null) {
					add(i, root);
				}
				_root = root;
				return;
			default:
				break;
			}
		}
	}

	public void load() throws Exception {
		load(Files.readAllBytes(_path));
	}

	public void load(InputStream in) throws Exception {
		load(in.readAllBytes());
	}

	public void load(byte[] content) throws Exception {
		_nodeList.clear();
		_xml = null;
		_dtd = null;
		_root = null;
		_warnings = null;
		_information = null;
		Processor processor = new Processor(content);
		List<Node> nodeList = processor.parse();
		_nodeList.addAll(nodeList);
		if (first() instanceof XmlDeclaration xml) {
			_xml = xml;
		}
		for (Node node : _nodeList) {
			if (node instanceof Element element) {
				_root = element;
				_root.setParent(this);
				break;
			} else if (node instanceof DocumentTypeDeclaration dtd) {
				_dtd = dtd;
			}
		}
		_warnings = processor.warnings();
		_information = processor.information();
	}

	public String[] warnings() {
		return _warnings;
	}

	public String[] information() {
		return _information;
	}

	public int toLineNumber(int offset) {
		return offset < 0 ? 0 : lineCount(offset) + 1;
	}

	public int toColumnNumber(int offset) {
		return offset < 0 ? 0 : columnCount(offset, 0) + 1;
	}

	public int indentation() {
		return _indentation;
	}

	public void setIndentation(int spaces) {
		_indentation = spaces;
	}

	/**
	 * This method locates Element instances that match the criteria specified by <i>name</i>.
	 * @param name the tag name pattern used to locate Element instances.
	 *             The pattern may include multiple tag names separated by slashes
	 *             to specify an Element hierarchy.
	 *             If <i>name</i> begins with a slash, the search is performed starting
	 *             from the root element. Otherwise, the search begins from
	 *             any descendant elements.
	 *             An asterisk acts as a wildcard that matches any tag name.
	 * @return List of Element instances
	 */
	public List<Element> getElements(String name) {
		List<Element> elementList = new ArrayList<>();
		if (name != null && name.length() > 0 && _root != null) {
			String[] names = name.split("/");
			boolean isRelative = names[0].length() > 0;
			int index = isRelative ? 0 : 1;
			boolean anyMatch = names[index].equals("*");
			if (anyMatch || _root.name.equals(names[index])) {
				if (index + 1 < names.length) {
					elementList.addAll(_root.getElements(names, index + 1, false));
				} else {
					elementList.add(_root);
				}
			}
			if (isRelative) {
				elementList.addAll(_root.getElements(names, index, true));
			}
		}
		return elementList;
	}

	public byte[] endOfLineSequence() {
		byte[] s = endOfLineSequence(_nodeList);
		return s != null ? s : LF_SEQUENCE;
	}

	private static byte[] endOfLineSequence(List<Node> nodeList) {
		for (Node node : nodeList) {
			if (node.type == S || node.type == CHAR_DATA) {
				byte[] s = node.sequence();
				int n = s.length;
				for (int i = 0; i < n; i++) {
					if (s[i] == 10) {
						return LF_SEQUENCE;
					} else if (s[i] == 13 && i + 1 < n && s[i + 1] == 10) {
						return CRLF_SEQUENCE;
					}
				}
			} else if (node.type == ELEMENT) {
				byte[] s = endOfLineSequence(((NodeList)node)._nodeList);
				if (s != null) {
					return s;
				}
			}
		}
		return null;
	}

	public void indent() {
		byte[] eol = endOfLineSequence();
		Node node;
		for (int i = 0; (node = get(i)).type != NULL; i++) {
			switch (node.type) {
			case ELEMENT:
				if (i > 0 && get(i - 1).type == S) {
					set(i - 1, Node.of(S, eol));
				} else {
					add(i, Node.of(S, eol));
					i++;
				}
				((Element)node).indent(eol, _indentation, 0);
				break;
			default:
				break;
			}
		}
	}

}
