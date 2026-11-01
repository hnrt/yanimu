package com.hideakin.yanimu.xml.util;

import java.util.LinkedHashMap;

import com.hideakin.yanimu.xml.Element;

/**
 * A mutable hash map to store elements.
 */
public class ElementMap extends LinkedHashMap<String, Element> {

	private static final long serialVersionUID = -5825270790321631347L;

	/**
	 * An {@code Element} that contains child elements.
	 */
	public final Element element;

	/**
	 * Constructs a new {@code ElementMap} with a tag name.
	 * The map will have no child elements at this moment.
	 * @param tagName a tag name to create a new {@code Element}
	 */
	protected ElementMap(String tagName) {
		super();
		element = new Element(tagName);
	}

	/**
	 * Constructs a new {@code ElementMap} with an {@code Element}.
	 * The map will have all descendant elements the specified {@code Element} holds in it.
	 * Children can be obtained by invoking method get with its name.
	 * Also, grand children can be obtained by invoking method get
	 * with its child's name followed by a slash and the grand child's name.
	 * @param element an {@code Element} to set
	 */
	protected ElementMap(Element element) {
		super();
		this.element = element;
		initialize();
	}

	private void initialize() {
		for (Element child : element.getElements("/*")) {
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

	/**
	 * Returns a {@code String} content of the specified child {@code Element}.
	 * @param key the key to specify the child {@code Element}
	 * @param fallback a value to be returned if the specified child is not found
	 * @return a {@code String} content of the specified child {@code Element}, or {@code fallback} if not found
	 */
	protected String getString(String key, String fallback) {
		Element child = super.get(key);
		return child != null ? child.innerText() : fallback;
	}

	/**
	 * Sets the content of the specified child {@code Element} to the specified {@code String}.
	 * <p>
	 * Note that the specified child {@code Element} is removed if a value of {@code null} is specified.
	 * @param key the key to specify the child {@code Element}
	 * @param value a {@code String} to set
	 */
	protected void setString(String key, String value) {
		Element child = super.get(key);
		if (child != null) {
			if (value != null) {
				child.setInnerText(value);
			} else {
				super.remove(key);
				element.remove(child);
			}
		} else if (value != null) {
			child = new Element(key, value);
			super.put(key, child);
			element.add(child);
		}
	}

	/**
	 * Returns a {@code Boolean} content of the specified child {@code Element}.
	 * @param key the key to specify the child {@code Element}
	 * @param fallback a value to be returned if the specified child is not found
	 * @return a {@code Boolean} content of the specified child {@code Element}, or {@code fallback} if not found
	 */
	protected Boolean getBoolean(String key, Boolean fallback) {
		Element child = super.get(key);
		String value = child != null ? child.innerText() : null;
		return "true".equals(value) ? Boolean.valueOf(true) :
			"false".equals(value) ? Boolean.valueOf(false) :
			fallback;
	}

	/**
	 * Sets the content of the specified child {@code Element} to
	 * a {@code String} representation of the specified {@code Boolean}.
	 * <p>
	 * Note that the specified child {@code Element} is removed if a value of {@code null} is specified.
	 * @param key the key to specify the child {@code Element}
	 * @param value a {@code Boolean} to set
	 */
	protected void setBoolean(String key, Boolean value) {
		Element child = super.get(key);
		if (child != null) {
			if (value != null) {
				child.setInnerText(value ? "true" : "false");
			} else {
				super.remove(key);
				element.remove(child);
			}
		} else if (value != null) {
			child = new Element(key, value ? "true" : "false");
			super.put(key, child);
			element.add(child);
		}
	}

	/**
	 * Returns a {@code Integer} content of the specified child {@code Element}.
	 * @param key the key to specify the child {@code Element}
	 * @param fallback a value to be returned if the specified child is not found
	 * @return a {@code Integer} content of the specified child {@code Element}, or {@code fallback} if not found
	 */
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

	/**
	 * Sets the content of the specified child {@code Element} to
	 * a {@code String} representation of the specified {@code Integer}.
	 * <p>
	 * Note that the specified child {@code Element} is removed if a value of {@code null} is specified.
	 * @param key the key to specify the child {@code Element}
	 * @param value a {@code Integer} to set
	 */
	protected void setInteger(String key, Integer value) {
		Element child = super.get(key);
		if (child != null) {
			if (value != null) {
				child.setInnerText(value.toString());
			} else {
				super.remove(key);
				element.remove(child);
			}
		} else if (value != null) {
			child = new Element(key, value.toString());
			super.put(key, child);
			element.add(child);
		}
	}

}
