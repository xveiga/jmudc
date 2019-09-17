package mudc.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Window {

	private JFrame frame;
	private GUI gui;
	private JPanel contentPanel;
	private Window thisInstance = null;
	private Dimension size = null;
	private Runnable onVisibleRunnable = null;

	public Window(GUI c, Dimension defaultSize) {
		thisInstance = this;
		gui = c;
		size = defaultSize;
	}
	
	public Window(GUI c, JPanel contentPanel, Dimension defaultSize) {
		thisInstance = this;
		gui = c;
		size = defaultSize;
		initialize(contentPanel, defaultSize, Dimensions.windowMinSize);
	}
	
	public Window(GUI c, JPanel contentPanel, Dimension defaultSize, Dimension minSize) {
		thisInstance = this;
		gui = c;
		size = defaultSize;
		initialize(contentPanel, defaultSize, minSize);
	}
	
	public void initialize(JPanel panel, Dimension minimumSize) {
		initialize(panel, size, minimumSize);
	}

	private void initialize(JPanel panel, Dimension defaultSize, Dimension minimumSize) {
		contentPanel = panel;
		frame = new JFrame();
		frame.setMinimumSize(minimumSize);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (contentPanel != null) setContentPane(contentPanel);
		Window thisWindow = this;
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				gui.onWindowClose(thisWindow);
				frame.dispose();
			}
		});
		frame.setSize((int) Math.round(defaultSize.getWidth()),
				(int) Math.round(defaultSize.getHeight()));
		frame.setLocationRelativeTo(null);
	}
	
	private void mainPanelSlide(JPanel panel, int position) {
		if (panel instanceof MainPanel) {
			MainPanel main = (MainPanel) panel;
			main.slideTo(position);
		}
	}
	
	private void mainPanelBack(JPanel panel) {
		if (panel instanceof MainPanel) {
			MainPanel main = (MainPanel) panel;
			main.back();
		}
		else if (panel instanceof TabCourses) {
			TabCourses courseTab = (TabCourses) panel;
			courseTab.changeToCoursesPanel();
		}
		else if (panel instanceof TabMessages) {
			TabMessages messageTab = (TabMessages) panel;
			messageTab.changeToMessagesPanel();
		}
	}
	
	private void mainPanelUpdateCurrent() {
		gui.updateCurrent(contentPanel);
	}

	public void launch() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					frame.setVisible(true);
					if (onVisibleRunnable != null) {
						onVisibleRunnable.run();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void dispose() {
		frame.dispose();
		gui.onWindowClose(this);
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public void setContentPane(JPanel contentPane) {
		contentPanel = contentPane;
		frame.setContentPane(contentPanel);
		frame.revalidate();
		
		AbstractAction F1Action = new AbstractAction() {

			private static final long serialVersionUID = -1738898908616088925L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] F1 pressed");
			}
		};
		AbstractAction F2Action = new AbstractAction() {

			private static final long serialVersionUID = -2338292875530896250L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] F2 pressed");	
				mainPanelSlide(contentPanel, 0);
			}
		};
		AbstractAction F3Action = new AbstractAction() {
			
			private static final long serialVersionUID = 2724448125611535073L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] F3 pressed");	
				mainPanelSlide(contentPanel, 1);
			}
		};
		AbstractAction F4Action = new AbstractAction() {
			
			private static final long serialVersionUID = 3340777525878798930L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] F4 pressed");	
				mainPanelSlide(contentPanel, 2);
			}
		};
		AbstractAction F5Action = new AbstractAction() {

			private static final long serialVersionUID = -2903672040661472283L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] F5 pressed");	
				mainPanelUpdateCurrent();
			}
		};
		AbstractAction F6Action = new AbstractAction() {

			private static final long serialVersionUID = -8088245759409581891L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] F6 pressed");	
			}
		};
		AbstractAction F7Action = new AbstractAction() {

			private static final long serialVersionUID = 4477026692584583312L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] F7 pressed");	
				mainPanelSlide(contentPanel, 3);
			}
		};
		AbstractAction F8Action = new AbstractAction() {

			private static final long serialVersionUID = 7275429918249915227L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] F8 pressed");	
				mainPanelSlide(contentPanel, 4);
			}
		};
		AbstractAction EscAction = new AbstractAction() {

			private static final long serialVersionUID = 3975780427775757324L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] Esc pressed");	
				mainPanelBack(contentPanel);
			}
		};
		AbstractAction ShiftF5Action = new AbstractAction() {

			private static final long serialVersionUID = -4840531915096096595L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] Shift+F5 pressed");	
				gui.updateAll();
			}
		};
		AbstractAction CtrlNAction = new AbstractAction() {

			private static final long serialVersionUID = 4429969116716115820L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] Ctrl+N pressed");	
				gui.openNewWindow();
			}
		};
		AbstractAction CtrlWAction = new AbstractAction() {

			private static final long serialVersionUID = 7170231041939754382L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] Ctrl+W pressed");	
				gui.closeAllWindows();
			}
		};
		AbstractAction CtrlQAction = new AbstractAction() {

			private static final long serialVersionUID = 6539782783425518093L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] Ctrl+Q pressed");	
				gui.closeWindow(thisInstance);
			}
		};
		AbstractAction CtrlSAction = new AbstractAction() {

			private static final long serialVersionUID = 3537725157766230742L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Window] Ctrl+S pressed");	
				gui.saveData();
			}
		};
		InputMap keyMap = contentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actMap = contentPanel.getActionMap();
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "F1Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "F2Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "F3Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "F4Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "F5Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "F6Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "F7Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "F8Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "EscAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.SHIFT_DOWN_MASK), "ShiftF5Action");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "CtrlNAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), "CtrlWAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "CtrlQAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "CtrlBAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "CtrlSAction");
		actMap.put("F1Action", F1Action);
		actMap.put("F2Action", F2Action);
		actMap.put("F3Action", F3Action);
		actMap.put("F4Action", F4Action);
		actMap.put("F5Action", F5Action);
		actMap.put("F6Action", F6Action);
		actMap.put("F7Action", F7Action);
		actMap.put("F8Action", F8Action);
		actMap.put("EscAction", EscAction);
		actMap.put("ShiftF5Action", ShiftF5Action);
		actMap.put("CtrlNAction", CtrlNAction);
		actMap.put("CtrlWAction", CtrlWAction);
		actMap.put("CtrlQAction", CtrlQAction);
		actMap.put("CtrlBAction", EscAction);
		actMap.put("CtrlSAction", CtrlSAction);
	}
	
	public void setOnTop() {
		frame.toFront();
		frame.requestFocus();
	}
	
	public void setOnVisibleRunnable(Runnable run) {
		onVisibleRunnable = run;
	}
	
	public Container getContentPane() {
		return frame.getContentPane();
	}
	
	public void setTitle(String title) {
		frame.setTitle(title);
	}
	
	public String getTitle() {
		return frame.getTitle();
	}

}
