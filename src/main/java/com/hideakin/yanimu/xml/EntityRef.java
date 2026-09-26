package com.hideakin.yanimu.xml;

public class EntityRef extends ImmutableNode {

	public static final String START = "&";
	public static final String END = ";";

	public static final byte[] START_BYTES = START.getBytes();
	public static final byte[] END_BYTES = END.getBytes();

	public static EntityRef of(String name, String translated) {
		String sequence = START + name + END;
		return new EntityRef(sequence, translated);
	}

	public static EntityRef of(byte[] sequence) {
		return new EntityRef(sequence);
	}

	public static EntityRef of(String sequence) {
		return new EntityRef(sequence);
	}

	public final String name;
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
		super(ENTITY_REF, sequence, START, END);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
		translated = super.toString();
	}

	private EntityRef(String sequence, String translated) {
		super(ENTITY_REF, sequence, START, END);
		name = super.innerText(START_BYTES.length, END_BYTES.length);
		this.translated = translated;
	}

	public EntityRef with(String translated) {
		return new EntityRef(_sequence, translated);
	}

}
