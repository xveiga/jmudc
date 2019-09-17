package mudc.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class CustomJButton extends JButton {

	private static final long serialVersionUID = 5553556743788300973L;
	private Color backgroundColor = Colors.customJButtonBackground;
	private Color hoveredColor = Colors.customJButtonHovered;
	private Color clickedColor = Colors.customJButtonClicked;
	private Color textColor = Colors.customJButtonText;
	private boolean isPressed = false;
	private boolean isHovered = false;
	
	public CustomJButton() {
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setForeground(textColor);
		this.setBackground(new Color(0, 0, 0, 255));
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				isPressed = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				isPressed = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				isHovered = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				isHovered = false;
			}
			
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int width = getWidth()-1;
		int height = getHeight()-1;
		if (isPressed) {
			g2d.setColor(clickedColor);
		} else if (isHovered) {
			g2d.setColor(hoveredColor);
		} else {
			g2d.setColor(backgroundColor);
		}
		g2d.fillRoundRect(0, 0, width, height, 16, 16);
		g2d.setColor(textColor);
		g2d.drawRoundRect(0, 0, width, height, 16, 16);

		super.paintComponent(g);
	}

}
