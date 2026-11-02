package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class ProxyMap extends SettingMap<Proxy> {

	private static final long serialVersionUID = -6796925216120353555L;

	public ProxyMap() {
		super();
	}

	public ProxyMap(Element element) {
		super(element, element.getElements("/proxy"), e -> Proxy.of(e));
	}
	
}
