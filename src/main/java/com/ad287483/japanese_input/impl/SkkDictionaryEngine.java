package com.ad287483.japanese_input.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkkDictionaryEngine {

    private static SkkDictionaryEngine instance;

    private static final Map<Character, String> OKURI_MAP = new HashMap<>();
    static {
        OKURI_MAP.put('r', "る");
        OKURI_MAP.put('k', "く");
        OKURI_MAP.put('g', "ぐ");
        OKURI_MAP.put('s', "す");
        OKURI_MAP.put('t', "つ");
        OKURI_MAP.put('n', "ぬ");
        OKURI_MAP.put('b', "ぶ");
        OKURI_MAP.put('m', "む");
        OKURI_MAP.put('w', "う");
    }

    private final Map<String, List<String>> dictionary = new HashMap<>(250000);

    public static synchronized SkkDictionaryEngine getInstance() {
        if (instance == null) {
            instance = new SkkDictionaryEngine();
        }
        return instance;
    }

    // 後方互換性およびSingleton内部呼び出し用
    public SkkDictionaryEngine() {
        loadDictionary();
    }

    private void loadDictionary() {
        try (InputStream is = getClass().getResourceAsStream("/skk_dict.txt")) {
            if (is == null) {
                System.err.println("Error: skk_dict.txt not found in resources.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "EUC-JP"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(";")) {
                        continue; // コメント行スキップ
                    }

                    int spaceIndex = line.indexOf(" ");
                    if (spaceIndex != -1) {
                        String kana = line.substring(0, spaceIndex);
                        String kanjiPart = line.substring(spaceIndex + 1).trim();

                        // /候補1/候補2/ をパース
                        if (kanjiPart.startsWith("/") && kanjiPart.endsWith("/")) {
                            kanjiPart = kanjiPart.substring(1, kanjiPart.length() - 1);
                        }

                        String[] rawCandidates = kanjiPart.split("/");
                        List<String> candidates = new ArrayList<>(rawCandidates.length);
                        for (String c : rawCandidates) {
                            int semiIndex = c.indexOf(";");
                            if (semiIndex != -1) {
                                c = c.substring(0, semiIndex);
                            }
                            if (!c.isEmpty()) {
                                candidates.add(c);
                            }
                        }

                        // 1. そのまま登録
                        addCandidates(kana, candidates);

                        // 2. 送りありエントリ（末尾が英子音）の場合、基本形を展開して登録
                        if (kana.length() > 1) {
                            char lastChar = kana.charAt(kana.length() - 1);
                            if (OKURI_MAP.containsKey(lastChar)) {
                                String okuriKana = OKURI_MAP.get(lastChar);
                                String stem = kana.substring(0, kana.length() - 1);
                                String baseKana = stem + okuriKana;

                                List<String> baseCandidates = new ArrayList<>(candidates.size());
                                for (String c : candidates) {
                                    baseCandidates.add(c + okuriKana);
                                }
                                addCandidates(baseKana, baseCandidates);
                                // 語幹単体でも引けるように登録
                                addCandidates(stem, candidates);
                            }
                        }
                    }
                }
            }
            System.out.println("SKK dictionary loaded successfully. Entries: " + dictionary.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load SKK dictionary.");
        }
    }

    private void addCandidates(String key, List<String> newCandidates) {
        List<String> existing = dictionary.get(key);
        if (existing == null) {
            dictionary.put(key, new ArrayList<>(newCandidates));
        } else {
            for (String c : newCandidates) {
                if (!existing.contains(c)) {
                    existing.add(c);
                }
            }
        }
    }

    public List<String> getCandidates(String kana) {
        List<String> candidates = dictionary.get(kana);
        if (candidates == null || candidates.isEmpty()) {
            candidates = new ArrayList<>();
            candidates.add(kana); // 見つからない場合は入力されたひらがな自体をそのまま返す
        }
        return candidates;
    }
}
