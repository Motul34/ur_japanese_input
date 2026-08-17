package com.ad287483.japanese_input.impl;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;

public class SimpleIcon implements Icon {
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
        g.setColor(Color.WHITE);
        g.drawString("JP", x + 5, y + 20);
    }

    @Override
    public int getIconWidth() {
        return 30;
    }

    @Override
    public int getIconHeight() {
        return 30;
    }
}
