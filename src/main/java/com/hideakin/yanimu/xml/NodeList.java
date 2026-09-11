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
		byte[] destination = new byte[length()];
		int offset = 0;
		for (Node node : _nodeList) {
			byte[] source = node.sequence(); 
			int length = source.length;
			System.arraycopy(source, 0, destination, offset, length);
			offset += length;
		}
		return destination;
	}

	@Override
	public int length() {
		int length = 0;
		for (Node node : _nodeList) {
			length += node.length();
		}
		return length;
	}

	public List<Node> nodeList() {
		return List.copyOf(_nodeList);
	}

	public int size() {
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

	public void add(Node node) {
		_nodeList.add(node);
	}

	public void add(int index, Node node) {
		int size = _nodeList.size();
		if (index < 0) {
			index = 0;
		} else if (index > size) {
			index = size;
		}
		_nodeList.add(index, node);
	}

	public void removeAll() {
		_nodeList.clear();
	}

	public Node remove(int index) {
		int size = _nodeList.size();
		if (0 <= index && index < size) {
			return _nodeList.remove(index);
		} else {
			return NullNode;
		}
	}

	public Node remove(Node node) {
		int size = _nodeList.size();
		for (int index = 0; index < size; index++) {
			if (_nodeList.get(index) == node) {
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
				return _nodeList.remove(index);
			}
		}
		return NullNode;
	}

	/**
	 * Returns the offset of the specified node from the first node in this list.<br/>
	 * If the node is not found, this method returns -1.
	 * @param target the node to search for
	 * @return the offset of the node, or -1 if not found
	 */
	@Override
	public int offset(Node target) {
		if (this == target) {
			return 0;
		}
		int length = 0;
		for (Node node : _nodeList) {
			int delta = node.offset(target);
			if (delta >= 0) {
				return length + delta;
			}
			length += node.length();
		}
		return -1;
	}

	@Override
	public int lineCount() {
		int count = 0;
		for (Node node : _nodeList) {
			count += node.lineCount();
		}
		return count;
	}

	@Override
	public int lineCount(int offset) {
		int count = 0;
		int remaining = offset;
		for (Node node : _nodeList) {
			int length = node.length();
			if (remaining < length) {
				return count + node.lineCount(remaining);
			}
			count += node.lineCount();
			remaining -= length;
		}
		return count;
	}

	@Override
	public int columnCount(int initialCount) {
		int count = initialCount;
		for (Node node : _nodeList) {
			count = node.columnCount(count);
		}
		return count;
	}

	@Override
	public int columnCount(int initialCount, int offset) {
		int count = initialCount;
		int remaining = offset;
		for (Node node : _nodeList) {
			int length = node.length();
			if (remaining < length) {
				return node.columnCount(count, remaining);
			}
			count = node.columnCount(count);
			remaining -= length;
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

}
