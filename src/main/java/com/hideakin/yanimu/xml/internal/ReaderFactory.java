package com.hideakin.yanimu.xml.internal;

public class ReaderFactory {

	public ReaderFactory() {
	}

	public Reader create(byte[] content, NodeFactory nodeFactory) {
		if (3 <= content.length && content[0]  == -17 && content[1] == -69 && content[2] == -65) {
			// EF BB BF
			return new UTF8Reader(content, nodeFactory);
		} else if (2 <= content.length && content[0] == -1 && content[1] == -2) {
			// FF FE
			return new UTF16LEReader(content, nodeFactory);
		} else {
			return new UTF8Reader(content, nodeFactory);
		}
	}

}
