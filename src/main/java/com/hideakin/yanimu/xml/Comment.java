package com.hideakin.yanimu.xml;

/**
 * An immutable node for the XML Comment.
 */
public class Comment extends ImmutableNode {

	/**
	 * The start sequence of comment. ({@code String} representation)
	 */
	public static final String START_STRING = "<!--";

	/**
	 * The end sequence of comment. ({@code String} representation)
	 */
	public static final String END_STRING = "-->";

	/**
	 * The start sequence of comment. (byte representation)
	 */
	public static final byte[] START_BYTES = START_STRING.getBytes();

	/**
	 * The end sequence of comment. (byte representation)
	 */
	public static final byte[] END_BYTES = END_STRING.getBytes();

	/**
	 * Creates a new node of type {@code COMMENT}.
	 * @param sequence a UTF-8 encoded byte representation of a comment to assign to this node 
	 * @return a newly created node
	 */
	public static Comment of(byte[] sequence) {
		return new Comment(sequence);
	}

	/**
	 * Creates a new node of type {@code COMMENT}.
	 * @param sequence a {@code String} representation of a comment to assign to this node 
	 * @return a newly created node
	 */
	public static Comment of(String sequence) {
		return new Comment(sequence);
	}

	private Comment(byte[] sequence) {
		super(COMMENT, sequence, START_BYTES, END_BYTES);
	}

	private Comment(String sequence) {
		super(COMMENT, sequence, START_STRING, END_STRING);
	}

	/**
	 * Returns a comment in this node.
	 * @return a {@code String} representation of the comment in this node,
	 * excluding the start sequence "&lt;!--" and the end sequence "--&gt;".
	 */
	public String innerText() {
		return innerText(START_BYTES.length, END_BYTES.length);
	}

}
