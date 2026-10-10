package com.hideakin.yanimu.xml;

import java.util.List;

/**
 * A mutable node for XML End Tag.
 */
public class EndTag extends Tag {

	/**
	 * Creates a new node of type {@code ETAG} with the specified tag name.
	 * @param name a tag name to assign to this node
	 * @return a newly created node
	 */
	public static EndTag of(String name) {
		return new EndTag(name);
	}

	/**
	 * Creates a new node of type {@code ETAG} with the specified sequence of nodes.
	 * @param source a sequence of nodes this node consists of
	 * @return a newly created node
	 */
	public static EndTag of(List<Node> source) {
		return new EndTag(source);
	}

	private static final byte[] START_SEQUENCE = {'<', '/'};
	private static final byte[] END_SEQUENCE = {'>'};

	private EndTag(String name) {
		super(ETAG, List.of(
				ImmutableNode.of(ETAG_START, START_SEQUENCE),
				ImmutableNode.of(NAME, name),
				ImmutableNode.of(ETAG_END, END_SEQUENCE)));
	}

	private EndTag(List<Node> nodeList) {
		super(ETAG, nodeList);
	}

}
