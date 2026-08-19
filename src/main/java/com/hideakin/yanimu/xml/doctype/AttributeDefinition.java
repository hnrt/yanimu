package com.hideakin.yanimu.xml.doctype;

public class AttributeDefinition {

	public final String key;
	public final Object type;
	public final AttributeDefault value;

	public AttributeDefinition(String key, Object type, AttributeDefault value) {
		this.key = key;
		this.type = type;
		this.value = value;
	}

}
