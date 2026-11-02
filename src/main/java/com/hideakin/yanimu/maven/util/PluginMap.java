package com.hideakin.yanimu.maven.util;

import com.hideakin.yanimu.xml.Element;

public class PluginMap extends ArtifactMap<Plugin> {

	private static final long serialVersionUID = -572924994235221887L;

	public PluginMap() {
		super();
	}

	public PluginMap(Element element) {
		super(element, element.getElements("/plugin"), e -> new Plugin(e));
	}

}
