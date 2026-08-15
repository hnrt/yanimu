package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Element extends Node {

	public final List<Node> startLayout;
	public final List<Node> children;
	public final List<Node> endLayout;
	public final String name;
	public final List<Attribute> attributes;
	public Element parent;

	public Element(List<Node> nodeList, String name, List<Attribute> attributes, Element parent) {
		super(ELEMENT, nodeList);
		this.startLayout = nodeList;
		this.children = null;
		this.endLayout = null;
		this.name = name;
		this.attributes = attributes;
		this.parent = parent;
	}

	public Element(Element startTag, List<Node> childList, List<Node> endList) {
		super(ELEMENT, startTag.start, buildSequence(startTag.sequence, childList, endList));
		this.startLayout = startTag.startLayout;
		this.children = childList;
		this.endLayout = endList;
		this.name = startTag.name;
		this.attributes = startTag.attributes;
		this.parent = startTag.parent;
		for (Node child : childList) {
			if (child instanceof Element childElement) {
				childElement.parent = this;
			}
		}
	}

	public String attribute(String key) {
		for (Attribute a : attributes) {
			if (a.key.equals(key)) {
				return a.value;
			}
		}
		return null;
	}

	public String attribute(int index) {
		if (index < 0) {
			index += attributes.size();
		}
		return 0 <= index && index < attributes.size() ? attributes.get(index).value : null;
	}

	public String innerText() {
		if (children.size() > 0) {
			StringBuilder buffer = new StringBuilder();
			for (Node child : children) {
				switch (child.type) {
				case CHAR_DATA:
					buffer.append(child.toString());
					break;
				case ENTITY_REF:
					buffer.append(((EntityRef)child).translated);
					break;
				case CHAR_REF:
					buffer.appendCodePoint(((CharRef)child).codepoint);
					break;
				case CD_SECT:
					buffer.append(((CDATASection)child).innerText);
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
		List<Element> elements = new ArrayList<>();
		for (Node child : children) {
			if (child instanceof Element element) {
				if (element.name.equals(name)) {
					elements.add(element);
				}
				elements.addAll(element.getElements(name));
			}
		}
		return elements;
	}

	private static byte[] buildSequence(byte[] startTag, List<Node> childList, List<Node> endList) {
		int n = startTag.length;
		for (Node node : childList) {
			n += node.sequence.length;
		}
		for (Node node : endList) {
			n += node.sequence.length;
		}
		byte[] sequence = new byte[n];
		int i = 0;
		System.arraycopy(startTag, 0, sequence, i, startTag.length);
		i += startTag.length;
		for (Node node : childList) {
			System.arraycopy(node.sequence, 0, sequence, i, node.sequence.length);
			i += node.sequence.length;
		}
		for (Node node : endList) {
			System.arraycopy(node.sequence, 0, sequence, i, node.sequence.length);
			i += node.sequence.length;
		}
		return sequence;
	}

}
