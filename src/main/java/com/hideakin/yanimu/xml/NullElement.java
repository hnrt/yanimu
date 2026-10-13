package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * A sentinel class for Element.
 */
public class NullElement extends Element {

	/**
	 * A sentinel object to avoid null pointer exceptions.
	 * The node type is {@code NULL}.
	 * The size of the node sequence is 0.
	 */
	public static final Element NULL_ELEMENT = new NullElement();

	private NullElement() {
		super();
	}

	@Override
	public void set(List<Node> nodeList) {
		throw new RuntimeException(getClass().getSimpleName() + "::set: INCORRECT USE!");
	}

	@Override
	public void setParent(Node parent) {
	}

	@Override
	public void add(Node node) {
	}

	@Override
	public void add(int index, Node node) {
	}

	@Override
	public boolean isEmptyElement() {
		return true;
	}

	@Override
	public AttributeTag startTag() {
		return EmptyElementTag.of("");
	}

	@Override
	public EndTag endTag() {
		return null;
	}

	@Override
	public int attributeCount() {
		return 0;
	}

	@Override
	public boolean empty() {
		return true;
	}

	@Override
	public void setInnerText(String value) {
	}

	@Override
	public List<Element> getElements(String name) {
		return new ArrayList<>();
	}

	@Override
	public Element getElement(String name) {
		return NULL_ELEMENT;
	}

}
