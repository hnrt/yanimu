package com.hideakin.yanimu.maven.util;

import com.hideakin.yanimu.xml.Element;

public class PluginMap extends ArtifactMap<Plugin> {

	private static final long serialVersionUID = -572924994235221887L;

	public PluginMap() {
		super();
	}

	public void load(Element element) {
		super.load(element, "plugin", e -> new Plugin(e));
	}

}
