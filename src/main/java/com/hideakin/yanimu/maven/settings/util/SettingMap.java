package com.hideakin.yanimu.maven.settings.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import com.hideakin.yanimu.xml.Element;

import static com.hideakin.yanimu.xml.NullElement.NULL_ELEMENT;

public class SettingMap<T extends SimpleSetting> extends LinkedHashMap<String, T> {

	private static final long serialVersionUID = 1184146726733155804L;

	public final Element element;

	protected SettingMap() {
		super();
		element = NULL_ELEMENT;
	}

	protected SettingMap(Element element, List<Element> children, Function<Element, T> creator) {
		super();
		this.element = element;
		for (Element child : children) {
			T setting = creator.apply(child);
			if (setting.id() != null && !super.containsKey(setting.id())) {
				super.put(setting.id(), setting);
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
				for (int i = 0; i < element.size(); i++) {
					if (element.get(i) instanceof Element childElement && childElement == existing.element) {
						element.set(i, setting.element);
						return existing;
					}
				}
				//NEVER REACH HERE BUT PASS THROUGH JUST IN CASE
			}
		}
		element.add(setting.element);
		return null;
	}

}
