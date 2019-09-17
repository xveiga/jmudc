package mudc.core;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import org.json.simple.parser.ParseException;

import mudc.core.casudc.MoodleToken;
import mudc.core.casudc.MoodleTokenData;
import mudc.core.casudc.MoodleTokenObtainer;
import mudc.core.dataelements.MoodleCategory;
import mudc.core.dataelements.MoodleCourse;
import mudc.core.dataelements.MoodleElement;
import mudc.core.dataelements.MoodleInfo;
import mudc.core.dataelements.MoodleMessage;
import mudc.core.dataelements.MoodleModule;
import mudc.gui.GUI;

public class Core {

	private File configFile = null;

	private TaskSystem taskSystem = null;
	private MetadataStorage storage = null;
	private WebserviceFunctions moodle = null;
	private HttpClient webserviceClient = null;
	private UpdateComparator comparator = null;
	private Locales strings = null;

	private GUI gui = null;

	private AccountDataHolder data = null;
	private SettingsHolder settings = null;
	private List<UpdatedObject> updates = null; // TODO: Save on accountDataHolder
	
	private Exception accountsException = null;

	// TODO: system should create a temporary file and lock it to know if it closed
	// unexpectedly or if another instance is already running (by writing the pid)
	// (like ".pid").
	
	// TODO: Low memory mode, update timer.

	/*
	 * Done - has bugs: TaskSystem prevents execution of two conflicting tasks, to avoid thread
	 * crashes. Conflicting tasks: TODO: loadDataFile and saveDataFile cannot be
	 * executed with any other operation. any metadata operation cannot be executed
	 * concurrently with another metadata op. as they access the same lists.
	 * Download operations are not safe for the same reason.
	 * 
	 */

	public Core() {
		initCoreModules();
	}

	private void initCoreModules() {
		System.out.println("[Core] Initializing modules.");
		taskSystem = new TaskSystem();
		webserviceClient = new HttpClient();
		storage = new MetadataStorage();
		moodle = new WebserviceFunctions(webserviceClient);
		comparator = new UpdateComparator();
		strings = new Locales();
		updates = new ArrayList<UpdatedObject>();
	}

	public void registerGUIinstance(GUI guiInstance) {
		gui = guiInstance;
		if (gui != null) {
			taskSystem.setOnUpdateRunnable(gui.getTaskSystemNotifyRunnable());
			System.out.println("[Core] GUI instance registered.");
		}
	}

	public void loadConfigFile() {
		// Load defaults if no file specified
		System.out.println("[Core] Config file not found, loading defaults.");
		settings = new SettingsHolder();
		storage.setBufferSize(settings.metadataReadBufferSize);
		strings.setDefaultLocale();
		taskSystem.setMaxTaskNumber(settings.maxConcurrentTasks);
	}

	public void loadConfigFile(String path) {
		System.out.println("[Core] Loading configuration file.");
		File settingsFile;
		try {
			settingsFile = MetadataStorage.getRelativeFile(path);
			configFile = settingsFile;
			if (MetadataStorage.fileExists(settingsFile)) {
				settings = storage.loadSettingsFile(settingsFile);
				storage.setBufferSize(settings.metadataReadBufferSize);
				strings.setLocale(settings.locale);
				taskSystem.setMaxTaskNumber(settings.maxConcurrentTasks);
			} else {
				loadConfigFile();
			}
		} catch (URISyntaxException | MissingResourceException e) {
			handleTaskException(e);
		}
	}

	public void saveConfigFile() {
		storage.saveSettingsFile(configFile, settings);
		System.out.println("[Core] Saved configuration file.");
	}

	public void loadDataFile_start() {

		Task task = new Task();

		task.name = strings.getString("taskloaddata");
		task.description = strings.getString("taskloaddatadesc");
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = null;
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					AccountDataHolder account = null;
					String dataFilePath = settings.dataFolder + File.separator + settings.dataFile;
					File dataFile;
					if (settings.dataFolderPathRelative) {
						dataFile = MetadataStorage.getRelativeFile(dataFilePath);
					} else {
						dataFile = new File(dataFilePath);
					}
					if (MetadataStorage.fileExists(dataFile)) {
						account = storage.loadAccountDataFile(dataFile);
						System.out.println("[Core] Data file loaded");
					}
					else {
						System.out.println("[Core] Data file does not exist, loading empty template.");
						account = new AccountDataHolder();
						//TODO: Prompt user for token and first time init.
						// Get user token, get moodle info, then allow control to user.
					}
					loadDataFile_finish(account);
				} catch (ParseException | IOException | URISyntaxException e) {
					handleTaskException(e);
				}
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
	}

	private void loadDataFile_finish(AccountDataHolder account) {
		data = account;
		if (gui != null) {
			gui.onDataFileRefresh();
		}
	}

	public void saveDataFile_start() {
		Task task = new Task();

		task.name = strings.getString("tasksavedata");
		task.description = strings.getString("tasksavedatadesc");
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = null;
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					String dataFilePath = settings.dataFolder + File.separator + settings.dataFile;
					File dataFolder = MetadataStorage.getRelativeFile(settings.dataFolder);
					if (!MetadataStorage.fileExists(dataFolder)) {
						MetadataStorage.createFoldersForPath(MetadataStorage.getRelativeFile(settings.dataFolder));
					}
					if (settings.dataFolderPathRelative) {
						storage.saveAccountDataFile(MetadataStorage.getRelativeFile(dataFilePath), data);
					} else {
						storage.saveAccountDataFile(new File(dataFilePath), data);
					}
					saveDataFile_finish(data);
				} catch (Exception e) {
					handleTaskException(e);
				}
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
	}

	private void saveDataFile_finish(AccountDataHolder account) {
		if (gui != null) {
			
		}
	}
	
	public void updateMoodleInfo_start() {
		Task task = new Task();

		task.name = "UPDATEMOODLEINFO";
		task.description = "NODESC";
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = null;
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				MoodleInfo info = null;
				try {
					info = moodle.core_webservice_get_site_info(settings.httpClientSettings.serverURL, data.token);
				} catch (Exception e) {
					handleTaskException(e);
				}
				updateMoodleInfo_finish(info, null);
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
	}

	private synchronized void updateMoodleInfo_finish(MoodleInfo info, List<UpdatedObject> updatesList) {
		if (info != null) {
			data.accountInfo = info;
		}
		if (gui != null) {
			gui.onMoodleInfoRefresh();
		}
	}

	public void updateMoodleCourses_start() {
		Task task = new Task();

		task.name = strings.getString("taskupdatecourses");
		task.description = strings.getString("taskupdatecoursesdesc");
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = null;
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					if (settings != null && settings.httpClientSettings != null
							&& settings.httpClientSettings.serverURL != null) {
						if (data != null && data.token != null && data.accountInfo != null) {
							if (data.accountInfo.userid != -1) {
								List<MoodleCourse> serverList = moodle.core_course_get_courses(
										settings.httpClientSettings.serverURL, data.token, data.accountInfo.userid);
								UpdateComparatorOutput<MoodleCourse> comparison = comparator
										.compareMoodleCourses(data.courseList, serverList);
								updateMoodleCourses_finish(comparison.mergedList, comparison.updates);
							} else {
								throw new NullPointerException("User ID is undefined");
							}
						} else {
							throw new NullPointerException("User token is undefined");
						}
					} else {
						throw new NullPointerException("ServerURL is undefined");
					}
				} catch (Exception e) {
					handleTaskException(e);
				}
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
	}

	private void updateMoodleCourses_finish(List<MoodleCourse> courseList, List<UpdatedObject> updatesList) {
		data.courseList = courseList;
		updates.addAll(updatesList);
		if (gui != null) {
			gui.onCoursesRefresh(data.courseList);
			gui.onUpdatesRefresh(updates);
			// TODO: Gui code on update
		}

		// DEBUG
		/*for (MoodleCourse course : getCourses()) {
			System.out.println(course.shortname + "  " + course.status);
		}

		for (UpdatedObject update : getUpdates()) {
			System.out.println(((MoodleCourse) update.moodleObject).shortname);
			for (UpdatedValue value : update.changedValues) {
				System.out.println("\t" + value.name + ": \"" + value.oldValue + "\" -> \"" + value.newValue + "\"");
			}
		}*/
	}
	
	public void updateMoodleCourseContents_start(MoodleCourse c) {
		Task task = new Task();

		task.name = strings.getString("taskupdatecourses");
		task.description = strings.getString("taskupdatecoursesdesc");
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = null;
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					if (settings != null && settings.httpClientSettings != null
							&& settings.httpClientSettings.serverURL != null) {
						if (data != null && data.token != null && data.accountInfo != null) {
							if (data.accountInfo.userid != -1) {
								List<MoodleCategory> serverList = moodle.core_course_get_contents(settings.httpClientSettings.serverURL, data.token, c.id);
								updateMoodleCourseContents_finish(c, serverList, null);
							} else {
								throw new NullPointerException("User ID is undefined");
							}
						} else {
							throw new NullPointerException("User token is undefined");
						}
					} else {
						throw new NullPointerException("ServerURL is undefined");
					}
				} catch (Exception e) {
					handleTaskException(e);
				}
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
	}

	private void updateMoodleCourseContents_finish(MoodleCourse rootCourse, List<MoodleCategory> contentsList, List<UpdatedObject> updatesList) {
		MoodleCourse c = rootCourse;
		for (MoodleCourse course : data.courseList) {
			if (course.id == rootCourse.id) {
				course.categoryList = contentsList;
				c = course;
				break;
			}
		}
		if (gui != null) {
			gui.onCourseContentsRefresh(rootCourse, c.categoryList);
			//gui.onUpdatesRefresh(updates);
		}
	}
	
	public ProgressUpdater downloadFile_start(boolean openAfterDownload, Runnable onSuccess, Runnable onFail, MoodleCourse parentCourse, MoodleCategory parentCategory, MoodleModule parentModule, MoodleElement element) {
		Task task = new Task();

		task.name = "Download file";
		task.description = strings.getString("taskupdatemessagesdesc");
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = new ProgressUpdater(element.filesize);
		
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					HttpDownloader dl = new HttpDownloader();
					File path = generateElementFilePath(true, parentCourse, parentCategory, parentModule, element);
					//TODO: FileURL checking, check is same domain. If string does not have "?" in url add it.
					// Also, find element in local list and use that data instead.
					element.localPath = path.getPath();
					dl.downloadFileFromURL(element.fileurl + "&token=" + data.token, path, task.progress);
					downloadFile_finish(openAfterDownload, onSuccess, parentCourse, parentCategory, parentModule, element);
					//TODO: Save element path and new values on data holder, then update correct CardElement.
				} catch (Exception e) {
					downloadFile_finish(false, onFail, parentCourse, parentCategory, parentModule, element);
					handleTaskException(e);
				}
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
		return task.progress;
	}
	
	private void downloadFile_finish(boolean openAfterDownload, Runnable onFinish, MoodleCourse parentCourse, MoodleCategory parentCategory, MoodleModule parentModule, MoodleElement element) {
		if (onFinish != null) onFinish.run();
		if (openAfterDownload && Desktop.isDesktopSupported() && element.isLocal) {
			try {
				Desktop.getDesktop().open(MetadataStorage.getRelativeFile(element.localPath));
			} catch (IOException | URISyntaxException e) {
				handleTaskException(e);
			}
		}
	}
	
	private File generateElementFilePath(boolean mkdirs, MoodleCourse co, MoodleCategory ca, MoodleModule mo, MoodleElement el) {
		//TODO: Different organization policies
		File file = new File(settings.dataFolder + File.separator + co.id + File.separator + ca.id + File.separator + mo.id + File.separator + el.filename);
		if (mkdirs) {
			File path = new File(settings.dataFolder + File.separator + co.id + File.separator + ca.id + File.separator + mo.id);
			MetadataStorage.createFoldersForPath(path);
		}
		return file;
	}

	public void updateMoodleMessages_start() {
		Task task = new Task();

		task.name = strings.getString("taskupdatemessages");
		task.description = strings.getString("taskupdatemessagesdesc");
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = null;
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					if (settings != null && settings.httpClientSettings != null
							&& settings.httpClientSettings.serverURL != null) {
						if (data != null && data.token != null && data.accountInfo != null) {
							if (data.accountInfo.userid != -1) {
								List<MoodleMessage> serverMessages = moodle.core_message_get_messages(
										settings.httpClientSettings.serverURL, data.token, data.accountInfo.userid, 0);
								serverMessages.addAll(moodle.core_message_get_messages(
										settings.httpClientSettings.serverURL, data.token, 0, data.accountInfo.userid));
								// TODO: ComparatorMethods
								// UpdateComparatorOutput<MoodleCourse> comparison =
								// comparator.compareMoodleMessages(data.courseList, serverMessages);
								updateMoodleMessages_finish(serverMessages, null);
							} else {
								throw new NullPointerException("User ID is undefined");
							}
						} else {
							throw new NullPointerException("User token is undefined");
						}
					} else {
						throw new NullPointerException("ServerURL is undefined");
					}
				} catch (Exception e) {
					handleTaskException(e);
				}
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
	}

	private void updateMoodleMessages_finish(List<MoodleMessage> messageList, List<UpdatedObject> updatesList) {
		data.messages = messageList;
		// updates.addAll(updatesList);
		if (gui != null) {
			gui.onMessagesRefresh(data.messages);
		}
	}
	
	public void updateAll_start() {
		Task task = new Task();

		task.name = strings.getString("taskupdateall");
		task.description = strings.getString("taskupdatealldesc");
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = null;
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				//TODO: Do not start tasks directly here, wait for completion.
					updateMoodleCourses_start();
					updateMoodleMessages_start();
					if (data.courseList != null) {
						for (MoodleCourse c : data.courseList) {
							updateMoodleCourseContents_start(c);
						}
					}
					updateAll_finish();
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
	}
	
	private void updateAll_finish() {
		if (gui != null) {
			gui.onUpdateAllFinish();
		}
	}

	public void shutdown() {
		Thread shutdown = new Thread(new Runnable() {
			@Override
			public void run() { // TODO: Test this waiting system
				System.out.println("[Core] System is being shut down.");
				taskSystem.shutdown(); // Attempt normal shutdown
				for (int i = 30; i >= 0; i--) { // Sleep 10 seconds
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						handleTaskException(e);
					}
					if (!taskSystem.isSystemActive()) {
						System.out.println("[Core] System has been shut down.");
						break;
					} else {
						System.out.println("[Core] Waiting to brute force shut down: " + i + " seconds.");
					}
				}
				if (taskSystem.isSystemActive()) {
					taskSystem.forceShutdown();
					System.out.println("[Core] System has been shut down forcefully. Some errors may occur on next application launch.");
				}
				System.exit(0);
			}
		});
		shutdown.start();
	}
	
	public List<Task> getTasks() {
		return taskSystem.getCurrentTasks();
	}

	public MoodleInfo getInfo() {
		if (data != null) {
			return data.accountInfo;
		}
		return null;
	}

	public List<MoodleCourse> getCourses() {
		if (data != null) {
			return data.courseList;
		}
		return null;
	}

	public List<MoodleCategory> getCourseContents(MoodleCourse course) {
		if (data != null && data.courseList != null) {
			int index = data.courseList.indexOf(course);
			if (index != -1) {
				MoodleCourse localcourse = data.courseList.get(index);
				return localcourse.categoryList;
			}
		}
		return null;
	}

	public List<MoodleMessage> getMessages() {
		if (data != null) {
			return data.messages;
		}
		return null;
	}

	public List<UpdatedObject> getUpdates() {
		return updates;
	}

	public GUI getGUIinstance() {
		return gui;
	}
	
	public void setLocale(String code) {
		strings.setLocale(code);
	}

	public Locales getCoreStrings() {
		return strings;
	}
	
	public GUISettingsHolder getGUIsettings() {
		return settings.guiSettings;
	}
	
	public void setGUIsettings(GUISettingsHolder s) {
		if (s == null) {
			settings.guiSettings = new GUISettingsHolder();
		}
		else {
			settings.guiSettings = s;
		}
	}

	private void handleTaskException(Exception e) {
		System.out.println("[Core] An exception occurred. Expect a trace in the system standard error output.");
		e.printStackTrace();
		if (gui != null) {
			gui.displayException(e);
		}
	}

	public ProgressUpdater checkIntegrity_start(Runnable onSuccess, Runnable onFail, MoodleCourse co,
			MoodleCategory ca, MoodleModule mo, MoodleElement element) {
		Task task = new Task();

		task.name = strings.getString("taskcheckintegrity");
		task.description = strings.getString("taskcheckintegritydesc");
		task.group = 1; // Group 1 is accountDataHolder related access
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = new ProgressUpdater(element.filesize);
		
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					IntegrityChecker check = new IntegrityChecker();
					check.checkFileIntegrity(MetadataStorage.getRelativeFile(element.localPath), element.checksum, task.progress);
					checkIntegrity_finish(onSuccess, co, ca, mo, element);
				} catch (Exception e) {
					checkIntegrity_finish(onFail, co, ca, mo, element);
					handleTaskException(e);
				}
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
		return task.progress;
	}
	
	private void checkIntegrity_finish(Runnable onFinish, MoodleCourse co,
			MoodleCategory ca, MoodleModule mo, MoodleElement element) {
		onFinish.run();
	}
	
	public void getToken_start(String username, char[] password, boolean stayLoggedIn) {
		Task task = new Task();
		task.name = "GETMOODLETOKENS";
		task.description = "NODESC";
		task.group = 2; //Start on different group
		task.onStartRunnable = null;
		task.onStopRunnable = null;
		task.progress = null;
		task.isStoppable = false;
		task.actionRunnable = new Runnable() {
			@Override
			public void run() {
				MoodleTokenObtainer token = new MoodleTokenObtainer();
				MoodleTokenData tokens = null;
				try {
					tokens = token.getTokenForUser(username, password, stayLoggedIn);
				} catch (Exception e) {
					accountsException = e;
				}
				getToken_finish(tokens);
			}
		};
		taskSystem.addTask(task);
		try {
			taskSystem.startTask(task);
		} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
			handleTaskException(e);
		}
	}
	
	private void getToken_finish(MoodleTokenData tokens) {
		if (gui != null) {
			gui.onTokenReturn(tokens);
		}
	}

	public void setToken(MoodleToken token) {
		data.token = token.getToken();
	}

	public String getAccountsError() {
		return accountsException.getClass().getName() + ": " +accountsException.getMessage();
	}

}
