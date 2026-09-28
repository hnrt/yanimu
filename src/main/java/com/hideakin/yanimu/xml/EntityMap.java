package com.hideakin.yanimu.xml;

import java.io.StringReader;
import java.util.HashMap;

import com.hideakin.yanimu.xml.doctype.ExternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.ExternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalParameterEntityDefinition;

import static com.hideakin.yanimu.xml.Character.*;

public class EntityMap extends HashMap<String, Object> {

	private static final long serialVersionUID = -3291494999011143497L;

	public static final int DEFAULT_INITIAL_CAPACITY = 32;

	public static final int MAX_TRANSLATION_ITERATIONS = 10;

	public EntityMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	public EntityMap(int initialCapacity) {
		super(initialCapacity);
		installPredefinedEntities();
	}

	private void installPredefinedEntities() {
		put("lt", new InternalEntityDefinition("lt", translate("&#38;#60;")));
		put("gt", new InternalEntityDefinition("gt", translate("&#62;")));
		put("amp", new InternalEntityDefinition("amp", translate("&#38;#38;")));
		put("apos", new InternalEntityDefinition("apos", translate("&#39;")));
		put("quot", new InternalEntityDefinition("quot", translate("&#34;")));
	}

	public String getEntity(String key) {
		Object value = get(key);
		if (value instanceof InternalEntityDefinition ie) {
			return ie.value;
		} else if (value instanceof ExternalEntityDefinition) {
			return null; // NOT SUPPORTED
		} else {
			return null;
		}
	}

	public static String peKey(String key) {
		return "%" + key;
	}

	public String getParameterEntity(String key) {
		Object value = get(peKey(key));
		if (value instanceof InternalParameterEntityDefinition ipe) {
			return ipe.value;
		} else if (value instanceof ExternalParameterEntityDefinition) {
			return null; // NOT SUPPORTED
		} else {
			return null;
		}
	}

	@Override
	public Object put(String key, Object value) {
		if ((value instanceof InternalParameterEntityDefinition) || (value instanceof ExternalParameterEntityDefinition)) {
			return super.put(peKey(key), value);
		} else {
			return super.put(key, value);
		}
	}

	public String translate(String source) {
		if (source == null) {
			return null;
		}
		String intermediate = source;
		StringBuilder output = new StringBuilder();
		for (int attempts = 0; attempts < MAX_TRANSLATION_ITERATIONS; attempts++) {
			if (translate(intermediate, output)) {
				intermediate = output.toString();
			} else {
				break;
			}
			output.setLength(0);
		}
		return intermediate;
	}

	public boolean translate(String source, StringBuilder output) {
		try (StringReader input = new StringReader(source)) {
			int changes = 0;
			int c = input.read();
			while (c >= 0) {
				if (c == '&') {
					int length = output.length();
					output.append((char)c);
					c = input.read();
					if (c == '#') {
						output.append((char)c);
						c = input.read();
						int d = 0;
						if (c == 'x') {
							output.append((char)c);
							c = input.read();
							if (isHexadecimal(c)) {
								do {
									d = d * 16 + (c < 'A' ? c - '0' : c < 'a' ? c - 'A' + 10 : c - 'a' + 10);
									output.append((char)c);
									c = input.read();
								} while (isHexadecimal(c));
								if (c == ';') {
									output.setLength(length);
									output.appendCodePoint(d);
									changes++;
									c = input.read();
								}
							}
						} else if (isDigit(c)) {
							do {
								d = d * 10 + c - '0';
								output.append((char)c);
								c = input.read();
							} while (isDigit(c));
							if (c == ';') {
								output.setLength(length);
								output.appendCodePoint(d);
								changes++;
								c = input.read();
							}
						}
					} else if (isNameStartChar(c)) {
						int start = output.length();
						output.append((char)c);
						c = input.read();
						while (isNameChar(c)) {
							output.append((char)c);
							c = input.read();
						}
						if (c == ';') {
							String key = output.substring(start);
							String value = getEntity(key);
							if (value != null) {
								output.setLength(length);
								output.append(value);
								changes++;
							} else {
								output.append((char)c);
							}
							c = input.read();
						}
					}
				} else {
					output.append((char)c);
					c = input.read();
				}
			}
			return changes > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
