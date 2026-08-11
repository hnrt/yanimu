package com.hideakin.yanimu.xml;

public class CharData extends Token {

	public final String text;

	public CharData(int start, int end, String text) {
		super(CHAR_DATA, start, end);
		this.text = text;
	}

}
