package mudc.gui;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

@SuppressWarnings("serial")
public class CardString extends JPanel {

	private JLabel nameLabel;

	public CardString(String message) {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		nameLabel = new JLabel(message, JLabel.CENTER);
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, nameLabel, 4, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, nameLabel, -4, SpringLayout.EAST, this);
		add(nameLabel);
	}

	public int getTotalContentHeight() {
		return (int) nameLabel.getPreferredSize().getHeight() + 8;
	}
}
