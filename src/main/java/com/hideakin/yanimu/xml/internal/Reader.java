package com.hideakin.yanimu.xml.internal;

/**
 * The Reader interface.
 */
public interface Reader {

	/**
	 * Read the next code point.
	 * @return the code point read
	 */
	int readCodePoint();

	/**
	 * Attempts to read a sequence of the code points.
	 * If the specified sequence of the code points can be read,
	 * advances the internal reading pointer to the next code point.
	 * Otherwise, do nothing.
	 * @param cc a sequence of the code points to read
	 * @return true if read successfully, or false otherwise
	 */
	boolean next(int... cc);

	/**
	 * Attempts to read a sequence of the code points, but does not advance the internal reading pointer.
	 * @param cc a sequence of the code points to read
	 * @return true if the sequence of the code points can be read successfully, or false otherwise
	 */
	boolean peek(int... cc);

	/**
	 * Returns the code point currently read.
	 * @return the code point currently read.
	 */
	int codepoint();

}
