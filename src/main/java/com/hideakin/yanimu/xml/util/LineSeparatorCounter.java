package com.hideakin.yanimu.xml.util;

public class LineSeparatorCounter {

	private int _lf = 0;
	private int _crlf = 0;

	public LineSeparatorCounter() {
	}

	public int lf() {
		return _lf;
	}

	public int crlf() {
		return _crlf;
	}

	public void incrementLF() {
		_lf++;
	}

	public void incrementCRLF() {
		_crlf++;
	}

	public boolean isLF() {
		return _lf >= _crlf;
	}

	public boolean isCRLF() {
		return _lf < _crlf;
	}

}
