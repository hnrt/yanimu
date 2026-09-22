package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.util.ElementMap;

public class SimpleSetting extends ElementMap {

	private static final long serialVersionUID = -6776173812698013537L;

	protected SimpleSetting(String tagName) {
		super(tagName);
	}

	protected SimpleSetting(Element element) {
		super(element);
	}

	public String id() {
		return getString("id", null);
	}

	public void setId(String value) {
		setString("id", value);
	}
	
}
