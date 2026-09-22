package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class MirrorMap extends SettingMap<Mirror> {

	private static final long serialVersionUID = 8427035862781043466L;

	public MirrorMap() {
		super();
	}

	public void load(Element element) {
		super.load(element, element != null ? element.getElements("/mirror") : null, e -> Mirror.of(e));
	}

}
