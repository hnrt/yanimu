package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;

public class DocumentTest {

	@Test
	void test1() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(new ByteArrayInputStream(source.getBytes()));
			assertEquals("Hello, world!", doc.root().innerText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test2() {
		String source = "<?xml version=\"1.0\"?>\r\n"
				+ "<!DOCTYPE greeting SYSTEM \"hello.dtd\">\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(new ByteArrayInputStream(source.getBytes()));
			assertEquals("Hello, world!", doc.root().innerText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void test3() {
		String source = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n"
				+ "<!DOCTYPE greeting [\r\n"
				+ "  <!ELEMENT greeting (#PCDATA)>\r\n"
				+ "]>\r\n"
				+ "<greeting>Hello, world!</greeting>";
		Document doc = new Document();
		try {
			doc.load(new ByteArrayInputStream(source.getBytes()));
			assertEquals("Hello, world!", doc.root().innerText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
