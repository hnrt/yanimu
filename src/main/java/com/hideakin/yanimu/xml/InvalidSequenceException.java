package com.hideakin.yanimu.xml;

public class InvalidSequenceException extends ParseException {

	private static final long serialVersionUID = 5062932623500925063L;

	public InvalidSequenceException(int offset) {
		super("Invalid sequence of UTF-8.", offset);
	}

}
