package com.hideakin.yanimu.xml.doctype;

import java.util.ArrayList;

public class ContentChoice extends ArrayList<ContentParticle> {

	private static final long serialVersionUID = -1121125399750879282L;

	public ContentChoice(ContentParticle cp) {
		super();
		add(cp);
	}

}
