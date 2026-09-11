package com.hideakin.yanimu.xml.doctype;

import java.util.List;

import com.hideakin.yanimu.xml.Node;

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

	public String toDebuggingString() {
		if (value instanceof Integer intValue) {
			switch (intValue) {
			case Node.EMPTY:
				return "EMPTY";
			case Node.ANY:
				return "ANY";
			case Node.PCDATA:
				return "(#PCDATA)";
			default:
				return "unknown(%d)".formatted(intValue);
			}
		} else if (value instanceof String[] arrValue) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("(");
			buffer.append(arrValue[0]);
			for (int i = 1; i < arrValue.length; i++) {
				buffer.append("|");
				buffer.append(arrValue[i]);
			}
			buffer.append(")*");
			return buffer.toString();
		} else if (value instanceof ContentParticle particle) {
			return particle.toString();
		} else {
			return "(malformed ContentSpec)";
		}
	}

}
