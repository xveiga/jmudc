package mudc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

public class RoundLabel extends JLabel {
	
	private static final long serialVersionUID = -5335006276004579362L;
	
	private Color border = new Color(0, 0, 0, 255);

	public RoundLabel(String string, int center) {
		super(string, center);
	}
	
	public void setBorderColor(Color c) {
		border = c;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension arcs = Dimensions.roundLabelBorderArc; // Border arc size
		int bWidth = getWidth()-1;
		int bHeight = getHeight()-1;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(border);
		g2d.drawRoundRect(0, 0, bWidth, bHeight, arcs.width, arcs.height); // paint border
	}

}
