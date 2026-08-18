package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Attribute extends Node {

	protected List<Node> _nodeList = new ArrayList<>();
	public final String key;
	public final String value;

	public Attribute(List<Node> nodeList, String key, String value) {
		super(ATTRIBUTE, nodeList);
		_nodeList = nodeList;
		this.key = key;
		this.value = value;
	}

	@Override
	public byte[] sequence() {
		if (_sequence == null) {
			_sequence = buildSequence(_nodeList);
		}
		return _sequence;
	}

	public List<Node> nodeList() {
		return _nodeList;
	}

}
