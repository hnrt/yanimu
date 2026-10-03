package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class AttributeTag extends Tag {


	protected AttributeTag(int type, List<Node> nodeList) {
		super(type, nodeList);
	}

	/**
	 * Creates a new start tag node that has the same attributes as this node.
	 * @return a newly created start tag node
	 */
	public StartTag toStartTag() {
		if (last().type == STAG_END) {
			return StartTag.of(_nodeList);
		} else if (last().type == EETAG_END) {
			List<Node> nodeList = new ArrayList<>(_nodeList);
			nodeList.set(nodeList.size() - 1, ImmutableNode.of(STAG_END, STAG_END_SEQUENCE));
			return StartTag.of(nodeList);
		} else {
			throw new RuntimeException(getClass().getSimpleName() + "::toStartTag: Possible corruption.");
		}
	}

	/**
	 * Creates a new empty element tag node that has the same attributes as this node.
	 * @return a newly created empty element tag node
	 */
	public EmptyElementTag toEmptyElementTag() {
		if (last().type == EETAG_END) {
			return EmptyElementTag.of(_nodeList);
		} else if (last().type == STAG_END) {
			List<Node> nodeList = new ArrayList<>(_nodeList);
			nodeList.set(nodeList.size() - 1, ImmutableNode.of(EETAG_END, EETAG_END_SEQUENCE));
			return EmptyElementTag.of(nodeList);
		} else {
			throw new RuntimeException(getClass().getSimpleName() + "::toEmptyElementTag: Possible corruption.");
		}
	}

}
