package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Element extends Token {

	public final String name;
	public final List<Attribute> attributes;
	public final List<Token> children;

	public Element(int start, int end, String name, List<Attribute> attributes) {
		super(ELEMENT, start, end);
		this.name = name;
		this.attributes = attributes;
		this.children = new ArrayList<>();
	}

	public Element(Element source, int end) {
		super(ELEMENT, source.start, end);
		this.name = source.name;
		this.attributes = source.attributes;
		this.children = source.children;
	}

	public void addChild(Token token) {
		children.add(token);
	}

	public String innerText() {
		if (children.size() > 0) {
			if (children.get(0) instanceof CharData cd) {
				return cd.text;
			}
		}
		return null;
	}

}
