package com.hideakin.yanimu.xml;

import java.util.List;

public class ProcessingInstruction extends Token {

	public final String name;
	public final String body;

	public ProcessingInstruction(List<Token> tokenList, String name, String body) {
		super(PI, tokenList);
		this.name = name;
		this.body = body;
	}

}
