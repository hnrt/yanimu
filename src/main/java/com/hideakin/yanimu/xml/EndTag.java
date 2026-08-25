package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class EndTag extends Tag {

	public static final byte[] START_SEQUENCE = {'<', '/'};
	public static final byte[] END_SEQUENCE = {'>'};

	public EndTag(String name) {
		super(ETAG, new ArrayList<>(
				List.of(new Node(ETAG_START, START_SEQUENCE),
						new Node(NAME, name),
						new Node(ETAG_END, END_SEQUENCE))));
	}

	public EndTag(List<Node> nodeList) {
		super(ETAG, nodeList);
	}

}
