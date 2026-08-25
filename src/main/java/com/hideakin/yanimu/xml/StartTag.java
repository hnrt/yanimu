package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class StartTag extends Tag {

	public static final byte[] START_SEQUENCE = {'<'};
	public static final byte[] END_SEQUENCE = {'>'};

	protected final List<Attribute> _attributeList;

	public StartTag(String name) {
		this(STAG, new ArrayList<Node>(
				List.of(new Node(STAG_START, START_SEQUENCE),
						new Node(NAME, name),
						new Node(STAG_END, END_SEQUENCE))),
				new ArrayList<>());
	}

	public StartTag(List<Node> nodeList, List<Attribute> attributeList) {
		this(STAG, nodeList, attributeList);
	}

	protected StartTag(int type, List<Node> nodeList, List<Attribute> attributeList) {
		super(type, nodeList);
		_attributeList = attributeList;
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

	public StartTag clone(int type) {
		List<Node> nodeList = new ArrayList<>(_nodeList);
		List<Attribute> attributeList = new ArrayList<>(_attributeList);
		if (type == STAG) {
			int index = nodeList.size() - 1;
			Node node = nodeList.get(index);
			if (node.type == STAG_END) {
				//OK
			} else if (node.type == EETAG_END) {
				nodeList.set(index, new Node(STAG_END, END_SEQUENCE));
			} else {
				nodeList.add(index, new Node(STAG_END, END_SEQUENCE));
			}
			return new StartTag(nodeList, attributeList);
		} else if (type == EETAG) {
			int index = nodeList.size() - 1;
			Node node = nodeList.get(index);
			if (node.type == EETAG_END) {
				//OK
			} else if (node.type == STAG_END) {
				nodeList.set(index, new Node(EETAG_END, EmptyElementTag.END_SEQUENCE));
			} else {
				nodeList.add(index, new Node(EETAG_END, EmptyElementTag.END_SEQUENCE));
			}
			return new EmptyElementTag(nodeList, attributeList);
		} else {
			throw new RuntimeException("StartTag::clone: Bad type!");
		}
	}

}
