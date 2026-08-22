package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Element extends NodeList {

	protected StartTag _startTag;
	protected Object _parent;
	public final String name;

	public Element(String name) {
		super(ELEMENT);
		_startTag = new EmptyElementTag(name);
		_parent = null;
		this.name = name;
		_nodeList.add(_startTag);
	}

	public Element(String name, String innerText) {
		super(ELEMENT);
		_startTag = new StartTag(name);
		_parent = null;
		this.name = name;
		_nodeList.add(_startTag);
		_nodeList.add(new EndTag(name));
		setInnerText(innerText);
	}

	public Element(StartTag startTag, Element parent) {
		super(ELEMENT, startTag);
		_startTag = startTag;
		_parent = parent;
		name = startTag.name;
	}

	public void set(List<Node> nodeList, EndTag endTag) {
		if (_startTag.type == STAG && _nodeList.size() == 1) {
			_nodeList.addAll(nodeList);
			_nodeList.add(endTag);
		} else {
			throw new RuntimeException("Element::set: Incorrect use!");
		}
	}

	@Override
	public void clearSequence() {
		_sequence = null;
		if (_parent instanceof Node node) {
			node.clearSequence();
		}
	}

	public Element parent() {
		if (_parent instanceof Element element) {
			return element;
		} else {
			return null;
		}
	}

	public void setParent(Object parent) {
		_parent = parent;
		if (_parent instanceof Node node) {
			node.clearSequence();
		}
	}

	public Document document() {
		if (_parent instanceof Element parentElement) {
			return parentElement.document();
		} else if (_parent instanceof Document theDocument) {
			return theDocument;
		} else {
			return null;
		}
	}

	public StartTag startTag() {
		return _startTag;
	}

	public EndTag endTag() {
		if (_startTag.type == STAG) {
			Node node = lastNode();
			if (node.type == ETAG) {
				return (EndTag)node;
			}
		}
		return null;
	}

	public int attributeCount() {
		return _startTag.attributeCount();
	}

	public String attribute(int index) {
		return _startTag.attribute(index);
	}

	public String attribute(int index, String defaultValue) {
		return _startTag.attribute(index, defaultValue);
	}

	public String attribute(String key) {
		return _startTag.attribute(key);
	}

	public String attribute(String key, String defaultValue) {
		return _startTag.attribute(key, defaultValue);
	}

	public List<String> attributeKeys() {
		return _startTag.attributeKeys();
	}

	public int childCount() {
		return _startTag.type == EETAG ? 0 : _nodeList.size() - 2;
	}

	public List<Node> children() {
		return _startTag.type == EETAG ? List.of() : List.copyOf(_nodeList.subList(1, _nodeList.size() - 1));
	}

	public Node child(int index) {
		if (_startTag.type == STAG) {
			int count = childCount();
			if (index < 0) {
				index += count;
			}
			if (0 <= index && index < count) {
				return _nodeList.get(index + 1);
			}
		}
		return null;
	}

	public boolean empty() {
		if (_startTag.type == EETAG) {
			return true;
		} else {
			int count = childCount();
			if (count > 1) {
				return false;
			} else if (count == 1) {
				Node node = _nodeList.get(1);
				if (node.type != CHAR_DATA) {
					return false;
				}
				byte[] sequence = node.sequence();
				int length = sequence.length;
				for (int i = 0; i < length; i++) {
					int c = sequence[i];
					if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
						continue;
					} else {
						return false;
					}
				}
			}
			_startTag = _startTag.clone(EETAG);
			_nodeList.clear();
			_nodeList.add(_startTag);
			clearSequence();
			return true;
		}
	}

	public void addChild(Node node) {
		if (_startTag.type == EETAG) {
			_startTag = _startTag.clone(STAG);
			_nodeList.clear();
			_nodeList.add(_startTag);
			if (node.type == ELEMENT) {
				Document document = this.document();
				byte[] eol = document != null ? document.endOfLineSequence() : Document.LF_SEQUENCE;
				int indentation = document != null ? document.indentation() : Document.INDENTATION_DEFAULT;
				int level = this.level();
				_nodeList.add(Node.endOfLineAndIndentation(eol, indentation, level + 1));
				_nodeList.add(node);
				_nodeList.add(Node.endOfLineAndIndentation(eol, indentation, level));
			} else {
				_nodeList.add(node);
			}
			_nodeList.add(new EndTag(name));
		} else {
			int index = _nodeList.size() - 1;
			if (node.type == ELEMENT && _nodeList.get(index - 1).isEndOfLineAndIndentation()) {
				Document document = this.document();
				byte[] eol = document != null ? document.endOfLineSequence() : Document.LF_SEQUENCE;
				int indentation = document != null ? document.indentation() : Document.INDENTATION_DEFAULT;
				int level = this.level();
				_nodeList.add(index - 1, Node.endOfLineAndIndentation(eol, indentation, level + 1));
				_nodeList.add(index, node);
			} else {
				_nodeList.add(index, node);
			}
		}
		if (node instanceof Element element) {
			element.setParent(this);
		}
		clearSequence();
	}

	public void addChild(int index, Node node) {
		if (_startTag.type == EETAG) {
			_startTag = _startTag.clone(STAG);
			_nodeList.clear();
			_nodeList.add(_startTag);
			_nodeList.add(node);
			_nodeList.add(new EndTag(name));
		} else {
			int count = childCount();
			if (index < 0) {
				index += count;
			}
			if (index < 0) {
				index = 0;
			}
			index = index < count ? index + 1 : count + 1;
			_nodeList.add(index, node);
		}
		if (node instanceof Element element) {
			element.setParent(this);
		}
		clearSequence();
	}

	public void removeAllChildren() {
		if (_startTag.type == STAG) {
			for (int count = childCount(); count > 0; count--) {
				Node node = _nodeList.remove(1);
				if (node instanceof Element element) {
					element.setParent(null);
				}
			}
		}
	}

	public Node removeChild(int index) {
		if (_startTag.type == STAG) {
			int count = childCount();
			if (index < 0) {
				index += count;
			}
			if (0 <= index && index < count) {
				clearSequence();
				Node node = _nodeList.remove(index + 1);
				if (node instanceof Element element) {
					element.setParent(null);
				}
				return node;
			}
		}
		return null;
	}

	public Node removeChild(Node node) {
		if (_startTag.type == STAG) {
			int count = childCount();
			for (int index = 0; index < count; index++) {
				if (_nodeList.get(index + 1) == node) {
					clearSequence();
					node = _nodeList.remove(index + 1);
					if (node instanceof Element element) {
						element.setParent(null);
					}
					return node;
				}
			}
		}
		return null;
	}

	public String innerText() {
		if (_startTag.type == STAG) {
			StringBuilder buffer = new StringBuilder();
			for (Node node : _nodeList) {
				switch (node.type) {
				case CHAR_DATA:
					buffer.append(node.toString());
					break;
				case ENTITY_REF:
					buffer.append(((EntityRef)node).translated);
					break;
				case CHAR_REF:
					buffer.appendCodePoint(((CharRef)node).codepoint);
					break;
				case CD_SECT:
					buffer.append(((CDATASection)node).innerText());
					break;
				default:
					break;
				}
			}
			return buffer.toString();
		}
		return null;
	}

	public void setInnerText(String value) {
		List<Node> nodeList = new ArrayList<>();
		int h = 0;
		int i = 0;
		int n = value.length();
		while (i < n) {
			char c = value.charAt(i);
			switch (c) {
			case '<':
				if (h < i) {
					nodeList.add(new Node(CHAR_DATA, value.substring(h, i)));
				}
				nodeList.add(new EntityRef("lt", "<"));
				h = ++i;
				break;
			case '&':
				if (h < i) {
					nodeList.add(new Node(CHAR_DATA, value.substring(h, i)));
				}
				nodeList.add(new EntityRef("amp", "&"));
				h = ++i;
				break;
			case ']':
				if (i + 2 < n && value.charAt(i + 1) == ']' && value.charAt(i + 2) == '>') {
					if (h < i) {
						nodeList.add(new Node(CHAR_DATA, value.substring(h, i)));
					}
					nodeList.add(new CharRef('['));
					nodeList.add(new CharRef('['));
					nodeList.add(new EntityRef("gt", ">"));
					i += 3;
					h = i;
				} else {
					++i;
				}
				break;
			default:
				++i;
				break;
			}
		}
		if (h == 0) {
			nodeList.add(new Node(CHAR_DATA, value));
		} else if (h < n) {
			nodeList.add(new Node(CHAR_DATA, value.substring(h, n)));
		}
		removeAllChildren();
		int index = 1;
		for (Node node : nodeList) {
			_nodeList.add(index, node);
			index++;
		}
	}

	public int level() {
		int n = 0;
		Object parent = _parent;
		while (parent instanceof Element element) {
			n++;
			parent = element.parent();
		}
		return n;
	}

	public List<Element> getElements(String name) {
		List<Element> elementList = new ArrayList<>();
		if (_startTag.type == STAG) {
			for (Node node : _nodeList) {
				if (node instanceof Element element) {
					if (element.name.equals(name)) {
						elementList.add(element);
					}
					elementList.addAll(element.getElements(name));
				}
			}
		}
		return elementList;
	}

	public void indent(byte[] eol, int indentation, int level) {
		if (empty()) {
			return;
		}
		Node prev = _nodeList.get(0);
		if (prev.type == STAG) {
			Node node;
			int i = 1;
			while((node = _nodeList.get(i)).type != ETAG) {
				if (node instanceof Element element) {
					if (prev.isEndOfLineAndIndentation()) {
						_nodeList.set(i - 1, Node.endOfLineAndIndentation(eol, indentation, level + 1));
						element.indent(eol, indentation, level + 1);
					} else {
						_nodeList.add(i, Node.endOfLineAndIndentation(eol, indentation, level + 1));
						element.indent(eol, indentation, level + 1);
						i++;
					}
				}
				prev = node;
				i++;
			}
			if (prev.isEndOfLineAndIndentation()) {
				_nodeList.set(i - 1, Node.endOfLineAndIndentation(eol, indentation, level));
			} else if (prev.type == ELEMENT) {
				_nodeList.add(i, Node.endOfLineAndIndentation(eol, indentation, level));
			}
		}
		clearSequence();
	}

}
