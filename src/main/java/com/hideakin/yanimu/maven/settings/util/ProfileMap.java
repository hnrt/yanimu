package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class ProfileMap extends SettingMap<Profile> {

	private static final long serialVersionUID = 7828170064507915249L;

	public ProfileMap() {
		super();
	}

	public ProfileMap(Element element) {
		super(element, element.getElements("/profile"), e -> Profile.of(e));
	}

}
