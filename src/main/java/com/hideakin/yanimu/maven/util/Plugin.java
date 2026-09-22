package com.hideakin.yanimu.maven.util;

import com.hideakin.yanimu.xml.Element;

public class Plugin extends SimpleArtifact {

	private static final long serialVersionUID = -648001463152747397L;

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
