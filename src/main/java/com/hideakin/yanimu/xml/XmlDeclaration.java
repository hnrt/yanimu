package com.hideakin.yanimu.xml;

import java.util.List;

public class XmlDeclaration extends Node {

	public final Node[] layout;
	public final String version;
	public final String encoding;
	public final String standalone;

	public XmlDeclaration(List<Node> nodeList, String version, String encoding, String standalone) {
		super(XML_DECL, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}

}
