package mudc.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import mudc.core.Locales;
import mudc.core.MoodleMessage;

@SuppressWarnings("serial")
public class TabMessages extends JPanel {

	private PanelMessages messagesPanel = null;
	private PanelMessagesDetail detailsPanel;
	private GUI core = null;
	private Window window = null;
	private Locales strings = null;
	private Cursor normalCursor = null;
	private Cursor loadingCursor = null;
	private boolean currentTab = false;

	public TabMessages(GUI gui, Locales str, Window w) {
		window = w;
		core = gui;
		strings = str;
		messagesPanel = new PanelMessages(core, strings, this);
		setLayout(new BorderLayout(0, 0));
		add(messagesPanel, BorderLayout.CENTER);

		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				messagesPanel.revalidate();
				if (detailsPanel != null) {
					detailsPanel.revalidate();
				}
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

		normalCursor = Cursor.getDefaultCursor();
		loadingCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	}
	
	public PanelMessages getMessagesPanel() {
		return messagesPanel;
	}
	
	public JPanel getCurrentPanel() {
		if (currentTab) {
			return detailsPanel;
		}
		else {
			return messagesPanel;
		}
	}

	public void changeToDetailsPanel(MoodleMessage message) {
		if (!currentTab) {
			System.out.println("[TabMessages] Changing to message details panel.");
			setCursor(loadingCursor);
			window.setTitle(strings.getString("windowmessagedetails") + message.id);
			detailsPanel = new PanelMessagesDetail(core, this, message);
			// core.registerMessagesDetailTab(detailsPanel);
			// core.sendMessageDetailsPanel(message);
			remove(messagesPanel);
			add(detailsPanel, BorderLayout.CENTER);
			currentTab = true;
			detailsPanel.revalidate();
			detailsPanel.repaint();
			setCursor(normalCursor);
		}
	}

	public void changeToMessagesPanel() {
		if (currentTab) {
			System.out.println("[TabMessages] Changing to message list panel.");
			window.setTitle(strings.getString("windowmessages"));
			setCursor(loadingCursor);
			remove(detailsPanel);
			add(messagesPanel, BorderLayout.CENTER);
			currentTab = false;
			messagesPanel.revalidate();
			messagesPanel.repaint();
			setCursor(normalCursor);
		}
	}

}
