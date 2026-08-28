package com.hideakin.yanimu.xml;

import java.util.List;

public class XmlDeclaration extends NodeList {

	public static final String DEFAULT_VERSION = "1.0";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String YES = "yes";
	public static final String NO = "no";

	public final String version;
	public final String encoding;
	public final String standalone;

	public XmlDeclaration() {
		super(XML_DECL, List.of(
				Node.of(XML_START, "<?xml"),
				Node.of(S, " "),
				Node.of(NAME, "version"),
				Node.of(EQ, "="),
				QuotedString.of(ATT_VALUE, "\"" + DEFAULT_VERSION + "\""),
				Node.of(XML_END, "?>")));
		this.version = DEFAULT_VERSION;
		this.encoding = null;
		this.standalone = null;
	}

	public XmlDeclaration(String encoding) {
		super(XML_DECL, List.of(
				Node.of(XML_START, "<?xml"),
				Node.of(S, " "),
				Node.of(NAME, "version"),
				Node.of(EQ, "="),
				QuotedString.of(ATT_VALUE, "\"" + DEFAULT_VERSION + "\""),
				Node.of(S, " "),
				Node.of(NAME, "encoding"),
				Node.of(EQ, "="),
				QuotedString.of(ATT_VALUE, "\"" + encoding + "\""),
				Node.of(XML_END, "?>")));
		this.version = DEFAULT_VERSION;
		this.encoding = encoding;
		this.standalone = null;
	}

	public XmlDeclaration(String version, String encoding) {
		super(XML_DECL, List.of(
				Node.of(XML_START, "<?xml"),
				Node.of(S, " "),
				Node.of(NAME, "version"),
				Node.of(EQ, "="),
				QuotedString.of(ATT_VALUE, "\"" + version + "\""),
				Node.of(S, " "),
				Node.of(NAME, "encoding"),
				Node.of(EQ, "="),
				QuotedString.of(ATT_VALUE, "\"" + encoding + "\""),
				Node.of(XML_END, "?>")));
		this.version = version;
		this.encoding = encoding;
		this.standalone = null;
	}

	public XmlDeclaration(String version, String encoding, String standalone) {
		super(XML_DECL, List.of(
				Node.of(XML_START, "<?xml"),
				Node.of(S, " "),
				Node.of(NAME, "version"),
				Node.of(EQ, "="),
				QuotedString.of(ATT_VALUE, "\"" + version + "\""),
				Node.of(S, " "),
				Node.of(NAME, "encoding"),
				Node.of(EQ, "="),
				QuotedString.of(ATT_VALUE, "\"" + encoding + "\""),
				Node.of(S, " "),
				Node.of(NAME, "standalone"),
				Node.of(EQ, "="),
				QuotedString.of(ATT_VALUE, "\"" + standalone + "\""),
				Node.of(XML_END, "?>")));
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}

	public XmlDeclaration(List<Node> nodeList, String version, String encoding, String standalone) {
		super(XML_DECL, nodeList);
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
		if (get(0).type == PI_START && get(1).type == NAME) {
			set(0, Node.of(XML_START, "<?xml"));
			remove(1);
		}
	}

}
