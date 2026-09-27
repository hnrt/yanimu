package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class CharData extends ImmutableNode {

	public static final int IMMEDIATE = 0;
	public static final int NORMALIZE = 1;

	public static CharData of(byte[] sequence) {
		return new CharData(sequence, NORMALIZE);
	}

	public static CharData of(String sequence) {
		return new CharData(sequence.getBytes(StandardCharsets.UTF_8), NORMALIZE);
	}

	public static CharData of(byte[] sequence, int flag) {
		return new CharData(sequence, flag);
	}

	public static CharData of(String sequence, int flag) {
		return new CharData(sequence.getBytes(StandardCharsets.UTF_8), flag);
	}

	private CharData(byte[] sequence, int flag) {
		super(CHAR_DATA, flag == NORMALIZE ? normalize(sequence) : sequence);
	}

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
