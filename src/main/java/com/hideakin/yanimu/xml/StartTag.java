package com.hideakin.yanimu.xml;

import java.util.List;

public class StartTag extends AttributeTag {

	public static StartTag of(String name) {
		return new StartTag(name);
	}

	public static StartTag of(List<Node> nodeList) {
		return new StartTag(nodeList);
	}

	protected StartTag(String name) {
		super(STAG, List.of(
				ImmutableNode.of(STAG_START, START_SEQUENCE),
				ImmutableNode.of(NAME, name),
				ImmutableNode.of(STAG_END, STAG_END_SEQUENCE)));
	}

	protected StartTag(List<Node> nodeList) {
		super(STAG, nodeList);
	}

}
