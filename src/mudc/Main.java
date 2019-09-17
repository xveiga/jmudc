package mudc;

import mudc.core.Core;
import mudc.gui.GUI;

public class Main {

	public static void main(String[] args) {
		if (args.length == 0) {
			// GUI Mode
			Core core = new Core();
			core.loadConfigFile("config.json");
			GUI gui = new GUI(core);
			gui.launchMainWindow();
			core.loadDataFile_start();
		} else {
			// CLI Mode
			System.out.println("This application does not accept any arguments yet.");
		}
	}
}
