package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class EntityRef extends Node {

	public final String name;
	public final String translated;

	public EntityRef(byte[] sequence) {
		super(ENTITY_REF, sequence);
		name = new String(sequence, 1, sequence.length - 2, StandardCharsets.UTF_8);
		translated = this.name;
	}

	public EntityRef(EntityRef base, String translated) {
		super(ENTITY_REF, base._sequence);
		name = base.name;
		this.translated = translated;
	}

}
