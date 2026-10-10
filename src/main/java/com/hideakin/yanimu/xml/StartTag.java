package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A mutable node for XML Start Tag.
 */
public class StartTag extends AttributeTag {

	private static final byte[] STAG_END_SEQUENCE = {'>'};

	/**
	 * Creates a new node of type {@code STAG} with the specified tag name.
	 * @param name a tag name to assign to this node
	 * @return a newly created node
	 */
	public static StartTag of(String name) {
		return new StartTag(name);
	}

	/**
	 * Creates a new node of type {@code STAG} with the specified sequence of nodes.
	 * @param source a sequence of nodes this node consists of
	 * @return a newly created node
	 */
	public static StartTag of(List<Node> source) {
		return new StartTag(source);
	}

	/**
	 * Creates a new node of type {@code STAG} with the specified attribute tag.
	 * @param source an attribute tag the nodes of which are to be contained in this node
	 * @return a newly created node
	 */
	public static StartTag of(AttributeTag source) {
		List<Node> nodeList = new ArrayList<>(source._nodeList);
		nodeList.set(nodeList.size() - 1, ImmutableNode.of(STAG_END, STAG_END_SEQUENCE));
		return new StartTag(nodeList);
	}

	private StartTag(String name) {
		super(STAG, List.of(
				ImmutableNode.of(STAG_START, START_SEQUENCE),
				ImmutableNode.of(NAME, name),
				ImmutableNode.of(STAG_END, STAG_END_SEQUENCE)));
	}

	private StartTag(List<Node> nodeList) {
		super(STAG, nodeList);
	}

	@Override
	public int safeSize() {
		return super.safeSize(STAG_END);
	}

}
