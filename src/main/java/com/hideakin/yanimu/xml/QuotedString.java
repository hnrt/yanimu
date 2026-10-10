package com.hideakin.yanimu.xml;

/**
 * An immutable node for text enclosed in quotes.
 * <p>
 * The supported node types are:
 * <ul>
 * <li>ENTITY_VALUE</li>
 * <li>ATT_VALUE</li>
 * <li>SYSTEM_LITERAL</li>
 * <li>PUBID_LITERAL</li>
 * </ul>
 * <p>
 * If the specified text starts and ends with the same quote character
 * that is either a double quote (U+0022) or a single quote (U+0027),
 * it is stored as is. Otherwise, it is enclosed in quotes.
 * <p>
 * In the latter case,
 * the quote character chosen is the one that appears fewer times in the specified text,
 * and all occurrences of the quote character are replaced with the corresponding entity reference.
 */
public class QuotedString extends ImmutableNode {

	private static final byte[] DOUBLE_QUOTE_ENTITY_REF = {'&','q','u','o','t',';'};
	private static final byte[] SINGLE_QUOTE_ENTITY_REF = {'&','a','p','o','s',';'};
	private static final String DOUBLE_QUOTE_ENTITY_REF_STRING = "&quot;";
	private static final String SINGLE_QUOTE_ENTITY_REF_STRING = "&apos;";

	/**
	 * Creates a new node with a node type and a byte sequence stored as text enclosed in quotes.
	 * @param type a node type to assign to this node
	 * @param sequence a byte sequence stored as text enclosed in quotes
	 * @return a newly created node
	 */
	public static QuotedString of(int type, byte[] sequence) {
		return new QuotedString(type, sequence);
	}

	/**
	 * Creates a new node with a node type and a string stored as text enclosed in quotes.
	 * @param type a node type to assign to this node
	 * @param sequence a string stored as text enclosed in quotes
	 * @return a newly created node
	 */
	public static QuotedString of(int type, String sequence) {
		return new QuotedString(type, sequence);
	}

	/**
	 * Initializes a newly created node with a node type and a byte sequence stored as text enclosed in quotes.
	 * @param type a node type to assign to this node
	 * @param sequence a byte sequence stored as text enclosed in quotes
	 */
	protected QuotedString(int type, byte[] sequence) {
		super(type, encloseTextInQuotes(sequence));
	}

	/**
	 * Initializes a newly created node with a node type and a string stored as text enclosed in quotes.
	 * @param type a node type to assign to this node
	 * @param sequence a string stored as text enclosed in quotes
	 */
	protected QuotedString(int type, String sequence) {
		super(type, encloseTextInQuotes(sequence));
	}

	/**
	 * Returns an inner text.
	 * @return a string exclude the quotes that enclose the text in this node.
	 */
	public String innerText() {
		return super.innerText(1, 1);
	}

	private static byte[] encloseTextInQuotes(byte[] source) {
		if (source.length == 0) {
			byte[] out = new byte[2];
			out[0] = '\"';
			out[1] = '\"';
			return out;
		} else if (source.length == 1) {
			byte[] out = new byte[3];
			if (source[0] == '\"') {
				out[0] = '\'';
				out[1] = '\"';
				out[2] = '\'';
			} else {
				out[0] = '\"';
				out[1] = source[0];
				out[2] = '\"';
			}
			return out;
		} else {
			int start, end;
			if ((source[0] == '\"' || source[0] == '\'') && source[0] == source[source.length - 1]) {
				start = 1;
				end = source.length - 1;
			} else {
				start = 0;
				end = source.length;
			}
			int singleQuotes = 0;
			int doubleQuotes = 0;
			for (int i = start; i < end; i++) {
				if (source[i] == '\'') {
					singleQuotes++;
				} else if (source[i] == '\"') {
					doubleQuotes++;
				}
			}
			if (doubleQuotes == 0 && singleQuotes == 0) {
				if (start == 1) {
					return source;
				} else {
					byte[] out = new byte[1 + source.length + 1];
					out[0] = '\"';
					System.arraycopy(source, 0, out, 1, source.length);
					out[1 + source.length] = '\"';
					return out;
				}
			} else if (doubleQuotes == 0) {
				byte[] out = new byte[1 + (end - start) + 1];
				out[0] = '\"';
				System.arraycopy(source, start, out, 1, end - start);
				out[1 + end - start] = '\"';
				return out;
			} else if (singleQuotes == 0) {
				byte[] out = new byte[1 + (end - start) + 1];
				out[0] = '\'';
				System.arraycopy(source, start, out, 1, end - start);
				out[1 + end - start] = '\'';
				return out;
			} else if (singleQuotes >= doubleQuotes) {
				byte[] out = new byte[1 + (end - start) + (DOUBLE_QUOTE_ENTITY_REF.length - 1) * doubleQuotes + 1];
				out[0] = '\"';
				int j = 1;
				int h = start;
				for (int i = start; i < end; i++) {
					if (source[i] == '\"') {
						int n = i - h;
						System.arraycopy(source, h, out, j, n);
						j += n;
						System.arraycopy(DOUBLE_QUOTE_ENTITY_REF, 0, out, j, DOUBLE_QUOTE_ENTITY_REF.length);
						j += DOUBLE_QUOTE_ENTITY_REF.length;
						h = i + 1;
					}
				}
				if (h < end) {
					int n = end - h;
					System.arraycopy(source, h, out, j, n);
					j += n;
				}
				out[j] = '\"';
				return out;
			} else {
				byte[] out = new byte[1 + (end - start) + (SINGLE_QUOTE_ENTITY_REF.length - 1) * singleQuotes + 1];
				out[0] = '\'';
				int j = 1;
				int h = start;
				for (int i = start; i < end; i++) {
					if (source[i] == '\'') {
						int n = i - h;
						System.arraycopy(source, h, out, j, n);
						j += n;
						System.arraycopy(SINGLE_QUOTE_ENTITY_REF, 0, out, j, SINGLE_QUOTE_ENTITY_REF.length);
						j += SINGLE_QUOTE_ENTITY_REF.length;
						h = i + 1;
					}
				}
				if (h < end) {
					int n = end - h;
					System.arraycopy(source, h, out, j, n);
					j += n;
				}
				out[j] = '\'';
				return out;
			}
		}
	}

	private static String encloseTextInQuotes(String source) {
		int length = source.length();
		if (length == 0) {
			return "\"\"";
		} else if (length == 1) {
			if (source.charAt(0) == '\"') {
				return "\'\"\'";
			} else {
				return "\"" + source + "\"";
			}
		} else {
			int start, end;
			if ((source.charAt(0) == '\"' || source.charAt(0) == '\'') && source.charAt(0) == source.charAt(length - 1)) {
				start = 1;
				end = length - 1;
			} else {
				start = 0;
				end = length;
			}
			int singleQuotes = 0;
			int doubleQuotes = 0;
			for (int i = start; i < end; i++) {
				if (source.charAt(i) == '\'') {
					singleQuotes++;
				} else if (source.charAt(i) == '\"') {
					doubleQuotes++;
				}
			}
			if (doubleQuotes == 0 && singleQuotes == 0) {
				if (start == 1) {
					return source;
				} else {
					return "\"" + source.substring(start, end) + "\"";
				}
			} else if (doubleQuotes == 0) {
				return "\"" + source.substring(start, end) + "\"";
			} else if (singleQuotes == 0) {
				return "\'" + source.substring(start, end) + "\'";
			} else if (singleQuotes >= doubleQuotes) {
				StringBuilder out = new StringBuilder();
				out.append('\"');
				int h = start;
				int i = source.indexOf('\"', h);
				while (h <= i && i < end) {
					out.append(source.substring(h, i));
					out.append(DOUBLE_QUOTE_ENTITY_REF_STRING);
					h = i + 1;
					i = source.indexOf('\"', h);
				}
				if (h < end) {
					out.append(source.substring(h, end));
				}
				out.append('\"');
				return out.toString();
			} else {
				StringBuilder out = new StringBuilder();
				out.append('\'');
				int h = start;
				int i = source.indexOf('\'', h);
				while (h <= i && i < end) {
					out.append(source.substring(h, i));
					out.append(SINGLE_QUOTE_ENTITY_REF_STRING);
					h = i + 1;
					i = source.indexOf('\'', h);
				}
				if (h < end) {
					out.append(source.substring(h, end));
				}
				out.append('\'');
				return out.toString();
			}
		}
	}

}
