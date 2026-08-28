package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Content extends NodeList {

	public static Content of() {
		return new Content();
	}

	public static Content of(Node node) {
		return new Content(node);
	}

	public static Content of(List<Node> nodeList) {
		return new Content(nodeList);
	}

	private Content() {
		super(CONTENT);
	}

	private Content(Node node) {
		super(CONTENT, node);
	}

	private Content(List<Node> nodeList) {
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
					add(Node.of(CHAR_DATA, value.substring(h, i)));
				}
				add(EntityRef.of("lt", "<"));
				h = ++i;
				break;
			case '&':
				if (h < i) {
					add(Node.of(CHAR_DATA, value.substring(h, i)));
				}
				add(EntityRef.of("amp", "&"));
				h = ++i;
				break;
			case ']':
				if (i + 2 < n && value.charAt(i + 1) == ']' && value.charAt(i + 2) == '>') {
					if (h < i) {
						add(Node.of(CHAR_DATA, value.substring(h, i)));
					}
					add(CharRef.of(']'));
					add(CharRef.of(']'));
					add(EntityRef.of("gt", ">"));
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
			add(Node.of(CHAR_DATA, value));
		} else if (h < n) {
			add(Node.of(CHAR_DATA, value.substring(h, n)));
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
		for (int i = 0; ; i++) {
			Node node = get(i);
			if (node.type == NULL) {
				break;
			} else if (node instanceof Element element) {
				if (i == 0 || get(i - 1).type == ELEMENT) {
					add(i, Node.endOfLineAndIndentation(eol, indentation, level));
					i++;
				} else if (get(i - 1).isEndOfLineAndIndentation()) {
					set(i - 1, Node.endOfLineAndIndentation(eol, indentation, level));
				}
				element.indent(eol, indentation, level);
			}
		}
		if (count() == 0 || last().type == ELEMENT) {
			add(Node.endOfLineAndIndentation(eol, indentation, level - 1));
		} else if (last().isEndOfLineAndIndentation()) {
			set(lastIndex(), Node.endOfLineAndIndentation(eol, indentation, level - 1));
		}
		clearSequence();
	}

}
