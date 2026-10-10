package com.hideakin.yanimu.xml;

import static com.hideakin.yanimu.xml.Character.EQ;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * An immutable node for a name-value pair stored in an XML element.
 */
public class Attribute extends ImmutableNodeList {

	private static final byte[] EQ_SEQUENCE = {'='};

	/**
	 * A name of attribute in an XML element
	 */
	public final String name;

	/**
	 * A value of attribute in an XML element
	 */
	public final String value;

	/**
	 * Initializes a newly created attribute with a sequence of nodes, a name, and a value.
	 * @param sequence a sequence of nodes containing:
	 * <ol>
	 * <li>name</li>
	 * <li>optional white space</li>
	 * <li>equal sign</li>
	 * <li>optional white space</li>
	 * <li>quoted string</li>
	 * </ol>
	 * @param name a name of attribute
	 * @param value a value of attribute
	 */
	public Attribute(List<Node> sequence, String name, String value) {
		super(ATTRIBUTE, sequence);
		this.name = name;
		this.value = value;
		safeSize(); // just to check the sequence
	}

	/**
	 * Initializes a newly created attribute with a name and a value.
	 * @param name a name of attribute
	 * @param value a value of attribute
	 */
	public Attribute(String name, String value) {
		super(ATTRIBUTE, List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				Node.of(EQ, EQ_SEQUENCE),
				Node.of(ATT_VALUE, "\"" + value + "\"")));
		this.name = name;
		this.value = value;
	}

	/**
	 * Initializes a newly created attribute with a name, a value, and an attribute to copy the nodes from.
	 * @param name a name of attribute
	 * @param value a value of attribute
	 * @param source an attribute from which the white space nodes are copied
	 */
	public Attribute(String name, String value, Attribute source) {
		super(ATTRIBUTE, nodeListOf(name, value, source));
		this.name = name;
		this.value = value;
	}

	private static List<Node> nodeListOf(String name, String value, Attribute source) {
		String qs = "\"" + value + "\"";
		int size = source.safeSize();
		if (size == 3) {
			return List.of(
					Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
					Node.of(EQ, EQ_SEQUENCE),
					Node.of(ATT_VALUE, qs));
		} else if (size == 4) {
			if (source.get(1).type == S) {
				return List.of(
						Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
						source.get(1),
						Node.of(EQ, EQ_SEQUENCE),
						Node.of(ATT_VALUE, qs));
			} else if (source.get(2).type == S) {
				return List.of(
						Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
						Node.of(EQ, EQ_SEQUENCE),
						source.get(2),
						Node.of(ATT_VALUE, qs));
			} else {
				throw new RuntimeException("Attribute::nodeListOf: BUG1!");
			}
		} else if (size == 5) {
			return List.of(
					Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
					source.get(1),
					Node.of(EQ, EQ_SEQUENCE),
					source.get(3),
					Node.of(ATT_VALUE, qs));
		} else {
			throw new RuntimeException("Attribute::nodeListOf: BUG2!");
		}
	}

	/**
	 * Verifies if this node has a valid sequence of nodes and returns the number of nodes. 
	 * @return the number of nodes
	 * @throws CorruptionException if this node has a malformed sequence of nodes.
	 */
	public int safeSize() {
		int size = super.size();
		switch (size) {
		case 3:
			if (super.get(0).type == NAME
				&& super.get(1).type == EQ
				&& super.get(2).type == ATT_VALUE) {
				return 3;
			}
			break;
		case 4:
			if (super.get(0).type == NAME
				&& super.get(1).type == S
				&& super.get(2).type == EQ
				&& super.get(3).type == ATT_VALUE) {
				return 4;
			} else if (super.get(0).type == NAME
				&& super.get(1).type == EQ
				&& super.get(2).type == S
				&& super.get(3).type == ATT_VALUE) {
				return 4;
			}
			break;
		case 5:
			if (super.get(0).type == NAME
				&& super.get(1).type == S
				&& super.get(2).type == EQ
				&& super.get(3).type == S
				&& super.get(4).type == ATT_VALUE) {
				return 5;
			}
			break;
		default:
			break;
		}
		if (size <= 0) {
			throw new CorruptionException(this);
		} else if (super.get(0).type != NAME) {
			throw new CorruptionException(this, 0);
		} else if (size <= 1) {
			throw new CorruptionException(this, 1);
		} else if (super.get(1).type != S && super.get(1).type != EQ) {
			throw new CorruptionException(this, 1);
		} else if (size <= 2) {
			throw new CorruptionException(this, 2);
		} else if ((super.get(1).type == S && super.get(2).type != EQ)
				|| (super.get(1).type == EQ && super.get(2).type != S)) {
			throw new CorruptionException(this, 2);
		} else if (size <= 3) {
			throw new CorruptionException(this, 3);
		} else if ((super.get(2).type == EQ && super.get(3).type != S && super.get(3).type != ATT_VALUE)
				|| (super.get(2).type == S && super.get(3).type != ATT_VALUE)) {
			throw new CorruptionException(this, 3);
		} else if (size <= 4) {
			throw new CorruptionException(this, 4);
		} else if ((super.get(3).type == S && super.get(4).type != ATT_VALUE)
				|| (super.get(3).type == ATT_VALUE)) {
			throw new CorruptionException(this, 4);
		} else {
			throw new CorruptionException(this, 5);
		}
	}

}
