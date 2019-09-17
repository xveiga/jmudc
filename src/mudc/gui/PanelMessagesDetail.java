package mudc.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import mudc.core.dataelements.MoodleMessage;

@SuppressWarnings("serial")
public class PanelMessagesDetail extends JPanel {

	public PanelMessagesDetail(GUI gui, TabMessages messagesTab, MoodleMessage message) {
		setLayout(null);

		setBackground(new Color(255, 255, 255, 255));

		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setBackground(new Color(255, 255, 255, 255));
		toolbarPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(64, 64, 64, 255)));
		SpringLayout sl_toolbarPanel = new SpringLayout();
		toolbarPanel.setLayout(sl_toolbarPanel);

		JButton backButton = new JButton("");
		backButton.setIcon(new ImageIcon(PanelMessagesDetail.class
				.getResource("/com/sun/javafx/scene/control/skin/caspian/fxvk-backspace-button.png")));
		sl_toolbarPanel.putConstraint(SpringLayout.NORTH, backButton, 2, SpringLayout.NORTH, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.WEST, backButton, 2, SpringLayout.WEST, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.EAST, backButton, 34, SpringLayout.WEST, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.SOUTH, backButton, -2, SpringLayout.SOUTH, toolbarPanel);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				messagesTab.changeToMessagesPanel();
			}
		});
		toolbarPanel.add(backButton);

		JLabel viewTitle = new JLabel();
		viewTitle.setFont(new Font("Tahoma", Font.PLAIN, 14));
		viewTitle.setHorizontalAlignment(SwingConstants.CENTER);
		String title = "Message " + message.id + " details";
		if (message.fullmessageformat == 1) {
			title += " (HTML)";
		}
		else {
			title += " (Plain text)";
		}
		viewTitle.setText(title);
		sl_toolbarPanel.putConstraint(SpringLayout.NORTH, viewTitle, 0, SpringLayout.NORTH, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.WEST, viewTitle, 8, SpringLayout.EAST, backButton);
		sl_toolbarPanel.putConstraint(SpringLayout.SOUTH, viewTitle, 0, SpringLayout.SOUTH, toolbarPanel);
		sl_toolbarPanel.putConstraint(SpringLayout.EAST, viewTitle, -8, SpringLayout.EAST, toolbarPanel);

		toolbarPanel.add(viewTitle);

		JEditorPane htmlPanel = new JEditorPane();
		htmlPanel.setEditable(false);
		htmlPanel.setOpaque(false);
		htmlPanel.setBackground(new Color(255, 255, 255, 255));
		if (message.fullmessageformat == 1) {
			if (message.fullmessagehtml != null && !message.fullmessagehtml.equals("")) {
				htmlPanel.setContentType("text/html");
				htmlPanel.setText("<html><body>" + message.fullmessagehtml + "</body></html>");
			}
		} else {
			htmlPanel.setText(message.fullmessage);
		}

		JPopupMenu htmlPanelPopup = new JPopupMenu();
		JMenuItem selectAllItem = new JMenuItem("Select all");
		selectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				htmlPanel.requestFocusInWindow();
				htmlPanel.selectAll();
			}
		});
		htmlPanelPopup.add(selectAllItem);
		htmlPanelPopup.add(new JSeparator());
		JMenuItem copyTextItem = new JMenuItem("Copy text");
		copyTextItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selectedText = htmlPanel.getSelectedText();
				if (selectedText != null && !selectedText.equals("")) {
					StringSelection stringSelection = new StringSelection(selectedText);
					Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					systemClipboard.setContents(stringSelection, null);
				}
			}
		});
		htmlPanelPopup.add(copyTextItem);
		htmlPanel.setComponentPopupMenu(htmlPanelPopup);

		htmlPanel.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException | URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setBackground(new Color(255, 255, 255, 255));
		scrollPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192, 255)));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(24); // TODO: Save in settings

		scrollPanel.setViewportView(htmlPanel);

		add(scrollPanel);

		add(toolbarPanel);

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
	}

}
