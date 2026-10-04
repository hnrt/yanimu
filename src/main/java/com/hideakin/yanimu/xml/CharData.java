package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

/**
 * An immutable node object for XML Character Data.
 * <p>
 * Character data defines the following illegal characters:
 * <ul>
 * <li>a less-than sign (<b>&lt;</b>)</li>
 * <li>a ampersand (<b>&amp;</b>)</li>
 * <li>a sequence of two closing square brackets followed by a greater-than sign (<b>]]&gt;</b>)</li>
 * </ul>
 * These characters are not allowed to appear in character data.
 * <p>
 * Replacement of characters such as less-than sign, ampersand, and greater-than sign
 * with their corresponding entity reference is called as "normalization" in this class.
 */
public class CharData extends ImmutableNode {

	/**
	 * A flag value not to normalize the sequence; to take the sequence as it is.
	 */
	public static final int IMMEDIATE = 0;

	/**
	 * A flag value to normalize the sequence.
	 */
	public static final int NORMALIZE = 1;

	/**
	 * Creates a new immutable node of {@code CHAR_DATA}.
	 * <p>
	 * The normalization is took place on storing the given sequence in it.
	 * @param sequence the UTF-8 representation of character data
	 * @return a newly created node of {@code CHAR_DATA}
	 */
	public static CharData of(byte[] sequence) {
		return new CharData(sequence, NORMALIZE);
	}

	/**
	 * Creates a new immutable node of {@code CHAR_DATA}.
	 * <p>
	 * The normalization is took place on storing the given sequence in it.
	 * @param sequence the String representation of character data
	 * @return a newly created node of {@code CHAR_DATA}
	 */
	public static CharData of(String sequence) {
		return new CharData(sequence.getBytes(StandardCharsets.UTF_8), NORMALIZE);
	}

	/**
	 * Creates a new immutable node of {@code CHAR_DATA} with a flag to indicate the normalization processing.
	 * <p>
	 * If the flag is set to {@code NORMALIZE}, the normalization is took place on storing the given sequence in it.
	 * <p>
	 * If the flag is set to {@code IMMEDIATE}, the normalization is not took place; the given sequence is stored as it is.
	 * @param sequence the UTF-8 representation of character data
	 * @return a newly created node of {@code CHAR_DATA}
	 */
	public static CharData of(byte[] sequence, int flag) {
		return new CharData(sequence, flag);
	}

	/**
	 * Creates a new immutable node of {@code CHAR_DATA} with a flag to indicate the normalization processing.
	 * <p>
	 * If the flag is set to {@code NORMALIZE}, the normalization is took place on storing the given sequence in it.
	 * <p>
	 * If the flag is set to {@code IMMEDIATE}, the normalization is not took place; the given sequence is stored as it is.
	 * @param sequence the String representation of character data
	 * @return a newly created node of {@code CHAR_DATA}
	 */
	public static CharData of(String sequence, int flag) {
		return new CharData(sequence.getBytes(StandardCharsets.UTF_8), flag);
	}

	private CharData(byte[] sequence, int flag) {
		super(CHAR_DATA, flag == NORMALIZE ? normalize(sequence) : sequence);
	}

	/**
	 * Replaces a less-than sign, an ampersand, and a greater-than sign contained in the given byte sequences
	 * with their corresponding entity reference.
	 * For a greater-than sign, the replacement is took place only if it follows two closing square brackets.
	 * @param source the byte sequence to process
	 * @return the resulting byte sequence
	 */
	public static byte[] normalize(byte[] source) {
		for (int i = 0; i < source.length; i++) {
			switch (source[i]) {
			case '<':
			case '&':
				return normalize(source, i);
			case ']':
				if (i + 2 < source.length && source[i + 1] == ']' && source[i + 2] == '>') {
					return normalize(source, i);
				}
				//FALLTHROUGH
			default:
				break;
			}
		}
		return source;
	}

	private static final byte[] LT = { '&', 'l', 't', ';' };
	private static final byte[] AMP = { '&', 'a', 'm', 'p', ';' };
	private static final byte[] CBCBGT = { ']', ']', '&', 'g', 't', ';' };

	private static byte[] normalize(byte[] source, int first) {
		int d = 0;
		for (int i = first; i < source.length; i++) {
			switch (source[i]) {
			case '<':
				d += LT.length - 1;
				break;
			case '&':
				d += AMP.length - 1;
				break;
			case ']':
				if (i + 2 < source.length && source[i + 1] == ']' && source[i + 2] == '>') {
					d += CBCBGT.length - 3;
					i += 2;
					break;
				}
				//FALLTHROUGH
			default:
				break;
			}
		}
		byte[] destination = new byte[source.length + d];
		int j = 0;
		int h = 0;
		for (int i = first; i < source.length; i++) {
			byte b = source[i];
			switch (b) {
			case '<':
				if (h < i) {
					int n = i - h;
					System.arraycopy(source, h, destination, j, n);
					j += n;
				}
				h = i + 1;
				System.arraycopy(LT, 0, destination, j, LT.length);
				j += LT.length;
				break;
			case '&':
				if (h < i) {
					int n = i - h;
					System.arraycopy(source, h, destination, j, n);
					j += n;
				}
				h = i + 1;
				System.arraycopy(AMP, 0, destination, j, AMP.length);
				j += AMP.length;
				break;
			case ']':
				if (i + 2 < source.length && source[i + 1] == ']' && source[i + 2] == '>') {
					if (h < i) {
						int n = i - h;
						System.arraycopy(source, h, destination, j, n);
						j += n;
					}
					h = i + 1;
					System.arraycopy(CBCBGT, 0, destination, j, CBCBGT.length);
					j += CBCBGT.length;
					i += 2;
					break;
				}
				//FALLTHROUGH
			default:
				break;
			}
		}
		if (h < source.length) {
			int n = source.length - h;
			System.arraycopy(source, h, destination, j, n);
		}
		return destination;
	}

}
