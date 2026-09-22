package com.hideakin.yanimu.maven.settings.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;

public class SettingMap<T extends SimpleSetting> extends LinkedHashMap<String, T> {

	private static final long serialVersionUID = 1184146726733155804L;

	protected Element _element;

	protected SettingMap() {
		super();
	}

	public Element element() {
		return _element;
	}

	public void load(Element element, List<Element> children, Function<Element, T> creator) {
		super.clear();
		_element = element;
		if (_element != null) {
			for (Element child : children) {
				T setting = creator.apply(child);
				if (setting.id() != null && !super.containsKey(setting.id())) {
					super.put(setting.id(), setting);
				}
			}
		}
	}

	public T put(T setting) {
		return this.put(setting.id(), setting);
	}

	@Override
	public T put(String key, T setting) {
		if (key != null) {
			T existing = super.put(key, setting);
			if (existing != null) {
				for (int i = 0; i < _element.size(); i++) {
					Node node = _element.get(i);
					if (node.type == Node.ELEMENT && (Element)node == existing.element()) {
						_element.set(i, setting.element());
						return existing;
					}
				}
				//NEVER REACH HERE BUT PASS THROUGH JUST IN CASE
			}
		}
		_element.add(setting.element());
		return null;
	}

}
