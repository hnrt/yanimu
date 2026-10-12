package com.hideakin.yanimu.xml.util;

import com.hideakin.yanimu.xml.AttributeTag;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.EndTag;
import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.NodeList;

import static com.hideakin.yanimu.xml.Node.S;

import java.util.Arrays;

import static com.hideakin.yanimu.xml.Node.CHAR_DATA;
import static com.hideakin.yanimu.xml.Node.ELEMENT;

public class FormatHelper {

	public static final int LF = 10;
	public static final int CR = 13;
	public static final int SP = 32;
	public static final String LF_STRING = "\n";
	public static final String CRLF_STRING = "\r\n";
	public static final byte[] LF_SEQUENCE = { 10 };
	public static final byte[] CRLF_SEQUENCE = { 13, 10 };

	public static final int INDENTATION_DEFAULT = 2;

	public static byte[] lineSeparator(NodeList nonTerminalNode) {
		LineSeparatorCounter counter = new LineSeparatorCounter();
		countLineSeparators(nonTerminalNode, counter);
		return counter.isLF() ? LF_SEQUENCE : CRLF_SEQUENCE;
	}

	public static void countLineSeparators(NodeList nonTerminalNode, LineSeparatorCounter counter) {
		for (Node node : nonTerminalNode.copy()) {
			if (node.type == S || node.type == CHAR_DATA) {
				byte[] s = node.sequence();
				if (s.length > 0) {
					int n = s.length - 1;
					int i = 0;
					while (i < n) {
						if (s[i] == LF) {
							counter.incrementLF();
							i++;
						} else if (s[i + 0] == CR && s[i + 1] == LF) {
							counter.incrementCRLF();
							i += 2;
						} else {
							i++;
						}
					}
					if (i == n && s[i] == LF) {
						counter.incrementLF();
					}
				}
			} else if (node instanceof NodeList child) {
				countLineSeparators(child, counter);
			}
		}
	}

	private static int _indentationUnitWidth = INDENTATION_DEFAULT;

	public static int defaultIndentationUnitWidth() {
		return _indentationUnitWidth;
	}

	public static void setDefaultIndentationUnitWidth(int width) {
		_indentationUnitWidth = width;
	}

	public static void indent(NodeList nonTerminalNode) {
		byte[] lineSeparator = lineSeparator(nonTerminalNode);
		indent(nonTerminalNode, lineSeparator, _indentationUnitWidth, 0);
	}

	public static void indent(NodeList nonTerminalNode, int unitWidth) {
		byte[] lineSeparator = lineSeparator(nonTerminalNode);
		indent(nonTerminalNode, lineSeparator, unitWidth, 0);
	}

	public static void indent(NodeList nonTerminalNode, byte[] lineSeparator, int unitWidth, int level) {
		if (nonTerminalNode.type == ELEMENT) {
			Element element = (Element)nonTerminalNode;
			AttributeTag st = element.startTag();
			for (int i = 0; i < st.size(); i++) {
				Node node = st.get(i);
				if (node.type == S) {
					st.set(i, Node.of(S, normalizeLineSeparators(node.sequence(), lineSeparator)));
				}
			}
			if (element.empty()) {
				return;
			}
			EndTag et = element.endTag();
			for (int i = 0; i < et.size(); i++) {
				Node node = et.get(i);
				if (node.type == S) {
					et.set(i, Node.of(S, normalizeLineSeparators(node.sequence(), lineSeparator)));
				}
			}
			Node previous = element.startTag();
			for (int i = 0; i < nonTerminalNode.size(); i++) {
				Node node = nonTerminalNode.get(i);
				if (node.isOneOf(ELEMENT, Node.PI, Node.COMMENT)) {
					if (previous.isOneOf(ELEMENT, Node.PI, Node.COMMENT, Node.STAG)) {
						nonTerminalNode.add(i, Node.of(CHAR_DATA, lineSeparatorAndIndentation(lineSeparator, unitWidth, level)));
						i++;
					} else if (isLineSeparatorAndIndentation(previous)) {
						nonTerminalNode.set(i - 1, Node.of(CHAR_DATA, lineSeparatorAndIndentation(lineSeparator, unitWidth, level)));
					}
					if (node.type == ELEMENT) {
						indent((NodeList)node, lineSeparator, unitWidth, level + 1);
					}
				} else if (node.type == CHAR_DATA) {
					nonTerminalNode.set(i, Node.of(CHAR_DATA, normalizeLineSeparators(node.sequence(), lineSeparator)));
					if (isLineSeparatorAndIndentation(previous)) {
						nonTerminalNode.remove(i - 1);
						i--;
					}
				}
				previous = node;
			}
			if (previous.isOneOf(ELEMENT, Node.PI, Node.COMMENT, Node.STAG)) {
				nonTerminalNode.add(Node.of(CHAR_DATA, lineSeparatorAndIndentation(lineSeparator, unitWidth, level - 1)));
			} else if (isLineSeparatorAndIndentation(previous)) {
				nonTerminalNode.set(nonTerminalNode.lastIndex(), Node.of(CHAR_DATA, lineSeparatorAndIndentation(lineSeparator, unitWidth, level - 1)));
			}
		} else if (nonTerminalNode.type == Node.DOCUMENT) {
			Node previous = Node.NULL_NODE;
			for (int i = 0; i < nonTerminalNode.size(); i++) {
				Node node = nonTerminalNode.get(i);
				if (node.isOneOf(ELEMENT, Node.DOCTYPE_DECL, Node.PI, Node.COMMENT)) {
					if (previous.isOneOf(Node.XML_DECL, ELEMENT, Node.DOCTYPE_DECL, Node.PI, Node.COMMENT)) {
						nonTerminalNode.add(i, Node.of(S, lineSeparator));
						i++;
					} else if (isLineSeparatorAndIndentation(previous)) {
						nonTerminalNode.set(i - 1, Node.of(S, lineSeparator));
					}
					if (node.type == ELEMENT || node.type == Node.DOCTYPE_DECL) {
						indent((NodeList)node, lineSeparator, unitWidth, level + 1);
					}
				} else if (node.type == S) {
					nonTerminalNode.set(i, Node.of(S, normalizeLineSeparators(node.sequence(), lineSeparator)));
				}
				previous = node;
			}
		} else if (nonTerminalNode.type == Node.DOCTYPE_DECL) {
			for (int i = 0; i < nonTerminalNode.size(); i++) {
				Node node = nonTerminalNode.get(i);
				if (node.type == Node.MARKUP_DECL_START) {
					Node previous = node;
					for (i++; i < nonTerminalNode.size(); i++) {
						node = nonTerminalNode.get(i);
						if (node.isOneOf(Node.ELEMENT_DECL, Node.ATTLIST_DECL, Node.ENTITY_DECL, Node.NOTATION_DECL, Node.PI, Node.COMMENT)) {
							if (previous.isOneOf(Node.ELEMENT_DECL, Node.ATTLIST_DECL, Node.ENTITY_DECL, Node.NOTATION_DECL, Node.PI, Node.COMMENT)) {
								nonTerminalNode.add(i, Node.of(S, lineSeparatorAndIndentation(lineSeparator, unitWidth, level)));
								i++;
							} else if (isLineSeparatorAndIndentation(previous)) {
								nonTerminalNode.set(i - 1, Node.of(S, lineSeparatorAndIndentation(lineSeparator, unitWidth, level)));
							}
						} else if (node.type == Node.MARKUP_DECL_END) {
							if (previous.isOneOf(Node.ELEMENT_DECL, Node.ATTLIST_DECL, Node.ENTITY_DECL, Node.NOTATION_DECL, Node.PI, Node.COMMENT)) {
								nonTerminalNode.add(Node.of(S, lineSeparatorAndIndentation(lineSeparator, unitWidth, level - 1)));
							} else if (isLineSeparatorAndIndentation(previous)) {
								nonTerminalNode.set(nonTerminalNode.lastIndex(), Node.of(S, lineSeparatorAndIndentation(lineSeparator, unitWidth, level - 1)));
							}
							break;
						} else if (node.type == S) {
							nonTerminalNode.set(i, Node.of(S, normalizeLineSeparators(node.sequence(), lineSeparator)));
						}
						previous = node;
					}
					break;
				}
			}
		}
	}

	/**
	 * Checks if the text of this node satisfies all of the following conditions:<br/>
	 * <ul>
	 * <li>The node type is either CHAR_DATA or S (white spaces)</li>
	 * <li>The text begins with either LF (\n) or CRLF (\r\n).</li>
	 * <li>The remainder of the text consists only of space characters.</li>
	 * </ul>
	 * @return true if the text satisfies all conditions; false otherwise
	 */
	public static boolean isLineSeparatorAndIndentation(Node node) {
		if (node.type == CHAR_DATA || node.type == S) {
			byte[] bb = node.sequence();
			int i;
			if (bb.length > 0 && bb[0] == LF) {
				i = 1;
			} else if (bb.length > 1 && bb[0] == CR && bb[1] == LF) {
				i = 2;
			} else {
				return false;
			}
			while (i < bb.length) {
				if (bb[i] == SP) {
					i++;
				} else {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates a new byte sequence of a line separator followed by space characters.<br/>
	 * The number of space characters is determined by multiplying the indentation unit length by the nesting level.
	 * @param lineSeparator the byte representation of the line separator (LF (\n) or CRLF (\r\n))
	 * @param unitWidth the indentation unit length in bytes
	 * @param level the nesting level of the node
	 * @return a newly created byte sequence
	 */
	public static byte[] lineSeparatorAndIndentation(byte[] lineSeparator, int unitWidth, int level) {
		int n1 = lineSeparator.length;
		int n2 = unitWidth * level;
		int n = n1 + n2;
		byte[] sequence = Arrays.copyOf(lineSeparator, n);
		Arrays.fill(sequence, n1, n, (byte)SP);
		return sequence;
	}

	/**
	 * Replaces all occurrences of line separator with the specified one.
	 * @param source an input
	 * @param lineSeparator the line separator to use
	 * @return the resulting byte sequence
	 */
	public static byte[] normalizeLineSeparators(byte[] source, byte[] lineSeparator) {
		if (lineSeparator[0] == '\n') {
			int d = 0;
			for (int i = 0; i < source.length; i++) {
				if (source[i] == '\n') {
					continue;
				} else if (source[i] == '\r') {
					i++;
					if (i < source.length && source[i] == '\n') {
						d--;
					} else {
						i--;
					}
				} else {
					continue;
				}
			}
			if (d == 0) {
				return source;
			}
			byte[] destination = new byte[source.length + d];
			int j = 0;
			for (int i = 0; i < source.length; i++) {
				if (source[i] == '\n') {
					destination[j++] = '\n';
				} else if (source[i] == '\r') {
					i++;
					if (i < source.length && source[i] == '\n') {
						destination[j++] = '\n';
					} else {
						destination[j++] = '\r';
						i--;
					}
				} else {
					destination[j++] = source[i];
				}
			}
			return destination;
		} else {
			int d = 0;
			for (int i = 0; i < source.length; i++) {
				if (source[i] == '\n') {
					d++;
				} else if (source[i] == '\r') {
					i++;
					if (i < source.length && source[i] == '\n') {
						continue;
					} else {
						i--;
					}
				} else {
					continue;
				}
			}
			if (d == 0) {
				return source;
			}
			byte[] destination = new byte[source.length + d];
			int j = 0;
			for (int i = 0; i < source.length; i++) {
				if (source[i] == '\n') {
					destination[j++] = '\r';
					destination[j++] = '\n';
				} else if (source[i] == '\r') {
					i++;
					if (i < source.length && source[i] == '\n') {
						destination[j++] = '\r';
						destination[j++] = '\n';
					} else {
						destination[j++] = '\r';
						i--;
					}
				} else {
					destination[j++] = source[i];
				}
			}
			return destination;
		}
	}

	/**
	 * Converts the specified text into a value of boolean.
	 * @param text to be converted
	 * @param fallback a return value on error
	 * @return a value of boolean converted from the text
	 */
	public static boolean toBoolean(String text, boolean fallback) {
		if (text == null) {
			return fallback;
		} else if ("true".equals(text)) {
			return true;
		} else if ("false".equals(text)) {
			return false;
		} else {
			return fallback;
		}
	}

}
