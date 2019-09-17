package mudc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelLoading extends JPanel {

	private static final long serialVersionUID = -3063923428908663994L;
	
	private PanelLoadingAnimation animation = null;

	public PanelLoading(String text) {
		setLayout(null);
		setBackground(new Color(255, 255, 255, 255));
		animation = new PanelLoadingAnimation();
		add(animation);
		JLabel taskName = new JLabel();
		taskName.setFont(new Font("Tahoma", Font.BOLD, 18));
		taskName.setHorizontalAlignment(JLabel.CENTER);
		taskName.setText(text);
		add(taskName);
		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				animation.setBounds(getWidth()/4, getHeight()/2-getHeight()/4, getWidth()/2, getHeight()/4);
				taskName.setBounds(getWidth()/4, getHeight()/2+16, getWidth()/2, 24);
			}
		};
		addComponentListener(componentAdapter);
		repaint();
	}
	
	public void setAnimationEnabled(boolean state) {
		animation.setAnimationEnabled(state);
	}

}
