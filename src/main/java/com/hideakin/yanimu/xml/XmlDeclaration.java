package com.hideakin.yanimu.xml;

import java.util.List;

import static com.hideakin.yanimu.xml.Character.*;

/**
 * The XML Declaration.
 */
public class XmlDeclaration extends NodeList {

	/**
	 * The default value for {@code version} of the XML declaration.
	 */
	public static final String DEFAULT_VERSION = "1.0";

	/**
	 * The default value for {@code encoding} of the XML declaration.
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * The YES value for {@code standalone} of the XML declaration.
	 */
	public static final String YES = "yes";

	/**
	 * The NO value for {@code standalone} of the XML declaration.
	 */
	public static final String NO = "no";

	/**
	 * The {@code version} value of this declaration.
	 */
	public final String version;

	/**
	 * The {@code encoding} value of this declaration.
	 */
	public final String encoding;

	/**
	 * The {@code standalone} value of this declaration.
	 */
	public final String standalone;

	/**
	 * Constructs a newly created node with no arguments.
	 */
	public XmlDeclaration() {
		super(XML_DECL, List.of(
				Node.of(XML_START, "<?xml"),
				Node.of(S, " "),
				Node.of(NAME, "version"),
				Node.of(EQ, "="),
				Node.of(ATT_VALUE, "\"" + DEFAULT_VERSION + "\""),
				Node.of(XML_END, "?>")));
		this.version = DEFAULT_VERSION;
		this.encoding = null;
		this.standalone = null;
	}

	/**
	 * Constructs a newly created node with an encoding.
	 * @param encoding a text encoding for this document
	 */
	public XmlDeclaration(String encoding) {
		super(XML_DECL, List.of(
				Node.of(XML_START, "<?xml"),
				Node.of(S, " "),
				Node.of(NAME, "version"),
				Node.of(EQ, "="),
				Node.of(ATT_VALUE, "\"" + DEFAULT_VERSION + "\""),
				Node.of(S, " "),
				Node.of(NAME, "encoding"),
				Node.of(EQ, "="),
				Node.of(ATT_VALUE, "\"" + encoding + "\""),
				Node.of(XML_END, "?>")));
		this.version = DEFAULT_VERSION;
		this.encoding = encoding;
		this.standalone = null;
	}

	/**
	 * Constructs a newly created node with a version and an encoding.
	 * @param version an XML version of this document
	 * @param encoding a text encoding for this document
	 */
	public XmlDeclaration(String version, String encoding) {
		super(XML_DECL, List.of(
				Node.of(XML_START, "<?xml"),
				Node.of(S, " "),
				Node.of(NAME, "version"),
				Node.of(EQ, "="),
				Node.of(ATT_VALUE, "\"" + version + "\""),
				Node.of(S, " "),
				Node.of(NAME, "encoding"),
				Node.of(EQ, "="),
				Node.of(ATT_VALUE, "\"" + encoding + "\""),
				Node.of(XML_END, "?>")));
		this.version = version;
		this.encoding = encoding;
		this.standalone = null;
	}

	/**
	 * Constructs a newly created node with a version, an encoding, and a standalone.
	 * @param version an XML version of this document
	 * @param encoding a text encoding for this document
	 * @param standalone either yes or no value (case-sensitive)
	 */
	public XmlDeclaration(String version, String encoding, String standalone) {
		super(XML_DECL, List.of(
				Node.of(XML_START, "<?xml"),
				Node.of(S, " "),
				Node.of(NAME, "version"),
				Node.of(EQ, "="),
				Node.of(ATT_VALUE, "\"" + version + "\""),
				Node.of(S, " "),
				Node.of(NAME, "encoding"),
				Node.of(EQ, "="),
				Node.of(ATT_VALUE, "\"" + encoding + "\""),
				Node.of(S, " "),
				Node.of(NAME, "standalone"),
				Node.of(EQ, "="),
				Node.of(ATT_VALUE, "\"" + standalone + "\""),
				Node.of(XML_END, "?>")));
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}

	/**
	 * Constructs a newly created node with a sequence of nodes, a version, an encoding, and a standalone.
	 * @param nodeList a sequence of nodes to represent this declaration
	 * @param version an XML version of this document
	 * @param encoding a text encoding for this document
	 * @param standalone either yes or no value (case-sensitive)
	 */
	public XmlDeclaration(List<Node> nodeList, String version, String encoding, String standalone) {
		super(XML_DECL, nodeList);
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
		if (get(0).type == PI_START && get(1).type == NAME) {
			set(0, Node.of(XML_START, "<?xml"));
			remove(1);
		}
	}

}
