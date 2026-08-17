package com.ad287483.japanese_input.impl;

import com.ur.urcap.api.contribution.toolbar.ToolbarContext;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarContribution;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class JapaneseInputToolbarContribution implements SwingToolbarContribution {

    private final ToolbarContext context;
    private SoftwareKeyboardPanel keyboardPanel;

    public JapaneseInputToolbarContribution(ToolbarContext context) {
        this.context = context;
    }

    @Override
    public void buildUI(JPanel jPanel) {
        jPanel.setLayout(new BorderLayout());
        keyboardPanel = new SoftwareKeyboardPanel();
        jPanel.add(keyboardPanel, BorderLayout.CENTER);
    }

    @Override
    public void openView() {
        if (keyboardPanel != null) {
            // View open logic if needed
        }
    }

    @Override
    public void closeView() {
    }
}
