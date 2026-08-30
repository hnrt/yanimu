package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class EntityRef extends Node {

	public static final String START = "&";
	public static final String END = ";";

	private static final int START_LENGTH = START.length();
	private static final int START_END_LENGTH = START.length() + END.length();

	public static EntityRef of(String name, String translated) {
		return new EntityRef(name, translated);
	}

	public static EntityRef of(byte[] sequence) {
		return new EntityRef(sequence);
	}

	public static EntityRef of(String sequence) {
		return new EntityRef(sequence.getBytes(StandardCharsets.UTF_8));
	}

	public final String name;
	public final String translated;

	private EntityRef(String name, String translated) {
		super(ENTITY_REF, START + name + END);
		this.name = name;
		this.translated = translated;
	}

	private EntityRef(byte[] sequence) {
		super(ENTITY_REF, sequence);
		name = new String(sequence, START_LENGTH, sequence.length - START_END_LENGTH, StandardCharsets.UTF_8);
		translated = new String(sequence, StandardCharsets.UTF_8);
	}

	private EntityRef(byte[] sequence, String translated) {
		super(ENTITY_REF, sequence);
		name = new String(sequence, START_LENGTH, sequence.length - START_END_LENGTH, StandardCharsets.UTF_8);
		this.translated = translated;
	}

	public EntityRef with(String translated) {
		return new EntityRef(_sequence, translated);
	}

}
