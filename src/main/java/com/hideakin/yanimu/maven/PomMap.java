package com.hideakin.yanimu.maven;

import java.util.LinkedHashMap;

import com.hideakin.yanimu.xml.Element;

public class PomMap extends LinkedHashMap<String, Element> {

	private static final long serialVersionUID = -5825270790321631347L;

	private final Element _element;

	protected PomMap(String tagName) {
		super();
		_element = new Element(tagName);
	}

	protected PomMap(Element element) {
		super();
		_element = element;
	}

	protected void initialize() {
		for (Element child : _element.getElements("/*")) {
			if (child.hasElement()) {
				initialize(child, child.name + "/");
			} else {
				super.put(child.name, child);
			}
		}
	}

	protected void initialize(Element element, String leader) {
		for (Element child : element.getElements("/*")) {
			if (child.hasElement()) {
				initialize(child, leader + child.name + "/");
			} else {
				super.put(leader + child.name, child);
			}
		}
	}

	public Element element() {
		return _element;
	}

	protected String getString(String key, String defaultValue) {
		Element child = super.get(key);
		return child != null ? child.innerText() : defaultValue;
	}

	protected void setString(String key, String value) {
		Element child = super.get(key);
		if (child != null) {
			child.setInnerText(value);
		} else {
			child = new Element(key, value);
			super.put(key, child);
			_element.addChild(child);
		}
	}

	protected Boolean getBoolean(String key, Boolean defaultValue) {
		Element child = super.get(key);
		String value = child != null ? child.innerText() : null;
		return "true".equals(value) ? Boolean.valueOf(true) :
			"false".equals(value) ? Boolean.valueOf(false) :
			defaultValue;
	}

	protected void setBoolean(String key, boolean value) {
		Element child = super.get(key);
		if (child != null) {
			child.setInnerText(value ? "true" : "false");
		} else {
			child = new Element(key, value ? "true" : "false");
			super.put(key, child);
			_element.addChild(child);
		}
	}

}
