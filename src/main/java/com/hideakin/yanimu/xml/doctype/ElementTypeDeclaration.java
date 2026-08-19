package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.NodeList;

public class ElementTypeDeclaration extends NodeList {

	public final String name;
	public final ContentSpec cs;

	public ElementTypeDeclaration(List<Node> nodeList, String name, ContentSpec cs) {
		super(ELEMENT_DECL, nodeList);
		this.name = name;
		this.cs = cs;
	}

}
