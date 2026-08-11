package com.hideakin.yanimu.xml;

public class OutOfRangeException extends ParseException {

	private static final long serialVersionUID = 1562721156982593217L;

	public OutOfRangeException(int offset) {
		super("Out of range for UTF-8.", offset);
	}

}
