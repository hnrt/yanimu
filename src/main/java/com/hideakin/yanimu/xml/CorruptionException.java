package com.hideakin.yanimu.xml;

/**
 * A runtime exception on a possible corruption in a node sequence.
 */
public class CorruptionException extends RuntimeException {

	private static final long serialVersionUID = -4983262716214444676L;

	/**
	 * The NodeList instance in which a possible corruption was detected.
	 */
	private NodeList _subject;

	/**
	 * The index number at which a possible corruption was detected. 
	 */
	private Integer _index;

	/**
	 * Constructs a newly created instance with the source information.
	 * @param subject the instance in which a possible corruption was detected.
	 */
	public CorruptionException(NodeList subject) {
		super(subject.getClass().getSimpleName() + (
				subject.size() == 0
				? ": Empty."
				: ": Possible corruption."));
		_subject = subject;
		_index = null;
	}

	/**
	 * Constructs a newly created instance with the source information.
	 * @param subject the instance in which a possible corruption was detected.
	 * @param index the index at which a possible corruption was detected.
	 */
	public CorruptionException(NodeList subject, int index) {
		super(subject.getClass().getSimpleName() + (
				index < 0
				? ": Possible corruption."
				: index < subject.size()
				? ": Possible corruption at index of %d.".formatted(index)
				: ": Premature end of the node list."));
		_subject = subject;
		_index = index;
	}

	/**
	 * @return the NodeList instance in which a possible corruption was detected.
	 */
	public NodeList subject() {
		return _subject;
	}

	/**
	 * @return the index at which a possible corruption was detected, or -1 if not specified.
	 */
	public int index() {
		return _index != null ? _index : -1;
	}

	/**
	 * @return true if specified, or false otherwise.
	 */
	public boolean hasIndex() {
		return _index != null;
	}

}
