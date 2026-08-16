package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.Test;

import com.hideakin.yanimu.xml.doctype.AttributeDefinition;
import com.hideakin.yanimu.xml.doctype.AttributeListDeclaration;
import com.hideakin.yanimu.xml.doctype.ContentParticle;
import com.hideakin.yanimu.xml.doctype.ContentSpec;
import com.hideakin.yanimu.xml.doctype.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.ElementTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.EntityDeclaration;
import com.hideakin.yanimu.xml.doctype.NotationDeclaration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class DocumentTest {

	@Test
	void test1() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(new ByteArrayInputStream(source.getBytes()));
			assertEquals("1.0", doc.version());
			assertEquals("Hello, world!", doc.root().innerText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test2() {
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n"
				+ "<!DOCTYPE greeting SYSTEM \"hello.dtd\">\r\n"
				+ "<greeting abc:xyz='123'>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			assertEquals("UTF-8", doc.encoding());
			assertEquals("Hello, world!", doc.root().innerText());
			assertEquals("123", doc.root().attribute("abc:xyz"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test3() {
		String source = "<?xml version=\"1.0\" standalone='yes' ?>\r\n"
				+ "<!DOCTYPE greeting [\r\n"
				+ "  <!ELEMENT greeting (#PCDATA)>\r\n"
				+ "]>\r\n"
				+ "<greeting abc=\"&lt;&amp;x&apos;&quot;&gt;\" xyz=\"&#x41;&#x42;&#x43;\" >Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes();
			doc.load(content);
			assertEquals("yes", doc.standalone());
			assertEquals("Hello, world!", doc.root().innerText());
			assertEquals("<&x\'\">", doc.root().attribute("abc"));
			assertEquals("ABC", doc.root().attribute("xyz"));
			assertEquals("<&x\'\">", doc.root().attribute(0));
			assertEquals("ABC", doc.root().attribute(1));
			assertEquals("ABC", doc.root().attribute(-1));
			assertEquals(null, doc.root().attribute("opq"));
			assertEquals(null, doc.root().attribute(2));
			int end = checkOffset("test3", doc.layout(), 0);
			System.out.printf("test3: content.length=%d actual=%d\n", content.length, end);
			assertEquals(content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test4() {
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone='no' ?>\r\n"
				+ "<!DOCTYPE greeting [\r\n"
				+ "  <!ATTLIST poem  xml:space (default|preserve) 'preserve'>\r\n"
				+ "\r\n"
				+ "  <!ATTLIST pre xml:space (preserve) #FIXED 'preserve'>\r\n"
				+ "]>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes();
			doc.load(content);
			assertEquals("no", doc.standalone());
			assertEquals("Hello, world!", doc.root().innerText());
			int end = checkOffset("test4", doc.layout(), 0);
			System.out.printf("test4: content.length=%d end=%d\n", content.length, end);
			assertEquals(content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test5() {
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone='no' ?>\r\n"
				+ "<!DOCTYPE greeting [\r\n"
				+ "  <!ENTITY % name.para 'X'>\r\n"
				+ "  <!ENTITY % content.para 'ANY'>\r\n"
				+ "  <!ENTITY open-hatch\r\n"
				+ "         SYSTEM \"http://www.textuality.com/boilerplate/OpenHatch.xml\">\r\n"
				+ "  <!ENTITY open-hatch\r\n"
				+ "         PUBLIC \"-//Textuality//TEXT Standard open-hatch boilerplate//EN\"\r\n"
				+ "         \"http://www.textuality.com/boilerplate/OpenHatch.xml\">\r\n"
				+ "  <!ENTITY hatch-pic\r\n"
				+ "         SYSTEM \"../grafix/OpenHatch.gif\"\r\n"
				+ "         NDATA gif >"
				+ "  <!ELEMENT br EMPTY>\r\n"
				+ "  <!ELEMENT p (#PCDATA|emph)* >\r\n"
				+ "  <!ELEMENT %name.para; %content.para; >\r\n"
				+ "  <!ELEMENT container ANY>\r\n"
				+ "]>\r\n"
				+ "<greeting>\r\n"
				+ "  <abc id='1'>1</abc>\r\n"
				+ "  <abc id='2'>2</abc>\r\n"
				+ "  <abc id='3'>3</abc>\r\n"
				+ "  <abc id='4'>\r\n"
				+ "    <abc id='41'>41</abc>\r\n"
				+ "    <abc2 id='42'><abc id='421'>421</abc><abc id='422'>422</abc></abc2>\r\n"
				+ "    <abc id='43'>43</abc>\r\n"
				+ "  </abc>\r\n"
				+ "</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes();
			doc.load(content);
			List<Element> elements = doc.root().getElements("abc");
			assertEquals(8, elements.size());
			assertEquals("1", elements.get(0).attribute("id"));
			assertEquals("2", elements.get(1).attribute("id"));
			assertEquals("3", elements.get(2).attribute("id"));
			assertEquals("4", elements.get(3).attribute("id"));
			assertEquals("41", elements.get(4).attribute("id"));
			assertEquals("421", elements.get(5).attribute("id"));
			assertEquals("422", elements.get(6).attribute("id"));
			assertEquals("43", elements.get(7).attribute("id"));
			int end = checkOffset("test5", doc.layout(), 0);
			System.out.printf("test5: content.length=%d end=%d\n", content.length, end);
			assertEquals(content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test6() {
		String source = "\uFEFF<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes(StandardCharsets.UTF_8);
			doc.load(content);
			assertEquals("Hello, world!", doc.root().innerText());
			int end = checkOffset("test6", doc.layout(), 0);
			System.out.printf("test6: content.length=%d-3=%d actual=%d\n", content.length, content.length - 3, end);
			assertEquals(content.length - 3, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test7() {
		String source = "\uFEFF<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes(StandardCharsets.UTF_16LE));
			assertEquals("Hello, world!", doc.root().innerText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test8() {
		String source = "<?xml version=\"1.0\" ?>\r\n"
				+ "<!DOCTYPE greeting [\r\n"
				+ "  <!ENTITY single-line-comment '//'>\r\n"
				+ "]>\r\n"
				+ "<greeting>\r\n"
				+ "  <code><![CDATA[[void func(int x) {\n"
				+ "\treturn x < 100 ? x * 4 : x * 2;\n"
				+ "}]]>&single-line-comment;&#60;&gt;&bogus;<!--@@@--></code>\r\n"
				+ "  <tests>\r\n"
				+ "    <test id='2'>2</test>\r\n"
				+ "    <test id='3'>30</test>\r\n"
				+ "    <test id='4'>400</test>\r\n"
				+ "  </tests>\r\n"
				+ "</greeting>\r\n";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes(); 
			doc.load(content);
			List<Element> elements = doc.root().getElements("test");
			assertEquals(3, elements.size());
			assertEquals("2", elements.get(0).attribute("id"));
			assertEquals("3", elements.get(1).attribute("id"));
			assertEquals("4", elements.get(2).attribute("id"));
			assertEquals("2", elements.get(0).innerText());
			assertEquals("30", elements.get(1).innerText());
			assertEquals("400", elements.get(2).innerText());
			assertEquals("void func(int x) {\n\treturn x < 100 ? x * 4 : x * 2;\n}//<>&bogus;", doc.root().getElements("code").get(0).innerText());
			int end = checkOffset("test8", doc.layout(), 0);
			System.out.printf("test8: content.length=%d actual=%d\n", content.length, end);
			assertEquals(content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int checkOffset(String header, List<Node> nodes, int expected) {
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			System.out.printf("%s[%d] %d %d %s\n", header, i, node.start, node.end, sprint(node));
			assertEquals(expected, node.start);
			expected += node.sequence.length;
			assertEquals(expected, node.end);
			if (node.type == Node.ELEMENT) {
				Element element = (Element)node;
				int iend = checkOffset(String.format("%s[%d]S", header, i), element.startLayout, element.start);
				if (element.endLayout != null) {
					if (element.children.size() > 0) {
						iend = checkOffset(String.format("%s[%d]C", header, i), element.children, iend);
					}
					iend = checkOffset(String.format("%s[%d]E", header, i), element.endLayout, iend);
				}
				assertEquals(expected, iend);
			} else if (node.type == Node.DOCTYPE_DECL) {
				DocumentTypeDeclaration dtd = (DocumentTypeDeclaration)node;
				assertEquals(dtd.end, checkOffset(String.format("%s[%d]DTD", header, i), Arrays.asList(dtd.layout), dtd.start));
				for (Object obj : dtd.declarations) {
					printDeclaration(header, obj);
				}
			}
		}
		return expected;
	}

	private String sprint(Node node) {
		StringBuilder buf;
		switch (node.type) {
		case Node.ELEMENT:
			return "<...>";
		case Node.S:
			buf = new StringBuilder();
			for (byte c : node.sequence) {
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
			return node.toString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n").replaceAll("\t", "\\t");
		}
	}

	private void printDeclaration(String header, Object obj) {
		if (obj instanceof ElementTypeDeclaration etd) {
			System.out.printf("%s:DTD:ELEMENT %s ", header, etd.name);
			print(etd.cs);
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
				System.out.printf("BUG!\n");
			}
		} else if (obj instanceof NotationDeclaration nd) {
			System.out.printf("%s:DTD:NOTATION %s system=%s pubid=%s\n", header, nd.name, nd.systemLiteral, nd.pubidLiteral);
		}
	}

	private void print(ContentSpec cs) {
		if (cs.value instanceof Integer intValue) {
			switch (intValue) {
			case Node.EMPTY:
				System.out.printf("EMPTY\n");
				break;
			case Node.ANY:
				System.out.printf("ANY\n");
				break;
			case Node.PCDATA:
				System.out.printf("(#PCDATA)\n");
				break;
			}
		} else if (cs.value instanceof String[] arrValue) {
			System.out.printf("(%s", arrValue[0]);
			for (int i = 1; i < arrValue.length; i++) {
				System.out.printf("|%s", arrValue[i]);
			}
			System.out.printf(")*\n");
		} else if (cs.value instanceof ContentParticle particle) {
			System.out.printf("%s\n", particle.toString());
		} else {
			System.out.printf("BUG!\n");
		}
	}

}
