package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A mutable node for XML Empty Element Tag.
 */
public class EmptyElementTag extends AttributeTag {

	private static final byte[] EETAG_END_SEQUENCE = {'/', '>'};

	/**
	 * Creates a new node of type {@code EETAG} with the specified tag name.
	 * @param name a tag name to assign to this node
	 * @return a newly created node
	 */
	public static EmptyElementTag of(String name) {
		return new EmptyElementTag(name);
	}

	/**
	 * Creates a new node of type {@code EETAG} with the specified sequence of nodes.
	 * @param source a sequence of nodes this node consists of
	 * @return a newly created node
	 */
	public static EmptyElementTag of(List<Node> source) {
		return new EmptyElementTag(source);
	}

	/**
	 * Creates a new node of type {@code EETAG} with the specified attribute tag.
	 * @param source an attribute tag the nodes of which are to be contained in this node
	 * @return a newly created node
	 */
	public static EmptyElementTag of(AttributeTag source) {
		List<Node> nodeList = new ArrayList<>(source._nodeList);
		nodeList.set(nodeList.size() - 1, ImmutableNode.of(EETAG_END, EETAG_END_SEQUENCE));
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

	@Override
	public int safeSize() {
		return safeSize(EETAG_END);
	}

}
