package com.hideakin.yanimu.maven.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.NullElement;

public class ArtifactMap<T extends SimpleArtifact> extends LinkedHashMap<String, T> {

	private static final long serialVersionUID = -8944849038660471980L;

	public final Element element;

	protected ArtifactMap() {
		super();
		element = NullElement.NULL_ELEMENT;
	}

	protected ArtifactMap(Element element, List<Element> elementList, Function<Element, T> creator) {
		super();
		this.element = element;
		for (Element child : elementList) {
			T artifact = creator.apply(child);
			String key = artifact.ga();
			if (super.containsKey(key)) {
				continue;
			}
			super.put(key, artifact);
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
				for (int i = 0; i < element.size(); i++) {
					Node node = element.get(i);
					if (node.type == Node.ELEMENT && (Element)node == existing.element) {
						element.set(i, artifact.element);
						return existing;
					}
				}
				//NEVER REACH HERE BUT PASS THROUGH JUST IN CASE
			}
		}
		element.add(artifact.element);
		return null;
	}

}
