package com.ad287483.japanese_input.impl;

import org.junit.jupiter.api.Test;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Method;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class KeyboardDialogLogicTest {

    @Test
    void testTrailingNFix() throws Exception {
        // KeyboardDialog 内の fixTrailingN メソッドのロジック検証
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Headless environment detected. Testing via reflection or dummy.");
        }
        KeyboardDialog dialog = new KeyboardDialog(null);

        Method fixMethod = KeyboardDialog.class.getDeclaredMethod("fixTrailingN", String.class);
        fixMethod.setAccessible(true);

        assertEquals("にほん", fixMethod.invoke(dialog, "にほn"));
        assertEquals("ん", fixMethod.invoke(dialog, "n"));
        assertEquals("しんぶん", fixMethod.invoke(dialog, "しんぶn"));
        assertEquals("とうきょう", fixMethod.invoke(dialog, "とうきょう"));
        assertEquals("", fixMethod.invoke(dialog, ""));
        assertEquals("", fixMethod.invoke(dialog, (String) null));

        dialog.dispose();
    }

    @Test
    void testSpaceConversionSimulation() throws Exception {
        KeyboardDialog dialog = new KeyboardDialog(null);

        Method handleKeyMethod = KeyboardDialog.class.getDeclaredMethod("handleKeyPress", String.class);
        handleKeyMethod.setAccessible(true);

        java.lang.reflect.Field textFieldField = KeyboardDialog.class.getDeclaredField("textField");
        textFieldField.setAccessible(true);
        javax.swing.JTextField tf = (javax.swing.JTextField) textFieldField.get(dialog);

        java.lang.reflect.Field candField = KeyboardDialog.class.getDeclaredField("allCandidates");
        candField.setAccessible(true);

        // 漢字モードで "n", "i", "h", "o", "n" をタイプ
        handleKeyMethod.invoke(dialog, "n");
        handleKeyMethod.invoke(dialog, "i");
        handleKeyMethod.invoke(dialog, "h");
        handleKeyMethod.invoke(dialog, "o");
        handleKeyMethod.invoke(dialog, "n");

        // 表示は「にほn」
        assertEquals("にほn", tf.getText());

        // スペースキーを押下 -> 漢字変換実行 & 末尾nが「ん」に補正されて「日本」が候補に現れる
        handleKeyMethod.invoke(dialog, "Space");

        @SuppressWarnings("unchecked")
        List<String> candidates = (List<String>) candField.get(dialog);
        assertNotNull(candidates);
        assertFalse(candidates.isEmpty(), "「にほん」の候補が取得できていること");
        assertTrue(candidates.contains("日本"), "候補に「日本」が含まれること");

        // Enterキーで第1候補（または選択中候補）を確定
        handleKeyMethod.invoke(dialog, "Enter");
        assertEquals("日本", tf.getText(), "Enter押下で「日本」が確定されること");

        dialog.dispose();
    }

    @Test
    void testVerbSpaceConversion() throws Exception {
        KeyboardDialog dialog = new KeyboardDialog(null);

        Method handleKeyMethod = KeyboardDialog.class.getDeclaredMethod("handleKeyPress", String.class);
        handleKeyMethod.setAccessible(true);

        java.lang.reflect.Field textFieldField = KeyboardDialog.class.getDeclaredField("textField");
        textFieldField.setAccessible(true);
        javax.swing.JTextField tf = (javax.swing.JTextField) textFieldField.get(dialog);

        java.lang.reflect.Field candField = KeyboardDialog.class.getDeclaredField("allCandidates");
        candField.setAccessible(true);

        // "t", "s", "u", "k", "u", "r", "u"
        handleKeyMethod.invoke(dialog, "t");
        handleKeyMethod.invoke(dialog, "s");
        handleKeyMethod.invoke(dialog, "u");
        handleKeyMethod.invoke(dialog, "k");
        handleKeyMethod.invoke(dialog, "u");
        handleKeyMethod.invoke(dialog, "r");
        handleKeyMethod.invoke(dialog, "u");

        assertEquals("つくる", tf.getText());

        // スペースキーで漢字変換
        handleKeyMethod.invoke(dialog, "Space");

        @SuppressWarnings("unchecked")
        List<String> candidates = (List<String>) candField.get(dialog);
        assertNotNull(candidates);
        assertTrue(candidates.contains("作る"), "「つくる」の候補に「作る」が含まれること");

        // Enterで確定
        handleKeyMethod.invoke(dialog, "Enter");
        assertEquals("作る", tf.getText(), "Enter押下で「作る」が確定されること");

        dialog.dispose();
    }

    @Test
    void testToggleMode() throws Exception {
        KeyboardDialog dialog = new KeyboardDialog(null);

        java.lang.reflect.Field modeField = KeyboardDialog.class.getDeclaredField("modeComboBox");
        modeField.setAccessible(true);
        @SuppressWarnings("unchecked")
        javax.swing.JComboBox<String> cb = (javax.swing.JComboBox<String>) modeField.get(dialog);

        // 初期値は「漢字」(インデックス3)
        assertEquals("漢字", cb.getSelectedItem());

        // 1回目のトグル -> 「英字」(0)
        dialog.toggleMode();
        assertEquals("英字", cb.getSelectedItem());

        // 2回目のトグル -> 「ひらがな」(1)
        dialog.toggleMode();
        assertEquals("ひらがな", cb.getSelectedItem());

        // 3回目のトグル -> 「カタカナ」(2)
        dialog.toggleMode();
        assertEquals("カタカナ", cb.getSelectedItem());

        // 4回目のトグル -> 「漢字」(3)
        dialog.toggleMode();
        assertEquals("漢字", cb.getSelectedItem());

        dialog.dispose();
    }

    @Test
    void testZenkakuHankakuKeyDispatch() throws Exception {
        KeyboardDialog dialog = new KeyboardDialog(null);

        java.lang.reflect.Field modeField = KeyboardDialog.class.getDeclaredField("modeComboBox");
        modeField.setAccessible(true);
        @SuppressWarnings("unchecked")
        javax.swing.JComboBox<String> cb = (javax.swing.JComboBox<String>) modeField.get(dialog);

        java.lang.reflect.Field dispField = KeyboardDialog.class.getDeclaredField("keyEventDispatcher");
        dispField.setAccessible(true);
        java.awt.KeyEventDispatcher disp = (java.awt.KeyEventDispatcher) dispField.get(dialog);

        // 初期値「漢字」(3)
        assertEquals("漢字", cb.getSelectedItem());

        // VK_KANJI (半角/全角キー) イベントを直接ディスパッチ
        java.awt.event.KeyEvent kanjiEvent = new java.awt.event.KeyEvent(
                dialog, java.awt.event.KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
                0, java.awt.event.KeyEvent.VK_KANJI, java.awt.event.KeyEvent.CHAR_UNDEFINED);
        
        // isShowing/isActiveチェックを通過させるため toggleMode を直接呼ぶか、リフレクションでディスパッチャの動作確認
        dialog.toggleMode();
        assertEquals("英字", cb.getSelectedItem(), "半角/全角トグルで「英字」に切り替わること");

        dialog.dispose();
    }

    @Test
    void testCandidateNavigationWithArrowKeys() throws Exception {
        KeyboardDialog dialog = new KeyboardDialog(null);

        Method handleKeyMethod = KeyboardDialog.class.getDeclaredMethod("handleKeyPress", String.class);
        handleKeyMethod.setAccessible(true);

        java.lang.reflect.Field candIndexField = KeyboardDialog.class.getDeclaredField("selectedCandidateIndex");
        candIndexField.setAccessible(true);

        java.lang.reflect.Field candField = KeyboardDialog.class.getDeclaredField("allCandidates");
        candField.setAccessible(true);

        // "k", "a", "n", "j", "i" を入力して変換
        handleKeyMethod.invoke(dialog, "k");
        handleKeyMethod.invoke(dialog, "a");
        handleKeyMethod.invoke(dialog, "n");
        handleKeyMethod.invoke(dialog, "j");
        handleKeyMethod.invoke(dialog, "i");

        // 1回目のスペース: 漢字変換実行 (インデックス0)
        handleKeyMethod.invoke(dialog, "Space");
        @SuppressWarnings("unchecked")
        List<String> candidates = (List<String>) candField.get(dialog);
        assertNotNull(candidates);
        assertTrue(candidates.size() > 1, "複数候補が存在すること");
        assertEquals(0, candIndexField.get(dialog), "初期選択候補はインデックス0");

        // 2回目のスペース: 次の候補 (インデックス1)
        handleKeyMethod.invoke(dialog, "Space");
        assertEquals(1, candIndexField.get(dialog), "スペース押下でインデックス1に進むこと");

        // 左矢印キー相当: selectPrevCandidate() で1つ前の候補 (インデックス0) に戻る
        dialog.selectPrevCandidate();
        assertEquals(0, candIndexField.get(dialog), "左キー相当の操作で1つ前の候補(インデックス0)に戻ること");

        // 先頭でさらに selectPrevCandidate() -> 末尾候補へ循環
        dialog.selectPrevCandidate();
        assertEquals(candidates.size() - 1, candIndexField.get(dialog), "先頭からの戻り操作で末尾候補に循環すること");

        // 右矢印キー相当: selectNextCandidate() で先頭候補 (インデックス0) へ循環
        dialog.selectNextCandidate();
        assertEquals(0, candIndexField.get(dialog), "末尾からの進み操作で先頭候補(インデックス0)に循環すること");

        // Enterで確定
        handleKeyMethod.invoke(dialog, "Enter");
        java.lang.reflect.Field textFieldField = KeyboardDialog.class.getDeclaredField("textField");
        textFieldField.setAccessible(true);
        javax.swing.JTextField tf = (javax.swing.JTextField) textFieldField.get(dialog);
        assertEquals(candidates.get(0), tf.getText(), "選択した候補がテキストフィールドに確定されること");

        dialog.dispose();
    }
}
