package mudc.gui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.border.MatteBorder;

import mudc.core.Locales;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class MainMenuBar extends JPanel {

	private static final long serialVersionUID = -7853638828027751770L;
	private boolean narrowMode = false;

	private MainMenuBarButton btnDashboard = null;
	private MainMenuBarButton btnCourses = null;
	private MainMenuBarButton btnMessages = null;
	private MainMenuBarButton btnTasks = null;
	private MainMenuBarButton btnMenu = null;
	
	private String dashboardStr = null;
	private String coursesStr = null;
	private String messagesStr = null;
	
	JMenuItem newWindowItem = null;
	JMenuItem closeWindowsItem = null;
	JMenuItem updateAllItem = null;
	JMenuItem settingsItem = null;
	
	private ComponentAdapter componentAdapter = null;
	private GUI gui;
	private Locales strings = null;

	public MainMenuBar(GUI g, MainPanel mainPanel, Locales str) {

		MainMenuBar thisInstance = this;
		gui = g;
		strings = str;

		setLayout(null);
		setBackground(Colors.inactiveTabBackground);

		dashboardStr = strings.getString("dashboard");
		coursesStr = strings.getString("courses");
		messagesStr = strings.getString("messages");

		btnDashboard = new MainMenuBarButton();
		btnCourses   = new MainMenuBarButton();
		btnMessages  = new MainMenuBarButton();
		btnTasks     = new MainMenuBarButton();
		btnMenu      = new MainMenuBarButton();
		
		btnDashboard.setBorderPainted(false);
		btnCourses  .setBorderPainted(false);
		btnMessages .setBorderPainted(false);
		btnTasks    .setBorderPainted(false);
		btnMenu     .setBorderPainted(false);
		
		btnDashboard.setButtonColors(Colors.selectedTabBackground, Colors.inactiveTabBackground);
		btnCourses  .setButtonColors(Colors.selectedTabBackground, Colors.inactiveTabBackground);   
		btnMessages .setButtonColors(Colors.selectedTabBackground, Colors.inactiveTabBackground);   
		btnTasks    .setButtonColors(Colors.selectedTabBackground, Colors.inactiveTabBackground);   
		btnMenu     .setButtonColors(Colors.selectedTabBackground, Colors.inactiveTabBackground);   

		switch (gui.getIconPack()) {
		case 1:
			String iconPath = gui.getIconPackPath(1);
			btnDashboard.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/reload.png")));
			btnCourses.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/course.png")));
			btnMessages.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/messages.png")));
			btnTasks.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/tick.png")));
			btnMenu.setIcon(new ImageIcon(MainMenuBar.class.getResource(iconPath + "elements/settings.png")));
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
			btnMenu.setIcon(
					new ImageIcon(MainMenuBar.class.getResource("/javax/swing/plaf/metal/icons/ocean/menu.gif")));
		}

		// PopupMenu
		JPopupMenu menu = new JPopupMenu();
		menu.setBackground(Colors.menuBarPopupBackground);
		menu.setBorder(new MatteBorder(1, 1, 1, 1, new Color(128, 128, 128, 255)));
		newWindowItem = new JMenuItem();
		newWindowItem.setBackground(Colors.menuBarPopupBackground);
		newWindowItem.setForeground(Colors.menuBarPopupText);
		newWindowItem.setBorder(new MatteBorder(Dimensions.titleBarPopupBorder.width, Dimensions.titleBarPopupBorder.height, Dimensions.titleBarPopupBorder.width, Dimensions.titleBarPopupBorder.height, Colors.menuBarPopupBackground));
		newWindowItem.setText(strings.getString("newwindow"));
		newWindowItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.openNewWindow();
			}
		});
		//TODO: below line is not safe, handle on window
		//newWindowItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menu.add(newWindowItem);
		closeWindowsItem = new JMenuItem();
		closeWindowsItem.setBackground(Colors.menuBarPopupBackground);
		closeWindowsItem.setForeground(Colors.menuBarPopupText);
		closeWindowsItem.setBorder(new MatteBorder(Dimensions.titleBarPopupBorder.width, Dimensions.titleBarPopupBorder.height, Dimensions.titleBarPopupBorder.width, Dimensions.titleBarPopupBorder.height, Colors.menuBarPopupBackground));
		closeWindowsItem.setText(strings.getString("closeallwindows"));
		closeWindowsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.closeAllWindows();
			}
		});
		//TODO: below line is not safe, handle on window
		//closeWindowsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
		menu.add(closeWindowsItem);
		
		JSeparator sep = new JSeparator();
		sep.setBackground(Colors.menuBarPopupBackground);
		sep.setForeground(new Color(128, 128, 128, 255));
		menu.add(sep);
		
		updateAllItem = new JMenuItem();
		updateAllItem.setBackground(Colors.menuBarPopupBackground);
		updateAllItem.setForeground(Colors.menuBarPopupText);
		updateAllItem.setBorder(new MatteBorder(Dimensions.titleBarPopupBorder.width, Dimensions.titleBarPopupBorder.height, Dimensions.titleBarPopupBorder.width, Dimensions.titleBarPopupBorder.height, Colors.menuBarPopupBackground));
		updateAllItem.setText(strings.getString("updateall"));
		updateAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.updateAll();
			}
		});
		//TODO: below line is not safe, handle on window
		//updateAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.SHIFT_MASK));
		menu.add(updateAllItem);
		
		JSeparator sep2 = new JSeparator();
		sep2.setBackground(Colors.menuBarPopupBackground);
		sep2.setForeground(new Color(128, 128, 128, 255));
		menu.add(sep2);
		
		settingsItem = new JMenuItem();
		settingsItem.setBackground(Colors.menuBarPopupBackground);
		settingsItem.setForeground(Colors.menuBarPopupText);
		settingsItem.setBorder(new MatteBorder(Dimensions.titleBarPopupBorder.width, Dimensions.titleBarPopupBorder.height, Dimensions.titleBarPopupBorder.width, Dimensions.titleBarPopupBorder.height, Colors.menuBarPopupBackground));
		settingsItem.setText(strings.getString("settings"));
		settingsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//onTabChange(4);
				System.out.println("[GUI Menu Bar] Started change to position 4 (Settings)");
				mainPanel.slideTo(4);
			}
		});
		menu.add(settingsItem);

		btnDashboard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//onTabChange(0);
				System.out.println("[GUI Menu Bar] Started change to position 0 (Dashboard)");
				mainPanel.slideTo(0);
			}
		});
		btnCourses.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//onTabChange(1);
				System.out.println("[GUI Menu Bar] Started change to position 1 (Courses)");
				mainPanel.slideTo(1);
			}
		});
		btnMessages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//onTabChange(2);
				System.out.println("[GUI Menu Bar] Started change to position 2 (Messages)");
				mainPanel.slideTo(2);
			}
		});
		btnTasks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//onTabChange(3);
				System.out.println("[GUI Menu Bar] Started change to position 3 (Tasks)");
				mainPanel.slideTo(3);
			}
		});
		btnMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("[GUI Menu Bar] Context menu open");
				menu.show(thisInstance,
						(int) (thisInstance.getWidth()
								- (menu.getPreferredSize().width + Dimensions.titleBarPopupPosition.getWidth())),
						(int) Dimensions.titleBarPopupPosition.getHeight());
			}
		});

		onTabChange(0);
		
		add(btnDashboard);
		add(btnDashboard);
		add(btnCourses);
		add(btnMessages);
		add(btnTasks);
		add(btnMenu);

		componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				if (width >= Dimensions.mainWindowNarrowModeSize) {
					if (narrowMode) {
						btnDashboard.setText(dashboardStr);
						btnCourses.setText(coursesStr);
						btnMessages.setText(messagesStr);
						narrowMode = false;
					}
					int smallButtonSize = Math.round(height * Dimensions.titleBarSmallButtonWidthFactor);
					double btnTasksX = width - 2 * height * Dimensions.titleBarSmallButtonWidthFactor;
					double tabButtonSize = btnTasksX / 3d;
					btnDashboard.setBounds(0, 0, (int) tabButtonSize, height);
					btnCourses.setBounds((int) Math.round(tabButtonSize), 0, (int) Math.round(tabButtonSize), height);
					btnMessages.setBounds((int) Math.round(tabButtonSize * 2), 0, (int) Math.round(tabButtonSize),
							height);
					btnTasks.setBounds((int) Math.round(btnTasksX), 0, smallButtonSize, height);
					btnMenu.setBounds(width - smallButtonSize, 0, smallButtonSize, height);
				} else {
					if (!narrowMode) {
						btnDashboard.setText("");
						btnCourses.setText("");
						btnMessages.setText("");
						narrowMode = true;
					}
					double buttonPos = width / 5d;
					int buttonSize = (int) Math.round(buttonPos);
					btnDashboard.setBounds(0, 0, buttonSize, height);
					btnCourses.setBounds((int) Math.round(buttonPos), 0, buttonSize, height);
					btnMessages.setBounds((int) Math.round(buttonPos * 2), 0, buttonSize, height);
					btnTasks.setBounds((int) Math.round(buttonPos * 3), 0, buttonSize, height);
					btnMenu.setBounds((int) Math.round(buttonPos * 4), 0, buttonSize, height);
				}
			}
		};
		addComponentListener(componentAdapter);

		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
	}

	public void onTabChange(int position) {
		switch (position) {
		case 0:
			btnDashboard.setButtonSelected(true);
			btnCourses  .setButtonSelected(false);
			btnMessages .setButtonSelected(false);
			btnTasks    .setButtonSelected(false);
			btnMenu     .setButtonSelected(false);
			
			btnDashboard.setForeground(Colors.selectedTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;
		case 1:
			btnDashboard.setButtonSelected(false);
			btnCourses  .setButtonSelected(true);
			btnMessages .setButtonSelected(false);
			btnTasks    .setButtonSelected(false);
			btnMenu     .setButtonSelected(false);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.selectedTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;
		case 2:
			btnDashboard.setButtonSelected(false);
			btnCourses  .setButtonSelected(false);
			btnMessages .setButtonSelected(true);
			btnTasks    .setButtonSelected(false);
			btnMenu     .setButtonSelected(false);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.selectedTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;
		case 3:
			btnDashboard.setButtonSelected(false);
			btnCourses  .setButtonSelected(false);
			btnMessages .setButtonSelected(false);
			btnTasks    .setButtonSelected(true);
			btnMenu     .setButtonSelected(false);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.selectedTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;
		case 4:
			btnDashboard.setButtonSelected(false);
			btnCourses  .setButtonSelected(false);
			btnMessages .setButtonSelected(false);
			btnTasks    .setButtonSelected(false);
			btnMenu     .setButtonSelected(true);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.selectedTabText);
			break;
		default:
			btnDashboard.setButtonSelected(false);
			btnCourses  .setButtonSelected(false);
			btnMessages .setButtonSelected(false);
			btnTasks    .setButtonSelected(false);
			btnMenu     .setButtonSelected(false);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;

		}
		/*switch (position) {
		case 0:
			btnDashboard.setBackground(Colors.selectedTabBackground);
			btnCourses.setBackground(Colors.inactiveTabBackground);
			btnMessages.setBackground(Colors.inactiveTabBackground);
			btnTasks.setBackground(Colors.inactiveTabBackground);
			btnMenu.setBackground(Colors.inactiveTabBackground);
			
			btnDashboard.setForeground(Colors.selectedTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;
		case 1:
			btnDashboard.setBackground(Colors.inactiveTabBackground);
			btnCourses.setBackground(Colors.selectedTabBackground);
			btnMessages.setBackground(Colors.inactiveTabBackground);
			btnTasks.setBackground(Colors.inactiveTabBackground);
			btnMenu.setBackground(Colors.inactiveTabBackground);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.selectedTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;
		case 2:
			btnDashboard.setBackground(Colors.inactiveTabBackground);
			btnCourses.setBackground(Colors.inactiveTabBackground);
			btnMessages.setBackground(Colors.selectedTabBackground);
			btnTasks.setBackground(Colors.inactiveTabBackground);
			btnMenu.setBackground(Colors.inactiveTabBackground);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.selectedTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;
		case 3:
			btnDashboard.setBackground(Colors.inactiveTabBackground);
			btnCourses.setBackground(Colors.inactiveTabBackground);
			btnMessages.setBackground(Colors.inactiveTabBackground);
			btnTasks.setBackground(Colors.selectedTabBackground);
			btnMenu.setBackground(Colors.inactiveTabBackground);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.selectedTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;
		case 4:
			btnDashboard.setBackground(Colors.inactiveTabBackground);
			btnCourses.setBackground(Colors.inactiveTabBackground);
			btnMessages.setBackground(Colors.inactiveTabBackground);
			btnTasks.setBackground(Colors.inactiveTabBackground);
			btnMenu.setBackground(Colors.selectedTabBackground);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.selectedTabText);
			break;
		default:
			btnDashboard.setBackground(Colors.inactiveTabBackground);
			btnCourses.setBackground(Colors.inactiveTabBackground);
			btnMessages.setBackground(Colors.inactiveTabBackground);
			btnTasks.setBackground(Colors.inactiveTabBackground);
			btnMenu.setBackground(Colors.inactiveTabBackground);
			
			btnDashboard.setForeground(Colors.inactiveTabText);
			btnCourses.setForeground(Colors.inactiveTabText);
			btnMessages.setForeground(Colors.inactiveTabText);
			btnTasks.setForeground(Colors.inactiveTabText);
			btnMenu.setForeground(Colors.inactiveTabText);
			break;

		}*/
		System.out.println("[GUI Menu Bar] Changed to position: " + position);
	}
	
	public void onLocaleChange() {
		dashboardStr = strings.getString("dashboard");
		coursesStr = strings.getString("courses");
		messagesStr = strings.getString("messages");
		btnDashboard.setText(dashboardStr);
		btnCourses.setText(coursesStr);
		btnMessages.setText(messagesStr);
		newWindowItem.setText(strings.getString("newwindow"));
		closeWindowsItem.setText(strings.getString("closeallwindows"));
		updateAllItem.setText(strings.getString("updateall"));
		settingsItem.setText(strings.getString("settings"));
	}

}
