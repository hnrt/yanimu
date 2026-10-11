package com.hideakin.yanimu.xml;

/**
 * A runtime exception on a parse error.
 */
public class ParseException extends RuntimeException {

	private static final long serialVersionUID = -5612922712059444218L;

	/**
	 * The offset at which a parse error is detected.
	 */
	protected final int _offset;

	/**
	 * Constructs an exception with a {@link ParseResult}.
	 * @param result an object from which the error message and the offset is retrieved.
	 */
	public ParseException(ParseResult result) {
		super(result.error().message);
		_offset = result.error().offset;
	}

	/**
	 * Returns the offset at which a parse error is detected.
	 * @return the offset at which a parse error is detected.
	 */
	public int offset() {
		return _offset;
	}

}
