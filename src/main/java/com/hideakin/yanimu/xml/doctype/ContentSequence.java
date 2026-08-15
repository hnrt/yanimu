package com.hideakin.yanimu.xml.doctype;

import java.util.ArrayList;

public class ContentSequence extends ArrayList<ContentParticle> {

	private static final long serialVersionUID = -38371040028461624L;

	public ContentSequence(ContentParticle cp) {
		super();
		add(cp);
	}

}
