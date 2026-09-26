package com.hideakin.yanimu.xml;

import java.util.List;

/**
 * An immutable node containing a sequence of mutable/immutable nodes.
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

	@Override
	public void set(int index, Node node) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::set: FORBIDDEN!");
	}

	@Override
	public void add(Node node) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::add: FORBIDDEN!");
	}

	@Override
	public void add(int index, Node node) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::add: FORBIDDEN!");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::clear: FORBIDDEN!");
	}

	@Override
	public Node remove(int index) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::remove: FORBIDDEN!");
	}

	@Override
	public Node remove(Node node) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::remove: FORBIDDEN!");
	}

	@Override
	public Node remove(Node node, int start, int end) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + "::remove: FORBIDDEN!");
	}

}
