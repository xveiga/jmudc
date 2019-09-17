# jmudc
Java GUI client for UDC's Moodle service.

Requires [json-simple](https://github.com/fangyidong/json-simple).

Developed using Eclipse targeting Java-SE 1.8

A project that I started before knowing anything about good design practices.
Currently broken, would need an extensive redesign. Not being developed anymore.

## Importing in Eclipse

#### Option 1: Direct download
In Eclipse, go to `File > Import > Git > Projects from Git`, then `Clone URI`.
Paste the project URI on the corresponding field, click next, select the `master` branch and configure the project location to your liking.

#### Option 2: Manual Import
Download the project, then go to `File > Import > General > Existing Projects into Workspace`, then browse to the project directory or .zip archive and click `Finish`.

Json-simple should be automatically downloaded by Maven.

## First run

Run the `Main.java` class.
A first-time start assistant was planned but never implemented. Therefore, to get the application to work you'll need to go to the gear icon on the top right, then "Settings", and then click on "Manage Login Information".
A new window will ask you for your credentials. Once you are logged in, you can start using the application.
Press the key combination `CTRL+S` so that the login information is saved.

## Usage

The main interface consists of five different tabs:
* **Dashboard:** Intended to be a summary of all updates that occurred recently on Moodle. Never implemented. Shortcut: F2
* **Courses:** Courses the user is enrolled in. Shows the contents and files of each one. Shortcuts: F3 to access the tab, F5 to update contents of the current view.
* **Messages: ** Messages the user sent or received. Sending messages is not supported. Shortcuts: F4 to access the tab, F5 to update contents.
* **Tasks (tick icon): ** Shows the actions the application is performing and the status. Intended to be a "downloads in progress" tab. Mostly broken and not useful.
* **Menu (gear icon): ** Menu with common tasks. Allows the creation of new windows with any of the previously mentioned tabs, updating all information and accessing the settings menu.

## Notes
The first time any tab is open it will be empty. Press F5 to update the contents.
	
For debugging purposes no data is saved permanently (surviving an application restart) unless the key combination `CTRL+S` is pressed.

The Courses tab is broken in many ways, especially with file downloading and viewing. Expect lots of errors.

If the "Look for updates periodically" setting is enabled, the application minimizes to the task bar. It was intended that the service would connect to the server on a timer and notify the user about changes. This works, but the timer and the logic that would detect if changes occurred was never implemented.