package mudc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import mudc.core.Locales;
import mudc.core.dataelements.MoodleCourse;

public class ToolbarFiles extends JPanel {

	private static final long serialVersionUID = 194791458811221002L;
	
	private GUI gui = null;
	private boolean drawShadow = false;
	private int shadowSize = Dimensions.shadowSize;
	private Color backgroundColor = new Color(255, 255, 255, 255);
	private Color foregroundColor = new Color(192, 192, 192, 255);
	private PanelFiles panel = null;

	public ToolbarFiles(GUI g, Locales strings, PanelFiles p, MoodleCourse course) {
		gui = g;
		panel = p;
		setBackground(new Color(255, 255, 255, 255));
        //setBorder(new MatteBorder(1, 1, 1, 1, new Color(64, 64, 64, 255)));
        SpringLayout sl = new SpringLayout();
        setLayout(sl);

        JButton backButton = new JButton(""); //TODO: Custom button class
        backButton.setBorderPainted(false);
        backButton.setBackground(Colors.panelFilesBackground);
        backButton.setIcon(new ImageIcon(PanelFiles.class.getResource("/com/sun/javafx/scene/control/skin/caspian/fxvk-backspace-button.png")));
        sl.putConstraint(SpringLayout.NORTH, backButton, 2, SpringLayout.NORTH, this);
        sl.putConstraint(SpringLayout.WEST, backButton, 2, SpringLayout.WEST, this);
        sl.putConstraint(SpringLayout.EAST, backButton, 34, SpringLayout.WEST, this);
        sl.putConstraint(SpringLayout.SOUTH, backButton, -2-shadowSize, SpringLayout.SOUTH, this);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            	panel.backCourses();
            }
        });
        
        add(backButton);

        JButton updateButton = new JButton();
        
        JLabel courseTitle = new JLabel(course.fullname);
        courseTitle.setFont(new Font("Tahoma", Font.PLAIN, 14));
        courseTitle.setHorizontalAlignment(SwingConstants.CENTER);
        sl.putConstraint(SpringLayout.NORTH, courseTitle, 0, SpringLayout.NORTH, this);
        sl.putConstraint(SpringLayout.WEST, courseTitle, 8, SpringLayout.EAST, backButton);
        sl.putConstraint(SpringLayout.SOUTH, courseTitle, -shadowSize, SpringLayout.SOUTH, this);
        sl.putConstraint(SpringLayout.EAST, courseTitle, -8, SpringLayout.WEST, updateButton);

        add(courseTitle);

        updateButton.setBorderPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.setIcon(new ImageIcon(PanelFiles.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
        sl.putConstraint(SpringLayout.NORTH, updateButton, 2, SpringLayout.NORTH, this);
        sl.putConstraint(SpringLayout.WEST, updateButton, -34, SpringLayout.EAST, this);
        sl.putConstraint(SpringLayout.SOUTH, updateButton, -2-shadowSize, SpringLayout.SOUTH, this);
        sl.putConstraint(SpringLayout.EAST, updateButton, -2, SpringLayout.EAST, this);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //core.updateMoodleCourseContents(course); // TODO: link to updateMoodleCourseContents
            	gui.updateCourseContents(course);
            }
        });
        add(updateButton);
	}
	
	public void onPropertiesChanged() {
		drawShadow = panel.getShadowsEnabled();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension arcs = Dimensions.toolbarBorderArc; // Border arc size
		int bWidth = getWidth()-1;
		int bHeight = getHeight()-1;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int shadowHeight = bHeight;
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
	
	

}
