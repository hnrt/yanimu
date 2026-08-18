package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class EntityRef extends Node {

	public final String name;
	public final String translated;

	public EntityRef(byte[] sequence) {
		super(ENTITY_REF, sequence);
		this.name = new String(sequence, 1, sequence.length - 2, StandardCharsets.UTF_8);
		this.translated = this.name;
	}

	public EntityRef(EntityRef base, String translated) {
		super(ENTITY_REF, base._sequence);
		this.name = base.name;
		this.translated = translated;
	}

	@Override
	public void setSequence(byte[] sequence) {
		throw new RuntimeException("EntityRef::setSequence: Unable to change!");
	}

	@Override
	public void setSequence(String string) {
		throw new RuntimeException("EntityRef::setSequence: Unable to change!");
	}

}
