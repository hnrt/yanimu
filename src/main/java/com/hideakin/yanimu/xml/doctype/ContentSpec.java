package com.hideakin.yanimu.xml.doctype;

import java.util.List;

public class ContentSpec {

	public final Object value;

	public ContentSpec(int csType) {
		this.value = Integer.valueOf(csType); // EMPTY, ANY or PCDATA
	}

	public ContentSpec(List<String> choiceList) {
		this.value = choiceList.toArray(new String[choiceList.size()]);
	}

	public ContentSpec(ContentParticle particle) {
		this.value = particle;
	}

}
