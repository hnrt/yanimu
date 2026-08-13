package com.hideakin.yanimu.xml;

public class ExternalParameterEntityDefinition {

	public final String key;
	public final String systemLiteral;
	public final String pubidLiteral;

	public ExternalParameterEntityDefinition(String key, ExternalIdentifiers extid) {
		this.key = key;
		this.systemLiteral = extid.systemLiteral;
		this.pubidLiteral = extid.pubidLiteral;
	}

}
