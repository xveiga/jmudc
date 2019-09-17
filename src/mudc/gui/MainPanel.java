package mudc.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import mudc.core.Locales;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 5831820456987214318L;

	private static final int animationSteps = 512; // TODO: Move to settings 48
	private static final int animationStepDelay = 5; // 5
	private static final float animationAcceleration = 2f;

	private TabPanelAnimator slidingPanel;
	private MainMenuBar titleBar;
	
	private TabDashboard dashboardPanel = null;
	private TabCourses coursesPanel = null;
	private TabMessages messagesPanel = null;
	private TabTasks tasksPanel = null;
	private TabSettings settingsPanel = null;
	private int lastPos = 0;

	public MainPanel(GUI gui, Locales strings, Window window) {
		setLayout(null);
		setBackground(Colors.defaultBackground);

		dashboardPanel = new TabDashboard(gui, strings);
		coursesPanel = new TabCourses(gui, strings, window);
		messagesPanel = new TabMessages(gui, strings, window);
		tasksPanel = new TabTasks(gui, strings);
		settingsPanel = new TabSettings(gui, strings);

		titleBar = new MainMenuBar(gui, this, strings);
		add(titleBar);

		slidingPanel = new TabPanelAnimator(dashboardPanel, coursesPanel, messagesPanel, tasksPanel, settingsPanel);
		slidingPanel.setAnimationEnabled(gui.getAnimationsEnabled());
		slidingPanel.setOnFinishRunnable(new Runnable() {
			@Override
			public void run() {
				titleBar.onTabChange(slidingPanel.getCurrentPosition());
			}
		});
		add(slidingPanel);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				titleBar.setBounds(0, 0, getWidth(), Dimensions.titleBarHeight);
				titleBar.validate();
				slidingPanel.setBounds(0, Dimensions.titleBarHeight, getWidth(),
						getHeight() - Dimensions.titleBarHeight);
				slidingPanel.onResize(getWidth(), getHeight() - Dimensions.titleBarHeight);
			}
		});
	}

	public void slideTo(int position) {
		lastPos = slidingPanel.getCurrentPosition();
		titleBar.onTabChange(position);
		slidingPanel.slideTo(position, animationSteps, animationStepDelay, animationAcceleration);
	}
	
	public void swapTab() {
		lastPos = slidingPanel.getCurrentPosition();
		slideTo(lastPos);
	}
	
	public void back() {
		switch (slidingPanel.getCurrentPosition()) {
		case 1:
				coursesPanel.changeToCoursesPanel();
				break;
		case 2:
				messagesPanel.changeToMessagesPanel();
				break;
		case 3:
				slideTo(lastPos);
				break;
		case 4:
				slideTo(lastPos);
				break;
		}
	}
	
	public JPanel getCurrentTab() {
		switch(slidingPanel.getCurrentPosition()) {
		case 0:
			return dashboardPanel;
		case 1:
			return coursesPanel;
		case 2:
			return messagesPanel;
		case 3:
			return tasksPanel;
		case 4:
			return settingsPanel;
		default:
				return null;
		}
	}
	
	public void setAnimationsEnabled(boolean enabled) {
		slidingPanel.setAnimationEnabled(enabled);
	}
	
	public void onLocaleChanged() {
		titleBar.onLocaleChange();
	}
	
	public TabDashboard getDashboardTab() {
		return dashboardPanel;
	}
	
	public TabCourses getCoursesTab() {
		return coursesPanel;
	}
	
	public TabMessages getMessagesTab() {
		return messagesPanel;
	}
	
	public TabTasks getTasksTab() {
		return tasksPanel;
	}
	
	public TabSettings getSettingsTab() {
		return settingsPanel;
	}

}
