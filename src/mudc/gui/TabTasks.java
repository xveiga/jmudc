package mudc.gui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import mudc.core.Locales;
import mudc.core.Task;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class TabTasks extends JPanel {
	
	JList<Task> list = null;

	public TabTasks(GUI core, Locales strings) {
		
		setLayout(null);
		
		setBackground(new Color(255, 255, 255, 255));
		
		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setBackground(new Color(255, 255, 255, 255));
		toolbarPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192, 255)));
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.X_AXIS));
		add(toolbarPanel);
		
		JButton btnStopAll = new JButton("Stop All");
		toolbarPanel.add(btnStopAll);
		
		JButton btnRequeueAll = new JButton("Requeue All");
		toolbarPanel.add(btnRequeueAll);
		
		JButton testButton = new JButton("Save data");
		 testButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
                            //core.saveDataFile();
			}
		});
		toolbarPanel.add(testButton);
                
                JButton testButton2 = new JButton("Update MoodleInfo");
		 testButton2.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
                            //core.updateMoodleInfo();
			}
		});
		toolbarPanel.add(testButton2);
		
		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setBackground(new Color(255, 255, 255, 255));
		scrollPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192, 255)));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(16); // TODO: Save in settings
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(0, 0));
		list = new JList<Task>();
		ListRendererTask courseListRenderer = new ListRendererTask(strings);
		list.setCellRenderer(courseListRenderer);
		list.setFixedCellHeight(48);
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1) {
					if (isInsideBounds(event.getX(), event.getY(),
							list.getBounds().width-64,
							list.getBounds().width-8,
							list.getBounds().y,
							list.getBounds().height)) {
						if (list.getSelectedIndices().length == 1) {
							//
						}
						else {
							//ERROR
						}
					}
				}
			}
			
		});
		list.addKeyListener(new KeyListener () {
			
			boolean enterPressed = false;
			boolean spacePressed = false;

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					enterPressed = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_SPACE){
					spacePressed = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (enterPressed) {
					enterPressed = false;
					spacePressed = false;
					if (list.getSelectedIndices().length == 1) {
						//
					}
					else {
						//ERROR
					}
				}
				else if (spacePressed) {
					enterPressed = false;
					spacePressed = false;
					if (list.getSelectedIndices().length == 1) {
						//
						
					}
					else {
						//ERROR
					}
				}
			}
		});
		
		JPopupMenu taskPopup = new JPopupMenu();
        taskPopup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				
            	List<Task> selectedList = list.getSelectedValuesList();
            	if (selectedList.size() ==  1) {
					
	                JMenuItem detailsItem = new JMenuItem("Details");
	                detailsItem.addActionListener(new ActionListener() {
	                    @Override
	                    public void actionPerformed(ActionEvent arg0) {
	                        //TODO: Open details window
	                    	//DetailWindowCourse details = new DetailWindowCourse(selectedList.get(0));
	                    	//details.setVisible(true);
	                    }
	                });
	                taskPopup.add(detailsItem);
            	}
            	
            	else {
	                JMenuItem detailsItem = new JMenuItem("No action available for current selection");
	                detailsItem.setEnabled(false);
	                taskPopup.add(detailsItem);
            	}
				
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				taskPopup.removeAll();
				
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
        	
        });
		
        list.setComponentPopupMenu(taskPopup);
		contentPanel.add(list);
		scrollPanel.setViewportView(contentPanel);
		
		add(scrollPanel);
		
		ComponentAdapter componentAdapter = new ComponentAdapter() {
		    @Override
			public void componentResized(ComponentEvent e) {
		    	toolbarPanel.setBounds(4, 4, getWidth()-8, 32);
		    	toolbarPanel.validate();
		    	scrollPanel.setBounds(4, 40, getWidth()-8, getHeight()-76);
		    	scrollPanel.validate();
		    }
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
		
		core.registerTasksPanel(this);
	}
	
	public void onRefresh(List<Task> taskList) {
		DefaultListModel<Task> listModel = new DefaultListModel<>();
		if (taskList != null && taskList.size() != 0) {
			for (Task task : taskList) {
				listModel.addElement(task);
			}
		}
		list.setModel(listModel);
		list.validate();
		if (taskList.size() > 0) {
			list.setSelectedIndex(0);
		}
		list.repaint();
	}
	
	private boolean isInsideBounds(int x, int y, int x1, int x2, int y1, int y2) {
		if (x > x1 && x < x2 && y > y1 && y < y2) {
			return true;
		}
		else {
			return false;
		}
	}
}
