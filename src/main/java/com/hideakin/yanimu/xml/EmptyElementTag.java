package com.hideakin.yanimu.xml;

import java.util.List;

public class EmptyElementTag extends StartTag {

	public static EmptyElementTag of(String name) {
		return new EmptyElementTag(name);
	}

	public static EmptyElementTag of(List<Node> nodeList) {
		return new EmptyElementTag(nodeList);
	}

	private EmptyElementTag(String name) {
		super(EETAG, List.of(
				TerminalNode.of(STAG_START, START_SEQUENCE),
				TerminalNode.of(NAME, name),
				TerminalNode.of(EETAG_END, EETAG_END_SEQUENCE)));
	}

	private EmptyElementTag(List<Node> nodeList) {
		super(EETAG, nodeList);
	}

}
