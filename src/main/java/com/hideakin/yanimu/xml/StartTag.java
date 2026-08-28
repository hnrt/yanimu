package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class StartTag extends Tag {

	public static StartTag of(String name) {
		return new StartTag(name);
	}

	public static StartTag of(List<Node> nodeList, List<Attribute> attributeList) {
		return new StartTag(nodeList, attributeList);
	}

	protected static final byte[] START_SEQUENCE = {'<'};
	protected static final byte[] STAG_END_SEQUENCE = {'>'};
	protected static final byte[] EETAG_END_SEQUENCE = {'/', '>'};

	protected final List<Attribute> _attributeList = new ArrayList<>();

	protected StartTag(String name) {
		this(STAG,
				List.of(Node.of(STAG_START, START_SEQUENCE),
						Node.of(NAME, name),
						Node.of(STAG_END, STAG_END_SEQUENCE)),
				List.of());
	}

	protected StartTag(List<Node> nodeList, List<Attribute> attributeList) {
		this(STAG, nodeList, attributeList);
	}

	protected StartTag(int type, List<Node> nodeList, List<Attribute> attributeList) {
		super(type, nodeList);
		_attributeList.addAll(attributeList);
	}

	public int attributeCount() {
		return _attributeList.size();
	}

	public String attribute(int index) {
		return attribute(index, null);
	}

	public String attribute(int index, String defaultValue) {
		int count = attributeCount();
		if (index < 0) {
			index += count;
		}
		if (0 <= index && index < count) {
			return _attributeList.get(index).value;
		}
		return defaultValue;
	}

	public String attribute(String key) {
		return attribute(key, null);
	}

	public String attribute(String key, String defaultValue) {
		for (Attribute a : _attributeList) {
			if (a.key.equals(key)) {
				return a.value;
			}
		}
		return defaultValue;
	}

	public List<String> attributeKeys() {
		List<String> keys = new ArrayList<>(_attributeList.size());
		for (Attribute a : _attributeList) {
			keys.add(a.key);
		}
		return keys;
	}

	public StartTag toStartTag() {
		if (last().type == STAG_END) {
			return StartTag.of(_nodeList, _attributeList);
		} else {
			List<Node> nodeList = new ArrayList<>(_nodeList);
			nodeList.set(nodeList.size() - 1, Node.of(STAG_END, STAG_END_SEQUENCE));
			return StartTag.of(nodeList, _attributeList);
		}
	}

	public EmptyElementTag toEmptyElementTag() {
		if (last().type == EETAG_END) {
			return EmptyElementTag.of(_nodeList, _attributeList);
		} else {
			List<Node> nodeList = new ArrayList<>(_nodeList);
			nodeList.set(nodeList.size() - 1, Node.of(EETAG_END, EETAG_END_SEQUENCE));
			return EmptyElementTag.of(nodeList, _attributeList);
		}
	}

}
