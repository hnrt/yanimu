package com.hideakin.yanimu.xml.doctype;

public class AttributeDefault {

	public static final int DEFAULT_REQUIRED = 1;
	public static final int DEFAULT_IMPLIED = 2;
	public static final int DEFAULT_FIXED = 3;

	public Integer type;
	public String value;

	public AttributeDefault(int type) {
		if (type != DEFAULT_REQUIRED && type != DEFAULT_IMPLIED) {
			throw new RuntimeException("AttributeDefault: Bad type.");
		}
		this.type = Integer.valueOf(type);
		this.value = null;
	}

	public AttributeDefault(String value) {
		this.type = null;
		this.value = value;
	}

	public AttributeDefault(int type, String value) {
		if (type != DEFAULT_FIXED) {
			throw new RuntimeException("AttributeDefault: Bad type.");
		}
		this.type = Integer.valueOf(type);
		this.value = value;
	}

	@Override
	public String toString() {
		if (this.type == null) {
			return String.format("\"%s\"", this.value.replaceAll("\"", "\\\\\""));
		} else if (this.type == DEFAULT_REQUIRED) {
			return "#REQUIRED";
		} else if (this.type == DEFAULT_IMPLIED) {
			return "#IMPLIED";
		} else {
			return String.format("#FIXED \"%s\"", this.value.replaceAll("\"", "\\\\\""));
		}
	}

}
