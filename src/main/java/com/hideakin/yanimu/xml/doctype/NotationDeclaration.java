package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.NodeList;

public class NotationDeclaration extends NodeList {

	public final String name;
	public final String systemLiteral;
	public final String pubidLiteral;

	public NotationDeclaration(List<Node> nodeList, String name, ExternalIdentifiers extid) {
		super(ENTITY_DECL, nodeList);
		this.name = name;
		this.systemLiteral = extid.systemLiteral;
		this.pubidLiteral = extid.pubidLiteral;
	}

}
