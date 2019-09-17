package mudc.gui;

import javax.swing.JPanel;

import mudc.core.Locales;
import mudc.core.dataelements.MoodleCourse;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@SuppressWarnings("serial")
public class TabCourses extends JPanel {

	private PanelCourses coursesPanel = null;
	private PanelFiles filePanel = null;
	private GUI core = null;
	private Window window = null;
	private Cursor normalCursor = null;
	private Cursor loadingCursor = null;
	private Locales strings = null;
	private boolean currentTab = false;

	public TabCourses(GUI coreSystem, Locales str, Window w) {
		core = coreSystem;
		strings = str;
		window = w;
		coursesPanel = new PanelCourses(core, strings, this);
		setLayout(new BorderLayout(0, 0));
		add(coursesPanel, BorderLayout.CENTER);
		coursesPanel.focus();

		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				coursesPanel.revalidate();
				if (filePanel != null) {
					filePanel.revalidate();
				}
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

		normalCursor = Cursor.getDefaultCursor();
		loadingCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	}

	public void changeToFilesPanel(MoodleCourse course) {
		if (!currentTab) {
			System.out.println("[TabCourses] Change to files panel started.");
			setCursor(loadingCursor);
			window.setTitle(strings.getString("windowfiles") + course.shortname);
			filePanel = new PanelFiles(core, strings, this, course);
			remove(coursesPanel);
			add(filePanel, BorderLayout.CENTER);
			currentTab = true;
			filePanel.revalidate();
			filePanel.focus();
			filePanel.repaint();
			setCursor(normalCursor);
		}
	}

	public void changeToCoursesPanel() {
		if (currentTab) {
			System.out.println("[TabCourses] Change to courses panel started.");
			setCursor(loadingCursor);
			window.setTitle(strings.getString("windowcourses"));
			remove(filePanel);
			add(coursesPanel, BorderLayout.CENTER);
			currentTab = false;
			coursesPanel.revalidate();
			coursesPanel.focus();
			coursesPanel.repaint();
			setCursor(normalCursor);
		}
	}

	public void focus() {
		if (currentTab) {
			filePanel.focus();
		} else {
			coursesPanel.focus();
		}
	}
	
	public JPanel getCurrentPanel() {
		if (currentTab) {
			return filePanel;
		}
		else {
			return coursesPanel;
		}
	}

	public PanelCourses getCoursesPanel() {
		return coursesPanel;
	}

	public PanelFiles getFilesPanel() {
		return filePanel;
	}
	
	public void setCurrentWindow(Window w) {
		window = w;
	}
}
