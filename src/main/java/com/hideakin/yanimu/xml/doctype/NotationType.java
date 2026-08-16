package com.hideakin.yanimu.xml.doctype;

import java.util.ArrayList;

public class NotationType extends ArrayList<String> {

	private static final long serialVersionUID = 7174714680287513461L;

	public NotationType(String name) {
		super();
		add(name);
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("NOTATION(");
		buf.append(get(0));
		for (int i = 1; i < size(); i++) {
			buf.append("|");
			buf.append(get(i));
		}
		buf.append(")");
		return buf.toString();
	}

}
