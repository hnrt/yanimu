package com.hideakin.yanimu.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static com.hideakin.yanimu.util.TestHelper.finish;
import static com.hideakin.yanimu.util.TestHelper.start;
import static com.hideakin.yanimu.util.TestHelper.print;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SemanticVersionTest {

	@BeforeAll
	static void initAll() {
		start(SemanticVersionTest.class);
	}

	@AfterAll
	static void tearDownAll() {
		finish(SemanticVersionTest.class);
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
	void test001() {
		List<String> source = new ArrayList<>(List.of(
				"1.0.0-rc.1",
				"1.0.0-alpha.1",
				"1.0.0-beta.2",
				"1.0.0-alpha.beta",
				"1.0.0-beta.11",
				"1.0.0",
				"1.0.0-alpha",
				"1.0.0-beta"));
		List<SemanticVersion> sorted = source
				.stream()
				.map(e -> SemanticVersion.of(e))
				.sorted((a, b) -> a.compareTo(b))
				.toList();
		int i = 0;
		for (String s : source) {
			print("BEFORE[%d]=%s", i, s);
			SemanticVersion sv = SemanticVersion.of(s);
			print("BEFORE[%d] major=%d", i, sv.major());
			print("BEFORE[%d] minor=%d", i, sv.minor());
			print("BEFORE[%d] patch=%d", i, sv.minor());
			List<Object> pr = sv.preRelease();
			if (pr != null) {
				int j = 0;
				for (Object obj : pr) {
					if (obj instanceof Long n) {
						print("BEFORE[%d] pr[%d]=Long %d", i, j, n);
					} else if (obj instanceof String t) {
						print("BEFORE[%d] pr[%d]=String \"%s\"", i, j, t);
					} else {
						print("BEFORE[%d] pr[%d]=(UNKNOWN)", i, j);
					}
					j++;
				}
			}
			if (sv.build() != null) {
				print("BEFORE[%d] build=%s", i, sv.build());
			}
			i++;
		}
		i = 0;
		for (SemanticVersion sv : sorted) {
			print("AFTER[%d]=%s", i, sv);
			i++;
		}
		assertEquals("1.0.0-alpha", sorted.get(0).toString());
		assertEquals("1.0.0-alpha.1", sorted.get(1).toString());
		assertEquals("1.0.0-alpha.beta", sorted.get(2).toString());
		assertEquals("1.0.0-beta", sorted.get(3).toString());
		assertEquals("1.0.0-beta.2", sorted.get(4).toString());
		assertEquals("1.0.0-beta.11", sorted.get(5).toString());
		assertEquals("1.0.0-rc.1", sorted.get(6).toString());
		assertEquals("1.0.0", sorted.get(7).toString());
		assertEquals(8, sorted.size());
	}

}
