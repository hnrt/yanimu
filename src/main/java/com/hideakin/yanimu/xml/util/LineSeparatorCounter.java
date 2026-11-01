package com.hideakin.yanimu.xml.util;

/**
 * A class to count line separators.
 */
public class LineSeparatorCounter {

	private static final String _native = System.getProperty("line.separator");

	private int _lf = 0;
	private int _crlf = 0;

	/**
	 * Initializes the counters to zero.
	 */
	public LineSeparatorCounter() {
	}

	/**
	 * Returns the number of LF's.
	 * @return the number of LF's
	 */
	public int lf() {
		return _lf;
	}

	/**
	 * Returns the number of CRLF's.
	 * @return the number of CRLF's
	 */
	public int crlf() {
		return _crlf;
	}

	/**
	 * Increments the number of LF's by 1.
	 */
	public void incrementLF() {
		_lf++;
	}

	/**
	 * Increments the number of CRLF's by 1.
	 */
	public void incrementCRLF() {
		_crlf++;
	}

	/**
	 * Returns true if the number of LF's is greater than that of CRLF's.
	 * <p>
	 * If the number of LF's is equal to that of CRLF's, it depends on the native line separator for the operating system;
	 * if the native line separator is LF, true is returned. Otherwise, false is returned.
	 * @return true if the number of LF's is greater than that of CRLF's, or false otherwise.
	 */
	public boolean isLF() {
		return _lf > _crlf || (_lf == _crlf && "\n".equals(_native));
	}

	/**
	 * Returns true if the number of CRLF's is greater than that of LF's.
	 * <p>
	 * If the number of CRLF's is equal to that of LF's, it depends on the native line separator for the operating system;
	 * if the native line separator is CRLF, true is returned. Otherwise, false is returned.
	 * @return true if the number of CRLF's is greater than that of LF's, or false otherwise.
	 */
	public boolean isCRLF() {
		return _lf < _crlf || (_lf == _crlf && "\r\n".equals(_native));
	}

}
