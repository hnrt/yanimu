package com.hideakin.yanimu.xml;

public class ExternalIdentifiers {

	public final String systemLiteral;
	public final String pubidLiteral;

	public ExternalIdentifiers(String systemLiteral) {
		this.systemLiteral = systemLiteral;
		this.pubidLiteral = null;
	}

	public ExternalIdentifiers(String pubidLiteral, String systemLiteral) {
		this.systemLiteral = systemLiteral;
		this.pubidLiteral = pubidLiteral;
	}

}
