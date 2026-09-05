package com.hideakin.yanimu.maven;

import com.hideakin.yanimu.xml.Element;

public class Dependency extends Artifact {

	private static final long serialVersionUID = 4705051321102482384L;

	public Dependency() {
		super("dependency");
	}

	public Dependency(String groupId, String artifactId) {
		super("dependency");
		setGroupId(groupId);
		setArtifactId(artifactId);
	}

	public Dependency(Element element) {
		super(element);
	}

	public String classifier() {
		return super.getString("classifier", null);
	}

	public String type() {
		return ArtifactType.of(super.getString("type", ArtifactType.DEFAULT)).value;
	}

	public String scope() {
		return DependencyScope.of(super.getString("scope", DependencyScope.DEFAULT)).value;
	}

	public String optional() {
		return super.getString("optional", null);
	}

	public String systemPath() {
		return super.getString("systemPath", null);
	}
	
	public void setClassifier(String value) {
		setString("classifier", value);
	}

	public void setType(String value) {
		setString("type", value);
	}

	public void setScope(String value) {
		setString("scope", value);
	}

	public void setOptional(String value) {
		setString("optional", value);
	}

	public void setSystemPath(String value) {
		setString("systemPath", value);
	}

}
