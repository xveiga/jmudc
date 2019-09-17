package mudc.gui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import mudc.core.Locales;
import mudc.core.dataelements.MoodleCourse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;

import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class PanelCourses extends JPanel {

	private JList<MoodleCourse> list = null;
	private ListRendererCourse courseListRenderer = null;
	private GUI gui = null;
	private ToolbarCourses toolbarPanel = null;

	public PanelCourses(GUI g, Locales strings, TabCourses courseTab) {

		gui = g;
		PanelCourses thisInstance = this;

		setLayout(null);
		setBackground(new Color(255, 255, 255, 255));

		toolbarPanel = new ToolbarCourses(gui, strings, this);
		add(toolbarPanel);

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setBackground(new Color(255, 255, 255, 255));
		scrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(16); // TODO: Save in settings
		scrollPanel.getVerticalScrollBar().setUI(new CustomScrollBar());
		scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(16, -1));

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(0, 0));
		list = new JList<MoodleCourse>();
		courseListRenderer = new ListRendererCourse(strings);
		courseListRenderer.setBottomSpacing(Dimensions.courseListBottomSpacing);
		courseListRenderer.setDrawShadow(gui.getShadowsEnabled());
		list.setCellRenderer(courseListRenderer);
		list.setFixedCellHeight(courseListRenderer.getTotalContentHeight());

		JPopupMenu coursePopup = new JPopupMenu();

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1) {
					if (isInsideBounds(event.getX(), event.getY(), list.getBounds().width - 64,
							list.getBounds().width - 8, list.getBounds().y, list.getBounds().height)) {
						if (list.getSelectedIndices().length == 1) {
							courseTab.changeToFilesPanel(list.getSelectedValue());
						} else {
							// ERROR
						}
					}
				}
			}

		});
		list.addKeyListener(new KeyListener() {

			boolean enterPressed = false;
			boolean spacePressed = false;

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					enterPressed = true;
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					spacePressed = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (enterPressed) {
					enterPressed = false;
					spacePressed = false;
					if (list.getSelectedIndices().length == 1) {
						courseTab.changeToFilesPanel(list.getSelectedValue());
					} else {
						// ERROR
					}
				} else if (spacePressed) {
					enterPressed = false;
					spacePressed = false;
					if (list.getSelectedIndices().length == 1) {
						coursePopup.show(thisInstance, thisInstance.getWidth() / 4,
								Dimensions.titleBarHeight + toolbarPanel.getHeight()
										+ courseListRenderer.getTotalContentHeight() * list.getSelectedIndex());

					} else {
						// ERROR
					}
				}
			}
		});

		coursePopup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

				List<MoodleCourse> selectedList = list.getSelectedValuesList();

				JMenuItem updateItem = new JMenuItem(strings.getString("update"));
				updateItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						for (MoodleCourse c : selectedList) {
							gui.updateCourseContents(c);
						}
					}
				});
				coursePopup.add(updateItem);

				JMenuItem downloadItem = new JMenuItem(strings.getString("downloadfiles"));
				downloadItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						for (MoodleCourse c : selectedList) {
							gui.downloadAllFiles(c);
						}
					}
				});
				coursePopup.add(downloadItem);

				if (selectedList.size() == 1) {

					coursePopup.addSeparator();

					JMenuItem renameItem = new JMenuItem(strings.getString("rename"));
					renameItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							// TODO add localname to MoodleCourse
						}
					});
					coursePopup.add(renameItem);

					JMenuItem deleteItem = new JMenuItem(strings.getString("delete"));
					deleteItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							// TODO show warning: Are you sure you want to delete %name% and all of its
							// data? Course will reappear if it still exists on Moodle
						}
					});
					coursePopup.add(deleteItem);

					coursePopup.addSeparator();

					JMenuItem detailsItem = new JMenuItem(strings.getString("details"));
					detailsItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							DetailWindowCourse details = new DetailWindowCourse(selectedList.get(0));
							details.setVisible(true);
						}
					});
					coursePopup.add(detailsItem);
				}

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				coursePopup.removeAll();

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

		});

		list.setComponentPopupMenu(coursePopup);
		contentPanel.add(list);
		scrollPanel.setViewportView(contentPanel);

		add(scrollPanel);

		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				toolbarPanel.setBounds(2, 4, getWidth() - 4, 32);
				toolbarPanel.validate();
				scrollPanel.setBounds(4, 36, getWidth()-4, getHeight() - toolbarPanel.getHeight() - 4);
				scrollPanel.validate();
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

		gui.registerCoursesPanel(this);

		List<MoodleCourse> courseList = gui.getCourses();
		if (courseList != null) {
			onRefresh(courseList);
		}
	}

	public void onRefresh(List<MoodleCourse> courseList) {
		DefaultListModel<MoodleCourse> listModel = new DefaultListModel<>();
		if (courseList != null && courseList.size() != 0) {
			for (MoodleCourse course : courseList) {
				listModel.addElement(course);
			}
		}
		list.setModel(listModel);
		list.validate();
		if (courseList.size() > 0) {
			list.setSelectedIndex(0);
		}
		list.repaint();
	}
	
	public void onPropertiesChanged() {
		courseListRenderer.setDrawShadow(gui.getShadowsEnabled());
		list.setFixedCellHeight(courseListRenderer.getTotalContentHeight());
		toolbarPanel.onPropertiesChanged();
		list.repaint();
	}

	public void focus() {
		list.requestFocusInWindow();
	}

	private boolean isInsideBounds(int x, int y, int x1, int x2, int y1, int y2) {
		if (x > x1 && x < x2 && y > y1 && y < y2) {
			return true;
		} else {
			return false;
		}
	}
}
