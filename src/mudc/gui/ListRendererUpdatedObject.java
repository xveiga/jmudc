package mudc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import mudc.core.Locales;
import mudc.core.UpdatedObject;
import mudc.core.UpdatedValue;
import mudc.core.dataelements.MoodleCategory;
import mudc.core.dataelements.MoodleCourse;
import mudc.core.dataelements.MoodleElement;
import mudc.core.dataelements.MoodleInfo;
import mudc.core.dataelements.MoodleModule;

@SuppressWarnings("serial")
public class ListRendererUpdatedObject extends JPanel implements ListCellRenderer<UpdatedObject> {
	private JLabel lblName = null;
	private JLabel lblDescription = null;
	private JLabel btnOpen = null;
	private Locales strings = null;

	public ListRendererUpdatedObject(Locales str) {

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
	public Component getListCellRendererComponent(JList<? extends UpdatedObject> list, UpdatedObject object, int index,
			boolean isSelected, boolean cellHasFocus) {

		String name = "(no name)";
		String description = "";
		
		if (object.moodleObject instanceof MoodleInfo) {
			MoodleInfo info = (MoodleInfo) object.moodleObject;
			name = info.sitename + " site information";
			description = "Status: not implemented yet";
		} else if (object.moodleObject instanceof MoodleCourse) {
			MoodleCourse course = (MoodleCourse) object.moodleObject;
			name = course.shortname;
			description = "";
			for (UpdatedValue v : object.changedValues) {
				description += v.name + ": " + v.oldValue + " -> " + v.newValue + "; ";
			}
		} else if (object.moodleObject instanceof MoodleCategory) {
			MoodleCategory course = (MoodleCategory) object.moodleObject;
			name = course.name;
			description = "Status: not implemented yet";
		} else if (object.moodleObject instanceof MoodleModule) {
			MoodleModule course = (MoodleModule) object.moodleObject;
			name = course.name;
			description = "Status: not implemented yet";
		} else if (object.moodleObject instanceof MoodleElement) {
			MoodleElement course = (MoodleElement) object.moodleObject;
			name = course.filename;
			description = "Status: not implemented yet";
		}
		lblName.setText(name);
		lblDescription.setText(description);

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
