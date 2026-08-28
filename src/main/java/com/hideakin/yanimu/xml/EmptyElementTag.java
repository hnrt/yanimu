package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class EmptyElementTag extends StartTag {

	public static EmptyElementTag of(String name) {
		return new EmptyElementTag(name);
	}

	public static EmptyElementTag of(List<Node> nodeList, List<Attribute> attributeList) {
		return new EmptyElementTag(nodeList, attributeList);
	}

	private EmptyElementTag(String name) {
		super(EETAG, new ArrayList<>(
				List.of(Node.of(STAG_START, START_SEQUENCE),
						Node.of(NAME, name),
						Node.of(EETAG_END, EETAG_END_SEQUENCE))),
				new ArrayList<>());
	}

	private EmptyElementTag(List<Node> nodeList, List<Attribute> attributeList) {
		super(EETAG, nodeList, attributeList);
	}

}
