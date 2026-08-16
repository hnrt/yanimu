package com.hideakin.yanimu.xml.doctype;

public class AttributeDefinition {

	public String key;
	public Object type;
	public AttributeDefault value;

	public AttributeDefinition(String key, Object type, AttributeDefault value) {
		this.key = key;
		this.type = type;
		this.value = value;
	}

}
