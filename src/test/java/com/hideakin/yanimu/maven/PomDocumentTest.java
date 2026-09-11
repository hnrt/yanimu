package com.hideakin.yanimu.maven;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.hideakin.yanimu.model.SemanticVersion;
import com.hideakin.yanimu.xml.Node;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import static com.hideakin.yanimu.util.TestHelper.finish;
import static com.hideakin.yanimu.util.TestHelper.start;
import static com.hideakin.yanimu.util.TestHelper.print;

public class PomDocumentTest {

	@BeforeAll
	static void initAll() {
		start(PomDocumentTest.class);
	}

	@AfterAll
	static void tearDownAll() {
		finish(PomDocumentTest.class);
	}

	@BeforeEach
    void beforeEach(TestInfo info) {
		start(info);
    }

	@AfterEach
	void afterEach(TestInfo info) {
		finish(info);
	}

	public static class StartEnd {

		public final int start;
		public final int end;

		public StartEnd(int start, int end) {
			this.start = start;
			this.end = end;
		}

	}

	@Test
	void test001() {
		try {
			URL url = getClass().getResource("/com/hideakin/yanimu/maven/test001.xml");
			Path path = Path.of(url.toURI());
			print("path=%s", path);
			PomDocument pom = PomDocument.of(path);
			pom.load();
			for (Dependency dep : pom.dependencies().values()) {
				Dependency dep1 = null;
				SemanticVersion sv1 = null, sv2 = null;
				int start1 = -1, end1 = -1, start2 = -1, end2 = -1;
				byte[] content1 = pom.sequence();
				String v1 = dep.version();
				print("g=%s a=%s v=%s",
						dep.groupId(),
						dep.artifactId(),
						v1);
				if (v1 != null) {
					dep1 = dep;
				} else {
					dep1 = pom.dependencyManagement().get(dep.ga());
					if (dep1 != null) {
						v1 = dep1.version();
						print("dependencyManagement v=%s",
								v1);
					} else {
						PomDocument pomBom = pom.dependencyManagement().pomDocument(dep.ga());
						if (pomBom != null) {
							print("POM/IMPORT g=%s a=%s v=%s",
									pomBom.groupId(),
									pomBom.artifactId(),
									pomBom.version());
							dep1 = pom.dependencyManagement().get(pomBom.ga());
							v1 = dep1.version();
						} else {
							dep1 = dep;
						}
					}
				}
				MetadataDocument meta = MetadataDocument.of(dep1.groupId(), dep1.artifactId());
				try {
					meta.load();
				} catch (Exception e) {
				}
				if (meta.root() == null) {
					try {
						meta.load(pom.repositories());
					} catch (Exception e) {
						e.printStackTrace();
						fail("Failed: " + e.getMessage());
					}
				}
				assertEquals(true, meta.root() != null);
				print("%s l=%s r=%s",
						meta.path(),
						meta.latest(),
						meta.release());
				sv2 = SemanticVersion.of(meta.release());
				Node node1 = null, node2 = null;
				if (v1 != null) {
					sv1 = SemanticVersion.of(pom.translate(v1));
					assertEquals(true, sv1.compareTo(sv2) < 0);
					String key = pom.referencingPropertyKey(v1);
					if (key != null) {
						if (pom.getElement("/project/properties/" + key) != null) {
							node1 = pom.getElement("/project/properties/" + key).child(0);
							start1 = pom.offset(node1);
							end1 = start1 + node1.length();
						}
						pom.setProperty(key, sv2.toString());
						node2 = pom.getElement("/project/properties/" + key).child(0);
						start2 = pom.offset(node2);
						end2 = start2 + node2.length();
					} else if (dep1.element().getElement("/version") != null) {
						node1 = dep1.element().getElement("/version").child(0);
						start1 = pom.offset(node1);
						end1 = start1 + node1.length();
						dep1.setVersion(sv2.toString());
						node2 = dep1.element().getElement("/version").child(0);
						start2 = pom.offset(node2);
						end2 = start2 + node2.length();
					} else {
						dep1.setVersion(sv2.toString());
					}
				} else {
					dep1.setVersion(sv2.toString());
				}
				print("%d,%d (%s) ==> %d,%d (%s)",
						start1,
						end1,
						sv1 != null ? sv1 : "?",
						start2,
						end2,
						sv2.toString());
				byte[] content2 = pom.sequence();
				assertEquals(start1, start2);
				assertArrayEquals(Arrays.copyOfRange(content1, 0, start1), Arrays.copyOfRange(content2, 0, start2));
				assertEquals(content1.length - end1, content2.length - end2);
				assertArrayEquals(Arrays.copyOfRange(content1, end1, content1.length), Arrays.copyOfRange(content2, end2, content2.length));
				String x1 = new String(Arrays.copyOfRange(content1, start1, end1));
				String x2 = new String(Arrays.copyOfRange(content2, start2, end2));
				assertEquals(sv1.toString(), x1);
				assertEquals(sv2.toString(), x2);
			}
			print("%s", new String(pom.sequence(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed: " + e.getMessage());
		}
	}

}
