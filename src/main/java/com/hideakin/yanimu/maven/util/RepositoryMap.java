package com.hideakin.yanimu.maven.util;

import java.util.LinkedHashMap;
import java.util.List;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.NullElement;

public class RepositoryMap extends LinkedHashMap<String, Repository> {

	private static final long serialVersionUID = 3246679621341023499L;

	public final Element element;

	public RepositoryMap() {
		super();
		element = NullElement.NULL_ELEMENT;
	}

	public RepositoryMap(Element element, List<Element> elementList) {
		super();
		this.element = element;
		for (Element child : elementList) {
			Repository repository = new Repository(child);
			String id = repository.id();
			if (id == null || super.containsKey(id)) {
				continue;
			}
			super.put(id, repository);
		}
	}

	public boolean containsUrl(String url) {
		for (Repository repository : super.values()) {
			if (url.equals(repository.url())) {
				return true;
			}
		}
		return false;
	}

}
