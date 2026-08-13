package com.hideakin.yanimu.xml;

import java.util.List;

public class XmlDeclaration extends Token {

	public final Token[] layout;
	public final String version;
	public final String encoding;
	public final String standalone;

	public XmlDeclaration(List<Token> tokenList, String version, String encoding, String standalone) {
		super(XML_DECL, tokenList);
		this.layout = tokenList.toArray(new Token[tokenList.size()]);
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}

}
