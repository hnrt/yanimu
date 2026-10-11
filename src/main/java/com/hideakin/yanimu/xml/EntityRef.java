package com.hideakin.yanimu.xml;

/**
 * An immutable node for the XML Entity Reference.
 */
public class EntityRef extends ImmutableNode {

	/**
	 * The start sequence of entity reference. (String representation)
	 */
	public static final String START_STRING = "&";

	/**
	 * The end sequence of entity reference. (String representation)
	 */
	public static final String END_STRING = ";";

	/**
	 * The start sequence of entity reference. (byte representation)
	 */
	public static final byte[] START_BYTES = START_STRING.getBytes();

	/**
	 * The end sequence of entity reference. (byte representation)
	 */
	public static final byte[] END_BYTES = END_STRING.getBytes();

	/**
	 * Creates a new node of type {@code ENTITY_REF} with a name and replacement text.
	 * @param name a name to assign to this node
	 * @param translated the replacement text for this node
	 * @return a newly created node
	 */
	public static EntityRef of(String name, String translated) {
		String sequence = START_STRING + name + END_STRING;
		return new EntityRef(sequence, translated);
	}

	/**
	 * Creates a new node of type {@code ENTITY_REF} with a byte sequence.
	 * @param sequence a UTF-8 encoded byte representation of an entity reference
	 * @return a newly created node
	 */
	public static EntityRef of(byte[] sequence) {
		return new EntityRef(sequence);
	}

	/**
	 * Creates a new node of type {@code ENTITY_REF} with a {@code String}.
	 * @param sequence a {@code String} representation of an entity reference
	 * @return a newly created node
	 */
	public static EntityRef of(String sequence) {
		return new EntityRef(sequence);
	}

	/**
	 * The name of the entity reference.
	 */
	public final String name;

	/**
	 * The replacement text of the entity reference.
	 */
	public final String translated;

	private EntityRef(byte[] sequence) {
		super(ENTITY_REF, sequence, START_BYTES, END_BYTES);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
		translated = super.toString();
	}

	private EntityRef(byte[] sequence, String translated) {
		super(ENTITY_REF, sequence, START_BYTES, END_BYTES);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
		this.translated = translated;
	}

	private EntityRef(String sequence) {
		super(ENTITY_REF, sequence, START_STRING, END_STRING);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
		translated = super.toString();
	}

	private EntityRef(String sequence, String translated) {
		super(ENTITY_REF, sequence, START_STRING, END_STRING);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
		this.translated = translated;
	}

	/**
	 * Creates a copy of this node with a new replacement text.
	 * @param translated a {@code String} representation of a new replacement text to create
	 * @return a newly created node
	 */
	public EntityRef with(String translated) {
		return new EntityRef(_sequence, translated);
	}

}
