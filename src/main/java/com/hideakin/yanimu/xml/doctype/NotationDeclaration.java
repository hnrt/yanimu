package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;

public class NotationDeclaration extends Node {

	public final Node[] layout;
	public final String name;
	public final String systemLiteral;
	public final String pubidLiteral;

	public NotationDeclaration(List<Node> nodeList, String name, ExternalIdentifiers extid) {
		super(ENTITY_DECL, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		this.name = name;
		this.systemLiteral = extid.systemLiteral;
		this.pubidLiteral = extid.pubidLiteral;
	}

}
