package com.ad287483.japanese_input.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class KeyboardDialog extends JDialog {

    private JTextField textField;
    private JComboBox<String> modeComboBox;

    private SkkDictionaryEngine dictionaryEngine;
    private String committedText = "";
    private String uncommittedText = "";
    private boolean shiftOn = false;
    private JButton shiftButton;

    // 変換候補のページングとフォーカス
    private List<String> allCandidates = new ArrayList<>();
    private int candidatePage = 0;
    private int selectedCandidateIndex = -1;
    private static final int CANDIDATES_PER_PAGE = 7;
    private JPanel candidateButtonPanel;
    private JButton prevCandidateBtn;
    private JButton nextCandidateBtn;

    // 物理キーボードイベント処理
    private KeyEventDispatcher keyEventDispatcher;

    // キー配列
    private static final String[][] KEY_ROWS = {
        {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "^"},
        {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "@", "["},
        {"a", "s", "d", "f", "g", "h", "j", "k", "l", ";", ":", "]"},
        {"z", "x", "c", "v", "b", "n", "m", ",", ".", "/"}
    };

    private static final Color BG_COLOR = new Color(220, 225, 230);
    private static final Color KEY_COLOR = new Color(230, 235, 240);
    private static final Color SPECIAL_KEY_COLOR = new Color(200, 205, 210);
    private static final Color CANDIDATE_BG = new Color(245, 245, 250);
    private static final Color SELECTED_CANDIDATE_COLOR = new Color(180, 215, 255);
    private static final Font KEY_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final int KEY_W = 55;
    private static final int KEY_H = 38;

    public KeyboardDialog(Window owner) {
        super(owner, "日本語入力", ModalityType.MODELESS);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout(0, 2));
        setSize(870, 400);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(BG_COLOR);
        enableInputMethods(false);

        // Singletonから取得
        dictionaryEngine = SkkDictionaryEngine.getInstance();

        // ============================
        // メインの縦パネル
        // ============================
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(12, 8, 8, 8));

        // ============================
        // 1段目: テキストエリア + 変換ボタン
        // ============================
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        GridBagConstraints gbc = new GridBagConstraints();

        textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 24));
        textField.setEditable(false);
        textField.enableInputMethods(false);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 170, 170), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 6);
        topPanel.add(textField, gbc);

        JButton convertButton = new JButton("変換");
        convertButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        convertButton.setPreferredSize(new Dimension(75, 38));
        convertButton.setBackground(new Color(100, 160, 220));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { doConvert(); }
        });
        gbc.gridx = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 0);
        topPanel.add(convertButton, gbc);

        mainPanel.add(topPanel);
        mainPanel.add(javax.swing.Box.createVerticalStrut(12));

        // ============================
        // 2段目: 予測変換候補バー
        // ============================
        JPanel candidateBar = new JPanel(new BorderLayout(4, 0));
        candidateBar.setBackground(CANDIDATE_BG);
        candidateBar.setOpaque(true);
        candidateBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(200, 200, 200)),
                new EmptyBorder(2, 4, 2, 4)));
        candidateBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        candidateBar.setPreferredSize(new Dimension(850, 36));

        candidateButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        candidateButtonPanel.setOpaque(false);
        candidateBar.add(candidateButtonPanel, BorderLayout.CENTER);

        // 左右ナビゲーションボタン
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 1));
        navPanel.setOpaque(false);
        prevCandidateBtn = new JButton("\u25C0");
        prevCandidateBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        prevCandidateBtn.setPreferredSize(new Dimension(36, 28));
        prevCandidateBtn.setFocusPainted(false);
        prevCandidateBtn.setEnabled(false);
        prevCandidateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (candidatePage > 0) {
                    candidatePage--;
                    selectedCandidateIndex = candidatePage * CANDIDATES_PER_PAGE;
                    showCandidatePage();
                }
            }
        });
        nextCandidateBtn = new JButton("\u25B6");
        nextCandidateBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        nextCandidateBtn.setPreferredSize(new Dimension(36, 28));
        nextCandidateBtn.setFocusPainted(false);
        nextCandidateBtn.setEnabled(false);
        nextCandidateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int maxPage = (allCandidates.size() - 1) / CANDIDATES_PER_PAGE;
                if (candidatePage < maxPage) {
                    candidatePage++;
                    selectedCandidateIndex = candidatePage * CANDIDATES_PER_PAGE;
                    showCandidatePage();
                }
            }
        });
        navPanel.add(prevCandidateBtn);
        navPanel.add(nextCandidateBtn);
        candidateBar.add(navPanel, BorderLayout.EAST);

        mainPanel.add(candidateBar);
        mainPanel.add(javax.swing.Box.createVerticalStrut(18));

        // ============================
        // キーボード行を作成
        // ============================
        ActionListener keyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleKeyPress(e.getActionCommand());
            }
        };

        // --- 1段目: 数字キー + BS ---
        JPanel row1 = createRow();
        for (String key : KEY_ROWS[0]) {
            row1.add(makeKey(key, KEY_W, KEY_H, KEY_COLOR, keyListener));
        }
        row1.add(makeKey("BS", 85, KEY_H, SPECIAL_KEY_COLOR, keyListener));
        mainPanel.add(row1);
        mainPanel.add(javax.swing.Box.createVerticalStrut(6));

        // --- 2段目: qwerty + Enter ---
        JPanel row2 = createRow();
        for (String key : KEY_ROWS[1]) {
            row2.add(makeKey(key, KEY_W, KEY_H, KEY_COLOR, keyListener));
        }
        JButton enterBtn = makeKey("Enter", 85, KEY_H, new Color(100, 180, 100), keyListener);
        enterBtn.setForeground(Color.WHITE);
        row2.add(enterBtn);
        mainPanel.add(row2);
        mainPanel.add(javax.swing.Box.createVerticalStrut(6));

        // --- 3段目: asdf ---
        JPanel row3 = createRow();
        for (String key : KEY_ROWS[2]) {
            row3.add(makeKey(key, KEY_W, KEY_H, KEY_COLOR, keyListener));
        }
        mainPanel.add(row3);
        mainPanel.add(javax.swing.Box.createVerticalStrut(6));

        // --- 4段目: Shift + zxcv + モード ---
        JPanel row4 = createRow();
        shiftButton = makeKey("Shift", 85, KEY_H, SPECIAL_KEY_COLOR, keyListener);
        row4.add(shiftButton);
        for (String key : KEY_ROWS[3]) {
            row4.add(makeKey(key, KEY_W, KEY_H, KEY_COLOR, keyListener));
        }
        String[] modes = {"英字", "ひらがな", "カタカナ", "漢字"};
        modeComboBox = new JComboBox<>(modes);
        modeComboBox.setSelectedIndex(3); // デフォルト漢字モード
        modeComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        modeComboBox.setPreferredSize(new Dimension(95, KEY_H));
        modeComboBox.setMaximumSize(new Dimension(95, KEY_H));
        modeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // モード変更時に未確定文字列を確定
                if (!allCandidates.isEmpty()) {
                    int idx = (selectedCandidateIndex >= 0 && selectedCandidateIndex < allCandidates.size()) ? selectedCandidateIndex : 0;
                    committedText += allCandidates.get(idx);
                    uncommittedText = "";
                    clearCandidates();
                } else if (!uncommittedText.isEmpty()) {
                    committedText += fixTrailingN(uncommittedText);
                    uncommittedText = "";
                }
                updateTextField();
            }
        });
        row4.add(modeComboBox);

        mainPanel.add(row4);
        mainPanel.add(javax.swing.Box.createVerticalStrut(6));

        // --- 5段目: Space + コピー + 閉じる ---
        JPanel row5 = createRow();
        row5.add(makeKey("Space", 400, KEY_H, KEY_COLOR, keyListener));

        JButton copyBtn = makeKey("コピー", 110, KEY_H, new Color(100, 160, 220), null);
        copyBtn.setForeground(Color.WHITE);
        copyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { doCopy(); }
        });
        row5.add(copyBtn);

        JButton closeBtn = makeKey("閉じる", 110, KEY_H, new Color(200, 80, 80), null);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
        row5.add(closeBtn);

        mainPanel.add(row5);
        mainPanel.add(javax.swing.Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

        // ============================
        // 物理キーボード入力イベントの登録
        // ============================
        setupPhysicalKeyboard();
    }

    // 物理キーボードイベント処理
    private long lastModeToggleTime = 0;

    private boolean isModeSwitchKey(KeyEvent e) {
        int keyCode = e.getKeyCode();
        int extCode = e.getExtendedKeyCode();
        char keyChar = e.getKeyChar();

        // 1. 半角/全角・漢字キー
        if (keyCode == KeyEvent.VK_KANJI || keyCode == KeyEvent.VK_HALF_WIDTH || keyCode == KeyEvent.VK_FULL_WIDTH) {
            return true;
        }
        if (extCode == 25 || extCode == 243 || extCode == 244) {
            return true;
        }
        // 2. 英語配列キーボードでの半角/全角キー (Back Quote / Tilde: 1の左のキー)
        if (keyCode == KeyEvent.VK_BACK_QUOTE || extCode == 192 || keyChar == '`') {
            return true;
        }
        // 3. 変換キー (VK_CONVERT) / 無変換キー (VK_NONCONVERT)
        if (keyCode == KeyEvent.VK_CONVERT || keyCode == KeyEvent.VK_NONCONVERT || extCode == 28 || extCode == 29) {
            return true;
        }
        // 4. Ctrl + Space
        if (keyCode == KeyEvent.VK_SPACE && (e.isControlDown() || e.isMetaDown())) {
            return true;
        }
        // 5. F1キー（ショートカット）
        if (keyCode == KeyEvent.VK_F1) {
            return true;
        }
        return false;
    }

    private void setupPhysicalKeyboard() {
        keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (!isShowing()) {
                    return false;
                }

                // モード切替キーの判定（KEY_PRESSED または KEY_RELEASED のどちらでも検知・デバウンス制御）
                if (e.getID() == KeyEvent.KEY_PRESSED || e.getID() == KeyEvent.KEY_RELEASED) {
                    if (isModeSwitchKey(e)) {
                        long now = System.currentTimeMillis();
                        if (now - lastModeToggleTime > 250) {
                            lastModeToggleTime = now;
                            toggleMode();
                        }
                        return true;
                    }
                }

                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = e.getKeyCode();
                    char keyChar = e.getKeyChar();

                    if (keyCode == KeyEvent.VK_ENTER) {
                        handleKeyPress("Enter");
                        return true;
                    } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
                        handleKeyPress("BS");
                        return true;
                    } else if (keyCode == KeyEvent.VK_SPACE) {
                        if (e.isShiftDown() && !allCandidates.isEmpty()) {
                            selectPrevCandidate();
                            return true;
                        }
                        handleKeyPress("Space");
                        return true;
                    } else if (keyCode == KeyEvent.VK_SHIFT) {
                        handleKeyPress("Shift");
                        return true;
                    } else if (keyCode == KeyEvent.VK_ESCAPE) {
                        if (!allCandidates.isEmpty()) {
                            clearCandidates();
                        } else if (!uncommittedText.isEmpty()) {
                            uncommittedText = "";
                            updateTextField();
                        } else {
                            dispose();
                        }
                        return true;
                    } else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_UP) {
                        if (!allCandidates.isEmpty()) {
                            selectPrevCandidate();
                        }
                        return true;
                    } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_DOWN) {
                        if (!allCandidates.isEmpty()) {
                            selectNextCandidate();
                        }
                        return true;
                    } else if (keyChar != KeyEvent.CHAR_UNDEFINED && keyChar >= 32 && keyChar <= 126) {
                        handleKeyPress(String.valueOf(keyChar));
                        return true;
                    }
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
    }

    public void toggleMode() {
        int current = modeComboBox.getSelectedIndex();
        int next = (current + 1) % modeComboBox.getItemCount();
        modeComboBox.setSelectedIndex(next);
    }

    @Override
    public void dispose() {
        if (keyEventDispatcher != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
            keyEventDispatcher = null;
        }
        super.dispose();
    }

    // ========== ユーティリティ ==========

    private JPanel createRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, KEY_H + 10));
        return row;
    }

    private JButton makeKey(String label, int w, int h, Color bg, ActionListener listener) {
        JButton btn = new JButton(label);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setFont(KEY_FONT);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        if (listener != null) {
            btn.addActionListener(listener);
        }
        return btn;
    }

    // ========== キー入力処理 ==========

    private void handleKeyPress(String cmd) {
        if ("Enter".equals(cmd)) {
            if (!allCandidates.isEmpty()) {
                int idx = (selectedCandidateIndex >= 0 && selectedCandidateIndex < allCandidates.size()) ? selectedCandidateIndex : 0;
                committedText += allCandidates.get(idx);
            } else {
                committedText += fixTrailingN(uncommittedText);
            }
            uncommittedText = "";
            clearCandidates();
        } else if ("BS".equals(cmd)) {
            if (!allCandidates.isEmpty()) {
                // 変換中にBSを押した場合は変換をキャンセルしてひらがなに戻す
                clearCandidates();
            } else {
                if (uncommittedText.length() > 0) {
                    uncommittedText = uncommittedText.substring(0, uncommittedText.length() - 1);
                } else if (committedText.length() > 0) {
                    committedText = committedText.substring(0, committedText.length() - 1);
                }
            }
        } else if ("Space".equals(cmd)) {
            String mode = (String) modeComboBox.getSelectedItem();
            if ("漢字".equals(mode) && !uncommittedText.isEmpty()) {
                // 漢字モードで未確定文字列がある時: スペースキーで漢字変換・次候補選択
                if (allCandidates.isEmpty()) {
                    doConvert();
                } else {
                    // 次の候補を選択
                    selectNextCandidate();
                }
                return;
            } else {
                // 通常のスペース
                if (!allCandidates.isEmpty()) {
                    int idx = (selectedCandidateIndex >= 0 && selectedCandidateIndex < allCandidates.size()) ? selectedCandidateIndex : 0;
                    committedText += allCandidates.get(idx);
                    uncommittedText = "";
                    clearCandidates();
                } else if (!uncommittedText.isEmpty()) {
                    committedText += fixTrailingN(uncommittedText);
                    uncommittedText = "";
                }
                committedText += " ";
            }
        } else if ("Shift".equalsIgnoreCase(cmd)) {
            shiftOn = !shiftOn;
            if (shiftOn) {
                shiftButton.setBackground(new Color(100, 150, 255));
                shiftButton.setForeground(Color.WHITE);
            } else {
                shiftButton.setBackground(SPECIAL_KEY_COLOR);
                shiftButton.setForeground(Color.BLACK);
            }
            return;
        } else {
            if (!allCandidates.isEmpty()) {
                // 変換中に次の文字が入力されたら、現在選択中の候補を自動確定して次へ
                int idx = (selectedCandidateIndex >= 0 && selectedCandidateIndex < allCandidates.size()) ? selectedCandidateIndex : 0;
                committedText += allCandidates.get(idx);
                uncommittedText = "";
                clearCandidates();
            }

            String mode = (String) modeComboBox.getSelectedItem();
            if ("英字".equals(mode) && shiftOn) {
                uncommittedText += cmd.toUpperCase();
            } else {
                uncommittedText += cmd;
            }
        }

        // モードに応じたかな変換
        String mode = (String) modeComboBox.getSelectedItem();
        if ("ひらがな".equals(mode) || "漢字".equals(mode)) {
            uncommittedText = RomajiToKanaConverter.convert(uncommittedText);
        } else if ("カタカナ".equals(mode)) {
            uncommittedText = RomajiToKanaConverter.convertToKatakana(
                    RomajiToKanaConverter.convert(uncommittedText));
        }

        updateTextField();
    }

    // ========== 漢字変換候補ナビゲーション ==========

    void selectNextCandidate() {
        if (allCandidates.isEmpty()) return;
        int current = (selectedCandidateIndex >= 0) ? selectedCandidateIndex : 0;
        selectedCandidateIndex = (current + 1) % allCandidates.size();
        candidatePage = selectedCandidateIndex / CANDIDATES_PER_PAGE;
        showCandidatePage();
    }

    void selectPrevCandidate() {
        if (allCandidates.isEmpty()) return;
        int current = (selectedCandidateIndex >= 0) ? selectedCandidateIndex : 0;
        selectedCandidateIndex = (current - 1 + allCandidates.size()) % allCandidates.size();
        candidatePage = selectedCandidateIndex / CANDIDATES_PER_PAGE;
        showCandidatePage();
    }

    // ========== 漢字変換 ==========

    private void doConvert() {
        String mode = (String) modeComboBox.getSelectedItem();
        if (!"漢字".equals(mode)) return;
        if (uncommittedText.isEmpty()) return;

        // 末尾の 'n' を自動的に「ん」に補正
        uncommittedText = fixTrailingN(uncommittedText);

        allCandidates = dictionaryEngine.getCandidates(uncommittedText);
        candidatePage = 0;
        selectedCandidateIndex = 0;
        showCandidatePage();
        updateTextField();
    }

    private void showCandidatePage() {
        candidateButtonPanel.removeAll();

        int start = candidatePage * CANDIDATES_PER_PAGE;
        int end = Math.min(start + CANDIDATES_PER_PAGE, allCandidates.size());

        for (int i = start; i < end; i++) {
            final int index = i;
            final String candidate = allCandidates.get(i);
            JButton btn = new JButton(candidate);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
            btn.setPreferredSize(new Dimension(90, 28));

            if (index == selectedCandidateIndex) {
                btn.setBackground(SELECTED_CANDIDATE_COLOR);
                btn.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 240), 2));
            } else {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
            }

            btn.setFocusPainted(false);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    committedText += candidate;
                    uncommittedText = "";
                    updateTextField();
                    clearCandidates();
                }
            });
            candidateButtonPanel.add(btn);
        }

        prevCandidateBtn.setEnabled(candidatePage > 0);
        int maxPage = allCandidates.isEmpty() ? 0 : (allCandidates.size() - 1) / CANDIDATES_PER_PAGE;
        nextCandidateBtn.setEnabled(candidatePage < maxPage);

        candidateButtonPanel.revalidate();
        candidateButtonPanel.repaint();
    }

    private void clearCandidates() {
        allCandidates = new ArrayList<>();
        candidatePage = 0;
        selectedCandidateIndex = -1;
        candidateButtonPanel.removeAll();
        prevCandidateBtn.setEnabled(false);
        nextCandidateBtn.setEnabled(false);
        candidateButtonPanel.revalidate();
        candidateButtonPanel.repaint();
    }

    private String fixTrailingN(String text) {
        if (text == null || text.isEmpty()) return "";
        if (text.endsWith("n") || text.endsWith("N")) {
            return text.substring(0, text.length() - 1) + "ん";
        }
        return text;
    }

    // ========== コピー ==========

    private void doCopy() {
        String text = textField.getText();
        if (!text.isEmpty()) {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        }
    }

    // ========== 表示更新 ==========

    private static final Highlighter.HighlightPainter UNCOMMITTED_PAINTER = 
            new DefaultHighlighter.DefaultHighlightPainter(new Color(180, 210, 255));

    private void updateTextField() {
        textField.setText(committedText + uncommittedText);
        try {
            Highlighter highlighter = textField.getHighlighter();
            highlighter.removeAllHighlights();
            if (uncommittedText.length() > 0) {
                highlighter.addHighlight(committedText.length(), 
                        committedText.length() + uncommittedText.length(), 
                        UNCOMMITTED_PAINTER);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
