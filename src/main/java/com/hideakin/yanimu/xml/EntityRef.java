package com.hideakin.yanimu.xml;

public class EntityRef extends Node {

	public final String name;
	public final String translated;

	public EntityRef(int offset, byte[] sequence) {
		super(ENTITY_REF, offset, sequence);
		this.name = new String(sequence, 1, sequence.length - 2);
		this.translated = this.name;
	}

	public EntityRef(EntityRef base, String translated) {
		super(ENTITY_REF, base.start, base.sequence);
		this.name = base.name;
		this.translated = translated;
	}

}
