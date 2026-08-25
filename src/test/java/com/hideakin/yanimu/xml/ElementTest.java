package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.Test;

import static com.hideakin.yanimu.xml.TestHelper.checkDocument;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

public class ElementTest {

	@Test
	void test201() {
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
	void test202() {
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
	void test203() {
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

	@Test
	void test211() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting></greeting>";
		String expectation = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting/>";
		Document doc = new Document();
		try {
			byte[] content1 = source.getBytes();
			byte[] content2 = expectation.getBytes();
			doc.load(content1);
			int end1 = checkDocument("test211BEFORE", doc, content1);
			System.out.printf("test211: content.length=%d actual=%d\n", content1.length, end1);
			assertEquals(Node.STAG, doc.root().startTag().type);
			boolean result = doc.root().empty();
			assertEquals(true, result);
			assertEquals(Node.EETAG, doc.root().startTag().type);
			int end2 = checkDocument("test211AFTER", doc, content2);
			System.out.printf("test211: content.length=%d actual=%d\n", content2.length, end2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test212() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>\r\n  \r\n  </greeting>\r\n";
		String expectation = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting/>\r\n";
		Document doc = new Document();
		try {
			byte[] content1 = source.getBytes();
			byte[] content2 = expectation.getBytes();
			doc.load(content1);
			int end1 = checkDocument("test212BEFORE", doc, content1);
			System.out.printf("test212: content.length=%d actual=%d\n", content1.length, end1);
			assertEquals(Node.STAG, doc.root().startTag().type);
			boolean result = doc.root().empty();
			assertEquals(true, result);
			assertEquals(Node.EETAG, doc.root().startTag().type);
			boolean result2 = doc.root().empty();
			assertEquals(true, result2);
			boolean result3 = doc.root().empty();
			assertEquals(true, result3);
			int end2 = checkDocument("test212AFTER", doc, content2);
			System.out.printf("test212: content.length=%d actual=%d\n", content2.length, end2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test213() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting></greeting>";
		String expectation = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>\r\n"
				+ "  <hello/>\r\n"
				+ "</greeting>";
		Document doc = new Document();
		try {
			byte[] content1 = source.getBytes();
			byte[] content2 = expectation.getBytes();
			doc.load(content1);
			int end1 = checkDocument("test213BEFORE", doc, content1);
			System.out.printf("test213: content.length=%d actual=%d\n", content1.length, end1);
			Document sup = new Document();
			sup.load("<X><hello/></X>".getBytes());
			doc.root().addChild(sup.root().removeChild(0));
			System.out.printf("test213: root=%d\n", doc.root().sequence().length);
			int end2 = checkDocument("test13AFTER", doc, content2);
			System.out.printf("test213: content.length=%d actual=%d\n", content2.length, end2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test214() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting/>";
		String expectation = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>\r\n  <hello>WOW!</hello>\r\n</greeting>";
		Document doc = new Document();
		try {
			byte[] content1 = source.getBytes();
			byte[] content2 = expectation.getBytes();
			doc.load(content1);
			int end1 = checkDocument("test214BEFORE", doc, content1);
			System.out.printf("test214: content.length=%d actual=%d\n", content1.length, end1);
			Document sup = new Document();
			sup.load("<X><hello>WOW!</hello></X>".getBytes());
			doc.root().addChild(sup.root().removeChild(0));
			int end2 = checkDocument("test214AFTER", doc, content2);
			System.out.printf("test214: content.length=%d actual=%d\n", content2.length, end2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test215() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting><hello/><hello>WOW!</hello><hi><ya>Oops!</ya></hi></greeting>";
		String expectation = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>\r\n"
				+ "  <hello/>\r\n"
				+ "  <hello>WOW!</hello>\r\n"
				+ "  <hi>\r\n"
				+ "    <ya>Oops!</ya>\r\n"
				+ "  </hi>\r\n"
				+ "</greeting>";
		Document doc = new Document();
		try {
			byte[] content1 = source.getBytes();
			byte[] content2 = expectation.getBytes();
			doc.load(content1);
			int end1 = checkDocument("test215BEFORE", doc, content1);
			System.out.printf("test215: content.length=%d actual=%d\n", content1.length, end1);
			doc.indent();
			int end2 = checkDocument("test215AFTER", doc, content2);
			System.out.printf("test215: content.length=%d actual=%d\n", content2.length, end2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
