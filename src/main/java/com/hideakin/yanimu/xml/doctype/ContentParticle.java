package com.hideakin.yanimu.xml.doctype;

public class ContentParticle {

	public final Object target; // String (Name), ContentChoice or ContentSequence
	public final Integer option; // occurrence: one or more (+), zero or more (*), or zero or one times (?)

	public ContentParticle(Object target, int option) {
		this.target = target;
		this.option = Integer.valueOf(option);
	}

	public ContentParticle(Object target) {
		this.target = target;
		this.option = null;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		if (target instanceof String name) {
			buffer.append(name);
		} else if (target instanceof ContentChoice choice) {
			buffer.append('(');
			buffer.append(choice.get(0).toString());
			for (int i = 1; i < choice.size(); i++) {
				buffer.append('|');
				buffer.append(choice.get(i).toString());
			}
			buffer.append(')');
		} else if (target instanceof ContentSequence seq) {
			buffer.append('(');
			buffer.append(seq.get(0).toString());
			for (int i = 1; i < seq.size(); i++) {
				buffer.append(',');
				buffer.append(seq.get(i).toString());
			}
			buffer.append(')');
		} else {
			throw new RuntimeException("ContentParticle: Bad target");
		}
		if (option != null) {
			buffer.appendCodePoint(option);
		}
		return buffer.toString();
	}

}
