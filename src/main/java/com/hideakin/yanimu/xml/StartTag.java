package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * An mutable node object for XML Start Tag.
 */
public class StartTag extends AttributeTag {

	private static final byte[] STAG_END_SEQUENCE = {'>'};

	/**
	 * Creates a new mutable node of {@code STAG} with no attributes in it.
	 * @param name the tag name to be set to this tag
	 * @return a newly created node of start tag
	 */
	public static StartTag of(String name) {
		return new StartTag(name);
	}

	/**
	 * Creates a new mutable node of {@code STAG} with the specified node sequence.
	 * @param source the node sequence to be set to this tag
	 * @return a newly created node of start tag
	 */
	public static StartTag of(List<Node> source) {
		return new StartTag(source);
	}

	/**
	 * Creates a new mutable node of {@code STAG} with a node sequence of the specified tag.
	 * @param source the tag from which the node sequence to be copied
	 * @return a newly created node of start tag
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
