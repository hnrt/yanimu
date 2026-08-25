package com.hideakin.yanimu.xml;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.hideakin.yanimu.xml.doctype.AttributeDefinition;
import com.hideakin.yanimu.xml.doctype.AttributeListDeclaration;
import com.hideakin.yanimu.xml.doctype.ContentParticle;
import com.hideakin.yanimu.xml.doctype.ContentSpec;
import com.hideakin.yanimu.xml.doctype.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.ElementTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.EntityDeclaration;
import com.hideakin.yanimu.xml.doctype.ExternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.ExternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalEntityDefinition;
import com.hideakin.yanimu.xml.doctype.InternalParameterEntityDefinition;
import com.hideakin.yanimu.xml.doctype.NotationDeclaration;

public class TestHelper {

	private static final String BUG = "!!!BUG!!!";

	public static int checkDocument(String header, Document doc, byte[] expected) {
		List<Node> nodeList = doc.nodeList();
		int start = 0;
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			int end = start + node.sequence().length;
			System.out.printf("%s[%d] %d %d %s\n", header, i, start, end, sprint(node));
			if (node.type == Node.ELEMENT) {
				assertEquals(end, printElement(String.format("%s[%d]E", header, i), (Element)node, start));
			} else if (node.type == Node.DOCTYPE_DECL) {
				printDocumentTypeDeclaration(String.format("%s[%d]D", header, i), (DocumentTypeDeclaration)node, start);
			}
			start = end;
		}
		assertArrayEquals(expected, doc.sequence());
		return start;
	}

	private static int printElement(String header, Element element, int start) {
		if (element.isEmptyElement()) {
			int end = start + element.startTag().sequence().length;
			System.out.printf("%s:EE %d %d %s\n", header, start, end, sprint(element.startTag()));
			start = end;
		} else {
			int end = start + element.startTag().sequence().length;
			System.out.printf("%s:S %d %d %s\n", header, start, end, sprint(element.startTag()));
			start = end;
			List<Node> children = element.children();
			for (int i = 0; i < children.size(); i++) {
				Node node = children.get(i);
				end = start + node.sequence().length;
				System.out.printf("%s:[%d] %d %d %s\n", header, i, start, end, sprint(node));
				if (node.type == Node.ELEMENT) {
					assertEquals(end, printElement(String.format("%s:[%d]", header, i), (Element)node, start));
				}
				start = end;
			}
			end = start + element.endTag().sequence().length;
			System.out.printf("%s:E %d %d %s\n", header, start, end, sprint(element.endTag()));
			start = end;
		}
		return start;
	}

	private static int printDocumentTypeDeclaration(String header, DocumentTypeDeclaration dtd, int start) {
		List<Node> nodeList = dtd.nodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			int end = start + node.sequence().length;
			System.out.printf("%s[%d] %d %d %s\n", header, i, start, end, sprint(node));
			start = end;
		}
		for (int i = 0; i < dtd.declarations.length; i++) {
			printDeclaration(header, dtd.declarations[i]);
		}
		return start;
	}

	private static void printDeclaration(String header, Object obj) {
		if (obj instanceof ElementTypeDeclaration etd) {
			System.out.printf("%s:DTD:ELEMENT %s %s\n", header, etd.name, sprint(etd.cs));
		} else if (obj instanceof AttributeListDeclaration ald) {
			System.out.printf("%s:DTD:ATTLIST %s", header, ald.name);
			for (int i = 0; i < ald.definitions.length; i++) {
				AttributeDefinition d = ald.definitions[i];
				System.out.printf(" [%d] %s %s %s", i, d.key, d.type, d.value);
			}
			System.out.printf("\n");
		} else if (obj instanceof EntityDeclaration ed) {
			System.out.printf("%s:DTD:ENTITY ", header);
			if (ed.definition instanceof InternalEntityDefinition ie) {
				System.out.printf("%s \"%s\"\n", ie.key, ie.value.replaceAll("\"", "\\\\\""));
			} else if (ed.definition instanceof ExternalEntityDefinition ee) {
				System.out.printf("%s system=%s pubid=%s ndata=%s\n", ee.key, ee.systemLiteral, ee.pubidLiteral, ee.ndata);
			} else if (ed.definition instanceof InternalParameterEntityDefinition ipe) {
				System.out.printf("%% %s \"%s\"\n", ipe.key, ipe.value.replaceAll("\"", "\\\\\""));
			} else if (ed.definition instanceof ExternalParameterEntityDefinition epe) {
				System.out.printf("%% %s system=%s pubid=%s\n", epe.key, epe.systemLiteral, epe.pubidLiteral);
			} else {
				System.out.printf(BUG + "\n");
			}
		} else if (obj instanceof NotationDeclaration nd) {
			System.out.printf("%s:DTD:NOTATION %s system=%s pubid=%s\n", header, nd.name, nd.systemLiteral, nd.pubidLiteral);
		}
	}

	private static String sprint(Node node) {
		StringBuilder buf;
		switch (node.type) {
		case Node.ELEMENT:
			return "<...>";
		case Node.S:
			buf = new StringBuilder();
			for (byte c : node.sequence()) {
				switch (c) {
				case 9: buf.append(" HT"); break;
				case 10: buf.append(" LF"); break;
				case 13: buf.append(" CR"); break;
				case 32: buf.append(" SP"); break;
				default: buf.append(" ?"); break;
				}
			}
			return buf.toString().substring(1);
		default:
			return node.toString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t");
		}
	}

	private static String sprint(ContentSpec cs) {
		if (cs.value instanceof Integer intValue) {
			switch (intValue) {
			case Node.EMPTY:
				return "EMPTY";
			case Node.ANY:
				return "ANY";
			case Node.PCDATA:
				return "(#PCDATA)";
			default:
				return BUG;
			}
		} else if (cs.value instanceof String[] arrValue) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("(");
			buffer.append(arrValue[0]);
			for (int i = 1; i < arrValue.length; i++) {
				buffer.append("|");
				buffer.append(arrValue[i]);
			}
			buffer.append(")*");
			return buffer.toString();
		} else if (cs.value instanceof ContentParticle particle) {
			return particle.toString();
		} else {
			return BUG;
		}
	}

}
