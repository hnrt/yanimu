package com.hideakin.yanimu.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.TestInfo;

import com.hideakin.yanimu.xml.Document;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.doctype.AttributeDefinition;
import com.hideakin.yanimu.xml.doctype.AttributeListDeclaration;
import com.hideakin.yanimu.xml.doctype.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.ElementTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.EntityDeclaration;
import com.hideakin.yanimu.xml.doctype.ExternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.ExternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.NotationDeclaration;

public class TestHelper {

	private static String _className;
	private static String _methodName;

	public static <T> void start(Class<T> clazz) {
		String[] ss = clazz.getName().split("\\.");
		_className = ss[ss.length - 1];
		_methodName = null;
		print("Started.");
	}

	public static <T> void finish(Class<T> clazz) {
		print("Finished.");
		_className = null;
	}

	public static void start(TestInfo info) {
		String name = info.getTestMethod().get().getName();
		if (name.startsWith("test")) {
			_methodName = name.substring("test".length());
		} else {
			_methodName = name;
		}
		print("Started.");
	}

	public static void finish(TestInfo info) {
		print("Finished.");
		_methodName = null;
	}

	public static void print(String format, Object...args) {
		String h;
		if (_className != null && _methodName != null) {
			h = "%s::%s: ".formatted(_className, _methodName);
		} else if (_className == null && _methodName != null) {
			h = "%s: ".formatted(_methodName);
		} else if (_className != null && _methodName == null) {
			h = "%s: ".formatted(_className);
		} else {
			h = null;
		}
		String s = format.formatted(args);
		if (h != null) {
			System.out.printf("%s%s\n", h, s.replaceAll("\n", "\n" + h));
		} else {
			System.out.printf("%s\n", s);
		}
	}

	public static int checkDocument(String header, Document doc, byte[] expected) {
		List<Node> nodeList = doc.nodeList();
		int start = 0;
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			int end = start + node.sequence().length;
			print("%s[%d] %d %d %s", header, i, start, end, node.toDebuggingString());
			if (node.type == Node.ELEMENT) {
				assertEquals(end, printElement(String.format("%s[%d]", header, i), (Element)node, start));
			} else if (node.type == Node.DOCTYPE_DECL) {
				printDocumentTypeDeclaration(String.format("%s[%d]", header, i), (DocumentTypeDeclaration)node, start);
			}
			start = end;
		}
		assertArrayEquals(expected, doc.sequence());
		return start;
	}

	private static int printElement(String header, Element element, int start) {
		if (element.isEmptyElement()) {
			int end = start + element.startTag().sequence().length;
			print("%s %d %d %s", header, start, end, element.startTag().toDebuggingString());
			start = end;
		} else {
			int end = start + element.startTag().sequence().length;
			print("%s %d %d %s", header, start, end, element.startTag().toDebuggingString());
			start = end;
			List<Node> children = element.children();
			for (int i = 0; i < children.size(); i++) {
				Node node = children.get(i);
				end = start + node.sequence().length;
				print("%s[%d] %d %d %s", header, i, start, end, node.toDebuggingString());
				if (node.type == Node.ELEMENT) {
					assertEquals(end, printElement(String.format("%s[%d]", header, i), (Element)node, start));
				}
				start = end;
			}
			end = start + element.endTag().sequence().length;
			print("%s %d %d %s", header, start, end, element.endTag().toDebuggingString());
			start = end;
		}
		return start;
	}

	private static int printDocumentTypeDeclaration(String header, DocumentTypeDeclaration dtd, int start) {
		List<Node> nodeList = dtd.nodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			int end = start + node.sequence().length;
			print("%s[%d] %d %d %s", header, i, start, end, node.toDebuggingString());
			start = end;
		}
		for (int i = 0; i < dtd.declarations.length; i++) {
			printDeclaration(header, dtd.declarations[i]);
		}
		return start;
	}

	private static void printDeclaration(String header, Object obj) {
		if (obj instanceof ElementTypeDeclaration etd) {
			print("%s ELEMENT %s %s", header, etd.name, etd.cs.toDebuggingString());
		} else if (obj instanceof AttributeListDeclaration ald) {
			for (int i = 0; i < ald.definitions.length; i++) {
				AttributeDefinition d = ald.definitions[i];
				print("%s ATTLIST %s[%d] %s %s %s", header, ald.name, i, d.key, d.type, d.value);
			}
		} else if (obj instanceof EntityDeclaration ed) {
			if (ed.definition instanceof InternalEntityDefinition ie) {
				print("%s ENTITY %s \"%s\"", header, ie.key, ie.value.replaceAll("\"", "\\\\\""));
			} else if (ed.definition instanceof ExternalEntityDefinition ee) {
				print("%s ENTITY %s system=%s pubid=%s ndata=%s", header, ee.key, ee.systemLiteral, ee.pubidLiteral, ee.ndata);
			} else if (ed.definition instanceof InternalParameterEntityDefinition ipe) {
				print("%s ENTITY %% %s \"%s\"", header, ipe.key, ipe.value.replaceAll("\"", "\\\\\""));
			} else if (ed.definition instanceof ExternalParameterEntityDefinition epe) {
				print("%s ENTITY %% %s system=%s pubid=%s", header, epe.key, epe.systemLiteral, epe.pubidLiteral);
			} else {
				print("%s ENTITY (malformed)", header);
			}
		} else if (obj instanceof NotationDeclaration nd) {
			print("%s NOTATION %s system=%s pubid=%s", header, nd.name, nd.systemLiteral, nd.pubidLiteral);
		}
	}

}
