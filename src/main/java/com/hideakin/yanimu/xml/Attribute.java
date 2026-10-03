package com.hideakin.yanimu.xml;

import static com.hideakin.yanimu.xml.Character.EQ;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A name-value pair embedded in an XML element
 */
public class Attribute extends ImmutableNodeList {

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
	 * Constructs an Attribute object with a name and a value with white spaces.
	 * @param name the name of attribute
	 * @param value the value of attribute
	 * @param quoteCharacter the preferred quote character to use 
	 * @param wsBeforeEq a node of white space that precedes the equal sign node, or null if not exists
	 * @param wsAfterEq a node of white space that follows the equal sign node, or null if not exists
	 */
	public Attribute(String name, String value, int quoteCharacter, Node wsBeforeEq, Node wsAfterEq) {
		super(ATTRIBUTE, nodeListOf(name, value, quoteCharacter, wsBeforeEq, wsAfterEq));
		this.name = name;
		this.value = value;
	}

	private static List<Node> nodeListOf(String name, String value, int quoteCharacter, Node wsBeforeEq, Node wsAfterEq) {
		if (wsBeforeEq != null && wsAfterEq != null) {
			return List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				wsBeforeEq,
				Node.of(EQ, Tag.EQ_SEQUENCE),
				wsAfterEq,
				Node.of(ATT_VALUE, quote(value, quoteCharacter).getBytes(StandardCharsets.UTF_8)));
		} else if (wsBeforeEq != null && wsAfterEq == null) {
			return List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				wsBeforeEq,
				Node.of(EQ, Tag.EQ_SEQUENCE),
				Node.of(ATT_VALUE, quote(value, quoteCharacter).getBytes(StandardCharsets.UTF_8)));
		} else if (wsBeforeEq == null && wsAfterEq != null) {
			return List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				Node.of(EQ, Tag.EQ_SEQUENCE),
				wsAfterEq,
				Node.of(ATT_VALUE, quote(value, quoteCharacter).getBytes(StandardCharsets.UTF_8)));
		} else {
			return List.of(
				Node.of(NAME, name.getBytes(StandardCharsets.UTF_8)),
				Node.of(EQ, Tag.EQ_SEQUENCE),
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

}
