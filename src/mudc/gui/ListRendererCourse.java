package mudc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import mudc.core.Locales;
import mudc.core.MoodleCourse;
import mudc.core.UpdateComparator;

@SuppressWarnings("serial")
public class ListRendererCourse extends JPanel implements ListCellRenderer<MoodleCourse> {
	private JLabel lblName = null;
	private JLabel lblDescription = null;
	private RoundLabel btnOpen = null;
	private Locales strings = null;
	boolean drawDot = false;
	private Color dotColor = new Color(0, 0, 0, 0);
	
	private Color backgroundColor = Colors.cardCourseBackground;
	private Color foregroundColor = Colors.cardCourseBorder;
	private boolean drawShadow = false;
	private int shadowSize = Dimensions.shadowSize;
	private int bottomSpacing = 0;

	public ListRendererCourse(Locales str) {

		super();
		
		strings = str;

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		btnOpen = new RoundLabel(strings.getString("buttonopen"), SwingConstants.CENTER);
		btnOpen.setBorderColor(Colors.cardCourseButtonBorder);
		springLayout.putConstraint(SpringLayout.NORTH, btnOpen, 8, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnOpen, -64, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnOpen, 40, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnOpen, -8, SpringLayout.EAST, this);
		btnOpen.setForeground(new Color(64, 64, 64, 255));
		//btnOpen.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(128, 128, 128, 255)));
		add(btnOpen);

		lblName = new JLabel();
		lblName.setFont(new Font("Dialog", Font.BOLD, 14));
		lblName.setForeground(new Color(64, 64, 64, 255));
		springLayout.putConstraint(SpringLayout.NORTH, lblName, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblName, 16, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblName, -8, SpringLayout.WEST, btnOpen);
		add(lblName);

		lblDescription = new JLabel();
		lblDescription.setFont(new Font("Dialog", Font.PLAIN, 11));
		lblDescription.setForeground(new Color(64, 64, 64, 255));
		springLayout.putConstraint(SpringLayout.NORTH, lblDescription, 2, SpringLayout.SOUTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, lblDescription, 16, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblDescription, -16, SpringLayout.WEST, btnOpen);
		add(lblDescription);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MoodleCourse> list, MoodleCourse course, int index,
			boolean isSelected, boolean cellHasFocus) {

		lblName.setText(course.shortname);
		lblDescription.setText(course.fullname);
		
		int status = (int) course.status;
		if (status == UpdateComparator.STATUS_NEW) {
			dotColor = Colors.cardCourseNewDot;
			drawDot = true;
		}
		else if (status == UpdateComparator.STATUS_UPDATED) {
			dotColor = Colors.cardCourseUpdatedDot;
			drawDot = true;
		}
		else if (status == UpdateComparator.STATUS_DELETED){
			lblDescription.setText(course.fullname + "  " + strings.getString("deleted"));
			drawDot = false;
		}
		else {
			drawDot = false;
		}
		
		if (isSelected) {
			backgroundColor = Colors.cardCourseSelected;
		} else if (status == UpdateComparator.STATUS_DELETED){
			backgroundColor = Colors.cardCourseDeleted;
			lblDescription.setText(course.fullname + "  " + strings.getString("deleted"));
		}
		else {
			backgroundColor = Colors.cardCourseBackground;
		}
		return this;
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
		
		// "New" dot
		if (drawDot) {
			g2d.setColor(dotColor);
			g2d.fillRoundRect(4, 4, 8, finalHeight-8, 8, 8); // paint border
		}
	}
	
	public int getTotalContentHeight() {
		return (int) 48 + shadowSize + bottomSpacing;
	}

}
