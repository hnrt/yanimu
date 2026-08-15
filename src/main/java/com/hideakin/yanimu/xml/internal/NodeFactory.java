package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.CDATASection;
import com.hideakin.yanimu.xml.CharRef;
import com.hideakin.yanimu.xml.Comment;
import com.hideakin.yanimu.xml.EntityRef;
import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.ParameterEntityReference;
import com.hideakin.yanimu.xml.QuotedString;

import static com.hideakin.yanimu.xml.Node.*;

public class NodeFactory {

	private StringBuilder _buffer = new StringBuilder();
	private int _offset = 0;

	public NodeFactory() {
	}

	public Node nodeOf(int code) {
		byte[] sequence = pop();
		switch (code) {
		case ENTITY_VALUE:
		case ATT_VALUE:
		case SYSTEM_LITERAL:
		case PUBID_LITERAL:
			return new QuotedString(code, _offset, _offset + sequence.length, sequence);
		case COMMENT:
			return new Comment(_offset, _offset + sequence.length, sequence);
		case CD_SECT:
			return new CDATASection(_offset, _offset + sequence.length, sequence);
		case CHAR_REF:
			return new CharRef(_offset, _offset + sequence.length, sequence);
		case ENTITY_REF:
			return new EntityRef(_offset, _offset + sequence.length, sequence);
		case PEREFERENCE:
			return new ParameterEntityReference(_offset, _offset + sequence.length, sequence);
		default:
			return new Node(code, _offset, _offset + sequence.length, sequence);
		}
	}

	public void push(int c) {
		_buffer.appendCodePoint(c);
	}

	private byte[] pop() {
		byte[] sequence = _buffer.toString().getBytes();
		_buffer.setLength(0);
		_offset += sequence.length;
		return sequence;
	}

	public int getLength() {
		return _buffer.length();
	}

	public void setLength(int length) {
		_buffer.setLength(length);
	}

}
