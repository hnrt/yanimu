package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

import static com.hideakin.yanimu.xml.Character.*;

public class Node {

	public static final int NULL = 0;

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
	public static final int MARKUP_DECL_START = 2002802;
	public static final int MARKUP_DECL_END = 2002803;
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

	public static final Node NULL_NODE = TerminalNode.of(NULL, new byte[0]);

	/**
	 * Creates a new terminal node instance.
	 * <p>
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
		throw new RuntimeException(getClass().getSimpleName() + "::sequence: NO IMPLEMENTATION!");
	}

	/**
	 * Returns the number of bytes of the UTF-8 encoded text representation of this node.
	 * @return number of bytes
	 */
	public int length() {
		throw new RuntimeException(getClass().getSimpleName() + "::length: NO IMPLEMENTATION!");
	}

	/**
	 * Returns 0 if the specified node is equal to this node.
	 * <p>
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
	 * Returns the number of LF occurrences found within the first {@code offset} bytes of this node.
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
	 * Returns the column position at the end of the text of this node.
	 * <p>
	 * A column position is defined as the number of bytes after the last LF occurrence.
	 * <p>
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
	 * Returns the column position at the specified byte offset within the text of this node.
	 * <p>
	 * A column position is defined as the number of bytes after the last LF occurrence.
	 * <p>
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
	 * Returns true if the type of this node is one of the specified types.
	 * @param nodeTypes the list of the types to compare
	 * @return true if the node type is one of the specified types, or false if not
	 */
	public boolean isOneOf(int...nodeTypes) {
		for (int i = 0; i < nodeTypes.length; i++) {
			if (type == nodeTypes[i]) {
				return true;
			}
		}
		return false;
	}

}
