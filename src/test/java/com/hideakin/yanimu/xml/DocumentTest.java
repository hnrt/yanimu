package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
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
			doc.load(source.getBytes());
			assertEquals("yes", doc.standalone());
			assertEquals("Hello, world!", doc.root().innerText());
			assertEquals("<&x\'\">", doc.root().attribute("abc"));
			assertEquals("ABC", doc.root().attribute("xyz"));
			assertEquals("<&x\'\">", doc.root().attribute(0));
			assertEquals("ABC", doc.root().attribute(1));
			assertEquals("ABC", doc.root().attribute(-1));
			assertEquals(null, doc.root().attribute("opq"));
			assertEquals(null, doc.root().attribute(2));
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
			doc.load(source.getBytes());
			assertEquals("no", doc.standalone());
			assertEquals("Hello, world!", doc.root().innerText());
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
			doc.load(source.getBytes());
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
			doc.load(source.getBytes(StandardCharsets.UTF_8));
			assertEquals("Hello, world!", doc.root().innerText());
			print("test6", doc.layout());
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
			doc.load(source.getBytes());
			List<Element> elements = doc.root().getElements("test");
			assertEquals(3, elements.size());
			assertEquals("2", elements.get(0).attribute("id"));
			assertEquals("3", elements.get(1).attribute("id"));
			assertEquals("4", elements.get(2).attribute("id"));
			assertEquals("2", elements.get(0).innerText());
			assertEquals("30", elements.get(1).innerText());
			assertEquals("400", elements.get(2).innerText());
			assertEquals("void func(int x) {\n\treturn x < 100 ? x * 4 : x * 2;\n}//<>&bogus;", doc.root().getElements("code").get(0).innerText());
			print("test8", doc.layout());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void print(String header, Node[] tokens) {
		for (Node token : tokens) {
			if (token.code == Node.S) {
				System.out.printf("%s:", header);
				for (byte c : token.sequence) {
					System.out.printf(" %s",
							c == ' ' ? "SP" :
							c == 9 ? "HT" :
							c == 10 ? "LF" :
							c == 13 ? "CR" :
							"?");
				}
				System.out.printf("\n");
			} else if (token.code == Node.ELEMENT) {
				System.out.printf("%s: %s\n", header, token);
				Element element = (Element)token;
				if (element.endLayout != null) {
					print(header + ":ELEMENT:S", element.startLayout);
					print(header + ":ELEMENT:C", element.children);
					print(header + ":ELEMENT:E", element.endLayout);
				}
			} else if (token.code == Node.CHAR_DATA) {
				System.out.printf("%s: %s\n", header, token.toString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n").replaceAll("\t", "\\t"));
			} else {
				System.out.printf("%s: %s\n", header, token);
			}
		}
	}

}
