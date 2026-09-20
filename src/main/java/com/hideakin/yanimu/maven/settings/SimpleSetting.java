package com.hideakin.yanimu.maven.settings;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.util.ElementMap;

public class SimpleSetting extends ElementMap {

	private static final long serialVersionUID = 8553744112686815293L;

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
