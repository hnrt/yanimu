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
			if (node.type == Node.NAME) {
				return new String(node.sequence(), StandardCharsets.UTF_8);
			}
		}
		throw new RuntimeException("Tag: No name.");
	}

}
