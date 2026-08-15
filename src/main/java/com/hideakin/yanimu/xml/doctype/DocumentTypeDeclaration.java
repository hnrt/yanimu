package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;

public class DocumentTypeDeclaration extends Node {

	public final Node[] layout;
	public final String name;
	public final ExternalIdentifiers extid;
	public final Object[] declarations;

	public DocumentTypeDeclaration(List<Node> nodeList, String name, ExternalIdentifiers extid, List<Object> declarationList) {
		super(DOCTYPE_DECL, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		this.name = name;
		this.extid = extid;
		this.declarations = declarationList.toArray(new Object[declarationList.size()]);
	}

}
