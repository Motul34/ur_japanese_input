package com.ad287483.japanese_input.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkkDictionaryEngine {

    private final Map<String, List<String>> dictionary = new HashMap<>();

    public SkkDictionaryEngine() {
        loadDictionary();
    }

    private void loadDictionary() {
        // SKK辞書のフォーマット例:
        // ;; コメント行
        // あい /愛/相/藍/
        
        try (InputStream is = getClass().getResourceAsStream("/skk_dict.txt")) {
            if (is == null) {
                System.err.println("Error: skk_dict.txt not found in resources.");
                return;
            }
            
            // SKK-JISYO は EUC-JP エンコーディング
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
                        List<String> candidates = new ArrayList<>();
                        for (String c : rawCandidates) {
                            // SKK辞書には セミコロンによる注釈が含まれる場合がある (例: 候補;注釈)
                            int semiIndex = c.indexOf(";");
                            if (semiIndex != -1) {
                                c = c.substring(0, semiIndex);
                            }
                            if (!c.isEmpty()) {
                                candidates.add(c);
                            }
                        }
                        dictionary.put(kana, candidates);
                    }
                }
            }
            System.out.println("SKK dictionary loaded successfully. Entries: " + dictionary.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load SKK dictionary.");
        }
    }

    public List<String> getCandidates(String kana) {
        List<String> candidates = dictionary.get(kana);
        if (candidates == null) {
            candidates = new ArrayList<>();
            candidates.add(kana); // 見つからない場合は入力されたひらがな自体をそのまま返す
        }
        return candidates;
    }
}
