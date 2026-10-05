package com.hideakin.yanimu.xml;

import java.util.List;

/**
 * An immutable XML node containing a sequence of nodes.
 */
public class ImmutableNodeList extends NodeList {

	/**
	 * Constructs a new object with no nodes.
	 * @param type the node type to be set
	 */
	protected ImmutableNodeList(int type) {
		super(type);
	}

	/**
	 * Constructs a new object with just a node.
	 * @param type the node type to be set
	 * @param firstNode a node to be contained in this object
	 */
	protected ImmutableNodeList(int type, Node firstNode) {
		super(type, firstNode);
	}

	/**
	 * Constructs a new object with a sequence of nodes.
	 * @param type the node type to be set
	 * @param nodeList a sequence of nodes to be contained in this object
	 */
	protected ImmutableNodeList(int type, List<Node> nodeList) {
		super(type, nodeList);
	}

	@Deprecated
	@Override
	public void set(int index, Node node) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::set: FORBIDDEN!");
	}

	@Deprecated
	@Override
	public void add(Node node) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::add: FORBIDDEN!");
	}

	@Deprecated
	@Override
	public void add(int index, Node node) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::add: FORBIDDEN!");
	}

	@Deprecated
	@Override
	public void clear() {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::clear: FORBIDDEN!");
	}

	@Deprecated
	@Override
	public Node remove(int index) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::remove: FORBIDDEN!");
	}

	@Deprecated
	@Override
	public Node remove(Node node) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::remove: FORBIDDEN!");
	}

}
