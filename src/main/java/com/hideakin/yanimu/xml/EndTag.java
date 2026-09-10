package com.hideakin.yanimu.xml;

import java.util.List;

public class EndTag extends Tag {

	public static EndTag of(String name) {
		return new EndTag(name);
	}

	public static EndTag of(List<Node> nodeList) {
		return new EndTag(nodeList);
	}

	private static final byte[] START_SEQUENCE = {'<', '/'};
	private static final byte[] END_SEQUENCE = {'>'};

	private EndTag(String name) {
		super(ETAG,
				List.of(TerminalNode.of(ETAG_START, START_SEQUENCE),
						TerminalNode.of(NAME, name),
						TerminalNode.of(ETAG_END, END_SEQUENCE)));
	}

	private EndTag(List<Node> nodeList) {
		super(ETAG, nodeList);
	}

}
