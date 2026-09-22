package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class ProxyMap extends SettingMap<Proxy> {

	private static final long serialVersionUID = -6796925216120353555L;

	public ProxyMap() {
		super();
	}

	public void load(Element element) {
		super.load(element, element != null ? element.getElements("/proxy") : null, e -> Proxy.of(e));
	}
	
}
