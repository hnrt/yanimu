package com.hideakin.yanimu.xml.internal;

public class ReaderFactory {

	public static Reader create(byte[] content) {
		if (3 <= content.length && content[0] == 0xEF && content[1] == 0xBB && content[2] == 0xBF) {
			return new UTF8Reader(content);
		} else if (2 <= content.length && content[0] == 0xFF && content[1] == 0xFE) {
			return new UTF16LEReader(content);
		} else {
			return new UTF8Reader(content);
		}
	}

}
