package com.hideakin.yanimu.maven;

import com.hideakin.yanimu.xml.Element;

public class PluginCollection extends ArtifactCollection<Plugin> {

	private static final long serialVersionUID = 2750804175414762230L;

	public PluginCollection() {
		super();
	}

	public void load(Element element) {
		super.load(element, "plugin", e -> new Plugin(e));
	}

}
