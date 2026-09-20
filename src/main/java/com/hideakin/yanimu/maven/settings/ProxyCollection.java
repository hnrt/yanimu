package com.hideakin.yanimu.maven.settings;

import com.hideakin.yanimu.xml.Element;

public class ProxyCollection extends SettingCollection<Proxy> {

	private static final long serialVersionUID = -3087394652630087886L;

	public ProxyCollection() {
		super();
	}

	public void load(Element element) {
		super.load(element, "proxy", e -> Proxy.of(e));
	}
	
}
