package mudc.gui;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;

import mudc.core.Locales;

public class TrayModule {
	
	//TODO: Maybe move TrayModule to Core
	
	private boolean isSupported = false;
	private GUI gui = null;
	private Locales strings = null;
	private TrayIcon trayIcon = null;
	
	private MenuItem openItem = null;
	private MenuItem checkUpdatesItem = null;
	private Menu updateTimerItem = null;
	private MenuItem exitItem = null;
	
	public TrayModule(GUI g, Locales str) {
		gui = g;
		strings = str;
		isSupported = SystemTray.isSupported();
		if (isSupported) {
			System.out.println("[TrayModule] System tray is supported.");
		}
		else {
			System.out.println("[TrayModule] System tray not supported.");
		}
	}
	
	public void enable() throws IOException {
		if (isSupported && trayIcon == null) {
			SystemTray systemTray = SystemTray.getSystemTray();
			Image image = ImageIO.read(getClass().getResourceAsStream("/mudc/icons/moodle/elements/course.png"));
			
			PopupMenu trayPopup = new PopupMenu();
			
			openItem = new MenuItem();
			openItem.setLabel(strings.getString("trayopen"));
			openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gui.launchMainWindow();
				}
			});
			trayPopup.add(openItem);
			
			trayPopup.addSeparator();
			
			checkUpdatesItem = new MenuItem();
			checkUpdatesItem.setLabel(strings.getString("traycheckupdates"));
			checkUpdatesItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gui.updateAll();
				}
			});
			trayPopup.add(checkUpdatesItem);
			
			updateTimerItem = new Menu();
			updateTimerItem.setLabel(strings.getString("trayupdatetimer"));
			//TODO: Sync with gui and core
			updateTimerItem.add(new CheckboxMenuItem("5 min"));
			updateTimerItem.add(new CheckboxMenuItem("15 min"));
			updateTimerItem.add(new CheckboxMenuItem("30 min"));
			trayPopup.add(updateTimerItem);
			
			trayPopup.addSeparator();
			
			exitItem = new MenuItem();
			exitItem.setLabel(strings.getString("trayexit"));
			exitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					gui.exit();
				}
			});
			trayPopup.add(exitItem);
			
			trayIcon = new TrayIcon(image, strings.getString("trayname"), trayPopup);
			trayIcon.setImageAutoSize(true);
			
			try {
				systemTray.add(trayIcon);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void disable() {
		SystemTray systemTray = SystemTray.getSystemTray();
		systemTray.remove(trayIcon);
		trayIcon = null;
	}
	
	public void onLocaleChanged() {
		if (trayIcon != null) {
			trayIcon.setToolTip(strings.getString("trayname"));
			openItem.setLabel(strings.getString("trayopen"));
			checkUpdatesItem.setLabel(strings.getString("traycheckupdates"));
			updateTimerItem.setLabel(strings.getString("trayupdatetimer"));
			exitItem.setLabel(strings.getString("trayexit"));
		}
	}
	
	public boolean displayNotification(String caption, String text, MessageType messageType) {
		if (trayIcon != null) {
			trayIcon.displayMessage(caption, text, messageType);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isTraySupported() {
		return isSupported;
	}

}
