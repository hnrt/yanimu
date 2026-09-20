package com.hideakin.yanimu.xml.internal;

public class ReaderFactory {

	public static Reader create(byte[] content, CodePointBuffer buffer) {
		if (3 <= content.length && content[0]  == -17 && content[1] == -69 && content[2] == -65) {
			// EF BB BF
			return new UTF8Reader(content, buffer);
		} else if (2 <= content.length && content[0] == -1 && content[1] == -2) {
			// FF FE
			return new UTF16LEReader(content, buffer);
		} else {
			return new UTF8Reader(content, buffer);
		}
	}

}
