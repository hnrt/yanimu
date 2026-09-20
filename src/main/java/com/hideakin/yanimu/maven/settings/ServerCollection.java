package com.hideakin.yanimu.maven.settings;

import com.hideakin.yanimu.xml.Element;

public class ServerCollection extends SettingCollection<Server> {

	private static final long serialVersionUID = -3923016412116034875L;

	public ServerCollection() {
		super();
	}

	public void load(Element element) {
		super.load(element, "server", e -> Server.of(e));
	}

}
