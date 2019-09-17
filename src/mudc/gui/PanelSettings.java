package mudc.gui;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import mudc.core.Locales;

public class PanelSettings extends JPanel {

	private static final long serialVersionUID = -3425727143308128678L;
	
	private PanelSettingsContents panel;

	public PanelSettings(GUI gui, Locales strings) {
		setLayout(null);
		setBackground(Colors.settingsBackground);
		panel = new PanelSettingsContents(gui, strings);
		add(panel);
		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				panel.setBounds((getWidth()-Dimensions.settingsWidth)/2, 0, Dimensions.settingsWidth, panel.getTotalContentHeight());
				setPreferredSize(new Dimension(Dimensions.windowMinSize.width, panel.getTotalContentHeight()));
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
		gui.registerSettingsPanel(this);
	}
	
	public void onLocaleChange() {
		panel.onLocaleChange();
	}

	public void onPropertiesChanged() {
		panel.onPropertiesChanged();
		panel.setPreferredSize(new Dimension(Dimensions.windowMinSize.width, panel.getTotalContentHeight()));
		validate();
		repaint();
	}
	
	public void onMoodleInfoChange() {
		panel.onMoodleInfoChange();
	}

}
