package mudc.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomScrollBar extends BasicScrollBarUI {

	private int wpad = 3;
	private int wpad2 = wpad * 2;

	public CustomScrollBar() {
		super();
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
		super.paintThumb(g, c, r);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(255, 255, 255, 255));
		g2d.fillRect(r.x, r.y, r.width, r.height);
		g2d.setColor(new Color(192, 192, 192, 255));
		g2d.fillRect(r.x + wpad, r.y + wpad + (r.width - wpad2) / 2, r.width - wpad2, r.height - r.width);
		g2d.fillOval(r.x + wpad, r.y + wpad, r.width - wpad2, r.width - wpad2);
		g2d.fillOval(r.x + wpad, r.y + r.height - r.width + wpad, r.width - wpad2, r.width - wpad2);
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(255, 255, 255, 255));
		g2d.fillRect(r.x, r.y, r.width, r.height);
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		return new ScrollButton(orientation);
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		return new ScrollButton(orientation);
	}

	private static class ScrollButton extends JButton {

		private static final long serialVersionUID = 7070824659144549477L;
		
		private Color backgroundColor = new Color(255, 255, 255, 255);
		private Color hoveredColor = new Color(192, 192, 192, 255);
		private Color clickedColor = new Color(128, 128, 128, 255);
		private Color arrowColor = new Color(32, 32, 32, 255);

		private boolean isPressed = false;
		private boolean isHovered = false;
		private int o;

		public ScrollButton(int orientation) {
			super();
			o = orientation;
			this.setBorderPainted(false);
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
				}

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
		public Dimension getPreferredSize() {
			return new Dimension(-1, 16);
		}

		@Override
		public void paintComponent(Graphics g) {
			//super.paintComponent(g);
			int width = getWidth();
			int height = getHeight();
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(backgroundColor);
			g2d.fillRect(0, 0, width, height);
			if (isPressed) {
				g2d.setColor(clickedColor);
			} else if (isHovered) {
				g2d.setColor(hoveredColor);
			} else {
				g2d.setColor(backgroundColor);
			}
			g2d.fillOval(1, 1, width-2, height-2);
			g2d.setColor(arrowColor);
			g2d.setStroke(new BasicStroke(1));
			switch (o) {
			case BasicScrollBarUI.SOUTH:
				g2d.fillPolygon(new int[] {5, width/2, width-5}, new int[] {5, height-5, 5}, 3);
				break;
			case BasicScrollBarUI.NORTH:
				g2d.fillPolygon(new int[] {5, width/2, width-5}, new int[] {height-5, 5, height-5}, 3); 
				break;
			}
			//g2d.drawLine(4, height-4, width/2, 4);
			//g2d.drawLine(width/2, 4, width-4, height-4);
		}
	}
}
