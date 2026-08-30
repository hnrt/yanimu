package com.hideakin.yanimu.xml;

import java.io.StringReader;
import java.util.HashMap;

import com.hideakin.yanimu.xml.doctype.ExternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.ExternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.internal.Lexer;

public class EntityMap extends HashMap<String, Object> {

	private static final long serialVersionUID = -3291494999011143497L;

	public static final int DEFAULT_INITIAL_CAPACITY = 32;

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
		String text = source;
		StringBuilder buffer = new StringBuilder();
		for (int retries = 10; retries > 0; retries--) {
			if (translate(text, buffer)) {
				text = buffer.toString();
				buffer.setLength(0);
			} else {
				break;
			}
		}
		return text;
	}

	private boolean translate(String text, StringBuilder buffer) {
		try (StringReader reader = new StringReader(text)) {
			int replaced = 0;
			int c = reader.read();
			while (c >= 0) {
				if (c == '&') {
					int length = buffer.length();
					buffer.append((char)c);
					c = reader.read();
					if (c == '#') {
						buffer.append((char)c);
						c = reader.read();
						boolean successful = false;
						int d = 0;
						if (c == 'x') {
							buffer.append((char)c);
							c = reader.read();
							if (Lexer.isHexadecimal(c)) {
								do {
									d = d * 16 + (c < 'A' ? c - '0' : c < 'a' ? c - 'A' + 10 : c - 'a' + 10);
									buffer.append((char)c);
									c = reader.read();
								} while (Lexer.isHexadecimal(c));
								if (c == ';') {
									buffer.append((char)c);
									c = reader.read();
									successful = true;
								}
							}
						} else if (Lexer.isDigit(c)) {
							do {
								d = d * 10 + c - '0';
								buffer.append((char)c);
								c = reader.read();
							} while (Lexer.isDigit(c));
							if (c == ';') {
								buffer.append((char)c);
								c = reader.read();
								successful = true;
							}
						}
						if (successful) {
							buffer.setLength(length);
							buffer.appendCodePoint(d);
							replaced++;
						}
					} else if (Lexer.isNameStartChar(c)) {
						int start = buffer.length();
						buffer.append((char)c);
						c = reader.read();
						while (Lexer.isNameChar(c)) {
							buffer.append((char)c);
							c = reader.read();
						}
						if (c == ';') {
							String key = buffer.substring(start);
							buffer.append((char)c);
							c = reader.read();
							String value = getEntity(key);
							if (value != null) {
								buffer.setLength(length);
								buffer.append(value);
								replaced++;
							}
						}
					}
				} else {
					buffer.append((char)c);
					c = reader.read();
				}
			}
			return replaced > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
