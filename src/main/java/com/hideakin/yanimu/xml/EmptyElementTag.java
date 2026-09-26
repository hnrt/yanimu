package com.hideakin.yanimu.xml;

import java.util.List;

public class EmptyElementTag extends AttributeTag {

	public static EmptyElementTag of(String name) {
		return new EmptyElementTag(name);
	}

	public static EmptyElementTag of(List<Node> nodeList) {
		return new EmptyElementTag(nodeList);
	}

	private EmptyElementTag(String name) {
		super(EETAG, List.of(
				ImmutableNode.of(STAG_START, START_SEQUENCE),
				ImmutableNode.of(NAME, name),
				ImmutableNode.of(EETAG_END, EETAG_END_SEQUENCE)));
	}

	private EmptyElementTag(List<Node> nodeList) {
		super(EETAG, nodeList);
	}

}
