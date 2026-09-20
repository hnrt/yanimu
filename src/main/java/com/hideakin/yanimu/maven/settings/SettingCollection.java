package com.hideakin.yanimu.maven.settings;

import java.util.LinkedHashMap;
import java.util.function.Function;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;

public class SettingCollection<T extends SimpleSetting> extends LinkedHashMap<String, T> {

	private static final long serialVersionUID = 1184146726733155804L;

	protected Element _element;

	protected SettingCollection() {
		super();
	}

	public Element element() {
		return _element;
	}

	public void load(Element element, String name, Function<Element, T> creator) {
		super.clear();
		_element = element;
		if (_element != null) {
			String pattern = "/" + name;
			for (Element child : _element.getElements(pattern)) {
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
				for (int i = 0; i < _element.childCount(); i++) {
					Node child = _element.child(i);
					if (child.type == Node.ELEMENT && (Element)child == existing.element()) {
						_element.remove(i);
						_element.add(i, setting.element());
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
