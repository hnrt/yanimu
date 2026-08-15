package com.hideakin.yanimu.xml;

import java.util.List;

public class XmlDeclaration extends Node {

	public final Node[] layout;
	public final String version;
	public final String encoding;
	public final String standalone;

	public XmlDeclaration(List<Node> tokenList, String version, String encoding, String standalone) {
		super(XML_DECL, tokenList);
		this.layout = tokenList.toArray(new Node[tokenList.size()]);
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}

}
