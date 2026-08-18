package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class Tag extends Node {

	protected List<Node> _nodeList;
	public final String name;

	protected Tag(int type, List<Node> nodeList) {
		super(type);
		_nodeList = nodeList;
		name = getName(nodeList);
	}

	@Override
	public byte[] sequence() {
		if (_sequence == null) {
			_sequence = buildSequence(_nodeList);
		}
		return _sequence;
	}

	private static String getName(List<Node> nodeList) {
		for (Node node : nodeList) {
			if (node.type == Node.NAME) {
				return new String(node.sequence(), StandardCharsets.UTF_8);
			}
		}
		throw new RuntimeException("Tag: No name.");
	}

}
