package com.hideakin.yanimu.maven;

import com.hideakin.yanimu.xml.Element;

public class SimpleArtifact extends PomMap implements Artifact {

	private static final long serialVersionUID = 2059870372450308123L;

	public static String ga(String g, String a) {
		return (g != null ? g : "") + ":" + (a != null ? a : "");
	}

	protected SimpleArtifact(String name) {
		super(name);
	}

	protected SimpleArtifact(Element element) {
		super(element);
		super.initialize();
	}

	@Override
	public String groupId() {
		return super.getString("groupId", null);
	}

	@Override
	public String artifactId() {
		return super.getString("artifactId", null);
	}

	@Override
	public String version() {
		return super.getString("version", null);
	}

	@Override
	public String ga() {
		String g = groupId();
		String a = artifactId();
		return ga(g, a);
	}

	public void setGroupId(String value) {
		setString("groupId", value);
	}

	public void setArtifactId(String value) {
		setString("artifactId", value);
	}

	public void setVersion(String value) {
		setString("version", value);
	}

}
