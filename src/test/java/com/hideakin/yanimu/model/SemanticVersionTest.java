package com.hideakin.yanimu.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SemanticVersionTest {

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
			System.out.printf("test001:BEFORE[%d]=%s\n", i, s);
			SemanticVersion sv = SemanticVersion.of(s);
			System.out.printf("test001:BEFORE[%d] major=%d\n", i, sv.major());
			System.out.printf("test001:BEFORE[%d] minor=%d\n", i, sv.minor());
			System.out.printf("test001:BEFORE[%d] patch=%d\n", i, sv.minor());
			List<Object> pr = sv.preRelease();
			if (pr != null) {
				int j = 0;
				for (Object obj : pr) {
					if (obj instanceof Long n) {
						System.out.printf("test001:BEFORE[%d] pr[%d]=Long %d\n", i, j, n);
					} else if (obj instanceof String t) {
						System.out.printf("test001:BEFORE[%d] pr[%d]=String \"%s\"\n", i, j, t);
					} else {
						System.out.printf("test001:BEFORE[%d] pr[%d]=(UNKNOWN)\n", i, j);
					}
					j++;
				}
			}
			if (sv.build() != null) {
				System.out.printf("test001:BEFORE[%d] build=%s\n", i, sv.build());
			}
			i++;
		}
		i = 0;
		for (SemanticVersion sv : sorted) {
			System.out.printf("test001:AFTER[%d]=%s\n", i, sv);
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
