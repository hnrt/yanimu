package com.hideakin.yanimu.xml;

import java.util.List;

public class Node {

	public static final int EOF = -1;
	public static final int EQ = 61;
	public static final int TAG_END = 62;

	// Character.MAX_CODE_POINT : 0x10FFFF = 1114111

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
	public static final int CD_SECT = 2001800;
	public static final int XML_DECL = 2002300;
	public static final int XML_START = 2002301;
	public static final int XML_END = 2002302;
	public static final int DOCTYPE_DECL = 2002800;
	public static final int ELEMENT = 2003900;
	public static final int STAG_START = 2004001;
	public static final int ATTRIBUTE = 2004100;
	public static final int ETAG_START = 2004201;
	public static final int EETAG_END = 2004401;
	public static final int ELEMENT_DECL = 2004500;
	public static final int EMPTY = 2004601;
	public static final int ANY = 2004602;
	public static final int PCDATA = 2005101;
	public static final int PCDATA_END = 2005102;
	public static final int ATTLIST_DECL = 2005200;
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
	public static final int CHAR_REF = 2006600;
	public static final int ENTITY_REF = 2006800;
	public static final int PEREFERENCE = 2006900;
	public static final int ENTITY_DECL = 2007000;
	public static final int SYSTEM = 2007501;
	public static final int PUBLIC = 2007502;
	public static final int NDATA = 2007601;
	public static final int NOTATION_DECL = 2008200;

	public static final int PREMATURE_EOF = 3000001;
	public static final int ILLEGAL_ENCODING = 3000002;
	public static final int ILLEGAL_CHARACTER = 3000003;
	public static final int ILLEGAL_SEQUENCE = 3000004;
	public static final int MALFORMED_REFERENCE = 3006700;
	public static final int MALFORMED_CHARREF = 3006600;
	public static final int MALFORMED_ENTITYREF = 3006800;
	public static final int MALFORMED_PEREFERENCE = 3006900;

	public final int type;
	public final int start;
	public final int end;
	public final byte[] sequence;

	public Node(int type, int start, byte[] sequence) {
		this.type = type;
		this.start = start;
		this.end = start + sequence.length;
		this.sequence = sequence;
	}

	public Node(int type, List<Node> tokenList) {
		this(type, tokenList.get(0).start, buildSequence(tokenList));
	}

	public Node(int type, int start, List<Node> tokenList) {
		this(type, start, buildSequence(tokenList));
	}

	@Override
	public String toString() {
		return sequence != null ? new String(sequence) : "";
	}

	private static byte[] buildSequence(List<Node> tokenList) {
		int n = 0;
		for (Node token : tokenList) {
			n += token.sequence.length;
		}
		byte[] sequence = new byte[n];
		int i = 0;
		for (Node token : tokenList) {
			System.arraycopy(token.sequence, 0, sequence, i, token.sequence.length);
			i += token.sequence.length;
		}
		return sequence;
	}

}
