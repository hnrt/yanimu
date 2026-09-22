package com.hideakin.yanimu.maven.util;

import java.util.LinkedHashMap;
import java.util.function.Function;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;

public class ArtifactMap<T extends SimpleArtifact> extends LinkedHashMap<String, T> {

	private static final long serialVersionUID = -8944849038660471980L;

	protected Element _element;

	protected ArtifactMap() {
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
		String key = SimpleArtifact.ga(groupId, artifactId);
		return super.get(key);
	}

	@Override
	public T put(String key, T artifact) {
		if (key != null) {
			T existing = super.put(key, artifact);
			if (existing != null) {
				for (int i = 0; i < _element.size(); i++) {
					Node node = _element.get(i);
					if (node.type == Node.ELEMENT && (Element)node == existing.element()) {
						_element.set(i, artifact.element());
						return existing;
					}
				}
				//NEVER REACH HERE BUT PASS THROUGH JUST IN CASE
			}
		}
		_element.add(artifact.element());
		return null;
	}

}
