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

	@Override
	public int length() {
		int length = 0;
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			length += _nodeList.get(index).length();
		}
		return length;
	}

	@Override
	public int length(Node target) {
		if (this == target) {
			return 0;
		}
		int length = 0;
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			Node node = _nodeList.get(index);
			int length2 = node.length(target);
			if (length2 >= 0) {
				return length + length2;
			}
			length += node.length();
		}
		return -1;
	}

	@Override
	public int lineCount() {
		int count = 0;
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			count += _nodeList.get(index).lineCount();
		}
		return count;
	}

	@Override
	public int lineCount(int offset) {
		int count = 0;
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			Node node = _nodeList.get(index);
			int length = node.length();
			if (offset < length) {
				return count + node.lineCount(offset);
			}
			count += node.lineCount();
			offset -= length;
		}
		return count;
	}

	@Override
	public int columnCount(int count) {
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			count = _nodeList.get(index).columnCount(count);
		}
		return count;
	}

	@Override
	public int columnCount(int offset, int count) {
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			Node node = _nodeList.get(index);
			int length = node.length();
			if (offset < length) {
				return node.columnCount(offset, count);
			}
			count = node.columnCount(count);
			offset -= length;
		}
		return count;
	}

	public int findNode(Node target) {
		return doFindNode(target, 0, _nodeList.size());
	}

	public int findNode(Node target, int start) {
		if (start < 0) {
			start = 0;
		}
		return doFindNode(target, start, _nodeList.size());
	}

	public int findNode(Node target, int start, int end) {
		if (start < 0) {
			start = 0;
		}
		int size = _nodeList.size();
		if (end > size) {
			end = size;
		}
		return doFindNode(target, start, end);
	}

	private int doFindNode(Node target, int start, int end) {
		for (int index = start; index < end; index++) {
			Node node = _nodeList.get(index);
			if (node == target) {
				return index;
			}
		}
		return -1;
	}

	public void addNode(Node node) {
		_nodeList.add(node);
	}

	public void addNode(int index, Node node) {
		int size = _nodeList.size();
		if (index < 0) {
			index += size;
		}
		if (index < 0) {
			index = 0;
		}
		index = index < size ? index : size;
		_nodeList.add(index, node);
	}

	public Node removeNode(int index) {
		int size = _nodeList.size();
		if (index < 0) {
			index += size;
		}
		if (0 <= index && index < size) {
			return _nodeList.remove(index);
		}
		return null;
	}

	public Node removeNode(Node node) {
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			if (_nodeList.get(index) == node) {
				return _nodeList.remove(index);
			}
		}
		return null;
	}

	public Node removeNode(Node node, int start, int end) {
		if (start < 0) {
			start = 0;
		}
		if (end < start) {
			return null;
		}
		int size = _nodeList.size();
		if (end > size) {
			end = size;
		}
		for (int index = start; index < end; index++) {
			if (_nodeList.get(index) == node) {
				return _nodeList.remove(index);
			}
		}
		return null;
	}

}
