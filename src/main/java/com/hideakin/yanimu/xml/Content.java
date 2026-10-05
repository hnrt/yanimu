package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A mutable node object containing children of an XML element.
 */
public class Content extends NodeList {

	/**
	 * Returns a new {@code Content} object with no children.
	 * @return a newly created {@code Content} object
	 */
	public static Content of() {
		return new Content();
	}

	/**
	 * Returns a new {@code Content} object with a child of the specified node.
	 * @param node a child to be contained in this object
	 * @return a newly created {@code Content} object
	 */
	public static Content of(Node node) {
		return new Content(node);
	}

	/**
	 * Returns a new {@code Content} object with children.
	 * @param nodeList a child node sequence to be contained in this object
	 * @return a newly created {@code Content} object
	 */
	public static Content of(List<Node> nodeList) {
		return new Content(nodeList);
	}

	/**
	 * Returns a new {@code Content} object with a child of the character data node.
	 * @param text character data for a child node to be contained in this object
	 * @return a newly created {@code Content} object
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
	 * Concatenates text of each child node contained in this object in their stored order
	 * and returns the resulting {@code String}. 
	 * @return the resulting {@code String}
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
	 * Replaces all the child nodes currently contained in this object
	 * with one or more newly created child nodes using the specified {@code String}.
	 * Depending on the characters in the specified {@code String},
	 * child nodes to be newly created are {@code CharData} and {@code EntityRef}. 
	 * @param source a {@code String} to be used to create a child node of the character data.
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
	 * Checks whether this Content contains at least one Element.
	 * @return true if at least one Element is present, false otherwise
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
	 * Locates the Element nodes that have the specified name in this Content node.
	 * If {@code name} is an asterisk, all Element nodes are returned.
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
	 * Locates the {@code Element} nodes that have the specified name in this {@code Content} node.
	 * <p>
	 * The name to search for is provided by {@code names[index]}.
	 * If it is an asterisk, all {@code Element} nodes are returned.
	 * <p>
	 * The search is performed recursively for all the descendants in this {@code Content} node.
	 * @param names the array of names in which the name to search for is stored
	 * @param index the index at which the name to search for is stored in the {@code names} array
	 * @param elementList the {@code List} of {@code Element} to receive the results 
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
