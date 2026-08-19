package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.NodeList;

public class DocumentTypeDeclaration extends NodeList {

	public final String name;
	public final ExternalIdentifiers extid;
	public final Object[] declarations;

	public DocumentTypeDeclaration(List<Node> nodeList, String name, ExternalIdentifiers extid, List<Object> declarationList) {
		super(DOCTYPE_DECL, nodeList);
		this.name = name;
		this.extid = extid;
		this.declarations = declarationList.toArray(new Object[declarationList.size()]);
	}

}
