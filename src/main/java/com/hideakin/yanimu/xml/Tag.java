package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class Tag extends NodeList {

	public final String name;

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

	@Override
	public void set(int index, Node node) {
		throw new RuntimeException(this.getClass().getSimpleName() + "::set: Forbidden.");
	}

	@Override
	public void add(Node node) {
		throw new RuntimeException(this.getClass().getSimpleName() + "::add: Forbidden.");
	}

	@Override
	public void add(int index, Node node) {
		throw new RuntimeException(this.getClass().getSimpleName() + "::add: Forbidden.");
	}

	@Override
	public void removeAll() {
		throw new RuntimeException(this.getClass().getSimpleName() + "::removeAll: Forbidden.");
	}

	@Override
	public Node remove(int index) {
		throw new RuntimeException(this.getClass().getSimpleName() + "::remove: Forbidden.");
	}

	@Override
	public Node remove(Node node) {
		throw new RuntimeException(this.getClass().getSimpleName() + "::remove: Forbidden.");
	}

	@Override
	public Node remove(Node node, int start, int end) {
		throw new RuntimeException(this.getClass().getSimpleName() + "::remove: Forbidden.");
	}

}
