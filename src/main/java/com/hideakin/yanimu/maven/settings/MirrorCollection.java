package com.hideakin.yanimu.maven.settings;

import com.hideakin.yanimu.xml.Element;

public class MirrorCollection extends SettingCollection<Mirror> {

	private static final long serialVersionUID = 1729220519634863688L;

	public MirrorCollection() {
		super();
	}

	public void load(Element element) {
		super.load(element, "mirror", e -> Mirror.of(e));
	}

}
