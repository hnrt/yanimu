package com.hideakin.yanimu.xml;

import java.util.List;

public class Attribute extends ImmutableNodeList {

	public final String key;
	public final String value;

	public Attribute(List<Node> nodeList, String key, String value) {
		super(ATTRIBUTE, nodeList);
		this.key = key;
		this.value = value;
	}

}
