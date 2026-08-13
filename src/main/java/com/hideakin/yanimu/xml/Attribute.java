package com.hideakin.yanimu.xml;

import java.util.List;

public class Attribute extends Token {

	public final String key;
	public final String value;

	public Attribute(List<Token> tokenList, String key, String value) {
		super(ATTRIBUTE, tokenList);
		this.key = key;
		this.value = value;
	}

}
