package com.hideakin.yanimu.maven.settings;

public class ActivationFile {

	public final String exists;
	public final String missing;

	public ActivationFile(String exists, String missing) {
		this.exists = exists;
		this.missing = missing;
	}

}
