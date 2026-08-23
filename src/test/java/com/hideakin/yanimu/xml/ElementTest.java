package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

public class ElementTest {

	@Test
	void test01() {
		String source = "<?xml version=\"1.0\"?>\n"
				+ "<communication>\n"
				+ "  <languages>\n"
				+ "    <language id='1' multibyte='true'>Japanese</language>\n"
				+ "    <language id='2' multibyte='false'>English</language>\n"
				+ "  </languages>\n"
				+ "  <communication>\n"
				+ "    <languages>\n"
				+ "      <language id='3' multibyte='true'>Japanese</language>\n"
				+ "      <language id='4' multibyte='false'>English</language>\n"
				+ "      <language id='5' multibyte='false'>French</language>\n"
				+ "      <language id='6' multibyte='false'>German</language>\n"
				+ "    </languages>\n"
				+ "    <communication>\n"
				+ "      <languages>\n"
				+ "        <language id='7' multibyte='true'>Japanese</language>\n"
				+ "        <languages>\n"
				+ "          <language id='8' multibyte='true'>Japanese</language>\n"
				+ "          <language id='9' multibyte='false'>English</language>\n"
				+ "          <language id='10' multibyte='false'>French</language>\n"
				+ "          <language id='11' multibyte='false'>German</language>\n"
				+ "          <language id='12' multibyte='false'>Spanish</language>\n"
				+ "        </languages>\n"
				+ "      </languages>\n"
				+ "    </communication>\n"
				+ "  </communication>\n"
				+ "</communication>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			List<Element> list1 = doc.getElements("/communication/languages/language");
			assertEquals("1", list1.get(0).attribute("id"));
			assertEquals("2", list1.get(1).attribute("id"));
			assertEquals(2, list1.size());
			List<Element> list2 = doc.getElements("communication/languages/language");
			assertEquals(7, list2.size());
			List<Element> list3 = doc.root().getElements("/communication/languages/language");
			assertEquals(4, list3.size());
			assertEquals("3", list3.get(0).attribute("id"));
			assertEquals("4", list3.get(1).attribute("id"));
			assertEquals("5", list3.get(2).attribute("id"));
			assertEquals("6", list3.get(3).attribute("id"));
			List<Element> list4 = doc.root().getElements("communication/languages/language");
			assertEquals(5, list4.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test02() {
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone='no' ?>\r\n"
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
			assertEquals("43", elements.get(5).attribute("id"));
			assertEquals("421", elements.get(6).attribute("id"));
			assertEquals("422", elements.get(7).attribute("id"));
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
				+ "<greeting abc:xyz=\"&lt;&amp;x&apos;&quot;&gt;\" xyz=\"&#x41;&#x42;&#x43;\" >Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes();
			doc.load(content);
			assertEquals("Hello, world!", doc.root().innerText());
			assertEquals("<&x\'\">", doc.root().attribute("abc:xyz"));
			assertEquals("ABC", doc.root().attribute("xyz"));
			assertEquals("<&x\'\">", doc.root().attribute(0));
			assertEquals("ABC", doc.root().attribute(1));
			assertEquals("ABC", doc.root().attribute(-1));
			assertEquals(null, doc.root().attribute("opq"));
			assertEquals(null, doc.root().attribute(2));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
