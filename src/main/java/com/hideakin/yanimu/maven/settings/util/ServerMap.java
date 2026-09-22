package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class ServerMap extends SettingMap<Server> {

	private static final long serialVersionUID = -9113167449448863681L;

	public ServerMap() {
		super();
	}

	public void load(Element element) {
		super.load(element, element != null ? element.getElements("/server") : null, e -> Server.of(e));
	}

}
