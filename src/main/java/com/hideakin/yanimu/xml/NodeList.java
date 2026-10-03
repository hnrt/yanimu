package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A mutable XML node containing a sequence of mutable/immutable nodes.
 */
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

	/**
	 * Returns an unmodifiable List containing the nodes in this node list, in its iteration order.
	 * @return an unmodifiable List
	 */
	public List<Node> copy() {
		return List.copyOf(_nodeList);
	}

	/**
	 * Returns the number of nodes in this node list.
	 * @return the number of nodes
	 */
	public int size() {
		return _nodeList.size();
	}

	/**
	 * Return the first node (index 0) in this node list.
	 * If this list contains no nodes, {@code NULL_NODE} is returned.
	 * @return the first node, or {@code NULL_NODE} if there are no nodes
	 */
	public Node first() {
		int size = _nodeList.size();
		return size > 0 ? _nodeList.get(0) : NULL_NODE;
	}

	/**
	 * Returns the second node (index 1) in this node list.
	 * If this list contains fewer than two nodes, {@code NULL_NODE} is returned.
	 * @return the second node, or {@code NULL_NODE} if no second node exists
	 */
	public Node second() {
		int size = _nodeList.size();
		return size > 1 ? _nodeList.get(1) : NULL_NODE;
	}

	/**
	 * Return the last node in this node list.
	 * If this list contains no nodes, {@code NULL_NODE} is returned.
	 * @return the last node, or {@code NULL_NODE} if there are no nodes
	 */
	public Node last() {
		int size = _nodeList.size();
		return size > 0 ? _nodeList.get(size - 1) : NULL_NODE;
	}

	/**
	 * Return the index of the last node in this node list.
	 * If this list contains no nodes, -1 is returned.
	 * @return the index of the last node, or -1 if it doesn't exist
	 */
	public int lastIndex() {
		return _nodeList.size() - 1;
	}

	/**
	 * Returns the node at the specified index in this node list.
	 * @param index the index of the node to return
	 * @return the node at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than or equal to the size)
	 */
	public Node get(int index) {
		int size = _nodeList.size();
		if (index < 0 || size <= index) {
			throw new IndexOutOfBoundsException(getClass().getSimpleName() + "::get: Index out of range.");
		}
		return _nodeList.get(index);
	}

	/**
	 * Returns the node at the specified index in this node list.
	 * <p>
	 * If the index is out of range (less than 0 or greater than or equal to the size),
	 * the specified fallback is returned.
	 * @param index the index of the node to return
	 * @param fallback the value to return if the index is out of range
	 * @return the node at the specified index, or {@code fallback} if the index is out of range
	 */
	public Node get(int index, Node fallback) {
		int size = _nodeList.size();
		if (index < 0 || size <= index) {
			return fallback;
		}
		return _nodeList.get(index);
	}

	/**
	 * Replaces the node at the specified index in this node list with the specified node.
	 * @param index the index of the node to replace
	 * @param node the node with which the node at the specified index is to be replaced
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than or equal to the size)
	 * @throws NullPointerException if null is specified for the node
	 * @throws IllegalArgumentException if a NULL-node is specified
	 */
	public void set(int index, Node node) {
		int size = _nodeList.size();
		if (index < 0 || size <= index) {
			throw new IndexOutOfBoundsException(getClass().getSimpleName() + "::set: Index out of range.");
		} else if (node == null) {
			throw new NullPointerException(getClass().getSimpleName() + "::set: null was specified.");
		} else if (node.type == NULL) {
			throw new IllegalArgumentException(getClass().getSimpleName() + "::set: NULL-node was specified.");
		}
		_nodeList.set(index, node);
	}

	/**
	 * Appends the specified node to the end of this node list.
	 * @param node the node to be appended to this node list
	 * @throws NullPointerException if null is specified for the node
	 * @throws IllegalArgumentException if a NULL-node is specified
	 */
	public void add(Node node) {
		if (node == null) {
			throw new NullPointerException(getClass().getSimpleName() + "::add: null was specified.");
		} else if (node.type == NULL) {
			throw new IllegalArgumentException(getClass().getSimpleName() + "::add: NULL-node was specified.");
		}
		_nodeList.add(node);
	}

	/**
	 * Inserts the specified node at the specified index in this node list.
	 * Shifts the node currently at that position (if any)
	 * and any subsequent nodes to the right (adds one to their indices).
	 * @param index the index at which the node is to be inserted
	 * @param node the node to be inserted
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than the size)
	 * @throws NullPointerException if null is specified for the node
	 * @throws IllegalArgumentException if a NULL-node is specified
	 */
	public void add(int index, Node node) {
		int size = _nodeList.size();
		if (index < 0 || size < index) {
			throw new IndexOutOfBoundsException(getClass().getSimpleName() + "::add: Index out of range.");
		} else if (node == null) {
			throw new NullPointerException(getClass().getSimpleName() + "::add: null was specified.");
		} else if (node.type == NULL) {
			throw new IllegalArgumentException(getClass().getSimpleName() + "::add: NULL-node was specified.");
		}
		_nodeList.add(index, node);
	}

	/**
	 * Removes all of the nodes from this list.
	 * <p>
	 * The list will be empty after this call returns.
	 */
	public void clear() {
		_nodeList.clear();
	}

	/**
	 * Removes the node at the specified index in this list.
	 * Shifts any subsequent nodes to the left (subtracts one from their indices).
	 * Returns the node that was removed from the list.
	 * @param index the index of the node to be removed
	 * @return the node previously at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than or equal to the size)
	 */
	public Node remove(int index) {
		int size = _nodeList.size();
		if (0 <= index && index < size) {
			return _nodeList.remove(index);
		} else {
			throw new IndexOutOfBoundsException(getClass().getSimpleName() + "::remove: Index out of range.");
		}
	}

	/**
	 * Removes the first occurrence of the specified node from this list, if it is present.
	 * Shifts any subsequent nodes to the left (subtracts one from their indices).
	 * Returns the node that was removed from the list.
	 * <p>
	 * If this list does not contain the node, it is unchanged and {@code NULL_NODE} is returned.
	 * @param node the node to be removed
	 * @return the node that was removed from the list, or {@code NULL_NODE} if not found
	 */
	public Node remove(Node node) {
		if (_nodeList.remove(node)) {
			return node;
		} else {
			return NULL_NODE;
		}
	}

	/**
	 * Returns the offset of the specified node from the first node in this list.
	 * <p>
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

	/**
	 * Returns the index of the first occurrence of the specified node in this list.
	 * <p>
	 * The search is performed from the index of 0 to the end of the list.
	 * <p>
	 * If this list doesn't contain the specified node, -1 is returned.
	 * @param target the node to search for
	 * @return the index of the node, or -1 if not found
	 */
	public int find(Node target) {
		return doFind(target, 0, _nodeList.size());
	}

	/**
	 * Returns the index of the first occurrence of the specified node in the search.
	 * <p>
	 * The search is performed from the index of {@code start} to the end of the list.
	 * <p>
	 * If {@code start} is less than 0, 0 is used as the start of search instead.
	 * <p>
	 * If the specified node is not found, -1 is returned.
	 * @param target the node to search for
	 * @param start the index of the node to start the search
	 * @return the index of the node, or -1 if not found
	 */
	public int find(Node target, int start) {
		if (start < 0) {
			start = 0;
		}
		return doFind(target, start, _nodeList.size());
	}

	/**
	 * Returns the index of the first occurrence of the specified node in the search.
	 * <p>
	 * The search is performed from the index of {@code start} to {@code end} - 1.
	 * <p>
	 * If {@code start} is less than 0, 0 is used as the start of search instead.
	 * <p>
	 * If {@code end} is greater than the size of this list, the size is used as the end of search instead.
	 * <p>
	 * If the specified node is not found, -1 is returned.
	 * @param target the node to search for
	 * @param start the index of the node to start the search from
	 * @param end the index to end the search at (the node at this index is not checked)
	 * @return the index of the node, or -1 if not found
	 */
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
