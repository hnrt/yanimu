package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class NodeList extends Node {

	protected final List<Node> _nodeList = new ArrayList<>();

	protected NodeList(int type) {
		super(type);
	}

	protected NodeList(int type, Node firstNode) {
		super(type);
		_nodeList.add(firstNode);
	}

	protected NodeList(int type, List<Node> nodeList) {
		super(type);
		_nodeList.addAll(nodeList);
	}

	@Override
	public byte[] sequence() {
		if (_sequence == null) {
			_sequence = buildSequence(_nodeList);
		}
		return _sequence;
	}

	@Override
	public void clearSequence() {
		_sequence = null;
	}

	public List<Node> nodeList() {
		return List.copyOf(_nodeList);
	}

	public int nodeCount() {
		return _nodeList.size();
	}

	public Node firstNode() {
		return _nodeList.get(0);
	}

	public Node lastNode() {
		return _nodeList.get(_nodeList.size() - 1);
	}

}
