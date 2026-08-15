package com.hideakin.yanimu.xml;

public class ParameterEntityReference extends Node {

	public final String name;

	public ParameterEntityReference(int offset, byte[] sequence) {
		super(PEREFERENCE, offset, sequence);
		name = new String(sequence, 1, sequence.length - 2);
	}

}
