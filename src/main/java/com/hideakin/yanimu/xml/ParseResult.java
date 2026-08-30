package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class ParseResult {

	public static class Message {

		public final int offset;
		public final String message;

		public Message(int offset, String format, Object...args) {
			this.offset = offset;
			this.message = String.format(format, args);
		}

	}

	protected final List<Message> _errorList;
	protected final List<Message> _warningList;
	protected final List<Message> _informationList;

	public ParseResult() {
		_errorList = new ArrayList<>();
		_warningList = new ArrayList<>();
		_informationList = new ArrayList<>();
	}

	protected ParseResult(ParseResult source) {
		_errorList = source._errorList;
		_warningList = source._warningList;
		_informationList = source._informationList;
	}

	public boolean isSuccessful() {
		return _errorList.size() == 0;
	}

	public Message error() {
		return _errorList.get(0);
	}

	public boolean hasWarning() {
		return _warningList.size() > 0;
	}

	public Message[] warnings() {
		return _warningList.toArray(new Message[_warningList.size()]);
	}

	public boolean hasInformation() {
		return _informationList.size() > 0;
	}

	public Message[] information() {
		return _informationList.toArray(new Message[_informationList.size()]);
	}

}
