package com.hideakin.yanimu.xml;

public class ParseException extends Exception {

	private static final long serialVersionUID = -5612922712059444218L;

	protected final int _offset;

	public ParseException(ParseResult result) {
		super(result.error().message);
		_offset = result.error().offset;
	}

	public int offset() {
		return _offset;
	}

}
