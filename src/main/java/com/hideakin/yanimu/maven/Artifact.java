package com.hideakin.yanimu.maven;

import com.hideakin.yanimu.xml.Element;

public class Artifact extends PomMap {

	private static final long serialVersionUID = 2059870372450308123L;

	protected Artifact(String name) {
		super(name);
	}

	protected Artifact(Element element) {
		super(element);
		super.initialize();
	}

	public String groupId() {
		return super.getString("groupId", null);
	}

	public String artifactId() {
		return super.getString("artifactId", null);
	}

	public String version() {
		return super.getString("version", null);
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

	public String ga() {
		return ga(groupId(), artifactId());
	}

	public String gav() {
		return gav(groupId(), artifactId(), version());
	}

	public static String ga(String g, String a) {
		return (g != null ? g : "") + ":" + (a != null ? a : "");
	}

	public static String gav(String g, String a, String v) {
		return (g != null ? g : "") + ":" + (a != null ? a : "") + ":" + (v != null ? v : "");
	}

}
