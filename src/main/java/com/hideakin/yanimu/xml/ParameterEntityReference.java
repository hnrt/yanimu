package com.hideakin.yanimu.xml;

/**
 * An immutable node for the XML Parameter Entity Reference.
 */
public class ParameterEntityReference extends ImmutableNode {

	/**
	 * The start sequence of parameter entity reference. (String representation)
	 */
	public static final String START_STRING = "%";

	/**
	 * The end sequence of parameter entity reference. (String representation)
	 */
	public static final String END_STRING = ";";

	/**
	 * The start sequence of parameter entity reference. (byte representation)
	 */
	public static final byte[] START_BYTES = START_STRING.getBytes();

	/**
	 * The end sequence of parameter entity reference. (byte representation)
	 */
	public static final byte[] END_BYTES = END_STRING.getBytes();

	/**
	 * Creates a new node of type {@code PEREFERENCE} with a byte sequence.
	 * @param sequence a UTF-8 encoded byte representation of this node
	 * @return a newly created node
	 */
	public static ParameterEntityReference of(byte[] sequence) {
		return new ParameterEntityReference(sequence);
	}

	/**
	 * Creates a new node of type {@code PEREFERENCE} with a {@code String} sequence.
	 * @param sequence a {@code String} representation of this node
	 * @return a newly created node
	 */
	public static ParameterEntityReference of(String sequence) {
		return new ParameterEntityReference(sequence);
	}

	/**
	 * The name of the parameter entity reference.
	 */
	public final String name;

	private ParameterEntityReference(byte[] sequence) {
		super(PEREFERENCE, sequence, START_BYTES, END_BYTES);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
	}

	private ParameterEntityReference(String sequence) {
		super(PEREFERENCE, sequence, START_STRING, END_STRING);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
	}

}
