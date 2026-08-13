package com.hideakin.yanimu.xml;

import java.util.Arrays;

public class EntityRef extends Token {

	public final String name;

	public EntityRef(int start, int end, byte[] sequence) {
		super(ENTITY_REF, start, end, sequence);
		name = new String(Arrays.copyOfRange(sequence, 1, sequence.length - 1));
	}

}
