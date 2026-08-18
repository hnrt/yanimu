package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EndTag extends Tag {

	public static final byte[] START_SEQUENCE = {'<', '/'};
	public static final byte[] END_SEQUENCE = {'>'};

	public EndTag(List<Node> nodeList) {
		super(ETAG, nodeList);
	}

	public EndTag(String name) {
		super(ETAG, new ArrayList<Node>(
				List.of(new Node(ETAG_START, START_SEQUENCE),
						new Node(NAME, name.getBytes(StandardCharsets.UTF_8)),
						new Node(TAG_END, END_SEQUENCE))));
	}

}
