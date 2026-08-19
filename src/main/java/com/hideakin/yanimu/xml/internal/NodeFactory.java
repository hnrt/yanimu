package com.hideakin.yanimu.xml.internal;

import com.hideakin.yanimu.xml.CDATASection;
import com.hideakin.yanimu.xml.CharRef;
import com.hideakin.yanimu.xml.Comment;
import com.hideakin.yanimu.xml.EntityRef;
import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.ParameterEntityReference;
import com.hideakin.yanimu.xml.QuotedString;

import static com.hideakin.yanimu.xml.Node.*;

import java.nio.charset.StandardCharsets;

public class NodeFactory {

	private final StringBuilder _buffer = new StringBuilder();

	public NodeFactory() {
	}

	public Node nodeOf(int type) {
		byte[] sequence = pop();
		switch (type) {
		case ENTITY_VALUE:
		case ATT_VALUE:
		case SYSTEM_LITERAL:
		case PUBID_LITERAL:
			return new QuotedString(type, sequence);
		case COMMENT:
			return new Comment(sequence);
		case CD_SECT:
			return new CDATASection(sequence);
		case CHAR_REF:
			return new CharRef(sequence);
		case ENTITY_REF:
			return new EntityRef(sequence);
		case PEREFERENCE:
			return new ParameterEntityReference(sequence);
		default:
			return Node.of(type, sequence);
		}
	}

	public void push(int c) {
		_buffer.appendCodePoint(c);
	}

	private byte[] pop() {
		byte[] sequence = _buffer.toString().getBytes(StandardCharsets.UTF_8);
		_buffer.setLength(0);
		return sequence;
	}

	public int getLength() {
		return _buffer.length();
	}

	public void setLength(int length) {
		_buffer.setLength(length);
	}

}
