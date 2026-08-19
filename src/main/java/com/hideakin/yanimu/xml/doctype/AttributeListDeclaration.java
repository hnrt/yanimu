package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.NodeList;

public class AttributeListDeclaration extends NodeList {

	public final String name;
	public final AttributeDefinition[] definitions;

	public AttributeListDeclaration(List<Node> nodeList, String name, List<AttributeDefinition> defList) {
		super(ELEMENT_DECL, nodeList);
		this.name = name;
		this.definitions = defList.toArray(new AttributeDefinition[defList.size()]);
	}

}
