package mudc.gui;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import mudc.core.Locales;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class TabSettings extends JPanel {

	private static final long serialVersionUID = -3425727143308128678L;
	
	private PanelSettings panel = null;

	public TabSettings(GUI gui, Locales strings) {
		setLayout(null);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(12); // TODO: Save in settings
		scrollPane.setBackground(Colors.settingsBackground);
		panel = new PanelSettings(gui, strings);
		scrollPane.setViewportView(panel);
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBar());
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(16, -1));
		add(scrollPane);
		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				scrollPane.setBounds(0, 0, getWidth(), getHeight());
				scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
	}
	
	public PanelSettings getSettingsPanel() {
		return panel;
	}
}
