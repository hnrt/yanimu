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

	private static final byte[] DOUBLE_QUOTE = {'&','q','u','o','t',';'};
	private static final byte[] SINGLE_QUOTE = {'&','a','p','o','s',';'};
	private static final String DOUBLE_QUOTE_STRING = "&quot;";
	private static final String SINGLE_QUOTE_STRING = "&apos;";

	/**
	 * Creates a new node with a node type and a byte sequence stored as text enclosed in quotes.
	 * @param type a node type to assign to this node
	 * @param sequence a byte sequence stored as text enclosed in quotes
	 * @return a newly created node
	 */
	public static QuotedString of(int type, byte[] sequence) {
		return new QuotedString(type, encloseTextInQuotes(sequence));
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
		} else if (source[0] == source[source.length - 1] && (source[0] == '\"' || source[0] == '\'')) {
			return source;
		} else {
			int s = 0;
			int d = 0;
			for (int i = 0; i < source.length; i++) {
				if (source[i] == '\'') {
					s++;
				} else if (source[i] == '\"') {
					d++;
				}
			}
			if (d == 0 || s == 0) {
				byte q = (byte)(d == 0 ? '\"' : '\'');
				byte[] out = new byte[1 + source.length + 1];
				out[0] = q;
				System.arraycopy(source, 0, out, 1, source.length);
				out[1 + source.length] = q;
				return out;
			} else if (s > d) {
				byte[] out = new byte[1 + source.length + (DOUBLE_QUOTE.length - 1) * d + 1];
				out[0] = '\"';
				int h = 0;
				int j = 0;
				for (int i = 0; i < source.length; i++) {
					if (source[i] == '\"') {
						int n = i - h;
						System.arraycopy(source, h, out, j, n);
						h = i + 1;
						j += n;
						System.arraycopy(DOUBLE_QUOTE, s, out, j, DOUBLE_QUOTE.length);
						j += DOUBLE_QUOTE.length;
					}
				}
				if (h < source.length) {
					System.arraycopy(source, h, out, j, source.length - h);
				}
				out[1 + source.length] = '\"';
				return out;
			} else {
				byte[] out = new byte[1 + source.length + (SINGLE_QUOTE.length - 1) * s + 1];
				out[0] = '\'';
				int h = 0;
				int j = 0;
				for (int i = 0; i < source.length; i++) {
					if (source[i] == '\'') {
						int n = i - h;
						System.arraycopy(source, h, out, j, n);
						h = i + 1;
						j += n;
						System.arraycopy(SINGLE_QUOTE, s, out, j, SINGLE_QUOTE.length);
						j += SINGLE_QUOTE.length;
					}
				}
				if (h < source.length) {
					System.arraycopy(source, h, out, j, source.length - h);
				}
				out[1 + source.length] = '\'';
				return out;
			}
		}
	}

	private static String encloseTextInQuotes(String source) {
		if (source.length() == 0) {
			return "\"\"";
		} else if (source.length() == 1) {
			if (source.charAt(0) == '\"') {
				return "\'\"\'";
			} else {
				return "\"" + source + "\"";
			}
		} else if (source.charAt(0) == source.charAt(source.length() - 1) && (source.charAt(0) == '\"' || source.charAt(0) == '\'')) {
			return source;
		} else {
			int s = 0;
			int d = 0;
			for (int i = 0; i < source.length(); i++) {
				if (source.charAt(i) == '\'') {
					s++;
				} else if (source.charAt(i) == '\"') {
					d++;
				}
			}
			if (d == 0 || s == 0) {
				String q = d == 0 ? "\"" : "\'";
				return q + source + q;
			} else if (s > d) {
				StringBuilder out = new StringBuilder();
				out.append('\"');
				int h = 0;
				for (int i = source.indexOf('\"', h); i != -1; i = source.indexOf('\"', h)) {
					if (h < i) {
						out.append(source.substring(h, i));
					}
					h = i + 1;
					out.append(DOUBLE_QUOTE_STRING);
				}
				if (h < source.length()) {
					out.append(source.substring(h));
				}
				out.append('\"');
				return out.toString();
			} else {
				StringBuilder out = new StringBuilder();
				out.append('\'');
				int h = 0;
				for (int i = source.indexOf('\'', h); i != -1; i = source.indexOf('\'', h)) {
					if (h < i) {
						out.append(source.substring(h, i));
					}
					h = i + 1;
					out.append(SINGLE_QUOTE_STRING);
				}
				if (h < source.length()) {
					out.append(source.substring(h));
				}
				out.append('\'');
				return out.toString();
			}
		}
	}

}
