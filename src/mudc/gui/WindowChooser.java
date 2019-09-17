package mudc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import mudc.core.Locales;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;

public class WindowChooser extends JPanel {
	
	private static final long serialVersionUID = -9163824976264321637L;

	public WindowChooser(GUI gui, Window window, Locales strings) {
		
		setLayout(null);
		
		window.setTitle(strings.getString("windowchoosertitle"));
		
		JLabel windowChooserLabel = new JLabel(strings.getString("windowchooserstring"), JLabel.CENTER);
		windowChooserLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		add(windowChooserLabel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(5, 1, 0, 0));

		JButton btnDashboard = new JButton(strings.getString("dashboard"));
		JButton btnCourses = new JButton(strings.getString("courses"));
		JButton btnMessages = new JButton(strings.getString("messages"));
		JButton btnTasks = new JButton(strings.getString("tasks"));
		JButton btnSettings = new JButton(strings.getString("notimplemented"));
		btnSettings.setEnabled(false);

		switch (gui.getIconPack()) {
		case 1:
			String iconPath = gui.getIconPackPath(1);
			btnDashboard.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/reload.png")));
			btnCourses.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/course.png")));
			btnMessages.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/messages.png")));
			btnTasks.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/tick.png")));
			/*btnSettings.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/settings.png")));*/
			break;
		default:
			btnDashboard.setIcon(new ImageIcon(
					MainMenuBar.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
			btnCourses.setIcon(new ImageIcon(MainMenuBar.class
					.getResource("/com/sun/javafx/scene/control/skin/modena/HTMLEditor-Paste.png")));
			btnMessages.setIcon(new ImageIcon(
					MainMenuBar.class.getResource("/com/sun/javafx/scene/web/skin/AlignJustified_16x16_JFX.png")));
			btnTasks.setIcon(new ImageIcon(MainMenuBar.class
					.getResource("/com/sun/javafx/scene/web/skin/OrderedListNumbers_16x16_JFX.png")));
			/*btnSettings.setIcon(
					new ImageIcon(MainMenuBar.class.getResource("/javax/swing/plaf/metal/icons/ocean/menu.gif")));*/
		}
		
		ComponentAdapter onResizeChooser = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				int buttonsHeight = height/2;
				int buttonsWidth = 0;
				int fontSize = 16;
				if (width > 512) {
					buttonsWidth = width/2;
					fontSize = 22;
				}
				else {
					buttonsWidth = 256;
				}
				windowChooserLabel.setBounds(0, 0, width, height/2);
				windowChooserLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
				windowChooserLabel.validate();
				buttonPanel.setBounds((width-buttonsWidth)/2, (height-buttonsHeight)*3/4, buttonsWidth, buttonsHeight);
				buttonPanel.validate();
			}
		};
		
		btnDashboard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.setTitle(strings.getString("windowdashboard"));
				window.setContentPane(new TabDashboard(gui, strings));
			}
		});
		btnCourses.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.setTitle(strings.getString("windowcourses"));
				window.setContentPane(new TabCourses(gui, strings, window));
			}
		});
		btnMessages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.setTitle(strings.getString("windowmessages"));
				window.setContentPane(new TabMessages(gui, strings, window));
			}
		});
		btnTasks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.setTitle(strings.getString("windowtasks"));
				window.setContentPane(new TabTasks(gui, strings));
			}
		});
		/*btnSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.setTitle(strings.getString("windowsettings"));
				//window.setContentPane(new TabCourses(gui));
			}
		});*/

		btnDashboard.setBackground(new Color(255, 255, 255, 255));
		btnCourses.setBackground(new Color(255, 255, 255, 255));
		btnMessages.setBackground(new Color(255, 255, 255, 255));
		btnTasks.setBackground(new Color(255, 255, 255, 255));
		btnSettings.setBackground(new Color(255, 255, 255, 255));

		buttonPanel.add(btnDashboard);
		buttonPanel.add(btnCourses);
		buttonPanel.add(btnMessages);
		buttonPanel.add(btnTasks);
		buttonPanel.add(btnSettings);
		
		add(buttonPanel);
		addComponentListener(onResizeChooser);

	}
}
