package com.hideakin.yanimu.maven;

import java.util.LinkedHashMap;

import com.hideakin.yanimu.xml.Element;

public class RepositoryCollection extends LinkedHashMap<String, Repository> {

	private static final long serialVersionUID = -5121318124641504158L;

	private Element _element;

	public RepositoryCollection() {
		super();
	}

	public void load(Element element) {
		super.clear();
		_element = element;
		if (_element != null) {
			for (Element child : _element.getElements("/repository")) {
				Repository repository = new Repository(child);
				String id = repository.id();
				if (id == null || super.containsKey(id)) {
					continue;
				}
				super.put(id, repository);
			}
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
