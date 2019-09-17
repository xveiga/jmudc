package mudc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import mudc.core.Locales;
import mudc.core.MoodleMessage;

@SuppressWarnings("serial")
public class ListRendererMessage extends JPanel implements ListCellRenderer<MoodleMessage> {
	private JLabel lblName = null;
	private JLabel lblDescription = null;
	private JLabel btnOpen = null;
	private Locales strings = null;

	public ListRendererMessage(Locales str) {

		super();
		
		strings = str;

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		btnOpen = new JLabel(strings.getString("buttonopen"), SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, btnOpen, 8, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnOpen, -64, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnOpen, -8, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnOpen, -8, SpringLayout.EAST, this);
		btnOpen.setForeground(new Color(64, 64, 64, 255));
		btnOpen.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(128, 128, 128, 255)));
		add(btnOpen);

		lblName = new JLabel();
		lblName.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblName.setForeground(new Color(64, 64, 64, 255));
		springLayout.putConstraint(SpringLayout.NORTH, lblName, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblName, 8, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblName, -8, SpringLayout.WEST, btnOpen);
		add(lblName);

		lblDescription = new JLabel();
		lblDescription.setFont(new Font("Dialog", Font.PLAIN, 10));
		lblDescription.setForeground(new Color(64, 64, 64, 255));
		springLayout.putConstraint(SpringLayout.NORTH, lblDescription, 4, SpringLayout.SOUTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, lblDescription, 8, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblDescription, -16, SpringLayout.WEST, btnOpen);
		add(lblDescription);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MoodleMessage> list, MoodleMessage message, int index,
			boolean isSelected, boolean cellHasFocus) {

		SimpleDateFormat sdf = new SimpleDateFormat(strings.getString("dateformat"));
		Date time = new Date(message.timecreated * 1000);

		lblName.setText(message.subject);
		lblDescription.setText(
				sdf.format(time) + "     " + strings.getString("from") + " " + message.userfromfullname + "     "+ strings.getString("to") + " " + message.usertofullname);

		if (isSelected) {
			this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(64, 64, 64, 255)));
			this.setBackground(new Color(200, 218, 235, 255));
		} else {
			this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(64, 64, 64, 255)));
			this.setBackground(new Color(255, 255, 255, 255));
		}
		return this;
	}

}
