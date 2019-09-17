package mudc.gui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;

import mudc.core.Locales;
import mudc.core.MoodleMessage;

@SuppressWarnings("serial")
public class PanelMessages extends JPanel {

	JList<MoodleMessage> list = null;

	public PanelMessages(GUI gui, Locales strings, TabMessages messageTab) {

		setLayout(null);

		setBackground(new Color(255, 255, 255, 255));

		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setBackground(new Color(255, 255, 255, 255));
		toolbarPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(64, 64, 64, 255)));
		SpringLayout sl_toolbarPanel = new SpringLayout();
		toolbarPanel.setLayout(sl_toolbarPanel);

		add(toolbarPanel);

		JButton updateButton = new JButton();
		int iconPack = gui.getIconPack();
		switch (iconPack) {
		case 1:
			updateButton.setIcon(new ImageIcon(
					PanelMessages.class.getResource(gui.getIconPackPath(iconPack) + "elements/reload.png")));
			break;
		default:
			updateButton.setIcon(new ImageIcon(
					PanelMessages.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
		}
		sl_toolbarPanel.putConstraint(SpringLayout.NORTH, updateButton, 2, SpringLayout.NORTH, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.WEST, updateButton, -34, SpringLayout.EAST, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.SOUTH, updateButton, -2, SpringLayout.SOUTH, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.EAST, updateButton, -2, SpringLayout.EAST, toolbarPanel);
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.updateMessages();
			}
		});
		toolbarPanel.add(updateButton);

		JLabel viewTitle = new JLabel(strings.getString("messages"));
		viewTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
		viewTitle.setHorizontalAlignment(SwingConstants.LEFT);
		sl_toolbarPanel.putConstraint(SpringLayout.NORTH, viewTitle, 0, SpringLayout.NORTH, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.WEST, viewTitle, 8, SpringLayout.WEST, this);
		sl_toolbarPanel.putConstraint(SpringLayout.SOUTH, viewTitle, 0, SpringLayout.SOUTH, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.EAST, viewTitle, -8, SpringLayout.EAST, updateButton);
		toolbarPanel.add(viewTitle);

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setBackground(new Color(255, 255, 255, 255));
		// scrollPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192,
		// 255)));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(16); // TODO: Save in settings

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(0, 0));
		list = new JList<MoodleMessage>();
		ListRendererMessage courseListRenderer = new ListRendererMessage(strings);
		list.setCellRenderer(courseListRenderer);
		list.setFixedCellHeight(48);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1) {
					if (isInsideBounds(event.getX(), event.getY(), list.getBounds().width - 64,
							list.getBounds().width - 8, list.getBounds().y, list.getBounds().height)) {
						if (list.getSelectedIndices().length == 1) {
							messageTab.changeToDetailsPanel(list.getSelectedValue());
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
						messageTab.changeToDetailsPanel(list.getSelectedValue());
					} else {
						// ERROR
					}
				} else if (spacePressed) {
					enterPressed = false;
					spacePressed = false;
					if (list.getSelectedIndices().length == 1) {
						messageTab.changeToDetailsPanel(list.getSelectedValue());

					} else {
						// ERROR
					}
				}
			}
		});
		
		JPopupMenu messagePopup = new JPopupMenu();
        messagePopup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				
            	List<MoodleMessage> selectedList = list.getSelectedValuesList();
            	
            	boolean oneSelected = (selectedList.size() == 1);
            	
            	if (oneSelected) {
                    JMenuItem openItem = new JMenuItem("View");
                    openItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                        	messageTab.changeToDetailsPanel(list.getSelectedValue());
                        }
                    });
                    messagePopup.add(openItem);
            	}
            	
                JMenuItem deleteItem = new JMenuItem("Delete");
                deleteItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        //TODO: Display confirmation window and delete selected messages.

                    }
                });
                messagePopup.add(deleteItem);
            	
            	if (oneSelected) {
	                messagePopup.addSeparator();
	                
	                JMenuItem detailsItem = new JMenuItem("Details");
	                detailsItem.addActionListener(new ActionListener() {
	                    @Override
	                    public void actionPerformed(ActionEvent arg0) {
	                        //TODO: Open details window

	                    }
	                });
	                messagePopup.add(detailsItem);
            	}
				
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				messagePopup.removeAll();
				
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
        	
        });

        list.setComponentPopupMenu(messagePopup);
		contentPanel.add(list);
		scrollPanel.setViewportView(contentPanel);

		add(scrollPanel);

		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				toolbarPanel.setBounds(4, 4, getWidth() - 8, 32);
				toolbarPanel.validate();
				scrollPanel.setBounds(4, 40, getWidth() - 8, getHeight() - toolbarPanel.getHeight()-8);
				scrollPanel.validate();
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

		gui.registerMessagesPanel(this);
		
		List<MoodleMessage> messageList = gui.getMessages();
		if (messageList != null) {
			onRefresh(messageList);
		}
	}

	public void onRefresh(List<MoodleMessage> messageList) {
		DefaultListModel<MoodleMessage> listModel = new DefaultListModel<MoodleMessage>();
		if (messageList != null && messageList.size() != 0) {
			for (MoodleMessage message : messageList) {
				listModel.addElement(message);
			}
		}
		list.setModel(listModel);
		list.validate();
	}

	private boolean isInsideBounds(int x, int y, int x1, int x2, int y1, int y2) {
		if (x > x1 && x < x2 && y > y1 && y < y2) {
			return true;
		} else {
			return false;
		}
	}
}
