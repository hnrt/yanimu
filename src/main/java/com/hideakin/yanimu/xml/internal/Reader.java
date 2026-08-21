package com.hideakin.yanimu.xml.internal;

public interface Reader {

	int readChar();
	boolean next(int... cc);
	boolean peek(int... cc);
	int codepoint();

}
