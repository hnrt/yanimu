package com.hideakin.yanimu.xml.doctype;

import java.util.ArrayList;

public class EnumerationType extends ArrayList<String> {

	private static final long serialVersionUID = -4986514221243964032L;

	public EnumerationType(String name) {
		super();
		add(name);
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("(");
		buf.append(get(0));
		for (int i = 1; i < size(); i++) {
			buf.append("|");
			buf.append(get(i));
		}
		buf.append(")");
		return buf.toString();
	}

}
