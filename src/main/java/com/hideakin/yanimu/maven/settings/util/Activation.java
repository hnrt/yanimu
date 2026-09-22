package com.hideakin.yanimu.maven.settings.util;

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

	public ActivationFile file() {
		Element exists = super.get("file/exists");
		Element missing = super.get("file/missing");
		return exists != null || missing != null
				? new ActivationFile(
						exists != null ? exists.innerText() : null,
						missing != null ? missing.innerText() : null)
				: null;
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
