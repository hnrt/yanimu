package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.hideakin.yanimu.xml.util.DebugHelper;
import com.hideakin.yanimu.xml.util.FormatHelper;

import static com.hideakin.yanimu.util.TestHelper.checkDocument;
import static com.hideakin.yanimu.util.TestHelper.finish;
import static com.hideakin.yanimu.util.TestHelper.start;
import static com.hideakin.yanimu.util.TestHelper.print;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

public class ElementTest {

	@BeforeAll
	static void initAll() {
		start(ElementTest.class);
	}

	@AfterAll
	static void tearDownAll() {
		finish(ElementTest.class);
	}

	@BeforeEach
    void beforeEach(TestInfo info) {
		start(info);
    }

	@AfterEach
	void afterEach(TestInfo info) {
		finish(info);
	}

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
			assertEquals(null, doc.root().attribute(-1));
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
			int end1 = checkDocument("BEFORE", doc, content1);
			print("content.length=%d actual=%d", content1.length, end1);
			assertEquals(Node.STAG, doc.root().startTag().type);
			boolean result = doc.root().empty();
			assertEquals(true, result);
			assertEquals(Node.EETAG, doc.root().startTag().type);
			int end2 = checkDocument("AFTER", doc, content2);
			print("content.length=%d actual=%d", content2.length, end2);
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
			int end1 = checkDocument("BEFORE", doc, content1);
			print("content.length=%d actual=%d", content1.length, end1);
			assertEquals(Node.STAG, doc.root().startTag().type);
			boolean result = doc.root().empty();
			assertEquals(true, result);
			assertEquals(Node.EETAG, doc.root().startTag().type);
			boolean result2 = doc.root().empty();
			assertEquals(true, result2);
			boolean result3 = doc.root().empty();
			assertEquals(true, result3);
			int end2 = checkDocument("AFTER", doc, content2);
			print("content.length=%d actual=%d", content2.length, end2);
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
			int end1 = checkDocument("BEFORE", doc, content1);
			print("content.length=%d actual=%d", content1.length, end1);
			Document sup = new Document();
			sup.load("<X><hello/></X>".getBytes());
			doc.root().add(sup.root().remove(0));
			FormatHelper.indent(doc);
			print("root=%d", doc.root().sequence().length);
			int end2 = checkDocument("AFTER", doc, content2);
			print("content.length=%d actual=%d", content2.length, end2);
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
			int end1 = checkDocument("BEFORE", doc, content1);
			print("content.length=%d actual=%d", content1.length, end1);
			Document sup = new Document();
			sup.load("<X><hello>WOW!</hello></X>".getBytes());
			doc.root().add(sup.root().remove(0));
			FormatHelper.indent(doc);
			int end2 = checkDocument("AFTER", doc, content2);
			print("content.length=%d actual=%d", content2.length, end2);
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
			int end1 = checkDocument("BEFORE", doc, content1);
			print("content.length=%d actual=%d", content1.length, end1);
			FormatHelper.indent(doc);
			int end2 = checkDocument("AFTER", doc, content2);
			print("content.length=%d actual=%d", content2.length, end2);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest001() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.addAttribute("abc", "xyz");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc=\"xyz\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest002() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\n/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.addAttribute("abc", "xyz");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc=\"xyz\"\r\n/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest003() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting>Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.addAttribute("abc1", "xyz1");
			element.addAttribute("abc2", "xyz2");
			element.addAttribute("abc3", "xyz3");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc1=\"xyz1\" abc2=\"xyz2\" abc3=\"xyz3\">", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest004() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting  abc0=\"xyz0\"   />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.addAttribute("abc1", "xyz1");
			element.addAttribute("abc2", "xyz2");
			element.addAttribute("abc3", "xyz3");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting  abc0=\"xyz0\" abc1=\"xyz1\" abc2=\"xyz2\" abc3=\"xyz3\"   />", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest005() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"   />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.addAttribute(0, "abc1", "xyz1");
			element.addAttribute(1, "abc1.5", "xyz1.5");
			element.addAttribute(3, "abc8", "xyz8");
			element.addAttribute(5, "abc9", "xyz9");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc1=\"xyz1\" abc1.5=\"xyz1.5\"\r\nabc2=\"xyz2\" abc8=\"xyz8\" abc8.1=\"xyz8.1\" abc9=\"xyz9\"   />", element.startTag().toString());
			assertEquals("xyz1", element.attribute(0));
			assertEquals("xyz1.5", element.attribute(1));
			assertEquals("xyz2", element.attribute(2));
			assertEquals("xyz8", element.attribute(3));
			assertEquals("xyz8.1", element.attribute(4));
			assertEquals("xyz9", element.attribute(5));
			assertEquals("xyz1", element.attribute("abc1"));
			assertEquals("xyz1.5", element.attribute("abc1.5"));
			assertEquals("xyz2", element.attribute("abc2"));
			assertEquals("xyz8", element.attribute("abc8"));
			assertEquals("xyz8.1", element.attribute("abc8.1"));
			assertEquals("xyz9", element.attribute("abc9"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest050() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			assertEquals(0, element.attributeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest051() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting ></greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			assertEquals(0, element.attributeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest052() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting x=\"1\"></greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			assertEquals(1, element.attributeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest053() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting x=\"1\" />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			assertEquals(1, element.attributeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest054() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting x=\"1\" y=\"2\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			assertEquals(2, element.attributeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest055() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting x=\"1\" y=\"2\" ></greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			assertEquals(2, element.attributeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest056() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting x=\"1\" y=\"2\" z=\"3\"></greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			assertEquals(3, element.attributeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest057() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting x=\"1\" y=\"2\" z=\"3\" />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			assertEquals(3, element.attributeCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest101() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.setAttribute(0, "abc1", "xyz1");
			element.setAttribute(1, "abc1.5", "xyz1.5");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting\r\nabc1=\"xyz1\" abc1.5=\"xyz1.5\"/>", element.startTag().toString());
			assertEquals("xyz1", element.attribute(0));
			assertEquals("xyz1.5", element.attribute(1));
			assertEquals("xyz1", element.attribute("abc1"));
			assertEquals("xyz1.5", element.attribute("abc1.5"));
			assertEquals(null, element.attribute("abc2"));
			assertEquals("opq", element.attribute("abc2", "opq"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest102() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.setAttribute("abc2", "xyz22");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting\r\nabc2 = \"xyz22\" abc8.1=\"xyz8.1\"/>", element.startTag().toString());
			assertEquals("xyz22", element.attribute(0));
			assertEquals("xyz8.1", element.attribute(1));
			assertEquals("xyz22", element.attribute("abc2"));
			assertEquals("xyz8.1", element.attribute("abc8.1"));
			assertEquals(null, element.attribute("abc3"));
			assertEquals("opq", element.attribute("abc3", "opq"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest103() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.setAttribute("abc8.1", "xyzzy8.1");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyzzy8.1\"/>", element.startTag().toString());
			assertEquals("xyz2", element.attribute(0));
			assertEquals("xyzzy8.1", element.attribute(1));
			assertEquals("xyz2", element.attribute("abc2"));
			assertEquals("xyzzy8.1", element.attribute("abc8.1"));
			assertEquals(null, element.attribute("abc3"));
			assertEquals("opq", element.attribute("abc3", "opq"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest104() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.setAttribute("abc9", "xyzzy9");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyz8.1\" abc9=\"xyzzy9\"/>", element.startTag().toString());
			assertEquals("xyz2", element.attribute(0));
			assertEquals("xyz8.1", element.attribute(1));
			assertEquals("xyzzy9", element.attribute(2));
			assertEquals("xyz2", element.attribute("abc2"));
			assertEquals("xyz8.1", element.attribute("abc8.1"));
			assertEquals("xyzzy9", element.attribute("abc9"));
			assertEquals(null, element.attribute(3));
			assertEquals("lmn", element.attribute(3, "lmn"));
			assertEquals(null, element.attribute("abc3"));
			assertEquals("opq", element.attribute("abc3", "opq"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest201() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.removeAllAttributes();
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest202() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"  >Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.removeAllAttributes();
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting  >", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest203() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"  />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.removeAllAttributes();
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting  />", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest204() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\">Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.removeAllAttributes();
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest301() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE  %s", DebugHelper.toString(element.startTag()));
			Node node = element.removeAttribute(0);
			print("AFTER   %s", DebugHelper.toString(element.startTag()));
			print("REMOVED %s", DebugHelper.toString(node));
			assertEquals("<greeting abc8.1=\"xyz8.1\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest302() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			Node node = element.removeAttribute(1);
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			print("REMOVED %s", DebugHelper.toString(node));
			assertEquals("<greeting\r\nabc2=\"xyz2\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest303() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE  %s", DebugHelper.toString(element.startTag()));
			Node node = element.removeAttribute(2);
			print("AFTER   %s", DebugHelper.toString(element.startTag()));
			print("REMOVED %s", DebugHelper.toString(node));
			assertEquals(Node.NULL, node.type);
			assertEquals("<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest311() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE  %s", DebugHelper.toString(element.startTag()));
			Node node = element.removeAttribute("abc2");
			print("AFTER   %s", DebugHelper.toString(element.startTag()));
			print("REMOVED %s", DebugHelper.toString(node));
			assertEquals("<greeting abc8.1=\"xyz8.1\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest312() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2 = \"xyz2\" abc8.1 = \"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE  %s", DebugHelper.toString(element.startTag()));
			Node node = element.removeAttribute("abc8.1");
			print("AFTER   %s", DebugHelper.toString(element.startTag()));
			print("REMOVED %s", DebugHelper.toString(node));
			assertEquals("<greeting\r\nabc2 = \"xyz2\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest313() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE  %s", DebugHelper.toString(element.startTag()));
			Node node = element.removeAttribute("xyz");
			print("AFTER   %s", DebugHelper.toString(element.startTag()));
			print("REMOVED %s", DebugHelper.toString(node));
			assertEquals(Node.NULL, node.type);
			assertEquals("<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest901() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.addAttribute("abc", "x\"y\"z");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc=\'x\"y\"z\'/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest902() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.addAttribute("abc", "x\'y\'z");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc=\"x\'y\'z\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest903() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.addAttribute("abc", "\"xy\'z\"");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc=\'\"xy&apos;z\"\'/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest904() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting aaa=\'zzz\' />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.setAttribute(0, "abc", "\"xy\'z\"");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc=\'\"xy&apos;z\"\' />", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void attributeTest905() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting aaa=\'zzz\'/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			Element element = doc.root();
			print("BEFORE %s", DebugHelper.toString(element.startTag()));
			element.setAttribute(0, "abc", "The quick brown \"fox\" jumps over the \'lazy\' dog.");
			print("AFTER  %s", DebugHelper.toString(element.startTag()));
			assertEquals("<greeting abc=\"The quick brown &quot;fox&quot; jumps over the \'lazy\' dog.\"/>", element.startTag().toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
