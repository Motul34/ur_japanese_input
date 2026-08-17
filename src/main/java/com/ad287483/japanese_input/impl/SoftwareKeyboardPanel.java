package com.ad287483.japanese_input.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SoftwareKeyboardPanel extends JPanel {

    private JButton openKeyboardButton;

    public SoftwareKeyboardPanel() {
        setLayout(new BorderLayout());

        openKeyboardButton = new JButton("日本語入力キーボードを開く");
        openKeyboardButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        openKeyboardButton.setPreferredSize(new Dimension(280, 80));
        
        openKeyboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 親ウィンドウを取得してダイアログを中央に表示する
                Window parent = SwingUtilities.getWindowAncestor(SoftwareKeyboardPanel.this);
                KeyboardDialog dialog = new KeyboardDialog(parent);
                dialog.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openKeyboardButton);
        
        add(buttonPanel, BorderLayout.NORTH);
    }
}
