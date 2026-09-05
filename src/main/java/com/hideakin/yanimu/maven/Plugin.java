package com.hideakin.yanimu.maven;

import com.hideakin.yanimu.xml.Element;

public class Plugin extends Artifact {

	private static final long serialVersionUID = -5628543134369984893L;

	public Plugin() {
		super("plugin");
	}

	public Plugin(String groupId, String artifactId) {
		super("plugin");
		setGroupId(groupId);
		setArtifactId(artifactId);
	}

	public Plugin(Element element) {
		super(element);
	}

}
