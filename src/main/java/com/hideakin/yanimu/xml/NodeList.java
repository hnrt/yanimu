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

	public int count() {
		return _nodeList.size();
	}

	public Node first() {
		int size = _nodeList.size();
		return size > 0 ? _nodeList.get(0) : NullNode;
	}

	public Node last() {
		int size = _nodeList.size();
		return size > 0 ? _nodeList.get(size - 1) : NullNode;
	}

	public int lastIndex() {
		return _nodeList.size() - 1;
	}

	public Node get(int index) {
		int size = _nodeList.size();
		return 0 <= index && index < size ? _nodeList.get(index) : NullNode;
	}

	public void set(int index, Node node) {
		int size = _nodeList.size();
		if (0 <= index && index < size) {
			_nodeList.set(index, node);
		} else {
			throw new RuntimeException("NodeList::set: Index out of range.");
		}
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

	public int find(Node target) {
		return doFind(target, 0, _nodeList.size());
	}

	public int find(Node target, int start) {
		if (start < 0) {
			start = 0;
		}
		return doFind(target, start, _nodeList.size());
	}

	public int find(Node target, int start, int end) {
		if (start < 0) {
			start = 0;
		}
		int size = _nodeList.size();
		if (end > size) {
			end = size;
		}
		return doFind(target, start, end);
	}

	private int doFind(Node target, int start, int end) {
		for (int index = start; index < end; index++) {
			Node node = _nodeList.get(index);
			if (node == target) {
				return index;
			}
		}
		return -1;
	}

	public void add(Node node) {
		_nodeList.add(node);
		clearSequence();
	}

	public void add(int index, Node node) {
		int size = _nodeList.size();
		if (index < 0) {
			index = 0;
		} else if (index > size) {
			index = size;
		}
		_nodeList.add(index, node);
		clearSequence();
	}

	public void removeAll() {
		_nodeList.clear();
		clearSequence();
	}

	public Node remove(int index) {
		int size = _nodeList.size();
		if (0 <= index && index < size) {
			clearSequence();
			return _nodeList.remove(index);
		} else {
			return NullNode;
		}
	}

	public Node remove(Node node) {
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			if (_nodeList.get(index) == node) {
				clearSequence();
				return _nodeList.remove(index);
			}
		}
		return NullNode;
	}

	public Node remove(Node node, int start, int end) {
		if (start < 0) {
			start = 0;
		}
		if (end < start) {
			return NullNode;
		}
		int size = _nodeList.size();
		if (end > size) {
			end = size;
		}
		for (int index = start; index < end; index++) {
			if (_nodeList.get(index) == node) {
				clearSequence();
				return _nodeList.remove(index);
			}
		}
		return NullNode;
	}

}
