package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;

public class EntityRef extends TerminalNode {

	public static final String START = "&";
	public static final String END = ";";

	private static final int START_LENGTH = START.length();
	private static final int START_END_LENGTH = START.length() + END.length();

	public static EntityRef of(String name, String translated) {
		String processed = START + name + END;
		byte[] sequence = processed.getBytes(StandardCharsets.UTF_8);
		return new EntityRef(sequence, translated);
	}

	public static EntityRef of(byte[] sequence) {
		return new EntityRef(sequence);
	}

	public static EntityRef of(String source) {
		byte[] sequence;
		if (source.startsWith(START)) {
			if (source.endsWith(END)) {
				sequence = source.getBytes(StandardCharsets.UTF_8);
			} else {
				String processed = source + END;
				sequence = processed.getBytes(StandardCharsets.UTF_8);
			}
		} else if (source.endsWith(END)) {
			String processed = START + source;
			sequence = processed.getBytes(StandardCharsets.UTF_8);
		} else {
			String processed = START + source + END;
			sequence = processed.getBytes(StandardCharsets.UTF_8);
		}
		return new EntityRef(sequence);
	}

	public final String name;
	public final String translated;

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
