package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.NodeList;

public class EntityDeclaration extends NodeList {

	public final Object definition;

	public EntityDeclaration(List<Node> nodeList, Object definition) {
		super(ENTITY_DECL, nodeList);
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
