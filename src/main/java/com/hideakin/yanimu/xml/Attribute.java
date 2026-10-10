package com.hideakin.yanimu.xml;

import static com.hideakin.yanimu.xml.Character.EQ;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * An immutable node object for storing a name-value pair contained in an XML element.
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
	 * Constructs an Attribute object with a sequence of XML nodes, a name, and a value.
	 * @param nodeList List of nodes containing a name node,
	 * an optional white space node,
	 * an equal sign node,
	 * an optional white space node,
	 * and a quoted string node
	 * @param name the name of attribute
	 * @param value the value of attribute
	 */
	public Attribute(List<Node> nodeList, String name, String value) {
		super(ATTRIBUTE, nodeList);
		this.name = name;
		this.value = value;
	}

	/**
	 * Constructs an Attribute object with a name and a value.
	 * @param name the name of attribute
	 * @param value the value of attribute
	 */
	public Attribute(String name, String value) {
		super(ATTRIBUTE, nodeListOf(name, value, '\"', null, null));
		this.name = name;
		this.value = value;
	}

	/**
	 * Constructs an Attribute object with a name and a value.
	 * @param name the name of attribute
	 * @param value the value of attribute
	 * @param source List of nodes to copy
	 */
	public Attribute(String name, String value, Attribute source) {
		super(ATTRIBUTE, nodeListOf(name, value, source));
		this.name = name;
		this.value = value;
	}

	private static List<Node> nodeListOf(String name, String value, Attribute source) {
		int size = source.size();
		if (size == 3) {
			return nodeListOf(name, value, source.last().sequence()[0], null, null);
		} else if (size == 4) {
			if (source.get(1).type == S) {
				return nodeListOf(name, value, source.last().sequence()[0], source.get(1), null);
			} else if (source.get(2).type == S) {
				return nodeListOf(name, value, source.last().sequence()[0], null, source.get(2));
			} else {
				throw new RuntimeException("Attribute::nodeListOf: BUG1!");
			}
		} else if (size == 5) {
			return nodeListOf(name, value, source.last().sequence()[0], source.get(1), source.get(3));
		} else {
			throw new RuntimeException("Attribute::nodeListOf: BUG2!");
		}
	}

	private static List<Node> nodeListOf(String name, String value, int quoteCharacter, Node wsBeforeEq, Node wsAfterEq) {
		if (wsBeforeEq != null && wsAfterEq != null) {
			return List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				wsBeforeEq,
				Node.of(EQ, EQ_SEQUENCE),
				wsAfterEq,
				Node.of(ATT_VALUE, quote(value, quoteCharacter).getBytes(StandardCharsets.UTF_8)));
		} else if (wsBeforeEq != null && wsAfterEq == null) {
			return List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				wsBeforeEq,
				Node.of(EQ, EQ_SEQUENCE),
				Node.of(ATT_VALUE, quote(value, quoteCharacter).getBytes(StandardCharsets.UTF_8)));
		} else if (wsBeforeEq == null && wsAfterEq != null) {
			return List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				Node.of(EQ, EQ_SEQUENCE),
				wsAfterEq,
				Node.of(ATT_VALUE, quote(value, quoteCharacter).getBytes(StandardCharsets.UTF_8)));
		} else {
			return List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				Node.of(EQ, EQ_SEQUENCE),
				Node.of(ATT_VALUE, quote(value, quoteCharacter).getBytes(StandardCharsets.UTF_8)));
		}
	}

	/**
	 * Creates a new byte sequence representing the value of an attribute node,
	 * enclosed in the specified quote characters.<br/>
	 * The provided {@code source} is treated as the raw attribute value and
	 * is not expected to include any quote characters.
	 * @param source the raw attribute value, not enclosed in quotes
	 * @param quoteCharacter the quote character used to enclose {@code source}
	 * @return a newly created byte array containing the quoted attribute value
	 */
	public static String quote(String source, int quoteCharacter) {
		return quote(source, quoteCharacter, 0);
	}

	/**
	 * Creates a new String representing the value of an attribute node,
	 * enclosed in the specified quote character.<br/>
	 * The provided {@code source} is treated as the raw attribute value and
	 * is not expected to include any quote characters.<br/>
	 * If the {@code source} contains the specified quote character, this
	 * method attempts to use the alternative quote character.<br/>
	 * For example, if the first attempt uses the double quotation mark,
	 * the second attempt uses the apostrophe, and vice versa.<br/>
	 * If the {@code source} contains both supported quote characters,
	 * this method replaces the quote characters occurring in the {@code source} with the corresponding entity reference.
	 * @param source the raw attribute value, not enclosed in quotes
	 * @param quoteCharacter the quote character used to enclose {@code source}
	 * @param quoteCharacterAttempted the quote character used previously
	 * @return a newly created String containing the quoted attribute value
	 */
	private static String quote(String source, int quoteCharacter, int quoteCharacterAttempted) {
		int pos = source.indexOf(quoteCharacter);
		if (pos >= 0) {
			if (quoteCharacterAttempted == 0) {
				return quote(source, quoteCharacter == '\"' ? '\'' : '\"' , quoteCharacter);
			} else {
				String entity = quoteCharacter == '\"' ? "&quot;" : "&apos;";
				StringBuilder buffer = new StringBuilder();
				buffer.append((char)quoteCharacter);
				int start = 0;
				do {
					if (start < pos) {
						buffer.append(source.substring(start, pos));
					}
					buffer.append(entity);
					start = pos + 1;
					pos = source.indexOf(quoteCharacter, start);
				} while (pos >= start);
				if (start < source.length()) {
					buffer.append(source.substring(start));
				}
				buffer.append((char)quoteCharacter);
				return buffer.toString();
			}
		} else {
			StringBuilder buffer = new StringBuilder();
			buffer.append((char)quoteCharacter);
			buffer.append(source);
			buffer.append((char)quoteCharacter);
			return buffer.toString();
		}
	}

	@Override
	public int size() {
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
