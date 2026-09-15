package com.ad287483.japanese_input.impl;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App and SkkDictionaryEngine.
 */
class AppTest {

	@Test
	void testApp() {
		assertTrue( true );
	}

	@Test
	void testDictionaryCandidates() {
		SkkDictionaryEngine engine = new SkkDictionaryEngine();
		
		// 「わる」の変換テスト
		List<String> waruCandidates = engine.getCandidates("わる");
		assertNotNull(waruCandidates);
		assertTrue(waruCandidates.contains("悪"), "「わる」の候補に「悪」が含まれること");

		// 「わら」の変換テスト (送りあり笑 + 送りなし藁)
		List<String> waraCandidates = engine.getCandidates("わら");
		assertNotNull(waraCandidates);
		assertTrue(waraCandidates.contains("笑"), "「わら」の候補に「笑」が含まれること");
		assertTrue(waraCandidates.contains("藁"), "「わら」の候補に「藁」が含まれること");

		// 「わた」の変換テスト (渡, 亘, 亙, 渉, 綿)
		List<String> wataCandidates = engine.getCandidates("わた");
		assertNotNull(wataCandidates);
		assertTrue(wataCandidates.contains("渡"), "「わた」の候補に「渡」が含まれること");
		assertTrue(wataCandidates.contains("綿"), "「わた」の候補に「綿」が含まれること");
	}
}
