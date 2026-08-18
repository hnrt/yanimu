package com.hideakin.yanimu.xml;

import java.util.List;

public class EmptyElementTag extends StartTag {

	public static final byte[] START_SEQUENCE = {'<'};
	public static final byte[] END_SEQUENCE = {'/', '>'};

	public EmptyElementTag(List<Node> nodeList, List<Attribute> attributeList) {
		super(EETAG, nodeList, attributeList);
	}

}
