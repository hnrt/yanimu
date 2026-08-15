package com.hideakin.yanimu.xml;

import java.util.List;

public class DocumentTypeDeclaration extends Node {

	public final Node[] layout;
	public final String name;

	public DocumentTypeDeclaration(List<Node> tokenList, String name) {
		super(DOCTYPE_DECL, tokenList);
		this.layout = tokenList.toArray(new Node[tokenList.size()]);
		this.name = name;
	}

}
