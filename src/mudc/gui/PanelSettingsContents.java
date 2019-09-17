package mudc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import mudc.core.Locales;
import mudc.core.MoodleInfo;

import javax.swing.DefaultComboBoxModel;

public class PanelSettingsContents extends JPanel {

	private static final long serialVersionUID = 8885878317694088822L;

	private GUI gui = null;
	
	private CardSettings title = null;
	private CardSettings accounts = null;
	private CardSettings behaviour = null;
	private CardSettings language = null;
	private CardSettings visuals = null;
	private CardSettings help = null;
	private CardSettings developer = null;
	
	public PanelSettingsContents(GUI g, Locales strings) {
		setLayout(null);
		setOpaque(false);
		
		gui = g;
		boolean shadowsEnabled = gui.getShadowsEnabled();
		
		title = new CardSettingsTitle(gui, strings);
		title.setShadowsEnabled(shadowsEnabled);
		add(title);
		
		accounts = new CardAccounts(gui, strings);
		accounts.setShadowsEnabled(shadowsEnabled);
		add(accounts);
		
		behaviour = new CardBehaviour(gui, strings);
		behaviour.setShadowsEnabled(shadowsEnabled);
		add(behaviour);
		
		language = new CardLanguage(gui, strings);
		language.setShadowsEnabled(shadowsEnabled);
		add(language);
		
		visuals = new CardVisuals(gui, strings);
		visuals.setShadowsEnabled(shadowsEnabled);
		add(visuals);
		
		help = new CardHelp(gui, strings);
		help.setShadowsEnabled(shadowsEnabled);
		add(help);
		
		developer = new CardDeveloper(gui, strings);
		developer.setShadowsEnabled(shadowsEnabled);
		add(developer);
		
		ComponentAdapter componentAdapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int cummulativeHeightTop = 8;
				int cummulativeHeightBottom = title.getTotalContentHeight();
				title.setBounds(0, cummulativeHeightTop, getWidth(), cummulativeHeightBottom);
				cummulativeHeightTop = cummulativeHeightBottom + Dimensions.settingsCardSeparation;
				cummulativeHeightBottom = cummulativeHeightBottom + accounts.getTotalContentHeight();
				accounts.setBounds(0, cummulativeHeightTop, getWidth(), accounts.getTotalContentHeight());
				cummulativeHeightTop = cummulativeHeightBottom + Dimensions.settingsCardSeparation;
				cummulativeHeightBottom = cummulativeHeightBottom + behaviour.getTotalContentHeight();
				behaviour.setBounds(0, cummulativeHeightTop, getWidth(), behaviour.getTotalContentHeight());
				cummulativeHeightTop = cummulativeHeightBottom + Dimensions.settingsCardSeparation;
				cummulativeHeightBottom = cummulativeHeightBottom + language.getTotalContentHeight();
				language.setBounds(0, cummulativeHeightTop, getWidth(), language.getTotalContentHeight());
				cummulativeHeightTop = cummulativeHeightBottom + Dimensions.settingsCardSeparation;
				cummulativeHeightBottom = cummulativeHeightBottom + visuals.getTotalContentHeight();
				visuals.setBounds(0, cummulativeHeightTop, getWidth(), visuals.getTotalContentHeight());
				cummulativeHeightTop = cummulativeHeightBottom + Dimensions.settingsCardSeparation;
				cummulativeHeightBottom = cummulativeHeightBottom + help.getTotalContentHeight();
				help.setBounds(0, cummulativeHeightTop, getWidth(), help.getTotalContentHeight());
				cummulativeHeightTop = cummulativeHeightBottom + 64;
				cummulativeHeightBottom = cummulativeHeightBottom + developer.getTotalContentHeight();
				developer.setBounds(0, cummulativeHeightTop, getWidth(), developer.getTotalContentHeight());
			}
		};
		addComponentListener(componentAdapter);
		componentAdapter.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
	}
	
	public int getTotalContentHeight() {
		return 1024;
	}
	
	class CardSettingsTitle extends CardSettings {

		private static final long serialVersionUID = 2044396715143512859L;
		
		private JLabel accountSettingsTitle = null;
		
		private CardSettingsTitle(GUI gui, Locales strings) {
			super(gui, strings);
			setLayout(null);
			accountSettingsTitle = new JLabel();
			accountSettingsTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
			accountSettingsTitle.setForeground(Colors.settingsTitleColor);
			accountSettingsTitle.setHorizontalAlignment(JLabel.CENTER);
			accountSettingsTitle.setVerticalAlignment(JLabel.CENTER);
			accountSettingsTitle.setText(strings.getString("settings"));
			add(accountSettingsTitle);
			ComponentAdapter componentAdapter = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					accountSettingsTitle.setBounds(16, 4, getWidth()-32, 32);
				}
			};
			addComponentListener(componentAdapter);
		}
		
		@Override
		public void onLocaleChange() {
			accountSettingsTitle.setText(getStrings().getString("settings"));
		}
		
		@Override
		public int getTotalContentHeight() {
			return 48;
		}
		
	}
	
	class CardAccounts extends CardSettings {

		private static final long serialVersionUID = 2044396715143512859L;
		
		private JLabel accountSettingsTitle = null;
		private JLabel accountLogin = null;
		private JLabel accountServer = null;
		private JButton accountSettingsButton = null;
		private MoodleInfo info = null;
		
		private CardAccounts(GUI gui, Locales strings) {
			super(gui, strings);
			setLayout(null);
			accountSettingsTitle = new JLabel();
			accountSettingsTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
			accountSettingsTitle.setForeground(Colors.settingsTitleColor);
			accountSettingsTitle.setHorizontalAlignment(JLabel.LEFT);
			accountSettingsTitle.setVerticalAlignment(JLabel.CENTER);
			accountSettingsTitle.setText(strings.getString("settingsaccounts"));
			add(accountSettingsTitle);
			
			accountLogin = new JLabel();
			accountLogin.setFont(new Font("Tahoma", Font.BOLD, 11));
			accountLogin.setForeground(Colors.settingsTitleColor);
			accountLogin.setHorizontalAlignment(JLabel.CENTER);
			accountLogin.setVerticalAlignment(JLabel.CENTER);
			add(accountLogin);
			
			accountServer = new JLabel();
			accountServer.setFont(new Font("Tahoma", Font.PLAIN, 9));
			accountServer.setForeground(Colors.settingsTitleColor);
			accountServer.setHorizontalAlignment(JLabel.CENTER);
			accountServer.setVerticalAlignment(JLabel.CENTER);
			add(accountServer);
			
			accountSettingsButton = new CustomJButton();
			accountSettingsButton.setText(strings.getString("settingsaccountsbutton"));
			accountSettingsButton.setPreferredSize(new Dimension(Dimensions.settingsWidth-64, 28));
			accountSettingsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gui.openAccountsWindow();
				}
			});
			add(accountSettingsButton);
			ComponentAdapter componentAdapter = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					accountSettingsTitle.setBounds(16, 4, Dimensions.settingsWidth-32, 28);
					accountLogin.setBounds(16, 32, Dimensions.settingsWidth-32, 16);
					accountServer.setBounds(32, 48, Dimensions.settingsWidth-64, 16);
					accountSettingsButton.setBounds(16, 72, Dimensions.settingsWidth-32, 32);
				}
			};
			addComponentListener(componentAdapter);
			updateMoodleInfo();
		}
		
		public void updateMoodleInfo() {
			info = gui.getInfo();
			if (info != null) {
				accountLogin.setText(info.fullname + " (" + info.username + ")");
				accountServer.setText(info.sitename);
			}
			else {
				accountLogin.setText("Not logged in");
			}
		}
		
		@Override
		public void onLocaleChange() {
			accountSettingsTitle.setText(getStrings().getString("settingsaccounts"));
			accountSettingsButton.setText(getStrings().getString("settingsaccountsbutton"));
		}
		
		@Override
		public int getTotalContentHeight() {
			return 128;
		}
		
	}
	
	class CardBehaviour extends CardSettings {

		private static final long serialVersionUID = 2044396715143512859L;
		
		private JLabel behaviourSettingsTitle = null;
		private JLabel folderStructureTitle = null;
		private JLabel updateStartupTitle = null;
		private JLabel automaticDownloadsTitle = null;
		private JLabel periodicUpdatesTitle = null;
		private JLabel updateTimerTitle = null;
		private JLabel periodicUpdatesDescription = null;
		
		private CardBehaviour(GUI gui, Locales strings) {
			super(gui, strings);
			setLayout(null);
			
			behaviourSettingsTitle = new JLabel();
			behaviourSettingsTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
			behaviourSettingsTitle.setForeground(Colors.settingsTitleColor);
			behaviourSettingsTitle.setHorizontalAlignment(JLabel.LEFT);
			behaviourSettingsTitle.setVerticalAlignment(JLabel.CENTER);
			behaviourSettingsTitle.setText(strings.getString("settingsbehaviour"));
			add(behaviourSettingsTitle);
			
			folderStructureTitle = new JLabel();
			folderStructureTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			folderStructureTitle.setForeground(Colors.settingsTitleColor);
			folderStructureTitle.setHorizontalAlignment(JLabel.LEFT);
			folderStructureTitle.setVerticalAlignment(JLabel.CENTER);
			folderStructureTitle.setText(strings.getString("settingsfolderstructure"));
			add(folderStructureTitle);
			
			JComboBox<String> folderStructure = new JComboBox<>();
			folderStructure.setModel(new DefaultComboBoxModel<String>(new String[] {"Element ID's", "Element Names", "Moodle Internal"}));
			add(folderStructure);
			
			updateStartupTitle = new JLabel();
			updateStartupTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			updateStartupTitle.setForeground(Colors.settingsTitleColor);
			updateStartupTitle.setHorizontalAlignment(JLabel.LEFT);
			updateStartupTitle.setVerticalAlignment(JLabel.CENTER);
			updateStartupTitle.setText(strings.getString("settingsupdatestartup"));
			add(updateStartupTitle);
			
			JCheckBox updateStartup = new JCheckBox();
			updateStartup.setSelected(gui.getUpdateOnStartup());
			updateStartup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gui.setUpdateOnStartup(updateStartup.isSelected());
				}
			});
			add(updateStartup);
			
			automaticDownloadsTitle = new JLabel();
			automaticDownloadsTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			automaticDownloadsTitle.setForeground(Colors.settingsTitleColor);
			automaticDownloadsTitle.setHorizontalAlignment(JLabel.LEFT);
			automaticDownloadsTitle.setVerticalAlignment(JLabel.CENTER);
			automaticDownloadsTitle.setText(strings.getString("settingsautomaticdownloads"));
			add(automaticDownloadsTitle);
			
			JCheckBox automaticDownloads = new JCheckBox();
			automaticDownloads.setSelected(gui.getAutomaticDownloads());
			automaticDownloads.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gui.setAutomaticDownloads(automaticDownloads.isSelected());
				}
			});
			add(automaticDownloads);
			
			periodicUpdatesTitle = new JLabel();
			periodicUpdatesTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			periodicUpdatesTitle.setForeground(Colors.settingsTitleColor);
			periodicUpdatesTitle.setHorizontalAlignment(JLabel.LEFT);
			periodicUpdatesTitle.setVerticalAlignment(JLabel.CENTER);
			periodicUpdatesTitle.setText(strings.getString("settingstimerenable"));
			add(periodicUpdatesTitle);
			
			updateTimerTitle = new JLabel();
			JComboBox<String> updateTimer = new JComboBox<>();
			
			JCheckBox periodicUpdates = new JCheckBox();
			periodicUpdates.setSelected(gui.isTrayEnabled());
			periodicUpdates.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					boolean enabled = periodicUpdates.isSelected();
					updateTimerTitle.setEnabled(enabled);
					updateTimer.setEnabled(enabled);
					if (enabled) {
						gui.enableTrayModule();
					}
					else {
						gui.disableTrayModule();
					}
				}
			});
			add(periodicUpdates);
			
			periodicUpdatesDescription = new JLabel();
			periodicUpdatesDescription.setFont(new Font("Tahoma", Font.PLAIN, 9));
			periodicUpdatesDescription.setForeground(Colors.settingsTitleColor);
			periodicUpdatesDescription.setHorizontalAlignment(JLabel.LEFT);
			periodicUpdatesDescription.setVerticalAlignment(JLabel.CENTER);
			periodicUpdatesDescription.setText(strings.getString("settingstimerenabledesc"));
			add(periodicUpdatesDescription);
			
			updateTimerTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			updateTimerTitle.setForeground(Colors.settingsTitleColor);
			updateTimerTitle.setHorizontalAlignment(JLabel.LEFT);
			updateTimerTitle.setVerticalAlignment(JLabel.CENTER);
			updateTimerTitle.setText(strings.getString("settingstimer"));
			updateTimerTitle.setEnabled(false);
			add(updateTimerTitle);
			
			updateTimer.setModel(new DefaultComboBoxModel<String>(new String[] {"1 min", "5 min", "10 min", "15 min", "30 min", "45 min", "1 hour", "2 hours", "3 hours", "6 hours", "12 hours", "1 day", "Custom..."}));
			updateTimer.setEnabled(false);
			add(updateTimer);
			
			ComponentAdapter componentAdapter = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					behaviourSettingsTitle.setBounds(16, 4, getWidth()-32, 28);
					folderStructureTitle.setBounds(32, 32, 128, 16);
					folderStructure.setBounds(160, 32, getWidth()-192, 16);
					updateStartupTitle.setBounds(32, 56, getWidth()-80, 16);
					updateStartup.setBounds(getWidth()-48, 56, 18, 16);
					automaticDownloadsTitle.setBounds(32, 80, getWidth()-80, 16);
					automaticDownloads.setBounds(getWidth()-48, 80, 18, 16);
					periodicUpdatesTitle.setBounds(32, 104, getWidth()-80, 16);
					periodicUpdatesDescription.setBounds(32, 120, getWidth()-80, 9);
					periodicUpdates.setBounds(getWidth()-48, 104, 18, 16);
					updateTimerTitle.setBounds(48, 136, 160, 16);
					updateTimer.setBounds(208, 136, getWidth()-240, 16);
				}
			};
			addComponentListener(componentAdapter);
		}
		
		@Override
		public void onLocaleChange() {
			behaviourSettingsTitle.setText(getStrings().getString("settingsbehaviour"));
			folderStructureTitle.setText(getStrings().getString("settingsfolderstructure"));
			updateStartupTitle.setText(getStrings().getString("settingsupdatestartup"));
			automaticDownloadsTitle.setText(getStrings().getString("settingsautomaticdownloads"));
			periodicUpdatesTitle.setText(getStrings().getString("settingstimerenable"));
			periodicUpdatesDescription.setText(getStrings().getString("settingstimerenabledesc"));
			updateTimerTitle.setText(getStrings().getString("settingstimer"));
		}
		
		@Override
		public int getTotalContentHeight() {
			return 172;
		}
		
	}
	
	class CardLanguage extends CardSettings {

		private static final long serialVersionUID = 2044396715143512859L;
		
		private CardLanguage(GUI gui, Locales strings) {
			super(gui, strings);
			setLayout(null);
			
			JLabel languageSettingsTitle = new JLabel();
			languageSettingsTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
			languageSettingsTitle.setForeground(Colors.settingsTitleColor);
			languageSettingsTitle.setHorizontalAlignment(JLabel.LEFT);
			languageSettingsTitle.setVerticalAlignment(JLabel.CENTER);
			languageSettingsTitle.setText("Language & formats");
			add(languageSettingsTitle);
			
			JLabel languageTitle = new JLabel();
			languageTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			languageTitle.setForeground(Colors.settingsTitleColor);
			languageTitle.setHorizontalAlignment(JLabel.LEFT);
			languageTitle.setVerticalAlignment(JLabel.CENTER);
			languageTitle.setText("Language");
			add(languageTitle);
			
			JComboBox<String> language = new JComboBox<>();
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
			for (String[] locale : strings.getAvailableLocales()) {
				model.addElement(locale[1]);
			}
			language.setModel(model);
			language.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String langName = (String) language.getSelectedItem();
					for (String[] locale : strings.getAvailableLocales()) {
						if (langName.equals(locale[1])) {
							gui.setLocale(locale[0]);
						}
					}
				}
			});
			add(language);
			
			JLabel dateFormatTitle = new JLabel();
			dateFormatTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			dateFormatTitle.setForeground(Colors.settingsTitleColor);
			dateFormatTitle.setHorizontalAlignment(JLabel.LEFT);
			dateFormatTitle.setVerticalAlignment(JLabel.CENTER);
			dateFormatTitle.setText("Date format");
			add(dateFormatTitle);
			
			JComboBox<String> dateFormat = new JComboBox<>();
			dateFormat.setModel(new DefaultComboBoxModel<String>(new String[] {"HH:MM DD/MM/YY", "H:MM DD/MM/YY", "HH:MM YY/MM/DD", "H:MM YY/MM/DD"}));
			add(dateFormat);
			
			ComponentAdapter componentAdapter = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					languageSettingsTitle.setBounds(16, 4, getWidth()-32, 28);
					languageTitle.setBounds(32, 32, 128, 16);
					language.setBounds(160, 32, getWidth()-192, 16);
					dateFormatTitle.setBounds(32, 56, 128, 16);
					dateFormat.setBounds(160, 56, getWidth()-192, 16);
				}
			};
			addComponentListener(componentAdapter);
		}
		
		@Override
		public int getTotalContentHeight() {
			return 96;
		}
		
	}
	
	class CardVisuals extends CardSettings {

		private static final long serialVersionUID = 2044396715143512859L;
		
		private CardVisuals(GUI gui, Locales strings) {
			super(gui, strings);
			setLayout(null);
			
			JLabel visualSettingsTitle = new JLabel();
			visualSettingsTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
			visualSettingsTitle.setForeground(Colors.settingsTitleColor);
			visualSettingsTitle.setHorizontalAlignment(JLabel.LEFT);
			visualSettingsTitle.setVerticalAlignment(JLabel.CENTER);
			visualSettingsTitle.setText("Visuals");
			add(visualSettingsTitle);
			
			JLabel animationsTitle = new JLabel();
			animationsTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			animationsTitle.setForeground(Colors.settingsTitleColor);
			animationsTitle.setHorizontalAlignment(JLabel.LEFT);
			animationsTitle.setVerticalAlignment(JLabel.CENTER);
			animationsTitle.setText("Enable animations");
			add(animationsTitle);
			
			JCheckBox animations = new JCheckBox();
			animations.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gui.setAnimationsEnabled(animations.isSelected());
				}
			});
			add(animations);
			
			JLabel shadowsTitle = new JLabel();
			shadowsTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
			shadowsTitle.setForeground(Colors.settingsTitleColor);
			shadowsTitle.setHorizontalAlignment(JLabel.LEFT);
			shadowsTitle.setVerticalAlignment(JLabel.CENTER);
			shadowsTitle.setText("Enable shadows");
			add(shadowsTitle);
			
			JCheckBox shadows = new JCheckBox();
			shadows.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gui.setShadowsEnabled(shadows.isSelected());
				}
			});
			add(shadows);
			
			ComponentAdapter componentAdapter = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					visualSettingsTitle.setBounds(16, 4, getWidth()-32, 28);
					animationsTitle.setBounds(32, 32, getWidth()-80, 16);
					animations.setBounds(getWidth()-48, 32, 18, 16);
					shadowsTitle.setBounds(32, 56, getWidth()-80, 16);
					shadows.setBounds(getWidth()-48, 56, 18, 16);
				}
			};
			addComponentListener(componentAdapter);
		}
		
		@Override
		public int getTotalContentHeight() {
			return 96;
		}
		
	}
	
	class CardHelp extends CardSettings {

		private static final long serialVersionUID = 2044396715143512859L;
		
		private CardHelp(GUI gui, Locales strings) {
			super(gui, strings);
			setLayout(null);
			JLabel helpTitle = new JLabel();
			helpTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
			helpTitle.setForeground(Colors.settingsTitleColor);
			helpTitle.setHorizontalAlignment(JLabel.LEFT);
			helpTitle.setVerticalAlignment(JLabel.CENTER);
			helpTitle.setText("About, Help");
			add(helpTitle);
			JButton helpButton = new CustomJButton();
			helpButton.setText("Browse help documents");
			helpButton.setPreferredSize(new Dimension(Dimensions.settingsWidth-64, 28));
			add(helpButton);
			ComponentAdapter componentAdapter = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					helpTitle.setBounds(16, 4, Dimensions.settingsWidth-16, 28);
					helpButton.setBounds(16, 40, Dimensions.settingsWidth-32, getTotalContentHeight()-64);
				}
			};
			addComponentListener(componentAdapter);
		}
		
		@Override
		public int getTotalContentHeight() {
			return 96;
		}
		
	}
	
	class CardDeveloper extends CardSettings {

		private static final long serialVersionUID = 2044396715143512859L;
		
		private CardDeveloper(GUI gui, Locales strings) {
			super(gui, strings);
			setLayout(null);
			JLabel developerTitle = new JLabel();
			developerTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
			developerTitle.setForeground(Colors.settingsTitleColor);
			developerTitle.setHorizontalAlignment(JLabel.LEFT);
			developerTitle.setVerticalAlignment(JLabel.CENTER);
			developerTitle.setText("Developer");
			add(developerTitle);
			JLabel descriptionTitle = new JLabel();
			descriptionTitle.setFont(new Font("Tahoma", Font.PLAIN, 10));
			descriptionTitle.setForeground(Colors.settingsTitleColor);
			descriptionTitle.setHorizontalAlignment(JLabel.CENTER);
			descriptionTitle.setVerticalAlignment(JLabel.CENTER);
			descriptionTitle.setText("<html><p>The following options are intended for advanced users only. Modify these at your own risk.</p></html>");
			add(descriptionTitle);
			ComponentAdapter componentAdapter = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					developerTitle.setBounds(16, 4, Dimensions.settingsWidth-16, 28);
					descriptionTitle.setBounds(32, 24, getWidth()-80, 40);
				}
			};
			addComponentListener(componentAdapter);
		}
		
		@Override
		public int getTotalContentHeight() {
			return 288;
		}
		
	}
	
	public void onLocaleChange() {
		title.onLocaleChange();
		accounts.onLocaleChange();
		behaviour.onLocaleChange();
		language.onLocaleChange();
		visuals.onLocaleChange();
		help.onLocaleChange();
		developer.onLocaleChange();
	}

	public void onPropertiesChanged() {
		boolean shadowsEnabled = gui.getShadowsEnabled();
		title.setShadowsEnabled(shadowsEnabled);
		accounts.setShadowsEnabled(shadowsEnabled);
		behaviour.setShadowsEnabled(shadowsEnabled);
		language.setShadowsEnabled(shadowsEnabled);
		visuals.setShadowsEnabled(shadowsEnabled);
		help.setShadowsEnabled(shadowsEnabled);
		developer.setShadowsEnabled(shadowsEnabled);
		validate();
	}
	
	public void onMoodleInfoChange() {
		if (accounts instanceof CardAccounts) {
			CardAccounts a = (CardAccounts) accounts;
			a.updateMoodleInfo();
		}
	}

}
