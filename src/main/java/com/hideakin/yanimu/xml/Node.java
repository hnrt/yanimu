package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.hideakin.yanimu.xml.internal.DebugHelper.NODE_TYPES;

public class Node {

	public static final int EOF = -1;
	public static final int NULL = 0;
	public static final int HT = 9; // HORIZONTAL TABULATION
	public static final int LF = 10; // LINE FEED
	public static final int CR = 13; // CARRIAGE RETURN
	private static final int SP = 32; // SPACE (private to avoid incorrect use)
	public static final int EQ = 61;
	public static final int TAG_END = 62;

	// Character.MAX_CODE_POINT : 0x10FFFF = 1114111

	public static final int DOCUMENT = 2000100;
	public static final int S = 2000300;
	public static final int NAME = 2000500;
	public static final int NMTOKEN = 2000700;
	public static final int ENTITY_VALUE = 2000900;
	public static final int ATT_VALUE = 2001000;
	public static final int SYSTEM_LITERAL = 2001100;
	public static final int PUBID_LITERAL = 2001200;
	public static final int CHAR_DATA = 2001400;
	public static final int COMMENT = 2001500;
	public static final int PI = 2001600;
	public static final int PI_START = 2001601;
	public static final int PI_BODY = 2001602;
	public static final int PI_END = 2001603;
	public static final int PI_TARGET = 2001700;
	public static final int CD_SECT = 2001800;
	public static final int XML_DECL = 2002300;
	public static final int XML_START = 2002301;
	public static final int XML_END = 2002302;
	public static final int DOCTYPE_DECL = 2002800;
	public static final int DOCTYPE_DECL_START = 2002801;
	public static final int ELEMENT = 2003900;
	public static final int STAG = 2004000;
	public static final int STAG_START = 2004001;
	public static final int STAG_END = 2004002;
	public static final int ATTRIBUTE = 2004100;
	public static final int ETAG = 2004200;
	public static final int ETAG_START = 2004201;
	public static final int ETAG_END = 2004202;
	public static final int CONTENT = 2004300;
	public static final int EETAG = 2004400;
	public static final int EETAG_END = 2004401;
	public static final int ELEMENT_DECL = 2004500;
	public static final int ELEMENT_DECL_START = 2004501;
	public static final int CONTENTSPEC = 2004600;
	public static final int EMPTY = 2004601;
	public static final int ANY = 2004602;
	public static final int PCDATA = 2005101;
	public static final int PCDATA_END = 2005102;
	public static final int ATTLIST_DECL = 2005200;
	public static final int ATTLIST_DECL_START = 2005201;
	public static final int TYPE_CDATA = 2005501;
	public static final int TYPE_ID = 2005601;
	public static final int TYPE_IDREF = 2005602;
	public static final int TYPE_IDREFS = 2005603;
	public static final int TYPE_ENTITY = 2005604;
	public static final int TYPE_ENTITIES = 2005605;
	public static final int TYPE_NMTOKEN = 2005606;
	public static final int TYPE_NMTOKENS = 2005607;
	public static final int TYPE_NOTATION = 2005801;
	public static final int REQUIRED = 2006001;
	public static final int IMPLIED = 2006002;
	public static final int FIXED = 2006003;
	public static final int SECTION_START = 2006101;
	public static final int SECTION_END = 2006102;
	public static final int INCLUDE = 2006201;
	public static final int IGNORE = 2006301;
	public static final int IGNORE_SECTION_CONTENTS = 2006400;
	public static final int CHAR_REF = 2006600;
	public static final int ENTITY_REF = 2006800;
	public static final int PEREFERENCE = 2006900;
	public static final int ENTITY_DECL = 2007000;
	public static final int ENTITY_DECL_START = 2007001;
	public static final int SYSTEM = 2007501;
	public static final int PUBLIC = 2007502;
	public static final int NDATA = 2007601;
	public static final int NOTATION_DECL = 2008200;
	public static final int NOTATION_DECL_START = 2008201;

	public static final int PREMATURE_EOF = 3000001;
	public static final int ILLEGAL_ENCODING = 3000002;
	public static final int ILLEGAL_CHARACTER = 3000003;
	public static final int ILLEGAL_SEQUENCE = 3000004;
	public static final int MALFORMED_REFERENCE = 3006700;
	public static final int MALFORMED_CHARREF = 3006600;
	public static final int MALFORMED_ENTITYREF = 3006800;
	public static final int MALFORMED_PEREFERENCE = 3006900;

	public static final Node NullNode = TerminalNode.of(NULL, new byte[0]);

	/**
	 * Creates a new terminal node instance.<br/>
	 * The concrete class to be instantiated is determined by the specified node type.
	 * @param type the node type that determines the concrete terminal node class
	 * @param sequence the UTF-8 encoded text representation of the node
	 * @return a newly created terminal node instance
	 */
	public static Node of(int type, byte[] sequence) {
		switch (type) {
		case ENTITY_VALUE:
		case ATT_VALUE:
		case SYSTEM_LITERAL:
		case PUBID_LITERAL:
			return QuotedString.of(type, sequence);
		case COMMENT:
			return Comment.of(sequence);
		case CD_SECT:
			return CDATASection.of(sequence);
		case CHAR_REF:
			return CharRef.of(sequence);
		case ENTITY_REF:
			return EntityRef.of(sequence);
		case PEREFERENCE:
			return ParameterEntityReference.of(sequence);
		default:
			return TerminalNode.of(type, sequence);
		}
	}

	/**
	 * Creates a new CHAR_DATA instance.<br/>
	 * The text representation of the node begins with a line separator followed by a sequence of space characters.<br/>
	 * The number of spaces is determined by multiplying the indentation unit by the nesting level.
	 * @param lineSeparator the byte representation of the line separator (LF (\n) or CRLF (\r\n))
	 * @param indentation the indentation unit length in bytes
	 * @param level the nesting level of the node
	 * @return a newly created CHAR_DATA instance
	 */
	public static Node lineSeparatorAndIndentation(byte[] lineSeparator, int indentation, int level) {
		return lineSeparatorAndIndentation(CHAR_DATA, lineSeparator, indentation, level);
	}

	/**
	 * Creates a new terminal node instance.<br/>
	 * The text representation of the node to be created begins with a line separator followed by a sequence of space characters.<br/>
	 * The number of spaces is determined by multiplying the indentation unit by the nesting level.
	 * @param type the node type to be created
	 * @param lineSeparator the byte representation of the line separator (LF (\n) or CRLF (\r\n))
	 * @param indentation the indentation unit length in bytes
	 * @param level the nesting level of the node
	 * @return a newly created terminal node instance
	 */
	public static Node lineSeparatorAndIndentation(int type, byte[] lineSeparator, int indentation, int level) {
		int n1 = lineSeparator.length;
		int n2 = indentation * level;
		int n = n1 + n2;
		byte[] sequence = Arrays.copyOf(lineSeparator, n);
		for (int i = n1; i < n; i++) {
			sequence[i] = SP;
		}
		return Node.of(type, sequence);
	}

	public final int type;

	protected Node(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		byte[] bb = sequence();
		return new String(bb, StandardCharsets.UTF_8);
	}

	/**
	 * Returns the UTF-8 encoded text representation of this node.
	 * @return array of byte
	 */
	public byte[] sequence() {
		throw new RuntimeException("Node::sequence: NO IMPLEMENTATION!");
	}

	/**
	 * Returns the number of bytes of the UTF-8 encoded text representation of this node.
	 * @return number of bytes
	 */
	public int length() {
		throw new RuntimeException("Node::length: NO IMPLEMENTATION!");
	}

	/**
	 * Returns 0 if the specified node is equal to this node.<br/>
	 * Otherwise, this method returns -1.
	 * @param target the node to check
	 * @return 0 if the node is equal to this one, or -1 if not
	 */
	public int offset(Node target) {
		return this == target ? 0 : -1;
	}

	/**
	 * Returns the number of LF bytes in this node.
	 * @return the number of LF bytes
	 */
	public int lineCount() {
		int count = 0;
		byte[] bb = sequence();
		for (byte b : bb) {
			if (b == LF) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the number of LF (\n) occurrences found within the first {@code offset} bytes of this node.
	 * @param offset the number of bytes to examine
	 * @return the number of LF occurrences
	 */
	public int lineCount(int offset) {
		int count = 0;
		int remaining = offset;
		byte[] bb = sequence();
		for (byte b : bb) {
			if (remaining-- <= 0) {
				return count;
			} else if (b == LF) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the column position at the end of the text of this node.<br/>
	 * A column position is defined as the number of bytes after the last LF (\n) occurrence.<br/>
	 * The calculation starts from the specified initial column position.
	 * @param initialCount the column position at the beginning of this node's text
	 * @return the column position at the end of the text
	 */
	public int columnCount(int initialCount) {
		int count = initialCount;
		byte[] bb = sequence();
		for (byte b : bb) {
			if (b == LF) {
				count = 0;
			} else {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the column position at the specified byte offset within the text of this node.<br/>
	 * A column position is defined as the number of bytes after the last LF (\n) occurrence.<br/>
	 * The calculation starts from the specified initial column position.
	 * @param initialCount the column position at the beginning of this node's text
	 * @param offset the number of bytes to examine
	 * @return the column position at the specified offset
	 */
	public int columnCount(int initialCount, int offset) {
		int count = initialCount;
		int remaining = offset;
		byte[] bb = sequence();
		for (byte b : bb) {
			if (remaining-- <= 0) {
				return count;
			}
			if (b == LF) {
				count = 0;
			} else {
				count++;
			}
		}
		return count;
	}

	/**
	 * Checks if the text of this node satisfies all of the following conditions:<br/>
	 * <ul>
	 * <li>The node type is either CHAR_DATA or S (white spaces)</li>
	 * <li>The text begins with either LF (\n) or CRLF (\r\n).</li>
	 * <li>The remainder of the text consists only of space characters.</li>
	 * </ul>
	 * @return true if the text satisfies all conditions; false otherwise
	 */
	public boolean isLineSeparatorAndIndentation() {
		if (type == CHAR_DATA || type == S) {
			byte[] bb = sequence();
			int i;
			if (bb.length > 0 && bb[0] == LF) {
				i = 1;
			} else if (bb.length > 1 && bb[0] == CR && bb[1] == LF) {
				i = 2;
			} else {
				return false;
			}
			while (i < bb.length) {
				if (bb[i] == SP) {
					i++;
				} else {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public String toDebuggingString() {
		StringBuilder buffer = new StringBuilder();
		String label = NODE_TYPES.get(Integer.valueOf(type));
		if (label == null) {
			if (type <= Character.MAX_CODE_POINT) {
				label = String.format("'%c'", type);
			} else {
				label = String.format("%d", type);
			}
		}
		buffer.append(label);
		buffer.append(" ");
		if (type == Node.S) {
			byte[] bb = sequence();
			switch (bb[0]) {
			case HT: buffer.append("HT"); break;
			case CR: buffer.append("CR"); break;
			case LF: buffer.append("LF"); break;
			case SP: buffer.append("SP"); break;
			default: buffer.append("?"); break;
			}
			for (int i = 1; i < bb.length; i++) {
				switch (bb[i]) {
				case HT: buffer.append(" HT"); break;
				case CR: buffer.append(" CR"); break;
				case LF: buffer.append(" LF"); break;
				case SP: buffer.append(" SP"); break;
				default: buffer.append(" ?"); break;
				}
			}
		} else if (this instanceof Element element) {
			buffer.append(element.startTag().toString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t"));
			if (!element.isEmptyElement()) {
				if (element.size() > 0) {
					buffer.append("...");
				}
				buffer.append(element.endTag().toString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t"));
			}
		} else  {
			buffer.append(toString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t"));
		}
		return buffer.toString();
	}

}
