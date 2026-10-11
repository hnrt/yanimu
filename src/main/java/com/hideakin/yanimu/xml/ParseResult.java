package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection set of information on parsing an XML document.
 */
public class ParseResult {

	/**
	 * A class to deliver a message with and offset.
	 */
	public static class Message {

		/**
		 * The offset at which the message is addressed.
		 */
		public final int offset;

		/**
		 * The message addressed on parsing an XML document.
		 */
		public final String message;

		/**
		 * Initializes a newly created object with an offset and a printf-style formatting string with its operands.
		 * @param offset an offset at which the message is addressed
		 * @param format a printf-style formatting string to construct a message
		 * @param args operands for the printf-style formatting string
		 */
		public Message(int offset, String format, Object...args) {
			this.offset = offset;
			this.message = String.format(format, args);
		}

	}

	/**
	 * The list of error messages.
	 */
	protected final List<Message> _errorList;

	/**
	 * The list of warning messages.
	 */
	protected final List<Message> _warningList;

	/**
	 * The list of informative messages.
	 */
	protected final List<Message> _informationList;

	/**
	 * Initializes a newly created object with no data.
	 */
	public ParseResult() {
		_errorList = new ArrayList<>();
		_warningList = new ArrayList<>();
		_informationList = new ArrayList<>();
	}

	/**
	 * Initializes a newly created object with another {@code ParseResult}.
	 */
	protected ParseResult(ParseResult source) {
		_errorList = source._errorList;
		_warningList = source._warningList;
		_informationList = source._informationList;
	}

	/**
	 * @return true if no error messages are found, false if one or more error messages are found
	 */
	public boolean isSuccessful() {
		return _errorList.size() == 0;
	}

	/**
	 * @return The first {@code Message} object stored as an error.
	 */
	public Message error() {
		return _errorList.get(0);
	}

	/**
	 * @return true if no warning messages are found, false if one or more warning messages are found
	 */
	public boolean hasWarning() {
		return _warningList.size() > 0;
	}

	/**
	 * @return an array of warning messages.
	 */
	public Message[] warnings() {
		return _warningList.toArray(new Message[_warningList.size()]);
	}

	/**
	 * @return true if no informative messages are found, false if one or more informative messages are found
	 */
	public boolean hasInformation() {
		return _informationList.size() > 0;
	}

	/**
	 * @return an array of informative messages.
	 */
	public Message[] information() {
		return _informationList.toArray(new Message[_informationList.size()]);
	}

}
