package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A mutable node containing children of an XML Element.
 */
public class Content extends NodeList {

	/**
	 * Returns a new node of type {@code CONTENT} with no children.
	 * @return a newly created node
	 */
	public static Content of() {
		return new Content();
	}

	/**
	 * Returns a new node of type {@code CONTENT} with a child.
	 * @param node a child to store in this node
	 * @return a newly created node
	 */
	public static Content of(Node node) {
		return new Content(node);
	}

	/**
	 * Returns a new node of type {@code CONTENT} with children.
	 * @param children one or more children nodes to store in this node
	 * @return a newly created node
	 */
	public static Content of(List<Node> children) {
		return new Content(children);
	}

	/**
	 * Returns a new node of type {@code CONTENT} with a child of a character data node.
	 * @param text character data to store as a child in this node
	 * @return a newly created node
	 */
	public static Content of(String text) {
		return new Content(text);
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

	private Content(String text) {
		super(CONTENT);
		setText(text);
	}

	@Override
	public void set(int index, Node node) {
		Node previous = super.get(index);
		if (previous instanceof Element element) {
			element.setParent(null);
		}
		super.set(index, node);
	}

	@Override
	public void clear() {
		for (Node node : _nodeList) {
			if (node instanceof Element element) {
				element.setParent(null);
			}
		}
		super.clear();
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

	/**
	 * Concatenates text of each child node stored in this node in their stored order
	 * and returns the resulting text. 
	 * @return a {@code String} representation of the resulting text
	 */
	public String text() {
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

	/**
	 * Replaces all the child nodes currently stored in this node
	 * with one or more newly created child nodes using the specified text.
	 * Depending on the characters in the specified text,
	 * {@code CharData} and {@code EntityRef} may be newly created and stored in this node in their occurrence order.
	 * @param source a {@code String} representation of text to store in this node
	 */
	public void setText(String source) {
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (c) {
			case '<':
			case '&':
				setText(source, i);
				return;
			case ']':
				if (i + 2 < source.length() && source.charAt(i + 1) == ']' && source.charAt(i + 2) == '>') {
					setText(source, i);
					return;
				}
				//FALLTHROUGH
			default:
				break;
			}
		}
		clear();
		add(CharData.of(source, CharData.IMMEDIATE));
	}

	private void setText(String source, int first) {
		clear();
		int h = 0;
		for (int i = first; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (c) {
			case '<':
				if (h < i) {
					add(CharData.of(source.substring(h, i), CharData.IMMEDIATE));
				}
				h = i + 1;
				add(EntityRef.of("lt", "<"));
				break;
			case '&':
				if (h < i) {
					add(CharData.of(source.substring(h, i), CharData.IMMEDIATE));
				}
				h = i + 1;
				add(EntityRef.of("amp", "&"));
				break;
			case ']':
				if (i + 2 < source.length() && source.charAt(i + 1) == ']' && source.charAt(i + 2) == '>') {
					i += 2;
					add(CharData.of(source.substring(h, i), CharData.IMMEDIATE));
					h = i + 1;
					add(EntityRef.of("gt", ">"));
					break;
				}
				//FALLTHROUGH
			default:
				break;
			}
		}
		if (h < source.length()) {
			add(CharData.of(source.substring(h), CharData.IMMEDIATE));
		}
	}

	/**
	 * Checks whether this node contains at least one Element.
	 * @return true if one Element is present at least, false otherwise
	 */
	public boolean hasElement() {
		for (Node node : _nodeList) {
			if (node.type == ELEMENT) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Locates the {@code Element} nodes that have the specified name in this node.
	 * If {@code name} is an asterisk, all {@code Element} nodes are returned.
	 * @param name the tag name of the Element to search for
	 * @return the List of Element nodes that match the specified name
	 */
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

	/**
	 * Locates the {@code Element} nodes that have the specified name in this node.
	 * <p>
	 * The name to search for is provided by {@code names[index]}.
	 * If it is an asterisk, all {@code Element} nodes are returned.
	 * <p>
	 * The search is performed recursively for all the descendants in this node.
	 * @param names an array of names in which the name to search for is stored
	 * @param index the index at which the name to search for is stored in {@code names}
	 * @param elementList a {@code List} of {@code Element} to receive the results 
	 * @return {@code elementList}
	 */
	public List<Element> getElementsRecursively(String[] names, int index, List<Element> elementList) {
		for (Node node : _nodeList) {
			if (node instanceof Element element) {
				elementList.addAll(element.getElements(names, index, true));
			}
		}
		return elementList;
	}

}
