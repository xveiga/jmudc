package mudc.gui;

import java.awt.Container;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mudc.core.Core;
import mudc.core.GUISettingsHolder;
import mudc.core.Locales;
import mudc.core.ProgressUpdater;
import mudc.core.Task;
import mudc.core.UpdatedObject;
import mudc.core.casudc.MoodleToken;
import mudc.core.casudc.MoodleTokenData;
import mudc.core.dataelements.MoodleCategory;
import mudc.core.dataelements.MoodleCourse;
import mudc.core.dataelements.MoodleElement;
import mudc.core.dataelements.MoodleInfo;
import mudc.core.dataelements.MoodleMessage;
import mudc.core.dataelements.MoodleModule;

public class GUI {

	private final static String[] iconPaths = { "/mudc/icons/basic/", "/mudc/icons/moodle/" };

	private Core core = null;
	private Locales strings = null;
	
	private GUISettingsHolder guiSettings = null;
	private boolean trayIcon = false; //TODO: Link to core
	private boolean automaticDownloads = false; //TODO: Link to core
	private boolean updateStartup = false; //TODO: Link to core

	private Window mainWindow = null;
	private MainPanel mainPanel = null;
	private TrayModule tray = null;

	private List<Window> windows = null;
	private List<TabDashboard> dashboardPanels = null;
	private List<PanelCourses> coursesPanels = null;
	private List<PanelFiles> filesPanels = null;
	private List<PanelMessages> messagesPanels = null;
	private List<TabTasks> tasksPanels = null; 
	private List<PanelSettings> settingsPanels = null;
	private List<PanelAccounts> accountsPanels = null;

	public GUI(Core c) {
		System.out.println("[GUI] Creating instance.");
		core = c;
		core.registerGUIinstance(this);
		strings = c.getCoreStrings();
		guiSettings = core.getGUIsettings();
		tray = new TrayModule(this, strings);
		if (trayIcon && tray.isTraySupported()) {
			try {
				tray.enable();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		windows = new ArrayList<Window>();
		dashboardPanels = new ArrayList<TabDashboard>();
		coursesPanels = new ArrayList<PanelCourses>();
		filesPanels = new ArrayList<PanelFiles>();
		messagesPanels = new ArrayList<PanelMessages>();
		tasksPanels = new ArrayList<TabTasks>();
		settingsPanels = new ArrayList<PanelSettings>();
		accountsPanels = new ArrayList<PanelAccounts>();
	}

	public void launchMainWindow() {
		if (mainWindow == null) {
			System.out.println("[GUI] Launching main window");
			mainWindow = new Window(this, Dimensions.mainWindowDefaultSize);
			mainPanel = new MainPanel(this, strings, mainWindow);
			mainWindow.initialize(mainPanel, Dimensions.windowMinSize);
			mainWindow.setTitle(strings.getString("appname"));
			mainWindow.launch();
		}
	}

	public void onWindowClose(Window w) {
		if (w == mainWindow) {
			if (trayIcon) {
				System.out.println("[GUI] Main window closed.");
				unregisterComponent(w.getContentPane());
				mainWindow = null;
			}
			else {
				System.out.println("[GUI] Main window closed. Initiating shutdown.");
				closeAllWindows();
				core.shutdown();
			}
		}
		else {
			windows.remove(w);
			unregisterComponent(w.getContentPane());
		}
		if (windows.size() == 0 && mainWindow == null) {
			System.out.println("[GUI] No gui components are active. Signalling low-memory mode.");
			// TODO: Call core function. Inside check time to next update. If time is < x minutes, dispose of all core components. Then recreate them on wakeup.
			// Avg windows minimum memory use for java application: 7.5 - 10 MB
		}
	}
	
	public void exit() {
		System.out.println("[GUI] Shutdown signal received");
		closeAllWindows();
		if (mainWindow != null) {
			unregisterComponent(mainWindow.getContentPane());
			closeWindow(mainWindow);
		}
		core.shutdown();
	}
	
	public void openNewWindow() {
		Window window = new Window(this, null, Dimensions.secondaryWindowDefaultSize);
		WindowChooser chooser = new WindowChooser(this, window, strings);
		window.setContentPane(chooser);
		windows.add(window);
		window.launch();
	}
	
	public void openAccountsWindow() {
		boolean exists = false;
		Window accountsWindow = null;
		for (Window w : windows) {
			if (w.getContentPane() instanceof PanelAccounts) {
				exists = true;
				accountsWindow = w;
				break;
			}
		}
		if (exists) {
			accountsWindow.setOnTop();
		}
		else {
			Window window = new Window(this, null, Dimensions.accountsWindowDefaultSize, Dimensions.accountsWindowMinSize);
			PanelAccounts accountPanel = new PanelAccounts(this, window, strings);
			window.setContentPane(accountPanel);
			window.setTitle(strings.getString("accountsettings"));
			windows.add(window);
			window.launch();
		}
	}
	
	public void closeWindow(Window w) {
		// Suspected severe memory leak here,
		// turns out the garbage collector takes some time to free all memory.
		System.out.println("[GUI] Closing window \"" + w.getTitle() + "\".");
		w.dispose();
	}

	public void closeAllWindows() {
		while(windows.size() > 0) {
			Window w = windows.get(0);
			System.out.println("[GUI] Closing window \"" + w.getTitle() + "\".");
			w.dispose();
		}
		Runtime.getRuntime().gc(); // Invoke GC to free memory ASAP
	}

	public Runnable getTaskSystemNotifyRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				onTasksRefresh(core.getTasks());
			}
		};
	}

	public void displayException(Exception e) {
		JOptionPane.showMessageDialog(mainWindow.getFrame(), exceptionToString(e), "Error", JOptionPane.ERROR_MESSAGE);
	}

	private String exceptionToString(Exception e) {
		return e.getMessage();
		/*StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();*/
	}
	
	public int getIconPack() {
		return guiSettings.iconPackID;
	}

	public String getIconPackPath(int id) {
		return iconPaths[id];
	}
	
	public void onDataFileRefresh() {
		System.out.println("[GUI] Refreshing all data file dependent panels.");
		onCoursesRefresh(core.getCourses());
		onMessagesRefresh(core.getMessages());
		onMoodleInfoRefresh();
	}
	
	public void updateMoodleInfo() {
		core.updateMoodleInfo_start();
	}
	
	public MoodleInfo getInfo() {
		return core.getInfo();
	}
	
	public void registerDashboardPanel(TabDashboard p) {
		dashboardPanels.add(p);
		System.out.println("[GUI] Registered dashboard panel instance.");
	}
	
	public List<UpdatedObject> getUpdates() {
		return core.getUpdates();
	}
	
	public void onUpdatesRefresh(List<UpdatedObject> u) {
		System.out.println("[GUI] Refreshing dashboard panels.");
		for (TabDashboard p : dashboardPanels) {
			p.onRefresh(u);
		}
	}
	
	public void registerCoursesPanel(PanelCourses p) {
		coursesPanels.add(p);
		System.out.println("[GUI] Registered courses panel instance.");
	}
	
	public List<MoodleCourse> getCourses() {
		return core.getCourses();
	}
	
	public void updateCourses() {
		core.updateMoodleCourses_start();
	}
	
	public void onCoursesRefresh(List<MoodleCourse> c) {
		System.out.println("[GUI] Refreshing course panels.");
		for (PanelCourses p : coursesPanels) {
			p.onRefresh(c);
		}
	}
	
	
	public void registerCourseContentsPanel(PanelFiles p) {
		filesPanels.add(p);
		System.out.println("[GUI] Registered files panel instance (" + p.getCourse().id + ").");
	}
	
	public void updateCourseContents(MoodleCourse c) {
		core.updateMoodleCourseContents_start(c);
	}
	
	public void onCourseContentsRefresh(MoodleCourse c, List<MoodleCategory> cat) {
		System.out.println("[GUI] Refreshing file panels for " + c.id);
		for (int i=0; i<filesPanels.size(); i++) {
			PanelFiles p = filesPanels.get(i);
			if (p.getCourse().id == c.id) {
				p.onRefresh(cat);
			}
		}
	}
	
	public List<MoodleCategory> getCourseContents(MoodleCourse rootCourse) {
		return core.getCourseContents(rootCourse);
	}

	public void registerMessagesPanel(PanelMessages p) {
		messagesPanels.add(p);
		System.out.println("[GUI] Registered messages panel instance.");
	}

	public List<MoodleMessage> getMessages() {
		return core.getMessages();
	}

	public void updateMessages() {
		core.updateMoodleMessages_start();
	}

	public void onMessagesRefresh(List<MoodleMessage> m) {
		System.out.println("[GUI] Refreshing message panels.");
		for (PanelMessages p : messagesPanels) {
			p.onRefresh(m);
		}
	}
	
	public void registerTasksPanel(TabTasks p) {
		tasksPanels.add(p);
		System.out.println("[GUI] Registered tasks panel instance.");
	}
	
	public void onTasksRefresh(List<Task> t) {
		System.out.println("[GUI] Refreshing task panels.");
		for (TabTasks p : tasksPanels) {
			p.onRefresh(t);
		}
	}
	
	public void registerSettingsPanel(PanelSettings s) {
		settingsPanels.add(s);
		System.out.println("[GUI] Registered settings panel instance.");
	}
	
	public void onMoodleInfoRefresh() {
		for (int i=0; i<settingsPanels.size(); i++) {
			PanelSettings s = settingsPanels.get(i);
			s.onMoodleInfoChange();
		}
		for (int i=0; i<accountsPanels.size(); i++) {
			PanelAccounts p = accountsPanels.get(i);
			p.onMoodleInfoRefresh();
		}
	}
	
	public void updateAll() {
		System.out.println("[GUI] Update all command");
		core.updateAll_start();
	}
	
	public void onUpdateAllFinish() {
		//TODO: Compare new items and display in notification
		tray.displayNotification(strings.getString("appname"), "No new files detected", MessageType.NONE);
	}
	
	public void saveData() {
		System.out.println("[GUI] Save data command");
		core.saveDataFile_start();
	}
	
	public void saveConfig() {
		System.out.println("[GUI] Save config command");
		core.setGUIsettings(guiSettings);
		//TODO: Core config values: trayIcon, timer, automatic downloads
		core.saveConfigFile();
	}
	
	public ProgressUpdater downloadFile(boolean openAfterDownload, Runnable onSuccess, Runnable onFail, MoodleCourse parentCourse, MoodleCategory parentCategory, MoodleModule parentModule, MoodleElement element) {
		return core.downloadFile_start(openAfterDownload, onSuccess, onFail, parentCourse, parentCategory, parentModule, element);
	}
	
	public void downloadAllFiles(MoodleCourse c) {
		if (c.categoryList != null) {
			for (MoodleCategory ca : c.categoryList) {
				if (ca.moduleList != null) {
					for (MoodleModule mo : ca.moduleList) {
						if (mo.elementList != null) {
							for (MoodleElement el : mo.elementList) {
								core.downloadFile_start(false, null, null, c, ca, mo, el);
							}
						}
					}
				}
			}
		}
	}
	
	public boolean unregisterComponent (Container c) {
		if (c instanceof TabDashboard) {
			TabDashboard p = (TabDashboard) c;
			if (dashboardPanels.remove(p)) {
				System.out.println("[GUI] Unregistered dashboard panel instance.");
				return true;
			}
		}
		else if (c instanceof TabCourses) {
			TabCourses t = (TabCourses) c;
			boolean success = false;
			if (coursesPanels.remove(t.getCoursesPanel())) {
				success = true;
				System.out.println("[GUI] Unregistered courses panel instance.");
			}
			if (filesPanels.remove(t.getFilesPanel())) {
				System.out.println("[GUI] Unregistered files panel instance.");
			}
			return success;
			
		}
		else if (c instanceof PanelFiles) {
			PanelFiles p = (PanelFiles) c;
			if (filesPanels.remove(p)) {
				System.out.println("[GUI] Unregistered files panel instance.");
				return true;
			}
		}
		else if (c instanceof TabMessages) {
			TabMessages p = (TabMessages) c;
			if (messagesPanels.remove(p.getMessagesPanel())) {
				System.out.println("[GUI] Unregistered messages panel instance.");
				return true;
			}
		}
		else if (c instanceof TabTasks) {
			TabTasks p = (TabTasks) c;
			if (tasksPanels.remove(p)) {
				System.out.println("[GUI] Unregistered tasks panel instance.");
				return true;
			}
		}
		else if (c instanceof TabSettings) {
			TabSettings p = (TabSettings) c;
			if (settingsPanels.remove(p.getSettingsPanel())) {
				System.out.println("[GUI] Unregistered settings panel instance.");
				return true;
			}
		}
		else if (c instanceof PanelAccounts) {
			PanelAccounts p = (PanelAccounts) c;
			if (accountsPanels.remove(p)) {
				System.out.println("[GUI] Unregistered accounts panel instance.");
				return true;
			}
		}
		else if (c instanceof MainPanel) {
			MainPanel p = (MainPanel) c;
			if (unregisterComponent(p.getDashboardTab()) &&
					unregisterComponent(p.getCoursesTab()) &&
					unregisterComponent(p.getMessagesTab()) &&
					unregisterComponent(p.getTasksTab()) &&
					unregisterComponent(p.getSettingsTab()))
			{
				System.out.println("[GUI] Unregistered main panel instance.");
				return true;
			}
			else {
				System.out.println("[GUI] Something went wrong while unregistering main panel components. Memory leaks may occur.");
				return false;
			}
		}
		return false;
	}
	
	public boolean isTrayEnabled() {
		return trayIcon;
	}
	
	public boolean getAutomaticDownloads() {
		return automaticDownloads;
	}
	
	public void setAutomaticDownloads(boolean enable) {
		automaticDownloads = enable;
	}
	
	public boolean getUpdateOnStartup() {
		return updateStartup;
	}
	
	public void setUpdateOnStartup(boolean enable) {
		updateStartup = enable;
	}
	
	public void enableTrayModule() {
		trayIcon = true;
		try {
			tray.enable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: Enable core timer
	}
	
	public void disableTrayModule() {
		trayIcon = false;
		tray.disable();
		//TODO: Disable core timer
	}
	
	public boolean getAnimationsEnabled() {
		return guiSettings.animationsEnabled;
	}
	
	public boolean getShadowsEnabled() {
		return guiSettings.shadowsEnabled;
	}
	
	public void setLocale(String code) {
		System.out.println("[GUI] Locale set to \"" + code + "\"");
		core.setLocale(code);
		strings = core.getCoreStrings();
		onLocaleChange();
		mainPanel.onLocaleChanged();
		onPropertiesChanged();
	}
	
	public void setAnimationsEnabled(boolean status) {
		guiSettings.animationsEnabled = status;
		mainPanel.setAnimationsEnabled(guiSettings.animationsEnabled);
		if (guiSettings.animationsEnabled)
			System.out.println("[GUI] Animations are now enabled");
		else
			System.out.println("[GUI] Animations are now disabled");
	}
	
	public void setShadowsEnabled(boolean status) {
		guiSettings.shadowsEnabled = status;
		if (guiSettings.shadowsEnabled)
			System.out.println("[GUI] Shadows are now enabled");
		else
			System.out.println("[GUI] Shadows are now disabled");
		
		onPropertiesChanged();
	}
	
	private void onLocaleChange() {
		for (PanelSettings p : settingsPanels) {
			p.onLocaleChange();
		}	
		if (tray != null) {
			tray.onLocaleChanged();
		}
	}
	
	private void onPropertiesChanged() {
		System.out.println("[GUI] Setting new component properties");
		for (PanelSettings p : settingsPanels) {
			p.onPropertiesChanged();
		}
		
		onUpdatesRefresh(core.getUpdates());
		
		for (PanelCourses p : coursesPanels) {
			p.onPropertiesChanged();
		}
		
		for (PanelFiles p : filesPanels) {
			p.onPropertiesChanged();
		}
		onMessagesRefresh(core.getMessages());
		onTasksRefresh(core.getTasks());
	}

	public void updateCurrent(JPanel contentPanel) {
		JPanel panel = contentPanel;
		if (contentPanel instanceof MainPanel) {
			MainPanel p = (MainPanel) contentPanel;
			panel = p.getCurrentTab();
		}
		
		if (panel instanceof TabDashboard) {
			//TODO: Link when tabDashboard finished
		} else if (panel instanceof TabCourses) {
			TabCourses tabCourses = (TabCourses) panel;
			JPanel tab = tabCourses.getCurrentPanel();
			if (tab instanceof PanelCourses) {
				updateCourses();
			} else if (tab instanceof PanelFiles) {
				PanelFiles panelFiles = (PanelFiles) tab;
				updateCourseContents(panelFiles.getCourse());
			}
		} else if (panel instanceof TabMessages) {
			TabMessages tabMessages = (TabMessages) panel;
			JPanel tab = tabMessages.getCurrentPanel();
			if (tab instanceof PanelMessages) {
				updateMessages();
			}
		}
	}

	public ProgressUpdater checkIntegrity(Runnable onFinishSuccessful, Runnable onFinishFail, MoodleCourse co,
			MoodleCategory ca, MoodleModule mo, MoodleElement el) {
		return core.checkIntegrity_start(onFinishSuccessful, onFinishFail, co, ca, mo, el);
	}
	
	public void registerAccountsPanel(PanelAccounts p) {
		accountsPanels.add(p);
		System.out.println("[GUI] Registered accounts panel instance.");
	}
	
	public void getTokenForUser(String username, char[] password, boolean stayLoggedIn) {
		core.getToken_start(username, password, stayLoggedIn);
	}
	
	public void onTokenReturn(MoodleTokenData tokens) {
		for (PanelAccounts p : accountsPanels) {
			p.onTokenReturn(tokens);
		}
	}
	
	public void setToken(MoodleToken token) {
		core.setToken(token);
	}

	public String getAccountsError() {
		return core.getAccountsError();
	}

}
