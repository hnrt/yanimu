package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class EmptyElementTag extends StartTag {

	public static final byte[] START_SEQUENCE = {'<'};
	public static final byte[] END_SEQUENCE = {'/', '>'};

	public EmptyElementTag(String name) {
		super(EETAG, new ArrayList<>(
				List.of(new Node(STAG_START, START_SEQUENCE),
						new Node(NAME, name),
						new Node(EETAG_END, END_SEQUENCE))),
				new ArrayList<>());
	}

	public EmptyElementTag(List<Node> nodeList, List<Attribute> attributeList) {
		super(EETAG, nodeList, attributeList);
	}

}
