package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Token {

	public final static int EOF = -1;
	public final static int HT = 9;
	public final static int LF = 10;
	public final static int CR = 13;
	public final static int SP = 32;
	public final static int EQ = 61;
	public final static int TAG_END = 62;

	// Character.MAX_CODE_POINT : 0x10FFFF = 1114111

	public final static int NAME = 2000500;
	public final static int NMTOKEN = 2000700;
	public final static int ENTITY_VALUE = 2000900;
	public final static int ATT_VALUE = 2001000;
	public final static int SYSTEM_LITERAL = 2001100;
	public final static int PUBID_LITERAL = 2001200;
	public final static int CHAR_DATA = 2001400;
	public final static int COMMENT = 2001500;
	public final static int PI = 2001600;
	public final static int PI_START = 2001601;
	public final static int PI_BODY = 2001602;
	public final static int PI_END = 2001603;
	public final static int CD_SECT = 2001800;
	public final static int XML_DECL = 2002300;
	public final static int XML_START = 2002301;
	public final static int XML_END = 2002302;
	public final static int DOCTYPE_DECL = 2002800;
	public final static int ELEMENT = 2003900;
	public final static int STAG_START = 2004001;
	public final static int ATTRIBUTE = 2004100;
	public final static int ETAG_START = 2004201;
	public final static int EETAG_END = 2004401;
	public final static int ELEMENT_DECL = 2004500;
	public final static int EMPTY = 2004601;
	public final static int ANY = 2004602;
	public final static int PCDATA = 2005101;
	public final static int PCDATA_END = 2005102;
	public final static int ATTLIST_DECL = 2005200;
	public final static int TYPE_CDATA = 2005501;
	public final static int TYPE_ID = 2005601;
	public final static int TYPE_IDREF = 2005602;
	public final static int TYPE_IDREFS = 2005603;
	public final static int TYPE_ENTITY = 2005604;
	public final static int TYPE_ENTITIES = 2005605;
	public final static int TYPE_NMTOKEN = 2005606;
	public final static int TYPE_NMTOKENS = 2005607;
	public final static int TYPE_NOTATION = 2005801;
	public final static int REQUIRED = 2006001;
	public final static int IMPLIED = 2006002;
	public final static int FIXED = 2006003;
	public final static int CHAR_REF = 2006600;
	public final static int ENTITY_REF = 2006800;
	public final static int PEREFERENCE = 2006900;
	public final static int ENTITY_DECL = 2007000;
	public final static int SYSTEM = 2007501;
	public final static int PUBLIC = 2007502;
	public final static int NDATA = 2007601;
	public final static int NOTATION_DECL = 2008200;

	public final static int PREMATURE_EOF = 3000001;
	public final static int ILLEGAL_ENCODING = 3000002;
	public final static int ILLEGAL_CHARACTER = 3000003;
	public final static int ILLEGAL_SEQUENCE = 3000004;
	public final static int MALFORMED_REFERENCE = 3006700;
	public final static int MALFORMED_CHARREF = 3006600;
	public final static int MALFORMED_ENTITYREF = 3006800;
	public final static int MALFORMED_PEREFERENCE = 3006900;

	public static Token of(int code, int start, int end, String text) {
		byte[] sequence = text.getBytes();
		switch (code) {
		case ENTITY_VALUE:
		case ATT_VALUE:
		case SYSTEM_LITERAL:
		case PUBID_LITERAL:
			return new QuotedString(code, start, end, sequence);
		case COMMENT:
			return new Comment(start, end, sequence);
		case CD_SECT:
			return new CDATASection(start, end, sequence);
		case CHAR_REF:
			return new CharRef(start, end, sequence);
		case ENTITY_REF:
			return new EntityRef(start, end, sequence);
		case PEREFERENCE:
			return new ParameterEntityReference(start, end, sequence);
		default:
			return new Token(code, start, end, sequence);
		}
	}

	public final int code;
	public final int start;
	public final int end;
	public final byte[] sequence;

	protected Token(int code, int start, int end, byte[] sequence) {
		this.code = code;
		this.start = start;
		this.end = end;
		this.sequence = sequence;
	}

	public Token(int code, List<Token> tokenList) {
		this.code = code;
		this.start = tokenList.get(0).start;
		this.end = tokenList.get(tokenList.size() - 1).end;
		int n = 0;
		for (Token token : tokenList) {
			if (token.sequence == null) continue;
			n += token.sequence.length;
		}
		this.sequence = new byte[n];
		int i = 0;
		for (Token token : tokenList) {
			if (token.sequence == null) continue;
			System.arraycopy(token.sequence, 0, this.sequence, i, token.sequence.length);
			i += token.sequence.length;
		}
	}

	@Override
	public String toString() {
		return sequence != null ? new String(sequence) : "";
	}

	public static List<Token> list(Token first) {
		List<Token> tt = new ArrayList<>();
		tt.add(first);
		return tt;
	}

}
