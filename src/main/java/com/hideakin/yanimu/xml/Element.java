package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Element extends NodeList {

	protected StartTag _startTag;
	protected Object _parent;
	public final String name;

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
				int length = _sequence.length;
				for (int i = 0; i < length; i++) {
					int c = _sequence[i];
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
			_nodeList.add(node);
			_nodeList.add(new EndTag(name));
		} else {
			int index = _nodeList.size() - 1;
			_nodeList.add(index, node);
		}
		clearSequence();
	}

	public void addChild(int index, Node node) {
		if (_startTag.type == EETAG) {
			addChild(node);
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
		clearSequence();
	}

	public Node removeChild(int index) {
		if (_startTag.type == STAG) {
			int count = childCount();
			if (index < 0) {
				index += count;
			}
			if (0 <= index && index < count) {
				clearSequence();
				return _nodeList.remove(index + 1);
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
					return _nodeList.remove(index + 1);
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

}
