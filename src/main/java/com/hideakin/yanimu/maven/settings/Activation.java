package com.hideakin.yanimu.maven.settings;

import com.hideakin.yanimu.maven.Property;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.util.ElementMap;

public class Activation extends ElementMap {

	private static final long serialVersionUID = -5990241763331755976L;

	public Activation(String tagName) {
		super(tagName);
	}

	public Activation(Element element) {
		super(element);
	}

	public String jdk() {
		return getString("jdk", null);
	}

	public String os() {
		return getString("os", null);
	}

	public Property property() {
		Element name = super.get("property/name");
		Element value = super.get("property/value");
		if (name != null && value != null) {
			return new Property(name.innerText(), value.innerText());
		} else {
			return null;
		}
	}

	public ActivationFile file() {
		Element exists = super.get("file/exists");
		Element missing = super.get("file/missing");
		return exists != null || missing != null
				? new ActivationFile(
						exists != null ? exists.innerText() : null,
						missing != null ? missing.innerText() : null)
				: null;
	}

	public void setJdk(String value) {
		setString("jdk", value);
	}

	public void setOs(String value) {
		setString("os", value);
	}

	public void setProperty(Property property) {
		Element name = super.get("property/name");
		Element value = super.get("property/value");
		if (name != null && value != null) {
			name.setInnerText(property.key);
			value.setInnerText(property.value);
		} else if (name != null) {
			name.setInnerText(property.key);
			value = new Element("value", property.value);
			name.parent().add(value);
		} else if (value != null) {
			value.setInnerText(property.value);
			name = new Element("name", property.key);
			value.parent().add(name);
		} else {
			name = new Element("name", property.key);
			value = new Element("value", property.value);
			Element element = new Element("property");
			element.add(name);
			element.add(value);
			_element.add(element);
		}
	}

	public void setFile(ActivationFile file) {
		Element exists = super.get("file/exists");
		Element missing = super.get("file/missing");
		if (exists != null && missing != null) {
			Element fileElement = exists.parent();
			if (file != null) {
				if (file.exists != null) {
					exists.setInnerText(file.exists);
				} else {
					fileElement.remove(exists);
					exists = null;
				}
				if (file.missing != null) {
					missing.setInnerText(file.missing);
				} else {
					fileElement.remove(missing);
					missing = null;
				}
			}
			if (file == null || (exists == null && missing == null)) {
				_element.remove(fileElement);
			}
		} else if (exists != null) {
			Element fileElement = exists.parent();
			if (file != null) {
				if (file.exists != null) {
					exists.setInnerText(file.exists);
				} else {
					fileElement.remove(exists);
					exists = null;
				}
				if (file.missing != null) {
					missing = new Element("missing", file.missing);
					fileElement.add(missing);
				}
			}
			if (file == null || (exists == null && missing == null)) {
				_element.remove(fileElement);
			}
		} else if (missing != null) {
			Element fileElement = missing.parent(); 
			if (file != null) {
				if (file.exists != null) {
					exists = new Element("exists", file.exists);
					fileElement.add(exists);
				}
				if (file.missing != null) {
					missing.setInnerText(file.missing);
				} else {
					fileElement.remove(missing);
					missing = null;
				}
			}
			if (file == null || (exists == null && missing == null)) {
				_element.remove(fileElement);
			}
		} else if (file != null) {
			if (file.exists != null) {
				exists = new Element("exists", file.exists);
			}
			if (file.missing != null) {
				missing = new Element("missing", file.missing);
			}
			if (exists != null || missing != null) {
				Element fileElement = new Element("file");
				if (exists != null) {
					fileElement.add(exists);
				}
				if (missing != null) {
					fileElement.add(missing);
				}
				_element.add(fileElement);
			}
		}
	}

}
