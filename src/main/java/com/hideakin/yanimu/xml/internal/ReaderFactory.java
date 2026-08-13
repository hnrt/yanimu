package com.hideakin.yanimu.xml.internal;

public class ReaderFactory {

	public static Reader create(byte[] content) {
		if (3 <= content.length && content[0]  == -17 && content[1] == -69 && content[2] == -65) {
			// EF BB BF
			return new UTF8Reader(content);
		} else if (2 <= content.length && content[0] == -1 && content[1] == -2) {
			// FF FE
			return new UTF16LEReader(content);
		} else {
			return new UTF8Reader(content);
		}
	}

}
