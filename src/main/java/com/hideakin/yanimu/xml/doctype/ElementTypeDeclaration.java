package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;

public class ElementTypeDeclaration extends Node {

	public final Node[] layout;
	public final String name;
	public final ContentSpec cs;

	public ElementTypeDeclaration(List<Node> nodeList, String name, ContentSpec cs) {
		super(ELEMENT_DECL, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		this.name = name;
		this.cs = cs;
	}

}
