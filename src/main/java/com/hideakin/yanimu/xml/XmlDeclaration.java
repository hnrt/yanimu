package com.hideakin.yanimu.xml;

import java.util.List;

public class XmlDeclaration extends NodeList {

	public final String version;
	public final String encoding;
	public final String standalone;

	public XmlDeclaration(List<Node> nodeList, String version, String encoding, String standalone) {
		super(XML_DECL, nodeList);
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}

}
