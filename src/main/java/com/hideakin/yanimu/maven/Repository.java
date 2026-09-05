package com.hideakin.yanimu.maven;

import com.hideakin.yanimu.xml.Element;

public class Repository extends PomMap {

	private static final long serialVersionUID = 5935771515093326870L;

	public Repository() {
		super("repository");
	}

	public Repository(Element element) {
		super(element);
		super.initialize();
	}

	public String name() {
		return super.getString("name", null);
	}

	public String id() {
		return super.getString("id", "default");
	}

	public String url() {
		return super.getString("url", null);
	}

	public String layout() {
		return super.getString("layout", "default");
	}

	public Boolean releasesEnabled() {
		return super.getBoolean("releases/enabled", Boolean.valueOf(true));
	}

	public Boolean snapshotsEnabled() {
		return super.getBoolean("snapshots/enabled", Boolean.valueOf(true));
	}

	public String snapshotsUpdatePolicy() {
		return super.getString("snapshots/updatePolicy", "daily");
	}

	public String snapshotsChecksumPolicy() {
		return super.getString("snapshots/checksumPolicy", "warn");
	}

	public void setName(String value) {
		setString("name", value);
	}

	public void setId(String value) {
		setString("id", value);
	}

	public void setUrl(String value) {
		setString("url", value);
	}

	public void setLayout(String value) {
		setString("layout", value);
	}

	public void setReleasesEnabled(boolean value) {
		setBoolean("releases/enabled", value);
	}

	public void setSnapshotsEnabled(boolean value) {
		setBoolean("snapshots/enabled", value);
	}

	public void setSnapshotsUpdatePolicy(String value) {
		setString("snapshots/updatePolicy", value);
	}

	public void setSnapshotsChecksumPolicy(String value) {
		setString("snapshots/checksumPolicy", value);
	}

}
