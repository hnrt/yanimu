package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;

public class ContentSpec extends Node {

	public final Node[] layout;
	public final Object value;

	public ContentSpec(List<Node> nodeList, int csType) {
		super(CONTENTSPEC, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		this.value = Integer.valueOf(csType); // EMPTY, ANY or PCDATA
	}

	public ContentSpec(List<Node> nodeList, List<String> choiceList) {
		super(CONTENTSPEC, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		this.value = choiceList.toArray(new String[choiceList.size()]);
	}

	public ContentSpec(List<Node> nodeList, ContentParticle particle) {
		super(CONTENTSPEC, nodeList);
		this.layout = nodeList.toArray(new Node[nodeList.size()]);
		this.value = particle;
	}

}
