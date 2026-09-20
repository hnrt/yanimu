package com.hideakin.yanimu.maven.settings;

import com.hideakin.yanimu.xml.Element;

public class Mirror extends SimpleSetting {

	private static final long serialVersionUID = 5939532917931958570L;

	public static Mirror of() {
		return new Mirror();
	}

	public static Mirror of(Element element) {
		return new Mirror(element);
	}

	private Mirror() {
		super("mirror");
	}

	private Mirror(Element element) {
		super(element);
	}

	public String name() {
		return getString("name", null);
	}

	public String url() {
		return getString("url", null);
	}

	public String mirrorOf() {
		return getString("mirrorOf", null);
	}

	public void setName(String value) {
		setString("name", value);
	}

	public void setUrl(String value) {
		setString("url", value);
	}

	public void setMirrorOf(String value) {
		setString("mirrorOf", value);
	}

}
