package com.hideakin.yanimu.xml.util;

import java.util.LinkedHashMap;

import com.hideakin.yanimu.xml.Element;

public class ElementMap extends LinkedHashMap<String, Element> {

	private static final long serialVersionUID = -5825270790321631347L;

	protected final Element _element;

	protected ElementMap(String tagName) {
		super();
		_element = new Element(tagName);
	}

	protected ElementMap(Element element) {
		super();
		_element = element;
		initialize();
	}

	private void initialize() {
		for (Element child : _element.getElements("/*")) {
			if (child.hasElement()) {
				initialize(child, child.name + "/");
			} else {
				super.put(child.name, child);
			}
		}
	}

	private void initialize(Element element, String leader) {
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

	protected String getString(String key, String fallback) {
		Element child = super.get(key);
		return child != null ? child.innerText() : fallback;
	}

	protected void setString(String key, String value) {
		Element child = super.get(key);
		if (child != null) {
			if (value != null) {
				child.setInnerText(value);
			} else {
				super.remove(key);
				_element.remove(child);
			}
		} else if (value != null) {
			child = new Element(key, value);
			super.put(key, child);
			_element.add(child);
		}
	}

	protected Boolean getBoolean(String key, Boolean fallback) {
		Element child = super.get(key);
		String value = child != null ? child.innerText() : null;
		return "true".equals(value) ? Boolean.valueOf(true) :
			"false".equals(value) ? Boolean.valueOf(false) :
			fallback;
	}

	protected void setBoolean(String key, Boolean value) {
		Element child = super.get(key);
		if (child != null) {
			if (value != null) {
				child.setInnerText(value ? "true" : "false");
			} else {
				super.remove(key);
				_element.remove(child);
			}
		} else if (value != null) {
			child = new Element(key, value ? "true" : "false");
			super.put(key, child);
			_element.add(child);
		}
	}

	protected Integer getInteger(String key, Integer fallback) {
		Element child = super.get(key);
		if (child != null) {
			try {
				int value = Integer.parseInt(child.innerText());
				return Integer.valueOf(value);
			} catch (NumberFormatException e) {
				return fallback;
			}
		} else {
			return fallback;
		}
	}

	protected void setInteger(String key, Integer value) {
		Element child = super.get(key);
		if (child != null) {
			if (value != null) {
				child.setInnerText(value.toString());
			} else {
				super.remove(key);
				_element.remove(child);
			}
		} else if (value != null) {
			child = new Element(key, value.toString());
			super.put(key, child);
			_element.add(child);
		}
	}

}
