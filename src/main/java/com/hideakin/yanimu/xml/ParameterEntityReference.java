package com.hideakin.yanimu.xml;

public class ParameterEntityReference extends ImmutableNode {

	public static final String START = "%";
	public static final String END = ";";

	public static final byte[] START_BYTES = START.getBytes();
	public static final byte[] END_BYTES = END.getBytes();

	public static ParameterEntityReference of(byte[] sequence) {
		return new ParameterEntityReference(sequence);
	}

	public static ParameterEntityReference of(String sequence) {
		return new ParameterEntityReference(sequence);
	}

	public final String name;

	private ParameterEntityReference(byte[] sequence) {
		super(PEREFERENCE, sequence, START_BYTES, END_BYTES);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
	}

	private ParameterEntityReference(String sequence) {
		super(PEREFERENCE, sequence, START, END);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
	}

}
