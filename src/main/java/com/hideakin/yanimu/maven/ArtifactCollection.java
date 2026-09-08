package com.hideakin.yanimu.maven;

import java.util.LinkedHashMap;
import java.util.function.Function;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;

public class ArtifactCollection<T extends SimpleArtifact> extends LinkedHashMap<String, T> {

	private static final long serialVersionUID = -6223550311914671971L;

	protected Element _element;

	protected ArtifactCollection() {
		super();
	}

	public Element element() {
		return _element;
	}

	public void load(Element element, String name, PropertyManager propertyManager, Function<Element, T> creator) {
		super.clear();
		_element = element;
		if (_element != null) {
			String pattern = "/" + name;
			for (Element child : _element.getElements(pattern)) {
				T artifact = creator.apply(child);
				String key = propertyManager.translate(artifact.ga());
				if (super.containsKey(key)) {
					continue;
				}
				super.put(key, artifact); 
			}
		}
	}

	public T get(String groupId, String artifactId) {
		String key = SimpleArtifact.ga(groupId, artifactId);
		return super.get(key);
	}

	@Override
	public T put(String key, T artifact) {
		T existing = super.put(key, artifact);
		if (existing != null) {
			for (int i = 0; i < _element.childCount(); i++) {
				Node child = _element.child(i);
				if (child.type == Node.ELEMENT && (Element)child == existing.element()) {
					_element.removeChild(i);
					_element.addChild(i, artifact.element());
					return existing;
				}
			}
			//NEVER REACH HERE BUT PASS THROUGH JUST IN CASE
		}
		_element.addChild(artifact.element());
		_element.indent();
		return null;
	}

}
