package com.hideakin.yanimu.xml.doctype;

public class ContentParticle {

	public Object target; // String (Name), ContentChoice or ContentSequence
	public Integer option; // occurrence: one or more (+), zero or more (*), or zero or one times (?)

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
		StringBuilder buf = new StringBuilder();
		if (target instanceof String name) {
			buf.append(name);
		} else if (target instanceof ContentChoice choice) {
			buf.append('(');
			buf.append(choice.get(0).toString());
			for (int i = 1; i < choice.size(); i++) {
				buf.append('|');
				buf.append(choice.get(i).toString());
			}
			buf.append(')');
		} else if (target instanceof ContentSequence seq) {
			buf.append('(');
			buf.append(seq.get(0).toString());
			for (int i = 1; i < seq.size(); i++) {
				buf.append(',');
				buf.append(seq.get(i).toString());
			}
			buf.append(')');
		} else {
			throw new RuntimeException("ContentParticle: Bad target");
		}
		if (option != null) {
			buf.appendCodePoint(option);
		}
		return buf.toString();
	}

}
