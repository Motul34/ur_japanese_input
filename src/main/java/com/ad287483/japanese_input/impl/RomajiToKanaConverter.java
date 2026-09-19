package com.ad287483.japanese_input.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RomajiToKanaConverter {

    private static final Map<String, String> ROMAJI_MAP = new HashMap<>();
    private static final Set<String> PREFIX_SET = new HashSet<>();

    static {
        // 基本母音
        ROMAJI_MAP.put("a", "あ"); ROMAJI_MAP.put("i", "い"); ROMAJI_MAP.put("u", "う");
        ROMAJI_MAP.put("e", "え"); ROMAJI_MAP.put("o", "お");

        // か行
        ROMAJI_MAP.put("ka", "か"); ROMAJI_MAP.put("ki", "き"); ROMAJI_MAP.put("ku", "く");
        ROMAJI_MAP.put("ke", "け"); ROMAJI_MAP.put("ko", "こ");

        // さ行
        ROMAJI_MAP.put("sa", "さ"); ROMAJI_MAP.put("si", "し"); ROMAJI_MAP.put("shi", "し");
        ROMAJI_MAP.put("su", "す"); ROMAJI_MAP.put("se", "せ"); ROMAJI_MAP.put("so", "そ");

        // た行
        ROMAJI_MAP.put("ta", "た"); ROMAJI_MAP.put("ti", "ち"); ROMAJI_MAP.put("chi", "ち");
        ROMAJI_MAP.put("tu", "つ"); ROMAJI_MAP.put("tsu", "つ");
        ROMAJI_MAP.put("te", "て"); ROMAJI_MAP.put("to", "と");

        // な行
        ROMAJI_MAP.put("na", "な"); ROMAJI_MAP.put("ni", "に"); ROMAJI_MAP.put("nu", "ぬ");
        ROMAJI_MAP.put("ne", "ね"); ROMAJI_MAP.put("no", "の");

        // は行
        ROMAJI_MAP.put("ha", "は"); ROMAJI_MAP.put("hi", "ひ");
        ROMAJI_MAP.put("hu", "ふ"); ROMAJI_MAP.put("fu", "ふ");
        ROMAJI_MAP.put("he", "へ"); ROMAJI_MAP.put("ho", "ほ");

        // ま行
        ROMAJI_MAP.put("ma", "ま"); ROMAJI_MAP.put("mi", "み"); ROMAJI_MAP.put("mu", "む");
        ROMAJI_MAP.put("me", "め"); ROMAJI_MAP.put("mo", "も");

        // や行
        ROMAJI_MAP.put("ya", "や"); ROMAJI_MAP.put("yu", "ゆ"); ROMAJI_MAP.put("yo", "よ");

        // ら行
        ROMAJI_MAP.put("ra", "ら"); ROMAJI_MAP.put("ri", "り"); ROMAJI_MAP.put("ru", "る");
        ROMAJI_MAP.put("re", "れ"); ROMAJI_MAP.put("ro", "ろ");

        // わ行・ん
        ROMAJI_MAP.put("wa", "わ"); ROMAJI_MAP.put("wo", "を");
        ROMAJI_MAP.put("nn", "ん");

        // 濁音
        ROMAJI_MAP.put("ga", "が"); ROMAJI_MAP.put("gi", "ぎ"); ROMAJI_MAP.put("gu", "ぐ");
        ROMAJI_MAP.put("ge", "げ"); ROMAJI_MAP.put("go", "ご");
        ROMAJI_MAP.put("za", "ざ"); ROMAJI_MAP.put("zi", "じ"); ROMAJI_MAP.put("ji", "じ");
        ROMAJI_MAP.put("zu", "ず"); ROMAJI_MAP.put("ze", "ぜ"); ROMAJI_MAP.put("zo", "ぞ");
        ROMAJI_MAP.put("da", "だ"); ROMAJI_MAP.put("di", "ぢ"); ROMAJI_MAP.put("du", "づ");
        ROMAJI_MAP.put("de", "で"); ROMAJI_MAP.put("do", "ど");
        ROMAJI_MAP.put("ba", "ば"); ROMAJI_MAP.put("bi", "び"); ROMAJI_MAP.put("bu", "ぶ");
        ROMAJI_MAP.put("be", "べ"); ROMAJI_MAP.put("bo", "ぼ");

        // 半濁音
        ROMAJI_MAP.put("pa", "ぱ"); ROMAJI_MAP.put("pi", "ぴ"); ROMAJI_MAP.put("pu", "ぷ");
        ROMAJI_MAP.put("pe", "ぺ"); ROMAJI_MAP.put("po", "ぽ");

        // 拗音（きゃ行）
        ROMAJI_MAP.put("kya", "きゃ"); ROMAJI_MAP.put("kyu", "きゅ"); ROMAJI_MAP.put("kyo", "きょ");
        // 拗音（しゃ行）
        ROMAJI_MAP.put("sha", "しゃ"); ROMAJI_MAP.put("shu", "しゅ"); ROMAJI_MAP.put("sho", "しょ");
        ROMAJI_MAP.put("sya", "しゃ"); ROMAJI_MAP.put("syu", "しゅ"); ROMAJI_MAP.put("syo", "しょ");
        // 拗音（ちゃ行）
        ROMAJI_MAP.put("cha", "ちゃ"); ROMAJI_MAP.put("chu", "ちゅ"); ROMAJI_MAP.put("cho", "ちょ");
        ROMAJI_MAP.put("tya", "ちゃ"); ROMAJI_MAP.put("tyu", "ちゅ"); ROMAJI_MAP.put("tyo", "ちょ");
        // 拗音（にゃ行）
        ROMAJI_MAP.put("nya", "にゃ"); ROMAJI_MAP.put("nyu", "にゅ"); ROMAJI_MAP.put("nyo", "にょ");
        // 拗音（ひゃ行）
        ROMAJI_MAP.put("hya", "ひゃ"); ROMAJI_MAP.put("hyu", "ひゅ"); ROMAJI_MAP.put("hyo", "ひょ");
        // 拗音（みゃ行）
        ROMAJI_MAP.put("mya", "みゃ"); ROMAJI_MAP.put("myu", "みゅ"); ROMAJI_MAP.put("myo", "みょ");
        // 拗音（りゃ行）
        ROMAJI_MAP.put("rya", "りゃ"); ROMAJI_MAP.put("ryu", "りゅ"); ROMAJI_MAP.put("ryo", "りょ");
        // 拗音（ぎゃ行）
        ROMAJI_MAP.put("gya", "ぎゃ"); ROMAJI_MAP.put("gyu", "ぎゅ"); ROMAJI_MAP.put("gyo", "ぎょ");
        // 拗音（じゃ行）
        ROMAJI_MAP.put("ja", "じゃ"); ROMAJI_MAP.put("ju", "じゅ"); ROMAJI_MAP.put("jo", "じょ");
        ROMAJI_MAP.put("jya", "じゃ"); ROMAJI_MAP.put("jyu", "じゅ"); ROMAJI_MAP.put("jyo", "じょ");
        // 拗音（ぢゃ行）
        ROMAJI_MAP.put("dya", "ぢゃ"); ROMAJI_MAP.put("dyu", "ぢゅ"); ROMAJI_MAP.put("dyo", "ぢょ");
        // 拗音（びゃ行）
        ROMAJI_MAP.put("bya", "びゃ"); ROMAJI_MAP.put("byu", "びゅ"); ROMAJI_MAP.put("byo", "びょ");
        // 拗音（ぴゃ行）
        ROMAJI_MAP.put("pya", "ぴゃ"); ROMAJI_MAP.put("pyu", "ぴゅ"); ROMAJI_MAP.put("pyo", "ぴょ");

        // 小文字かな
        ROMAJI_MAP.put("xa", "ぁ"); ROMAJI_MAP.put("xi", "ぃ"); ROMAJI_MAP.put("xu", "ぅ");
        ROMAJI_MAP.put("xe", "ぇ"); ROMAJI_MAP.put("xo", "ぉ");
        ROMAJI_MAP.put("xya", "ゃ"); ROMAJI_MAP.put("xyu", "ゅ"); ROMAJI_MAP.put("xyo", "ょ");
        ROMAJI_MAP.put("xtu", "っ");
        ROMAJI_MAP.put("la", "ぁ"); ROMAJI_MAP.put("li", "ぃ"); ROMAJI_MAP.put("lu", "ぅ");
        ROMAJI_MAP.put("le", "ぇ"); ROMAJI_MAP.put("lo", "ぉ");
        ROMAJI_MAP.put("lya", "ゃ"); ROMAJI_MAP.put("lyu", "ゅ"); ROMAJI_MAP.put("lyo", "ょ");
        ROMAJI_MAP.put("ltu", "っ");

        // ヴ行（外来語）
        ROMAJI_MAP.put("va", "ゔぁ"); ROMAJI_MAP.put("vi", "ゔぃ"); ROMAJI_MAP.put("vu", "ゔ");
        ROMAJI_MAP.put("ve", "ゔぇ"); ROMAJI_MAP.put("vo", "ゔぉ");

        // ふぁ行
        ROMAJI_MAP.put("fa", "ふぁ"); ROMAJI_MAP.put("fi", "ふぃ");
        ROMAJI_MAP.put("fe", "ふぇ"); ROMAJI_MAP.put("fo", "ふぉ");

        // でぃ・てぃ等
        ROMAJI_MAP.put("thi", "てぃ"); ROMAJI_MAP.put("dhi", "でぃ");

        // 記号（長音等）
        ROMAJI_MAP.put("-", "ー");

        // プレフィックスセットを構築（部分一致チェック用）
        for (String key : ROMAJI_MAP.keySet()) {
            for (int i = 1; i < key.length(); i++) {
                PREFIX_SET.add(key.substring(0, i));
            }
        }
    }

    public static String convert(String input) {
        if (input == null || input.isEmpty()) return "";

        StringBuilder result = new StringBuilder();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // アルファベットおよびハイフン以外は変換対象外として即出力
            if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '-')) {
                result.append(buffer.toString());
                result.append(c);
                buffer.setLength(0);
                continue;
            }

            buffer.append(Character.toLowerCase(c));

            // バッファの内容を処理
            while (buffer.length() > 0) {
                String key = buffer.toString();

                if (ROMAJI_MAP.containsKey(key)) {
                    // 完全一致 → 変換して消費
                    result.append(ROMAJI_MAP.get(key));
                    buffer.setLength(0);
                    break;
                } else if (PREFIX_SET.contains(key)) {
                    // 有効なプレフィックス → まだ後続の文字が来る可能性、待機
                    break;
                } else {
                    // マッチもプレフィックスもしない場合
                    // 促音（っ）チェック: 同じ子音が連続
                    if (key.length() >= 2 && key.charAt(0) == key.charAt(1) && isConsonant(key.charAt(0))) {
                        result.append("っ");
                        buffer.deleteCharAt(0);
                        continue;
                    }
                    // 「ん」チェック: n + 子音（y以外）
                    if (key.charAt(0) == 'n' && key.length() >= 2
                            && key.charAt(1) != 'a' && key.charAt(1) != 'i'
                            && key.charAt(1) != 'u' && key.charAt(1) != 'e'
                            && key.charAt(1) != 'o' && key.charAt(1) != 'y'
                            && key.charAt(1) != 'n') {
                        result.append("ん");
                        buffer.deleteCharAt(0);
                        continue;
                    }
                    // それ以外 → 先頭1文字をそのまま出力
                    result.append(key.charAt(0));
                    buffer.deleteCharAt(0);
                }
            }
        }

        result.append(buffer.toString());
        return result.toString();
    }

    private static boolean isConsonant(char c) {
        return c >= 'a' && c <= 'z'
                && c != 'a' && c != 'i' && c != 'u' && c != 'e' && c != 'o' && c != 'n';
    }

    public static String convertToKatakana(String hiragana) {
        if (hiragana == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hiragana.length(); i++) {
            char c = hiragana.charAt(i);
            if (c >= '\u3041' && c <= '\u3096') {
                sb.append((char) (c + 0x60));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
