package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.ParseResult;

public class ParseResultWriter extends ParseResult {

	public static ParseResultWriter of(ParseResult result) {
		return new ParseResultWriter(result);
	}

	private ParseResultWriter(ParseResult result) {
		super(result);
	}

	public ParseResultWriter error(int offset, String format, Object...args) {
		_errorList.add(new Message(offset, format, args));
		return this;
	}

	public ParseResultWriter warning(int offset, String format, Object...args) {
		_warningList.add(new Message(offset, format, args));
		return this;
	}

	public ParseResultWriter information(int offset, String format, Object...args) {
		_informationList.add(new Message(offset, format, args));
		return this;
	}

}
