package com.hideakin.yanimu.xml.internal;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.hideakin.yanimu.xml.Node;

public class DebugHelper {

	public static boolean enabled = false;

	public static final Map<Integer, String> NODE_TYPES;
	public static final Map<Integer, String> LEXER_CONTEXTS;

	static {
		Map<Integer, String> nt = new HashMap<>();
		nt.put(Integer.valueOf(Node.EOF), "EOF");
		nt.put(Integer.valueOf(Node.EQ), "EQ");
		nt.put(Integer.valueOf(Node.TAG_END), "TAG_END");
		nt.put(Integer.valueOf(Node.DOCUMENT), "DOCUMENT");
		nt.put(Integer.valueOf(Node.S), "S");
		nt.put(Integer.valueOf(Node.NAME), "NAME");
		nt.put(Integer.valueOf(Node.NMTOKEN), "NMTOKEN");
		nt.put(Integer.valueOf(Node.ENTITY_VALUE), "ENTITY_VALUE");
		nt.put(Integer.valueOf(Node.ATT_VALUE), "ATT_VALUE");
		nt.put(Integer.valueOf(Node.SYSTEM_LITERAL), "SYSTEM_LITERAL");
		nt.put(Integer.valueOf(Node.PUBID_LITERAL), "PUBID_LITERAL");
		nt.put(Integer.valueOf(Node.CHAR_DATA), "CHAR_DATA");
		nt.put(Integer.valueOf(Node.COMMENT), "COMMENT");
		nt.put(Integer.valueOf(Node.PI), "PI");
		nt.put(Integer.valueOf(Node.PI_START), "PI_START");
		nt.put(Integer.valueOf(Node.PI_BODY), "PI_BODY");
		nt.put(Integer.valueOf(Node.PI_END), "PI_END");
		nt.put(Integer.valueOf(Node.PI_TARGET), "PI_TARGET");
		nt.put(Integer.valueOf(Node.CD_SECT), "CD_SECT");
		nt.put(Integer.valueOf(Node.XML_DECL), "XML_DECL");
		nt.put(Integer.valueOf(Node.XML_END), "XML_END");
		nt.put(Integer.valueOf(Node.DOCTYPE_DECL), "DOCTYPE_DECL");
		nt.put(Integer.valueOf(Node.DOCTYPE_DECL_START), "DOCTYPE_DECL_START");
		nt.put(Integer.valueOf(Node.ELEMENT), "ELEMENT");
		nt.put(Integer.valueOf(Node.STAG), "STAG");
		nt.put(Integer.valueOf(Node.STAG_START), "STAG_START");
		nt.put(Integer.valueOf(Node.STAG_START), "STAG_END");
		nt.put(Integer.valueOf(Node.ATTRIBUTE), "ATTRIBUTE");
		nt.put(Integer.valueOf(Node.ETAG), "ETAG");
		nt.put(Integer.valueOf(Node.ETAG_START), "ETAG_START");
		nt.put(Integer.valueOf(Node.ETAG_END), "ETAG_END");
		nt.put(Integer.valueOf(Node.CONTENT), "CONTENT");
		nt.put(Integer.valueOf(Node.EETAG), "EETAG");
		nt.put(Integer.valueOf(Node.EETAG_END), "EETAG_END");
		nt.put(Integer.valueOf(Node.ELEMENT_DECL), "ELEMENT_DECL");
		nt.put(Integer.valueOf(Node.ELEMENT_DECL_START), "ELEMENT_DECL_START");
		nt.put(Integer.valueOf(Node.CONTENTSPEC), "CONTENTSPEC");
		nt.put(Integer.valueOf(Node.EMPTY), "EMPTY");
		nt.put(Integer.valueOf(Node.ANY), "ANY");
		nt.put(Integer.valueOf(Node.PCDATA), "PCDATA");
		nt.put(Integer.valueOf(Node.PCDATA_END), "PCDATA_END");
		nt.put(Integer.valueOf(Node.ATTLIST_DECL), "ATTLIST_DECL");
		nt.put(Integer.valueOf(Node.ATTLIST_DECL_START), "ATTLIST_DECL_START");
		nt.put(Integer.valueOf(Node.TYPE_CDATA), "TYPE_CDATA");
		nt.put(Integer.valueOf(Node.TYPE_ID), "TYPE_ID");
		nt.put(Integer.valueOf(Node.TYPE_IDREF), "TYPE_IDREF");
		nt.put(Integer.valueOf(Node.TYPE_IDREFS), "TYPE_IDREFS");
		nt.put(Integer.valueOf(Node.TYPE_ENTITY), "TYPE_ENTITY");
		nt.put(Integer.valueOf(Node.TYPE_ENTITIES), "TYPE_ENTITIES");
		nt.put(Integer.valueOf(Node.TYPE_NMTOKEN), "TYPE_NMTOKEN");
		nt.put(Integer.valueOf(Node.TYPE_NMTOKENS), "TYPE_NMTOKENS");
		nt.put(Integer.valueOf(Node.TYPE_NOTATION), "TYPE_NOTATION");
		nt.put(Integer.valueOf(Node.REQUIRED), "REQUIRED");
		nt.put(Integer.valueOf(Node.IMPLIED), "IMPLIED");
		nt.put(Integer.valueOf(Node.FIXED), "FIXED");
		nt.put(Integer.valueOf(Node.SECTION_START), "SECTION_START");
		nt.put(Integer.valueOf(Node.SECTION_END), "SECTION_END");
		nt.put(Integer.valueOf(Node.INCLUDE), "INCLUDE");
		nt.put(Integer.valueOf(Node.IGNORE), "IGNORE");
		nt.put(Integer.valueOf(Node.IGNORE_SECTION_CONTENTS), "IGNORE_SECTION_CONTENTS");
		nt.put(Integer.valueOf(Node.CHAR_REF), "CHAR_REF");
		nt.put(Integer.valueOf(Node.ENTITY_REF), "ENTITY_REF");
		nt.put(Integer.valueOf(Node.PEREFERENCE), "PEREFERENCE");
		nt.put(Integer.valueOf(Node.ENTITY_DECL), "ENTITY_DECL");
		nt.put(Integer.valueOf(Node.ENTITY_DECL_START), "ENTITY_DECL_START");
		nt.put(Integer.valueOf(Node.SYSTEM), "SYSTEM");
		nt.put(Integer.valueOf(Node.PUBLIC), "PUBLIC");
		nt.put(Integer.valueOf(Node.NDATA), "NDATA");
		nt.put(Integer.valueOf(Node.NOTATION_DECL), "NOTATION_DECL");
		nt.put(Integer.valueOf(Node.NOTATION_DECL_START), "NOTATION_DECL_START");
		nt.put(Integer.valueOf(Node.PREMATURE_EOF), "PREMATURE_EOF");
		nt.put(Integer.valueOf(Node.ILLEGAL_ENCODING), "ILLEGAL_ENCODING");
		nt.put(Integer.valueOf(Node.ILLEGAL_CHARACTER), "ILLEGAL_CHARACTER");
		nt.put(Integer.valueOf(Node.ILLEGAL_SEQUENCE), "ILLEGAL_SEQUENCE");
		nt.put(Integer.valueOf(Node.MALFORMED_REFERENCE), "MALFORMED_REFERENCE");
		nt.put(Integer.valueOf(Node.MALFORMED_CHARREF), "MALFORMED_CHARREF");
		nt.put(Integer.valueOf(Node.MALFORMED_ENTITYREF), "MALFORMED_ENTITYREF");
		nt.put(Integer.valueOf(Node.MALFORMED_PEREFERENCE), "MALFORMED_PEREFERENCE");
		NODE_TYPES = Map.copyOf(nt);
		Map<Integer, String> lc = new HashMap<>();
		lc.put(Integer.valueOf(LexerContext.BASE), "BASE");
		lc.put(Integer.valueOf(LexerContext.XML), "XML");
		lc.put(Integer.valueOf(LexerContext.PI), "PI");
		lc.put(Integer.valueOf(LexerContext.STAG), "STAG");
		lc.put(Integer.valueOf(LexerContext.CONTENT), "CONTENT");
		lc.put(Integer.valueOf(LexerContext.ETAG), "ETAG");
		lc.put(Integer.valueOf(LexerContext.DOCTYPE), "DOCTYPE");
		lc.put(Integer.valueOf(LexerContext.EXTERNAL), "EXTERNAL");
		lc.put(Integer.valueOf(LexerContext.ELEMENT), "ELEMENT");
		lc.put(Integer.valueOf(LexerContext.ATTLIST), "ATTLIST");
		lc.put(Integer.valueOf(LexerContext.ENTITY), "ENTITY");
		lc.put(Integer.valueOf(LexerContext.NOTATION), "NOTATION");
		lc.put(Integer.valueOf(LexerContext.CONDITIONAL), "CONDITIONAL");
		lc.put(Integer.valueOf(LexerContext.IGNORE), "IGNORE");
		LEXER_CONTEXTS = Map.copyOf(lc);
	}

	public static void print(Node node) {
		if (!enabled) return;
		String label = NODE_TYPES.get(Integer.valueOf(node.type));
		if (label == null) {
			if (node.type <= Character.MAX_CODE_POINT) {
				label = String.format("%c", node.type);
			} else {
				label = String.format("%d", node.type);
			}
		} 
		String text = new String(node.sequence(), StandardCharsets.UTF_8).replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t");
		System.out.printf("#NODE %s %s\n", label, text);
	}

	public static void printLexerContext(int ctx) {
		if (!enabled) return;
		String label = LEXER_CONTEXTS.get(Integer.valueOf(ctx));
		if (label == null) {
			label = String.format("%d", ctx);
		}
		System.out.printf("#LCTX %s\n", label);
	}

}
