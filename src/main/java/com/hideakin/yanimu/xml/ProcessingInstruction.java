package com.hideakin.yanimu.xml;

import java.util.List;

public class ProcessingInstruction extends NodeList {

	public final String name;
	public final String body;

	public ProcessingInstruction(List<Node> nodeList, String name, String body) {
		super(PI, nodeList);
		this.name = name;
		this.body = body;
	}

}
