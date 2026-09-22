package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class ProfileMap extends SettingMap<Profile> {

	private static final long serialVersionUID = 7828170064507915249L;

	public ProfileMap() {
		super();
	}

	public void load(Element element) {
		super.load(element, element != null ? element.getElements("/profile") : null, e -> Profile.of(e));
	}

}
