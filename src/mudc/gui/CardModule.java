package mudc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import mudc.core.MoodleModule;

@SuppressWarnings("serial")
public class CardModule extends JPanel {
	
	private JLabel nameLabel;
	private JLabel descLabel;
	
	private Color backgroundColor = Colors.cardModuleBackground;
	private Color foregroundColor = Colors.black;
	private boolean drawShadow = false;
	private int shadowSize = 0;
	private int bottomSpacing = 0;

	public CardModule(MoodleModule module) {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		//setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192, 255)));
		
		nameLabel = new JLabel();
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		nameLabel.setText("<html>" + module.name + "</html>");
		springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 4, SpringLayout.NORTH, this);
		//springLayout.putConstraint(SpringLayout.SOUTH, nameLabel, 20, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, nameLabel, 4, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, nameLabel, -4, SpringLayout.EAST, this);
		add(nameLabel);
		
		descLabel = new JLabel();
		if (module.description != null && !module.description.equals("")) {
			descLabel.setText("<HTML>" + module.description + "</HTML>");
			descLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
			//descLabel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(224, 224, 224, 255)));
			springLayout.putConstraint(SpringLayout.NORTH, descLabel, 2, SpringLayout.SOUTH, nameLabel);
			springLayout.putConstraint(SpringLayout.WEST, descLabel, 4, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, descLabel, -4, SpringLayout.EAST, this);
			add(descLabel);
		}
	}
	
	@Override
	public void setBackground(Color c) {
		backgroundColor = c;
	}
	
	@Override
	public void setForeground(Color c) {
		foregroundColor = c;
	}
	
	public void setBottomSpacing(int size) {
		bottomSpacing = size;
	}
	
	public void setDrawShadow(boolean state) {
		drawShadow = state;
		if (drawShadow) {
			shadowSize = Dimensions.shadowSize;
		}
		else {
			shadowSize = 0;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension arcs = Dimensions.cardModuleBorderArc; // Border arc size
		int bWidth = getWidth()-1;
		int bHeight = getHeight()-1;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int shadowHeight = bHeight-bottomSpacing;
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
		g2d.setColor(backgroundColor);
		g2d.fillRoundRect(0, 0, bWidth, finalHeight, arcs.width, arcs.height); // paint background
		g2d.setColor(foregroundColor);
		g2d.drawRoundRect(0, 0, bWidth, finalHeight, arcs.width, arcs.height); // paint border
	}
	
	public int getTotalContentHeight() {
		return (int) descLabel.getPreferredSize().getHeight() + (int) nameLabel.getPreferredSize().getHeight() + 12 + shadowSize + bottomSpacing;
	}
}
