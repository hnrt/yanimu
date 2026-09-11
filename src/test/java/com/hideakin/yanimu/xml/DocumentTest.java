package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.hideakin.yanimu.xml.internal.DebugHelper;

import static com.hideakin.yanimu.util.TestHelper.checkDocument;
import static com.hideakin.yanimu.util.TestHelper.start;
import static com.hideakin.yanimu.util.TestHelper.finish;
import static com.hideakin.yanimu.util.TestHelper.print;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@BeforeAll
	static void initAll() {
		start(DocumentTest.class);
	}

	@AfterAll
	static void tearDownAll() {
		finish(DocumentTest.class);
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
	void test101() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(new ByteArrayInputStream(source.getBytes()));
			assertEquals("1.0", doc.xml().version);
			assertEquals("Hello, world!", doc.root().innerText());
			boolean result = doc.root().empty();
			assertEquals(false, result);
			byte[] content = source.getBytes();
			int end = checkDocument("doc", doc, content);
			print("content.length=%d actual=%d", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test102() {
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n"
				+ "<!DOCTYPE greeting SYSTEM \"hello.dtd\">\r\n"
				+ "<greeting abc:xyz='123'>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes();
			doc.load(content);
			assertEquals("UTF-8", doc.xml().encoding);
			int end = checkDocument("doc", doc, content);
			print("content.length=%d actual=%d", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test103() {
		String source = "<?xml version=\"1.0\" standalone='yes' ?>\r\n"
				+ "<!DOCTYPE greeting [\r\n"
				+ "  <!ELEMENT greeting (#PCDATA)>\r\n"
				+ "]>\r\n"
				+ "<greeting abc=\"&lt;&amp;x&apos;&quot;&gt;\" xyz=\"&#x41;&#x42;&#x43;\" >Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes();
			doc.load(content);
			assertEquals("yes", doc.xml().standalone);
			int end = checkDocument("doc", doc, content);
			print("content.length=%d actual=%d", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test104() {
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
			assertEquals("no", doc.xml().standalone);
			assertEquals("Hello, world!", doc.root().innerText());
			int end = checkDocument("doc", doc, content);
			print("content.length=%d end=%d", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test105() {
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
			int end = checkDocument("doc", doc, content);
			print("content.length=%d end=%d", content.length, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test106() {
		String source = "\uFEFF<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			byte[] content = source.getBytes(StandardCharsets.UTF_8);
			doc.load(content);
			assertEquals("Hello, world!", doc.root().innerText());
			int end = checkDocument("doc", doc, Arrays.copyOfRange(content, 3, content.length));
			print("content.length=%d-3=%d actual=%d", content.length, content.length - 3, end);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test107() {
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
	void test108() {
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
			int end = checkDocument("doc", doc, content);
			print("content.length=%d actual=%d", content.length, end);
			assertEquals(244, doc.offset(elements.get(0)));
			assertEquals(258, doc.offset(elements.get(0).endTag()));
			assertEquals(271, doc.offset(elements.get(1)));
			assertEquals(286, doc.offset(elements.get(1).endTag()));
			assertEquals(299, doc.offset(elements.get(2)));
			assertEquals(315, doc.offset(elements.get(2).endTag()));
			assertEquals(1, doc.toLineNumber(0));
			assertEquals(3, doc.toLineNumber(50));
			assertEquals(6, doc.toLineNumber(100));
			assertEquals(10, doc.toLineNumber(doc.offset(elements.get(0))));
			assertEquals(11, doc.toLineNumber(doc.offset(elements.get(1))));
			assertEquals(12, doc.toLineNumber(doc.offset(elements.get(2))));
			assertEquals(1, doc.toColumnNumber(0));
			assertEquals(5, doc.toColumnNumber(50));
			assertEquals(1, doc.toColumnNumber(100));
			assertEquals(5, doc.toColumnNumber(doc.offset(elements.get(0))));
			assertEquals(5, doc.toColumnNumber(doc.offset(elements.get(1))));
			assertEquals(5, doc.toColumnNumber(doc.offset(elements.get(2))));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test109() {
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
		DebugHelper.enabled ^= DebugHelper.FLAG_LEXER;
		Document doc = new Document();
		ParseResult result = new ParseResult();
		try {
			byte[] content = source.getBytes(StandardCharsets.UTF_8);
			doc.load(content, result);
			int end = checkDocument("doc", doc, content);
			print("content.length=%d actual=%d", content.length, end);
			List<Element> authors = doc.root().getElements("author");
			assertEquals("Unknown Author", authors.get(0).innerText());
			assertEquals("Hanako", authors.get(1).innerText());
			for (ParseResult.Message warning : result.warnings()) {
				print("WARN: %s", warning.message);
			}
			for (ParseResult.Message information : result.information()) {
				print("INFO: %s", information.message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			DebugHelper.enabled ^= DebugHelper.FLAG_LEXER;
		}
	}

	@Test
	void test110() {
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
		ParseResult result = new ParseResult();
		try {
			byte[] content2 = source2.getBytes();
			Files.write(path2, content2, StandardOpenOption.CREATE);
			print("Wrote to %s", path2);
			byte[] content1 = source1.getBytes();
			doc.load(content1);
			int end = checkDocument("doc", doc, content1);
			print("content.length=%d actual=%d", content1.length, end);
			for (ParseResult.Message warning : result.warnings()) {
				print("WARN: %s", warning.message);
			}
			for (ParseResult.Message information : result.information()) {
				print("INFO: %s", information.message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if (Files.deleteIfExists(path2)) {
					print("Deleted %s", path2);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
