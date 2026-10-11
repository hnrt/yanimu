package com.hideakin.yanimu.xml.internal;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The lexical analyzing context store.
 * <p>
 * This store is capable of the stack operation such as push and pop.
 */
public class LexerContext {

	public static final int BASE = 0;
	public static final int XML = 1;
	public static final int PI = 2;
	public static final int STAG = 3;
	public static final int CONTENT = 4;
	public static final int ETAG = 5;
	public static final int DOCTYPE = 6;
	public static final int EXTERNAL = 7;
	public static final int ELEMENT = 8;
	public static final int ATTLIST = 9;
	public static final int ENTITY = 10;
	public static final int NOTATION = 11;
	public static final int CONDITIONAL = 12;
	public static final int IGNORE = 13;

	/**
	 * Creates a new context store.
	 * @param initialValue a context value to initialize
	 * @return a newly created store
	 */
	public static LexerContext of(int initialValue) {
		return new LexerContext(initialValue);
	}

	private final Deque<Integer> _deque = new ArrayDeque<>();
	private int _current;

	private LexerContext(int initialValue) {
		_current = initialValue;
	}

	/**
	 * Returns the context value currently set.
	 * @return the context value currently set.
	 */
	public int get() {
		return _current;
	}

	/**
	 * Sets the context value to the specified one.
	 * @param value a context value to set
	 */
	public void set(int value) {
		_current = value;
	}

	/**
	 * Pushes the specified context value onto the stack.
	 * @param value a context value to push
	 * @return the context value currently set
	 */
	public int push(int value) {
		_deque.push(Integer.valueOf(_current));
		_current = value;
		return value;
	}

	/**
	 * Removes the context value on top of the stack and sets the context value to the next one.
	 * @return the next context value.
	 * @throws RuntimeException if the stack is empty
	 */
	public int pop() {
		if (_deque.isEmpty()) {
			throw new RuntimeException("LexerContext::pop: No value!");
		}
		_current = _deque.poll();
		return _current;
	}

}
