package mudc.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class MainMenuBarButton extends JButton {

	private static final long serialVersionUID = 5553556743788300973L;
	private Color selectedColor = Colors.defaultBackground;
	private Color notSelectedColor = Colors.defaultBackground;
	private boolean selected = false;
	private boolean isPressed = false;
	private boolean isHovered = false;
	
	public MainMenuBarButton() {
		this.setBackground(new Color(0, 0, 0, 255));
	}
	
	public void setButtonColors(Color selected, Color notSelected) {
		selectedColor = selected;
		notSelectedColor = notSelected;
		this.setBackground(Colors.defaultBackground);
		this.setContentAreaFilled(false);
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
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
	
	public void setButtonSelected(boolean state) {
		selected = state;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int width = getWidth();
		int height = getHeight();
		g2d.setColor(notSelectedColor);
		g2d.fillRect(0, 0, width, height);
		if (selected) {
			g2d.setColor(selectedColor);
			g2d.fillRect(2, height/2, width-4, height/2);
			g2d.fillRoundRect(2, 2, width-4, height-4, 16, 16);
		}
		if (isHovered && !isPressed) {
			if (selected) {
				g2d.setColor(new Color(224, 224, 224, 255));
				g2d.fillRoundRect(8, 5, width-16, height-8, 32, 32);
			}
			else {
				g2d.setColor(new Color(88, 88, 88, 255));
				g2d.fillRoundRect(8, 4, width-16, height-8, 32, 32);
			}
		}
		else if (isPressed) {
			g2d.setColor(new Color(200, 218, 235, 255));
			if (selected) {
				g2d.fillRoundRect(8, 5, width-16, height-8, 32, 32);
			} else {
				g2d.fillRoundRect(8, 4, width-16, height-8, 32, 32);
			}
		}
		super.paintComponent(g);
	}

}
