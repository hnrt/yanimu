package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * An mutable node object for XML Empty Element Tag.
 */
public class EmptyElementTag extends AttributeTag {

	private static final byte[] EETAG_END_SEQUENCE = {'/', '>'};

	/**
	 * Creates a new mutable node of {@code EETAG} with no attributes in it.
	 * @param name the tag name to be set to this tag
	 * @return a newly created node of empty element tag
	 */
	public static EmptyElementTag of(String name) {
		return new EmptyElementTag(name);
	}

	/**
	 * Creates a new mutable node of {@code EETAG} with the specified node sequence.
	 * @param source the node sequence to be set to this tag
	 * @return a newly created node of empty element tag
	 */
	public static EmptyElementTag of(List<Node> source) {
		return new EmptyElementTag(source);
	}

	/**
	 * Creates a new mutable node of {@code EETAG} with the specified node sequence.
	 * @param source the tag from which the node sequence to be copied
	 * @return a newly created node of empty element tag
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
