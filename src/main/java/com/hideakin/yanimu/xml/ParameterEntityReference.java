package com.hideakin.yanimu.xml;

import java.util.Arrays;

public class ParameterEntityReference extends Node {

	public final String name;

	public ParameterEntityReference(int start, int end, byte[] sequence) {
		super(PEREFERENCE, start, end, sequence);
		name = new String(Arrays.copyOfRange(sequence, 1, sequence.length - 1));
	}

}
