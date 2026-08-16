package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;

public class AttributeListDeclaration extends Node {

	public final Node[] layout;
	public final String name;
	public final AttributeDefinition[] definitions;

	public AttributeListDeclaration(List<Node> nodeList, String name, List<AttributeDefinition> defList) {
		super(ELEMENT_DECL, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		this.name = name;
		this.definitions = defList.toArray(new AttributeDefinition[defList.size()]);
	}

}
