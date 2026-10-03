package com.hideakin.yanimu.xml;

import java.util.List;

/**
 * An immutable XML node containing a sequence of mutable/immutable nodes.
 */
public class ImmutableNodeList extends NodeList {

	protected ImmutableNodeList(int type) {
		super(type);
	}

	protected ImmutableNodeList(int type, Node firstNode) {
		super(type, firstNode);
	}

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
