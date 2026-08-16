package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;

public class EntityDeclaration extends Node {

	public final Node[] layout;
	public final Object definition;

	public EntityDeclaration(List<Node> nodeList, Object definition) {
		super(ENTITY_DECL, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		if (definition instanceof InternalEntityDefinition
				|| definition instanceof ExternalEntityDefinition
				|| definition instanceof InternalParameterEntityDefinition
				|| definition instanceof ExternalParameterEntityDefinition) {
			this.definition = definition;
		} else {
			throw new RuntimeException("EntityDeclaration: Bad definition.");
		}
	}

}
