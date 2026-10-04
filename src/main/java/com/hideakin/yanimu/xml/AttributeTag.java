package com.hideakin.yanimu.xml;

import java.util.List;

/**
 * A super class for {@link StartTag} and {@link EmptyElementTag}.
 */
public class AttributeTag extends Tag {

	/**
	 * The index number of the first Attribute node.
	 */
	public static final int LOWER_BOUND = 3;

	/**
	 * The increment to reach the next Attribute node.
	 */
	public static final int STEP = 2;

	/**
	 * The byte sequence of the start of Start Tag / Empty Element Tag.
	 */
	protected static final byte[] START_SEQUENCE = {'<'};

	/**
	 * Constructs a newly created instance with a node type and a node sequence.
	 * @param type the node type to set
	 * @param nodeList the node sequence
	 */
	protected AttributeTag(int type, List<Node> nodeList) {
		super(type, nodeList);
	}

	/**
	 * Creates a new start tag node that has the same attributes as this node.
	 * @return a newly created start tag node
	 */
	public StartTag toStartTag() {
		int lastType = last().type; 
		if (lastType == STAG_END || lastType == EETAG_END) {
			return StartTag.of(this);
		} else {
			throw new CorruptionException(this, lastIndex());
		}
	}

	/**
	 * Creates a new empty element tag node that has the same attributes as this node.
	 * @return a newly created empty element tag node
	 */
	public EmptyElementTag toEmptyElementTag() {
		int lastType = last().type; 
		if (lastType == EETAG_END || lastType == STAG_END) {
			return EmptyElementTag.of(this);
		} else {
			throw new CorruptionException(this, lastIndex());
		}
	}

	/**
	 * Verifies if this AttributeTag correctly contains a sequence of nodes
	 * as a start tag or an empty element tag
	 * and then returns the number of nodes in this AttributeTag.
	 * @return the number of nodes
	 */
	public int safeSize() {
		throw new RuntimeException(getClass().getSimpleName() + "::safeSize: NO IMPLEMENTATION!");
	}

	/**
	 * Verifies if this AttributeTag correctly contains a sequence of nodes
	 * as a start tag or an empty element tag
	 * and then returns the number of nodes in this AttributeTag.
	 * @param endType the node type of the last node
	 * @return the number of nodes
	 */
	protected int safeSize(int endType) {
		int size = super.size();
		if (size <= 0) {
			throw new CorruptionException(this);
		} else if (super.get(0).type != STAG_START) {
			throw new CorruptionException(this, 0);
		} else if (size == 1 || super.get(1).type != NAME) {
			throw new CorruptionException(this, 1);
		} else if (size == 2 || (super.get(2).type != endType && super.get(2).type != S)) {
			throw new CorruptionException(this, 2);
		} else if (size == 3) {
			if (super.get(2).type == endType) {
				return 3;
			} else {
				throw new CorruptionException(this, 3);
			}
		} else {
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				if (super.get(nodeIndex).type == S) {
					// Looks good.
				} else {
					throw new CorruptionException(this, nodeIndex);
				}
				if (super.get(nodeIndex + 1).type == ATTRIBUTE) {
					continue;
				} else {
					throw new CorruptionException(this, nodeIndex + 1);
				}
			}
			if (upperBound < size - 1) {
				if (super.get(upperBound).type == S) {
					// OK
				} else {
					throw new CorruptionException(this, upperBound);
				}
			}
			if (super.get(size - 1).type == endType) {
				return size;
			} else {
				throw new CorruptionException(this, size - 1);
			}
		}
	}

	/**
	 * Verifies if this AttributeTag correctly contains a sequence of nodes
	 * as a start tag or an empty element tag
	 * and then returns the number of attributes in this node.
	 * @return the number of attributes in this node.
	 */
	public int attributeCount() {
		return (safeSize() - LOWER_BOUND) / STEP;
	}

	/**
	 * Returns an Attribute object at the specified index.
	 * @param index the index of the Attribute object to be retrieved
	 * @return an Attribute object if it exists, or null otherwise
	 */
	public Attribute attribute(int index) {
		int nodeIndex = LOWER_BOUND + index * STEP;
		if (LOWER_BOUND <= nodeIndex && nodeIndex < safeSize() - 1) {
			return (Attribute)super.get(nodeIndex);
		} else {
			return null;
		}
	}

	/**
	 * Returns an Attribute object associated with the specified name.
	 * @param name the name of the Attribute object to be retrieved
	 * @return an Attribute object, or null if it is not found
	 */
	public Attribute attribute(String name) {
		int size = safeSize();
		for (int nodeIndex = LOWER_BOUND; nodeIndex < size - 1; nodeIndex += STEP) {
			Attribute a = (Attribute)super.get(nodeIndex);
			if (a.name.equals(name)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Returns the node index of an Attribute object at the specified attribute index.
	 * @param index the Attribute index of a node to be retrieved
	 * @return the node index of an Attribute object, or null if the Attribute index is out of range
	 */
	public int attributeIndex(int index) {
		int nodeIndex = LOWER_BOUND + index * STEP;
		if (LOWER_BOUND <= nodeIndex && nodeIndex < safeSize() - 1) {
			return nodeIndex;
		} else {
			return -1;
		}
	}

	/**
	 * Returns the node index of an Attribute object associated with the specified name.
	 * @param name the name of the Attribute object to be retrieved
	 * @return the node index of an Attribute object, or null if it is not found
	 */
	public int attributeIndex(String name) {
		int size = safeSize();
		for (int nodeIndex = LOWER_BOUND; nodeIndex < size - 1; nodeIndex += STEP) {
			Attribute a = (Attribute)super.get(nodeIndex);
			if (a.name.equals(name)) {
				return nodeIndex;
			}
		}
		return -1;
	}

	/**
	 * Returns the node index at the end of the Attribute object nodes.
	 * The node index to be returned is that of the tag end node or the white space node before the tag end node.
	 * @return the node index at the end of the Attribute object nodes.
	 */
	public int upperBound() {
		return (super.size() - 1) & ~1;
	}

}
