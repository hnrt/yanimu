package com.hideakin.yanimu.maven.settings;

import com.hideakin.yanimu.xml.Element;

public class ProfileCollection extends SettingCollection<Profile> {

	private static final long serialVersionUID = 7828170064507915249L;

	public ProfileCollection() {
		super();
	}

	public void load(Element element) {
		super.load(element, "profile", e -> Profile.of(e));
	}

}
