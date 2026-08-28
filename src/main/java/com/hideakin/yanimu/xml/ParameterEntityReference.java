package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class ParameterEntityReference extends Node {

	public static final String START = "%";
	public static final String END = ";";

	private static final int START_LENGTH = START.length();
	private static final int START_END_LENGTH = START.length() + END.length();

	public static ParameterEntityReference of(byte[] sequence) {
		return new ParameterEntityReference(sequence);
	}

	public static ParameterEntityReference of(String sequence) {
		return new ParameterEntityReference(sequence.getBytes(StandardCharsets.UTF_8));
	}

	public final String name;

	private ParameterEntityReference(byte[] sequence) {
		super(PEREFERENCE, sequence);
		name = new String(sequence, START_LENGTH, sequence.length - START_END_LENGTH, StandardCharsets.UTF_8);
	}

}
