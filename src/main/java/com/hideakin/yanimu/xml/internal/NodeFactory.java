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

	public Node nodeOf(int type) {
		byte[] sequence = pop();
		switch (type) {
		case ENTITY_VALUE:
		case ATT_VALUE:
		case SYSTEM_LITERAL:
		case PUBID_LITERAL:
			return new QuotedString(type, _offset, sequence);
		case COMMENT:
			return new Comment(_offset, sequence);
		case CD_SECT:
			return new CDATASection(_offset, sequence);
		case CHAR_REF:
			return new CharRef(_offset, sequence);
		case ENTITY_REF:
			return new EntityRef(_offset, sequence);
		case PEREFERENCE:
			return new ParameterEntityReference(_offset, sequence);
		default:
			return new Node(type, _offset, sequence);
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
