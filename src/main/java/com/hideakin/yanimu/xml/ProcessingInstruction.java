package com.hideakin.yanimu.xml;

import java.util.List;

/**
 * An immutable node for the XML Processing Instruction.
 */
public class ProcessingInstruction extends ImmutableNodeList {

	/**
	 * The name of processing instruction.
	 */
	public final String name;

	/**
	 * The body of processing instruction.
	 */
	public final String body;

	/**
	 * Initializes a newly created node of type {@code PI} with a sequence of nodes, a name and a body of instruction.
	 * @param nodeList a sequence of nodes to represent this node
	 * @param name a name of instruction
	 * @param body a body of instruction
	 */
	public ProcessingInstruction(List<Node> nodeList, String name, String body) {
		super(PI, nodeList);
		this.name = name;
		this.body = body;
	}

}
