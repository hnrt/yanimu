package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.ParseResult;

/**
 * The writer class for {@link ParseResult}.
 */
public class ParseResultWriter extends ParseResult {

	/**
	 * Creates a new object for the specified {@link ParseResult}.
	 * @param result an object to write
	 * @return a newly created object
	 */
	public static ParseResultWriter of(ParseResult result) {
		return new ParseResultWriter(result);
	}

	private ParseResultWriter(ParseResult result) {
		super(result);
	}

	/**
	 * Adds an error message to this object.
	 * @param offset the offset at which the message comes
	 * @param format a printf-style formatting string to format the message
	 * @param args arguments to format the message
	 * @return this object
	 */
	public ParseResultWriter error(int offset, String format, Object...args) {
		_errorList.add(new Message(offset, format, args));
		return this;
	}

	/**
	 * Adds a warning message to this object.
	 * @param offset the offset at which the message comes
	 * @param format a printf-style formatting string to format the message
	 * @param args arguments to format the message
	 * @return this object
	 */
	public ParseResultWriter warning(int offset, String format, Object...args) {
		_warningList.add(new Message(offset, format, args));
		return this;
	}

	/**
	 * Adds an informative message to this object.
	 * @param offset the offset at which the message comes
	 * @param format a printf-style formatting string to format the message
	 * @param args arguments to format the message
	 * @return this object
	 */
	public ParseResultWriter information(int offset, String format, Object...args) {
		_informationList.add(new Message(offset, format, args));
		return this;
	}

}
