package com.hideakin.yanimu.xml;

public class Attribute extends Token {

	public final String name;
	public final String value;

	public Attribute(int start, int end, String name, String value) {
		super(ATTRIBUTE, start, end);
		this.name = name;
		this.value = value;
	}

}
