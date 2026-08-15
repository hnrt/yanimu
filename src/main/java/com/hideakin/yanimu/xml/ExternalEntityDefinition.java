package com.hideakin.yanimu.xml;

import com.hideakin.yanimu.xml.doctype.ExternalIdentifiers;

public class ExternalEntityDefinition {

	public final String key;
	public final String systemLiteral;
	public final String pubidLiteral;
	public final String ndata;

	public ExternalEntityDefinition(String key, ExternalIdentifiers extid, String ndata) {
		this.key = key;
		this.systemLiteral = extid.systemLiteral;
		this.pubidLiteral = extid.pubidLiteral;
		this.ndata = ndata;
	}

}
