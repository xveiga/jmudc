package mudc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import mudc.core.Locales;

public abstract class CardSettings extends JPanel {

	private static final long serialVersionUID = 3642430061899628863L;
	private boolean drawShadow = false;
	private Locales strings = null;

	public CardSettings(GUI gui, Locales str) {
		strings = str;
		setBackground(Colors.transparency);
	}
	
	public void setShadowsEnabled(boolean status) {
		drawShadow = status;
	}
	
	public int getTotalContentHeight() {
		return 32;
	}
	
	public void onLocaleChange() {
		
	}
	
	public Locales getStrings() {
		return strings;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension arcs = Dimensions.cardModuleBorderArc; // Border arc size
		int bWidth = getWidth()-1;
		int bHeight = getHeight()-1;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int shadowHeight = bHeight-Dimensions.settingsAccountBottomSpacing;
		int shadowSize = Dimensions.shadowSize;
		// Drop shadow
		if (drawShadow) {
			for (int i=0; i<shadowSize; i++) {
				int shadow = i*Dimensions.shadowStrength;
				g2d.setColor(new Color(0, 0, 0, shadow));
				g2d.drawRoundRect(0, 0, bWidth, shadowHeight-i, arcs.width, arcs.height);
			}
		}
		// Draws the rounded panel with borders.
		int finalHeight = shadowHeight-shadowSize;
		g2d.setColor(Colors.settingsAccountsBackground);
		g2d.fillRoundRect(0, 0, bWidth, finalHeight, arcs.width, arcs.height); // paint background
		g2d.setColor(Colors.settingsAccountsBorder);
		g2d.drawRoundRect(0, 0, bWidth, finalHeight, arcs.width, arcs.height); // paint border
	}
}
