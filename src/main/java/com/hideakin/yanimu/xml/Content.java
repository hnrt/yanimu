package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Content extends NodeList {

	public Content() {
		super(CONTENT);
	}

	public Content(Node node) {
		super(CONTENT, node);
	}

	public Content(List<Node> nodeList) {
		super(CONTENT, nodeList);
	}

	@Override
	public byte[] sequence() {
		return super.sequence();
	}

	@Override
	public void removeAll() {
		for (Node node : _nodeList) {
			if (node instanceof Element element) {
				element.setParent(null);
			}
		}
		super.removeAll();
	}

	@Override
	public Node remove(int index) {
		Node node = super.remove(index);
		if (node instanceof Element element) {
			element.setParent(null);
		}
		return node;
	}

	@Override
	public Node remove(Node node) {
		node = super.remove(node);
		if (node instanceof Element element) {
			element.setParent(null);
		}
		return node;
	}

	@Override
	public Node remove(Node node, int start, int end) {
		node = super.remove(node, start, end);
		if (node instanceof Element element) {
			element.setParent(null);
		}
		return node;
	}

	public String innerText() {
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

	public void setInnerText(String value) {
		removeAll();
		int h = 0;
		int i = 0;
		int n = value.length();
		while (i < n) {
			char c = value.charAt(i);
			switch (c) {
			case '<':
				if (h < i) {
					_nodeList.add(new Node(CHAR_DATA, value.substring(h, i)));
				}
				_nodeList.add(new EntityRef("lt", "<"));
				h = ++i;
				break;
			case '&':
				if (h < i) {
					_nodeList.add(new Node(CHAR_DATA, value.substring(h, i)));
				}
				_nodeList.add(new EntityRef("amp", "&"));
				h = ++i;
				break;
			case ']':
				if (i + 2 < n && value.charAt(i + 1) == ']' && value.charAt(i + 2) == '>') {
					if (h < i) {
						_nodeList.add(new Node(CHAR_DATA, value.substring(h, i)));
					}
					_nodeList.add(new CharRef(']'));
					_nodeList.add(new CharRef(']'));
					_nodeList.add(new EntityRef("gt", ">"));
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
			_nodeList.add(new Node(CHAR_DATA, value));
		} else if (h < n) {
			_nodeList.add(new Node(CHAR_DATA, value.substring(h, n)));
		}
	}

	public boolean hasElement() {
		for (Node node : _nodeList) {
			if (node.type == ELEMENT) {
				return true;
			}
		}
		return false;
	}

	public List<Element> getElements(String name) {
		List<Element> elementList = new ArrayList<>();
		boolean anyMatch = name.equals("*");
		for (Node node : _nodeList) {
			if (node instanceof Element element) {
				if (anyMatch || element.name.equals(name)) {
					elementList.add(element);
				}
			}
		}
		return elementList;
	}

	public List<Element> getElementsRecursively(String[] names, int index, List<Element> elementList) {
		for (Node node : _nodeList) {
			if (node instanceof Element element) {
				elementList.addAll(element.getElements(names, index, true));
			}
		}
		return elementList;	
	}

	public void indent(byte[] eol, int indentation, int level) {
		int n = _nodeList.size();
		for (int i = 0; i < n; i++) {
			Node node = _nodeList.get(i);
			if (node instanceof Element element) {
				if (i == 0 || _nodeList.get(i - 1).type == ELEMENT) {
					_nodeList.add(i, Node.endOfLineAndIndentation(eol, indentation, level));
					i++;
					n++;
				} else if (_nodeList.get(i - 1).isEndOfLineAndIndentation()) {
					_nodeList.set(i - 1, Node.endOfLineAndIndentation(eol, indentation, level));
				}
				element.indent(eol, indentation, level);
			}			
		}
		if (n == 0 || _nodeList.get(n - 1).type == ELEMENT) {
			_nodeList.add(n, Node.endOfLineAndIndentation(eol, indentation, level - 1));
		} else if (_nodeList.get(n - 1).isEndOfLineAndIndentation()) {
			_nodeList.set(n - 1, Node.endOfLineAndIndentation(eol, indentation, level - 1));
		}
		clearSequence();
	}

}
