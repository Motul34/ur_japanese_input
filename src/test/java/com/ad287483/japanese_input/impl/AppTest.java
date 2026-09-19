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
		assertTrue(true);
	}

	@Test
	void testSingleton() {
		SkkDictionaryEngine engine1 = SkkDictionaryEngine.getInstance();
		SkkDictionaryEngine engine2 = SkkDictionaryEngine.getInstance();
		assertNotNull(engine1);
		assertSame(engine1, engine2, "SkkDictionaryEngine は同一のSingletonインスタンスであること");
	}

	@Test
	void testDictionaryCandidates() {
		SkkDictionaryEngine engine = SkkDictionaryEngine.getInstance();

		// 名詞の変換テスト
		List<String> tokyoCandidates = engine.getCandidates("とうきょう");
		assertNotNull(tokyoCandidates);
		assertTrue(tokyoCandidates.contains("東京"), "「とうきょう」の候補に「東京」が含まれること");

		List<String> teishiCandidates = engine.getCandidates("ていし");
		assertNotNull(teishiCandidates);
		assertTrue(teishiCandidates.contains("停止"), "「ていし」の候補に「停止」が含まれること");

		List<String> kakuninCandidates = engine.getCandidates("かくにん");
		assertNotNull(kakuninCandidates);
		assertTrue(kakuninCandidates.contains("確認"), "「かくにん」の候補に「確認」が含まれること");

		List<String> nihonCandidates = engine.getCandidates("にほん");
		assertNotNull(nihonCandidates);
		assertTrue(nihonCandidates.contains("日本"), "「にほん」の候補に「日本」が含まれること");

		// 送りあり動詞基本形の展開テスト
		List<String> tsukuruCandidates = engine.getCandidates("つくる");
		assertNotNull(tsukuruCandidates);
		assertTrue(tsukuruCandidates.contains("作る"), "「つくる」の候補に「作る」が含まれること");

		List<String> hashiruCandidates = engine.getCandidates("はしる");
		assertNotNull(hashiruCandidates);
		assertTrue(hashiruCandidates.contains("走る"), "「はしる」の候補に「走る」が含まれること");

		List<String> ugokuCandidates = engine.getCandidates("うごく");
		assertNotNull(ugokuCandidates);
		assertTrue(ugokuCandidates.contains("動く"), "「うごく」の候補に「動く」が含まれること");

		List<String> owaruCandidates = engine.getCandidates("おわる");
		assertNotNull(owaruCandidates);
		assertTrue(owaruCandidates.contains("終わる") || owaruCandidates.contains("終る"), "「おわる」の候補に「終わる」または「終る」が含まれること");

		List<String> nomuCandidates = engine.getCandidates("のむ");
		assertNotNull(nomuCandidates);
		assertTrue(nomuCandidates.contains("飲む"), "「のむ」の候補に「飲む」が含まれること");

		List<String> warauCandidates = engine.getCandidates("わらう");
		assertNotNull(warauCandidates);
		assertTrue(warauCandidates.contains("笑う"), "「わらう」の候補に「笑う」が含まれること");

		// 語幹単体でも候補が得られることの確認
		List<String> tsukuCandidates = engine.getCandidates("つく");
		assertNotNull(tsukuCandidates);
		assertTrue(tsukuCandidates.contains("作"), "「つく」の候補に「作」が含まれること");
	}

	@Test
	void testRomajiConversionWithLongVowel() {
		// 基本のローマ字かな変換
		assertEquals("か", RomajiToKanaConverter.convert("ka"));
		assertEquals("すし", RomajiToKanaConverter.convert("sushi"));
		assertEquals("きって", RomajiToKanaConverter.convert("kitte"));

		// 長音記号「ー」のテスト
		assertEquals("ー", RomajiToKanaConverter.convert("-"));
		assertEquals("らーめん", RomajiToKanaConverter.convert("ra-menn"));
		assertEquals("こーひー", RomajiToKanaConverter.convert("ko-hi-"));

		// カタカナ変換での長音記号の維持
		String katakana = RomajiToKanaConverter.convertToKatakana(RomajiToKanaConverter.convert("ra-menn"));
		assertEquals("ラーメン", katakana);
		String coffee = RomajiToKanaConverter.convertToKatakana(RomajiToKanaConverter.convert("ko-hi-"));
		assertEquals("コーヒー", coffee);
	}

	@Test
	void testPerformanceAndMemory() {
		long start = System.currentTimeMillis();
		SkkDictionaryEngine engine = SkkDictionaryEngine.getInstance();
		long duration = System.currentTimeMillis() - start;

		// 2回目以降は即座に取得できること（Singleton検証）
		assertTrue(duration < 50, "Singleton取得は即時（50ms未満）であること");

		// 10,000回検索パフォーマンス
		long searchStart = System.nanoTime();
		for (int i = 0; i < 10000; i++) {
			engine.getCandidates("とうきょう");
		}
		long searchDurationUs = (System.nanoTime() - searchStart) / 1000;
		assertTrue(searchDurationUs / 10000.0 < 50.0, "1回の検索時間は50マイクロ秒未満であること");
	}
}
