package com.hideakin.yanimu.maven;

import java.util.LinkedHashMap;
import java.util.function.Function;

import com.hideakin.yanimu.xml.Element;

public class ArtifactCollection<T extends Artifact> extends LinkedHashMap<String, T> {

	private static final long serialVersionUID = -6223550311914671971L;

	protected Element _element;

	protected ArtifactCollection() {
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
				T artifact = creator.apply(child);
				String key = artifact.ga();
				if (super.containsKey(key)) {
					continue;
				}
				super.put(key, artifact); 
			}
		}
	}

	public T get(String groupId, String artifactId) {
		String key = T.ga(groupId, artifactId);
		return super.get(key);
	}

	public void add(T artifact) {
		_element.addChild(artifact.element());
		String key = artifact.ga();
		super.put(key, artifact);
	}

}
