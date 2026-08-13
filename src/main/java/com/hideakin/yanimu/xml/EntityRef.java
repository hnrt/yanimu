package com.hideakin.yanimu.xml;

import java.util.Arrays;

public class EntityRef extends Token {

	public final String name;
	public final String translated;

	public EntityRef(int start, int end, byte[] sequence) {
		super(ENTITY_REF, start, end, sequence);
		this.name = new String(Arrays.copyOfRange(sequence, 1, sequence.length - 1));
		this.translated = null;
	}

	public EntityRef(EntityRef base, String translated) {
		super(ENTITY_REF, base.start, base.end, base.sequence);
		this.name = base.name;
		this.translated = translated;
	}

}
