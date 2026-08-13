package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Element extends Token {

	public final Token[] startLayout;
	public final Token[] children;
	public final Token[] endLayout;
	public final String name;
	public final Attribute[] attributes;
	public Element parent;

	public Element(List<Token> tokenList, String name, List<Attribute> attributes, Element parent) {
		super(ELEMENT, tokenList);
		this.startLayout = tokenList.toArray(new Token[tokenList.size()]);
		this.children = null;
		this.endLayout = null;
		this.name = name;
		this.attributes = attributes.toArray(new Attribute[attributes.size()]);
		this.parent = parent;
	}

	public Element(Element startTag, List<Token> childList, List<Token> endList) {
		super(ELEMENT, startTag.start, endList.get(endList.size() - 1).end, buildSequence(startTag.sequence, childList, endList));
		this.startLayout = startTag.startLayout;
		this.children = childList.toArray(new Token[childList.size()]);
		this.endLayout = endList.toArray(new Token[endList.size()]);
		this.name = startTag.name;
		this.attributes = startTag.attributes;
		this.parent = startTag.parent;
		for (Token child : childList) {
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
			index += attributes.length;
		}
		return 0 <= index && index < attributes.length ? attributes[index].value : null;
	}

	public String innerText() {
		if (children.length > 0) {
			if (children[0].code == CHAR_DATA) {
				return children[0].toString();
			}
		}
		return null;
	}

	public List<Element> getElements(String name) {
		List<Element> elements = new ArrayList<>();
		for (Token child : children) {
			if (child instanceof Element element) {
				if (element.name.equals(name)) {
					elements.add(element);
				}
				elements.addAll(element.getElements(name));
			}
		}
		return elements;
	}

	private static byte[] buildSequence(byte[] startTag, List<Token> childList, List<Token> endList) {
		int n = startTag.length;
		for (Token token : childList) {
			if (token.sequence == null) continue;
			n += token.sequence.length;
		}
		for (Token token : endList) {
			if (token.sequence == null) continue;
			n += token.sequence.length;
		}
		byte[] sequence = new byte[n];
		int i = 0;
		System.arraycopy(startTag, 0, sequence, i, startTag.length);
		i += startTag.length;
		for (Token token : childList) {
			if (token.sequence == null) continue;
			System.arraycopy(token.sequence, 0, sequence, i, token.sequence.length);
			i += token.sequence.length;
		}
		for (Token token : endList) {
			if (token.sequence == null) continue;
			System.arraycopy(token.sequence, 0, sequence, i, token.sequence.length);
			i += token.sequence.length;
		}
		return sequence;
	}

}
