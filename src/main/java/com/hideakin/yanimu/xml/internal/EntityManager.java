package com.hideakin.yanimu.xml.internal;

import java.util.HashMap;
import java.util.Map;

import com.hideakin.yanimu.xml.doctype.ExternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.ExternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalParameterEntityDefinition;

public class EntityManager {

	private final Map<String, Object> _entities = new HashMap<>();
	private final TextMessageManager _tm;

	public EntityManager(TextMessageManager tm) {
		_tm = tm;
		installPredefinedEntities();
	}

	private void installPredefinedEntities() {
		putEntity("lt", new InternalEntityDefinition("lt", translate("&#38;#60;")));
		putEntity("gt", new InternalEntityDefinition("gt", translate("&#62;")));
		putEntity("amp", new InternalEntityDefinition("amp", translate("&#38;#38;")));
		putEntity("apos", new InternalEntityDefinition("apos", translate("&#39;")));
		putEntity("quot", new InternalEntityDefinition("quot", translate("&#34;")));
	}

	public String getEntity(String key) {
		Object value = _entities.get(key);
		if (value instanceof InternalEntityDefinition ie) {
			return ie.value;
		} else if (value instanceof ExternalEntityDefinition) {
			_tm.addWarning("External entity reference %s cannot be used as it is not supported.", key);
			return null;
		} else {
			return null;
		}
	}

	public String getParameterEntity(String key) {
		Object value = _entities.get("%" + key);
		if (value instanceof InternalParameterEntityDefinition ipe) {
			return ipe.value;
		} else if (value instanceof ExternalParameterEntityDefinition) {
			_tm.addWarning("External parameter entity reference %s cannot be used as it is not supported.", key);
			return null;
		} else {
			return null;
		}
	}

	public void putEntity(String key, Object value) {
		if ((value instanceof InternalParameterEntityDefinition) || (value instanceof ExternalParameterEntityDefinition)) {
			String peKey = "%" + key;
			if (_entities.containsKey(peKey)) {
				_tm.addWarning("Parameter entity reference %s is declared multiple times.", key);
			} else {
				_entities.put(peKey, value);
			}
		} else if (_entities.containsKey(key)) {
			_tm.addWarning("Entity reference %s is declared multiple times.", key);
		} else {
			_entities.put(key, value);
		}
	}

	public String translate(String text) {
		if (text == null) {
			return null;
		}
		return translate(text, 1);
	}

	private String translate(String text, int depth) {
		int replaced = 0;
		StringBuilder buf = new StringBuilder();
		int n = text.length();
		int i = 0;
		int c = i < n ? text.codePointAt(i++) : -1;
		while (c >= 0) {
			if (c == '&') {
				int h = i;
				c = i < n ? text.codePointAt(i++) : -1;
				if (c == '#') {
					boolean successful = false;
					int d = 0;
					c = i < n ? text.codePointAt(i++) : -1;
					if (c == 'x') {
						c = i < n ? text.codePointAt(i++) : -1;
						if (Lexer.isHexadecimal(c)) {
							do {
								d = d * 16 + (c < 'A' ? c - '0' : c < 'a' ? c - 'A' + 10 : c - 'a' + 10);
								c = i < n ? text.codePointAt(i++) : -1;
							} while (Lexer.isHexadecimal(c));
							if (c == ';') {
								successful = true;
							}
						}
					} else if (Lexer.isDigit(c)) {
						do {
							d = d * 10 + c - '0';
							c = i < n ? text.codePointAt(i++) : -1;
						} while (Lexer.isDigit(c));
						if (c == ';') {
							successful = true;
						}
					}
					if (successful) {
						buf.appendCodePoint(d);
						replaced++;
					} else {
						buf.append('&');
						i = h;
					}
				} else if (Lexer.isNameStartChar(c)) {
					StringBuilder buf2 = new StringBuilder();
					buf2.appendCodePoint(c);
					c = i < n ? text.codePointAt(i++) : -1;
					while (Lexer.isNameChar(c)) {
						buf2.appendCodePoint(c);
						c = i < n ? text.codePointAt(i++) : -1;
					}
					if (c == ';') {
						String key = buf2.toString();
						String value = getEntity(key);
						if (value != null) {
							buf.append(value);
							replaced++;
						} else {
							buf.append('&');
							buf.append(key);
							buf.append(';');
						}
					} else {
						buf.append('&');
						i = h;
					}
				} else {
					buf.append('&');
					i = h;
				}
			} else {
				buf.appendCodePoint(c);
			}
			c = i < n ? text.codePointAt(i++) : -1;
		}
		if (replaced == 0) {
			return text;
		} else {
			text = buf.toString();
			if (depth < 10) {
				return translate(text, depth + 1);
			} else {
				_tm.addWarning("EntityManager::translate: Expression too complex: %s", text);
				return text;
			}
		}
	}

}
