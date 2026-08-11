package com.hideakin.yanimu.xml;

import java.util.Arrays;

public class Token {

	public final static int EOF = -1;
	public final static int HT = 9;
	public final static int LF = 10;
	public final static int CR = 13;
	public final static int SP = 32;
	public final static int EQ = 61;
	public final static int TAG_END = 62;

	public final static int UNICODE_MAX = 0x10FFFF; // 1,114,111
	public final static int COMMENT = 2000001;
	public final static int XML = 2000002;
	public final static int PI = 2000003;
	public final static int PI_START = 2000004;
	public final static int PI_BODY = 2000005;
	public final static int PI_END = 2000006;
	public final static int NAME = 2000007;
	public final static int STAG_START = 2000008;
	public final static int EETAG_END = 2000009;
	public final static int ETAG_START = 2000010;
	public final static int ATTVALUE = 2000011;
	public final static int CHAR_DATA = 2000012;
	public final static int DOCTYPE = 2000013;
	public final static int SYSTEM = 2000014;
	public final static int PUBLIC = 2000015;
	public final static int SYSTEM_LITERAL = 2000016;
	public final static int PUBID_LITERAL = 2000017;
	public final static int ELEMENT_DECL = 2000018;
	public final static int EMPTY = 2000019;
	public final static int ANY = 2000020;
	public final static int PCDATA = 2000021;
	public final static int PCDATA_END = 2000022;
	public final static int ATTLIST_DECL = 2000023;
	public final static int TYPE_CDATA = 2000024;
	public final static int TYPE_ID = 2000025;
	public final static int TYPE_IDREF = 2000026;
	public final static int TYPE_IDREFS = 2000027;
	public final static int TYPE_ENTITY = 2000028;
	public final static int TYPE_ENTITIES = 2000029;
	public final static int TYPE_NMTOKEN = 2000030;
	public final static int TYPE_NMTOKENS = 2000031;
	public final static int TYPE_NOTATION = 2000032;
	public final static int NMTOKEN = 2000033;
	public final static int REQUIRED = 2000034;
	public final static int IMPLIED = 2000035;
	public final static int FIXED = 2000036;
	public final static int ENTITY_DECL = 2000037;
	public final static int ENTITY_VALUE = 2000038;
	public final static int PEREFERENCE = 2000039;
	public final static int NDATA = 2000040;
	public final static int NOTATION_DECL = 2000041;
	public final static int ATTRIBUTE = 2000100;
	public final static int ELEMENT = 2000101;
	public final static int ENTITYREF = 2000102;
	public final static int CHARREF = 2000103;
	public final static int CDSECT = 2000104;

	public final static int ILLEGAL_SEQUENCE = 3000001;
	public final static int OUT_OF_RANGE = 3000002;
	public final static int PREMATURE_EOF = 3000003;
	public final static int MALFORMED_PEREFERENCE = 3000004;
	public final static int MALFORMED_REFERENCE = 3000005;
	public final static int MALFORMED_ENTITYREF = 3000006;
	public final static int MALFORMED_CHARREF = 3000007;
	public final static int ILLEGAL_CHARACTER = 3000008;
	
	public final int code;
	public final int start;
	public final int end;

	public Token(int code, int start, int end) {
		this.code = code;
		this.start = start;
		this.end = end;
	}

	public String text(byte[] contents) {
		return new String(Arrays.copyOfRange(contents, start, end));
	}

}
