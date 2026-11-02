package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class MirrorMap extends SettingMap<Mirror> {

	private static final long serialVersionUID = 8427035862781043466L;

	public MirrorMap() {
		super();
	}

	public MirrorMap(Element element) {
		super(element, element.getElements("/mirror"), e -> Mirror.of(e));
	}

}
