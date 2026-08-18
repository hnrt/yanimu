package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class ParameterEntityReference extends Node {

	public final String name;

	public ParameterEntityReference(byte[] sequence) {
		super(PEREFERENCE, sequence);
		name = new String(sequence, 1, sequence.length - 2, StandardCharsets.UTF_8);
	}

}
