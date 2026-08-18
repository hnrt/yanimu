package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Element extends Node {

	protected final List<Node> _nodeList = new ArrayList<>();
	protected StartTag _startTag;
	protected Object _parent;
	public final String name;

	public Element(StartTag startTag, Element parent) {
		super(ELEMENT);
		_nodeList.add(startTag);
		_startTag = startTag;
		_parent = parent;
		name = startTag.name;
	}

	public void set(List<Node> nodeList, EndTag endTag) {
		if (_startTag.type == STAG && _nodeList.size() == 1) {
			_nodeList.addAll(nodeList);
			_nodeList.add(endTag);
		} else {
			throw new RuntimeException("Element::set: Incorrectly called!");
		}
	}

	@Override
	public byte[] sequence() {
		if (_sequence == null) {
			_sequence = buildSequence(_nodeList);
		}
		return _sequence;
	}

	@Override
	public void setSequence(byte[] sequence) {
		_sequence = sequence;
		if (_parent instanceof Node node) {
			node.clearSequence();
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
		if (_parent instanceof Element element) {
			return element.document();
		} else if (_parent instanceof Document _document) {
			return _document;
		} else {
			return null;
		}
	}

	public List<Node> nodeList() {
		return _nodeList;
	}

	public StartTag startTag() {
		return _startTag;
	}

	public EndTag endTag() {
		if (_startTag.type == STAG) {
			Node node = _nodeList.get(_nodeList.size() - 1);
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

	public void addChild(Node node) {
		if (_startTag.type == EETAG) {
			_startTag = _startTag.clone(STAG);
			_nodeList.clear();
			_nodeList.add(_startTag);
			_nodeList.add(node);
			_nodeList.add(new EndTag(name));
		} else {
			int lastIndex = _nodeList.size() - 1;
			_nodeList.add(lastIndex, node);
		}
		clearSequence();
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
