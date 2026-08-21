package com.hideakin.yanimu.xml.internal;

import java.util.ArrayDeque;
import java.util.Deque;

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

	public static LexerContext of(int initialValue) {
		return new LexerContext(initialValue);
	}

	private final Deque<Integer> _deque = new ArrayDeque<>();
	private int _current;

	private LexerContext(int initialValue) {
		_current = initialValue;
	}

	public int get() {
		return _current;
	}

	public void set(int value) {
		_current = value;
	}

	public int push(int value) {
		_deque.push(Integer.valueOf(_current));
		_current = value;
		return value;
	}

	public int pop() {
		if (_deque.isEmpty()) {
			throw new RuntimeException("LexerContext::pop: No value!");
		}
		_current = _deque.poll();
		return _current;
	}

}
