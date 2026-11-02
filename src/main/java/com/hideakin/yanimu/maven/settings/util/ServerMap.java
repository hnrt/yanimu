package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class ServerMap extends SettingMap<Server> {

	private static final long serialVersionUID = -9113167449448863681L;

	public ServerMap() {
		super();
	}

	public ServerMap(Element element) {
		super(element, element.getElements("/server"), e -> Server.of(e));
	}

}
