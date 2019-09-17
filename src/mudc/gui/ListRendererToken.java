package mudc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;

import mudc.core.casudc.MoodleToken;

@SuppressWarnings("serial")
public class ListRendererToken extends JPanel implements ListCellRenderer<MoodleToken> {
	private JLabel lblName = null;
	private JLabel lblDescription = null;

	public ListRendererToken() {

		super();

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		lblName = new JLabel();
		lblName.setFont(new Font("Dialog", Font.BOLD, 14));
		lblName.setForeground(new Color(64, 64, 64, 255));
		springLayout.putConstraint(SpringLayout.NORTH, lblName, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblName, 16, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblName, -8, SpringLayout.EAST, this);
		add(lblName);

		lblDescription = new JLabel();
		lblDescription.setFont(new Font("Dialog", Font.PLAIN, 11));
		lblDescription.setForeground(new Color(64, 64, 64, 255));
		springLayout.putConstraint(SpringLayout.NORTH, lblDescription, 2, SpringLayout.SOUTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, lblDescription, 16, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblDescription, -16, SpringLayout.EAST, this);
		add(lblDescription);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MoodleToken> list, MoodleToken token, int index,
			boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(Colors.accountsTokenBackgroundSelected);
		} else {
			setBackground(Colors.accountsTokenBackground);
		}
		
		lblName.setText(token.getServiceName());
		lblDescription.setText(token.getToken());
		
		return this;
	}

}
