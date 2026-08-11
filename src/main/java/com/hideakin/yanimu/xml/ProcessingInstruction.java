package com.hideakin.yanimu.xml;

public class ProcessingInstruction extends Token {

	public final String name;
	public final String body;

	public ProcessingInstruction(int start, int end, String name, String body) {
		super(PI, start, end);
		this.name = name;
		this.body = body;
	}

}
