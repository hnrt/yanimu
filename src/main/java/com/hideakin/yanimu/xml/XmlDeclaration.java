package com.hideakin.yanimu.xml;

public class XmlDeclaration extends Token {

	public final String version;
	public final String encoding;
	public final String standalone;

	public XmlDeclaration(int start, int end, String version, String encoding, String standalone) {
		super(XML, start, end);
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}

}
