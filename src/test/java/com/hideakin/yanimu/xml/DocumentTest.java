package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.Test;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class DocumentTest {

	@Test
	void test01() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(new ByteArrayInputStream(source.getBytes()));
			assertEquals("1.0", doc.version());
			assertEquals("Hello, world!", doc.root().innerText());
			byte[] content = source.getBytes();
			int end = checkDocument("test01", doc, content);
			System.out.printf("test01: content.length=%d actual=%d\n", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test02() {
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n"
				+ "<!DOCTYPE greeting SYSTEM \"hello.dtd\">\r\n"
				+ "<greeting abc:xyz='123'>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes();
			doc.load(content);
			assertEquals("UTF-8", doc.encoding());
			assertEquals("Hello, world!", doc.root().innerText());
			assertEquals("123", doc.root().attribute("abc:xyz"));
			int end = checkDocument("test02", doc, content);
			System.out.printf("test02: content.length=%d actual=%d\n", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test03() {
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
			int end = checkDocument("test03", doc, content);
			System.out.printf("test03: content.length=%d actual=%d\n", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test04() {
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
			int end = checkDocument("test04", doc, content);
			System.out.printf("test04: content.length=%d end=%d\n", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test05() {
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
				+ "  <!ENTITY % div.mix 'A'>\r\n"
				+ "  <!ENTITY % dict.mix 'B'>\r\n"
				+ "  <!ENTITY % font \"W\" >\r\n"
				+ "  <!ENTITY % phrase \"X\" >\r\n"
				+ "  <!ENTITY % special \"Y\" >\r\n"
				+ "  <!ENTITY % form \"Z\" >\r\n"
				+ "  <!ELEMENT br EMPTY>\r\n"
				+ "  <!ELEMENT p (#PCDATA|emph)* >\r\n"
				+ "  <!ELEMENT %name.para; %content.para; >\r\n"
				+ "  <!ELEMENT container ANY>\r\n"
				+ "  <!ELEMENT spec (front, body, back?)>\r\n"
				+ "  <!ELEMENT div1 (head, (p | list | note)*, div2*)>\r\n"
				+ "  <!ELEMENT dictionary-body (%div.mix; | %dict.mix;)*>\r\n"
				+ "  <!ELEMENT p (#PCDATA|a|ul|b|i|em)*>\r\n"
				+ "  <!ELEMENT p (#PCDATA | %font; | %phrase; | %special; | %form;)* >\r\n"
				+ "  <!ELEMENT b (#PCDATA)>\r\n"
				+ "  <!ATTLIST termdef\r\n"
				+ "          id      ID      #REQUIRED\r\n"
				+ "          name    CDATA   #IMPLIED>\r\n"
				+ "  <!ATTLIST list\r\n"
				+ "          type    (bullets|ordered|glossary)  \"ordered\">\r\n"
				+ "  <!ATTLIST form\r\n"
				+ "          method  CDATA   #FIXED \"POST\">\r\n"
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
			int end = checkDocument("test05", doc, content);
			System.out.printf("test05: content.length=%d end=%d\n", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test06() {
		String source = "\uFEFF<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes(StandardCharsets.UTF_8);
			doc.load(content);
			assertEquals("Hello, world!", doc.root().innerText());
			int end = checkDocument("test06", doc, Arrays.copyOfRange(content, 3, content.length));
			System.out.printf("test06: content.length=%d-3=%d actual=%d\n", content.length, content.length - 3, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test07() {
		String source = "\uFEFF<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes(StandardCharsets.UTF_16LE));
			assertEquals("Hello, world!", doc.root().innerText());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test08() {
		String source = "<?xml version=\"1.0\" ?>\r\n"
				+ "<!DOCTYPE greeting [\r\n"
				+ "  <!ENTITY single-line-comment '//'>\r\n"
				+ "]>\r\n"
				+ "<greeting>\r\n"
				+ "  <code><![CDATA[void func(int x) {\n"
				+ "\treturn x < 100 ? x * 4 : x * 2;\n"
				+ "}]]>&single-line-comment;&#60;&gt;&bogus;<!--@@@--></code>\r\n"
				+ "  <tests>\r\n"
				+ "    <test id='2'>2</test>\r\n"
				+ "    <test id='3'>30</test>\r\n"
				+ "    <test id='4'>400</test>\r\n"
				+ "  </tests>\r\n"
				+ "  <options xyzzy=\"&lt;waldo&gt;\"/>\r\n"
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
			assertEquals("<waldo>", doc.root().getElements("options").get(0).attribute("xyzzy"));
			int end = checkDocument("test08", doc, content);
			System.out.printf("test08: content.length=%d actual=%d\n", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test09() {
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<!DOCTYPE library [\r\n"
				+ "    <!-- ENTITY 定義 -->\r\n"
				+ "    <!ENTITY authorDefault \"Unknown Author\">\r\n"
				+ "    <!ENTITY copyright \"(C) 2026 Example Library\">\r\n"
				+ "\r\n"
				+ "    <!-- 要素定義 -->\r\n"
				+ "    <!ELEMENT library (meta, books, logs)>\r\n"
				+ "    <!ELEMENT meta (name, created)>\r\n"
				+ "    <!ELEMENT name (#PCDATA)>\r\n"
				+ "    <!ELEMENT created (#PCDATA)>\r\n"
				+ "\r\n"
				+ "    <!ELEMENT books (book+)>\r\n"
				+ "    <!ELEMENT book (title, author?, description?, tags?)>\r\n"
				+ "\r\n"
				+ "    <!-- 属性定義 -->\r\n"
				+ "    <!ATTLIST book\r\n"
				+ "        id ID #REQUIRED\r\n"
				+ "        category CDATA #IMPLIED\r\n"
				+ "        status (draft | published | archived) \"draft\"\r\n"
				+ "    >\r\n"
				+ "\r\n"
				+ "    <!ELEMENT title (#PCDATA)>\r\n"
				+ "    <!ELEMENT author (#PCDATA)>\r\n"
				+ "    <!ELEMENT description (#PCDATA | note | highlight)*>\r\n"
				+ "    <!ELEMENT note (#PCDATA)>\r\n"
				+ "    <!ELEMENT highlight (#PCDATA)>\r\n"
				+ "\r\n"
				+ "    <!ELEMENT tags (tag*)>\r\n"
				+ "    <!ELEMENT tag (#PCDATA)>\r\n"
				+ "\r\n"
				+ "    <!-- ログ要素（再帰構造） -->\r\n"
				+ "    <!ELEMENT logs (log*)>\r\n"
				+ "    <!ELEMENT log (message, log*)>\r\n"
				+ "    <!ELEMENT message (#PCDATA)>\r\n"
				+ "]>\r\n"
				+ "<library>\r\n"
				+ "    <meta>\r\n"
				+ "        <name>Sample Library</name>\r\n"
				+ "        <created>2026-08-16</created>\r\n"
				+ "    </meta>\r\n"
				+ "\r\n"
				+ "    <books>\r\n"
				+ "        <book id=\"b001\" category=\"fiction\" status=\"published\">\r\n"
				+ "            <title>XML Adventures</title>\r\n"
				+ "            <author>&authorDefault;</author>\r\n"
				+ "            <description>\r\n"
				+ "                <![CDATA[\r\n"
				+ "                    This book explores XML parsing techniques.\r\n"
				+ "                ]]>\r\n"
				+ "                <note>Includes DTD and ENTITY examples.</note>\r\n"
				+ "                <highlight>Recommended for parser testing.</highlight>\r\n"
				+ "            </description>\r\n"
				+ "            <tags>\r\n"
				+ "                <tag>XML</tag>\r\n"
				+ "                <tag>Parser</tag>\r\n"
				+ "                <tag>DTD</tag>\r\n"
				+ "            </tags>\r\n"
				+ "        </book>\r\n"
				+ "\r\n"
				+ "        <book id=\"b002\" status=\"draft\">\r\n"
				+ "            <title>Advanced Parsing</title>\r\n"
				+ "            <author>Hanako</author>\r\n"
				+ "            <description>Work in progress.</description>\r\n"
				+ "        </book>\r\n"
				+ "    </books>\r\n"
				+ "\r\n"
				+ "    <logs>\r\n"
				+ "        <log>\r\n"
				+ "            <message>Library initialized.</message>\r\n"
				+ "            <log>\r\n"
				+ "                <message>Books loaded.</message>\r\n"
				+ "            </log>\r\n"
				+ "        </log>\r\n"
				+ "    </logs>\r\n"
				+ "\r\n"
				+ "    <!-- ENTITY 展開確認用 -->\r\n"
				+ "    <footer>&copyright;</footer>\r\n"
				+ "</library>\r\n";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes(StandardCharsets.UTF_8);
			doc.load(content);
			int end = checkDocument("test09", doc, content);
			System.out.printf("test09: content.length=%d actual=%d\n", content.length, end);
			List<Element> authors = doc.root().getElements("author");
			assertEquals("Unknown Author", authors.get(0).innerText());
			assertEquals("Hanako", authors.get(1).innerText());
			for (String message : doc.warnings()) {
				System.out.printf("test09: WARN: %s\n", message);
			}
			for (String message : doc.information()) {
				System.out.printf("test09: INFO: %s\n", message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test10() {
		Path path2 = Path.of("C2909F59-CA4C-4DE5-8B97-27E5DE002221.dtd");
		String source1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n"
				+ "<!DOCTYPE greeting PUBLIC \"-//X4D//Bogus version 1.0//EN\" \""
				+ path2.toString()
				+ "\">\r\n"
				+ "<greeting>Hello, world!</greeting>";
		String source2 = "<!ENTITY % draft 'INCLUDE' >\r\n"
				+ "<!ENTITY % final 'IGNORE' >\r\n"
				+ "\r\n"
				+ "<![%draft;[\r\n"
				+ "<!ELEMENT book (comments*, title, body, supplements?)>\r\n"
				+ "]]>\r\n"
				+ "<![%final;[\r\n"
				+ "<!ELEMENT book (title, body, supplements?)>\r\n"
				+ "]]>";
		Document doc = new Document();
		try {
			byte[] content2 = source2.getBytes();
			Files.write(path2, content2, StandardOpenOption.CREATE);
			System.out.printf("test10: Wrote to %s\n", path2);
			byte[] content1 = source1.getBytes();
			doc.load(content1);
			int end = checkDocument("test10", doc, content1);
			System.out.printf("test10: content.length=%d actual=%d\n", content1.length, end);
			for (String message : doc.warnings()) {
				System.out.printf("test10: WARN: %s\n", message);
			}
			for (String message : doc.information()) {
				System.out.printf("test10: INFO: %s\n", message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if (Files.deleteIfExists(path2)) {
					System.out.printf("test10: Deleted %s\n", path2);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int checkDocument(String header, Document doc, byte[] expected) {
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

	private int printElement(String header, Element element, int start) {
		List<Node> nodeList = element.nodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			int end = start + node.sequence().length;
			System.out.printf("%s[%d] %d %d %s\n", header, i, start, end, sprint(node));
			if (node.type == Node.ELEMENT) {
				assertEquals(end, printElement(String.format("%s[%d]", header, i), (Element)node, start));
			}
			start = end;
		}
		return start;
	}

	private int printDocumentTypeDeclaration(String header, DocumentTypeDeclaration dtd, int start) {
		Node[] nodeList = dtd.layout;
		for (int i = 0; i < nodeList.length; i++) {
			Node node = nodeList[i];
			int end = start + node.sequence().length;
			System.out.printf("%s[%d] %d %d %s\n", header, i, start, end, sprint(node));
			start = end;
		}
		for (int i = 0; i < dtd.declarations.length; i++) {
			printDeclaration(header, dtd.declarations[i]);
		}
		return start;
	}

	private String sprint(Node node) {
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
