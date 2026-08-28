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
				List.of(Node.of(ETAG_START, START_SEQUENCE),
						Node.of(NAME, name),
						Node.of(ETAG_END, END_SEQUENCE)));
	}

	private EndTag(List<Node> nodeList) {
		super(ETAG, nodeList);
	}

}
