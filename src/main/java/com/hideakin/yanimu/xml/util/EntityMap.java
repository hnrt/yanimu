package com.hideakin.yanimu.xml.util;

import java.io.StringReader;
import java.util.HashMap;

import com.hideakin.yanimu.xml.doctype.ExternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.ExternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalParameterEntityDefinition;

import static com.hideakin.yanimu.xml.Character.*;

/**
 * A mutable hash map for XML Entity declarations.
 * <p>
 * The key of this map is a name of entity.
 * <p>
 * The value of this map is one of the following objects:
 * <ul>
 * <li>{@link InternalEntityDefinition}</li>
 * <li>{@link ExternalEntityDefinition}</li>
 * <li>{@link InternalParameterEntityDefinition}</li>
 * <li>{@link ExternalParameterEntityDefinition}</li>
 * </ul>
 * <p>
 * As for a parameter entity, the key of this map is a percent character followed by a name of entity.
 * This way prevents collisions of the keys between (regular) entities and parameter entities.  
 * The percent character is automatically prepended to the specified name in {@link getParameterEntity} and {@link put}
 * and so it is not necessary to add it manually.
 */
public class EntityMap extends HashMap<String, Object> {

	private static final long serialVersionUID = -3291494999011143497L;

	/**
	 * The initial capacity to be applied to super class constructor.
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 32;

	/**
	 * The maximum iterations for the replacement processing in {@link EntityMap#translate(String)}.
	 */
	public static final int MAX_TRANSLATION_ITERATIONS = 10;

	/**
	 * Constructs a new map object with the initial capacity set to {@code DEFAULT_INITIAL_CAPACITY}.
	 * <p>
	 * The predefined entity references are registered to this map by default.
	 */
	public EntityMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Constructs a new map object with the initial capacity set to the specified amount.
	 * <p>
	 * The predefined entity references are registered to this map by default.
	 * @param initialCapacity the initial capacity to be applied to the map
	 */
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

	/**
	 * Returns a defined text of the entity associated with the specified name.
	 * <p>
	 * This method currently works only for entities of {@link InternalEntityDefinition},
	 * not for those of {@link ExternalEntityDefinition};
	 * a value of {@code null} is returned if {@link ExternalEntityDefinition} is associated
	 * with the specified name.
	 * @param name an entity name to search for
	 * @return a defined text of the entity associated with the specified name, or {@code null} if not found
	 */
	public String getEntity(String name) {
		Object value = get(name);
		if (value instanceof InternalEntityDefinition ie) {
			return ie.value;
		} else if (value instanceof ExternalEntityDefinition) {
			return null; // NOT SUPPORTED
		} else {
			return null;
		}
	}

	/**
	 * Returns a defined text of the parameter entity associated with the specified name.
	 * <p>
	 * This method currently works only for entities of {@link InternalParameterEntityDefinition},
	 * not for those of {@link ExternalParameterEntityDefinition};
	 * a value of {@code null} is returned if {@link ExternalParameterEntityDefinition} is associated
	 * with the specified name.
	 * @param name a parameter entity name to search for
	 * @return a defined text of the parameter entity associated with the specified name, or {@code null} if not found
	 */
	public String getParameterEntity(String name) {
		Object value = get(peKey(name));
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
		if (key == null) {
			throw new NullPointerException(getClass().getSimpleName() + "::put: key is null.");
		} else if (value == null) {
			throw new NullPointerException(getClass().getSimpleName() + "::put: value is null.");
		} else if ((value instanceof InternalEntityDefinition) || (value instanceof ExternalEntityDefinition)) {
			return super.put(key, value);
		} else if ((value instanceof InternalParameterEntityDefinition) || (value instanceof ExternalParameterEntityDefinition)) {
			return super.put(peKey(key), value);
		} else {
			throw new RuntimeException(getClass().getSimpleName() + "::put: The specified value (%s) is not supported.".formatted(value.getClass().getName()));
		}
	}

	/**
	 * Replaces entity references and character references in the given String
	 * with their corresponding replacement text by using this map.
	 * <p>
	 * The replacement process is repeated while the resulting String still contains any entity or character reference,
	 * up to a maximum of ten iterations.
	 * This prevents infinite expansion in cases where references expand into new references.
	 * @param source the input String containing entity or character references
	 * @return the fully expanded String, or a partially expanded String if the iteration limit is reached
	 */
	public String translate(String source) {
		if (source == null) {
			return null;
		}
		String last = source;
		StringBuilder output = new StringBuilder();
		for (int attempts = 0; attempts < MAX_TRANSLATION_ITERATIONS; attempts++) {
			if (translate(last, output)) {
				last = output.toString();
			} else {
				break;
			}
			output.setLength(0);
		}
		return last;
	}

	/**
	 * Replaces entity references and character references in the specified {@code String}
	 * with their corresponding replacement text by using this map
	 * and writes the expanded text to the specified {@code StringBuilder}.
	 * @param source the input {@code String} containing entity or character references
	 * @param output the output {@code StringBuilder} for the expanded text to be written to
	 * @return true if the replacement was took place one or more times, false otherwise
	 */
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

	/**
	 * Returns the key for a parameter entity.
	 * @param name a parameter entity name to search for
	 * @return the key to search this map for a parameter entity with the specified name 
	 */
	public static String peKey(String name) {
		return "%" + name;
	}

}
