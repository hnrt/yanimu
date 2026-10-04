package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A super class of {@link AttributeTag} and {@link EndTag}.
 */
public class Tag extends NodeList {

	/**
	 * The tag name of an XML element.
	 */
	public final String name;

	/**
	 * Constructs a newly created instance with a node type and a node sequence.
	 * @param type the node type
	 * @param nodeList the node sequence
	 */
	protected Tag(int type, List<Node> nodeList) {
		super(type, nodeList);
		name = getName(nodeList);
	}

	private static String getName(List<Node> nodeList) {
		for (Node node : nodeList) {
			if (node.type == NAME) {
				return new String(node.sequence(), StandardCharsets.UTF_8);
			}
		}
		throw new RuntimeException("Tag: No name.");
	}

}
