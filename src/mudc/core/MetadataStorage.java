package mudc.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import mudc.Main;

public class MetadataStorage {

	private final static String dataFileVersion = "1";
	private final static String dataEncoding = "UTF-8";
	private int readBufferSize = 32768;
	
	public void setBufferSize(int size) {
		readBufferSize = size;
	}
	
	public int getBufferSize() {
		return readBufferSize;
	}

	@SuppressWarnings("unchecked")
	public void saveAccountDataFile(File dataPath, AccountDataHolder a) throws IOException {
		JSONObject jsonFile = new JSONObject();
		jsonFile.put("accountdatafileversion", dataFileVersion);
		JSONObject jsonAccount = new JSONObject();
		jsonAccount.put("lastupdatetime", a.lastUpdateTime);

		// HttpClient Settings
		JSONObject jHttpSettings = new JSONObject();
		if (a.httpClientSettings != null) {
			jHttpSettings.put("useaccounthttpsettings", a.useLocalHttpSettings);
			if (a.useLocalHttpSettings) {
				if (a.httpClientSettings.serverURL != null)
					jHttpSettings.put("serverurl", a.httpClientSettings.serverURL);
				if (a.httpClientSettings.userAgent != null)
					jHttpSettings.put("useragent", a.httpClientSettings.userAgent);
				if (a.httpClientSettings.accept != null)
					jHttpSettings.put("accept", a.httpClientSettings.accept);
				if (a.httpClientSettings.acceptLanguage != null)
					jHttpSettings.put("acceptLanguage", a.httpClientSettings.acceptLanguage);
				jHttpSettings.put("httpbuffersize", a.httpClientSettings.bufferSize);
				jHttpSettings.put("gzipsupportenable", a.httpClientSettings.gzipSupport);
			}
		} else {
			jHttpSettings.put("useaccountsettings", false);
		}
		jsonAccount.put("httpclientsettings", jHttpSettings);

		// Custom data
		jsonAccount.put("folderstructure", a.folderStructure);
		if (a.customName != null)
			jsonAccount.put("displayname", a.customName);
		if (a.token != null)
			jsonAccount.put("usertoken", a.token);

		// Moodle account info
		JSONObject ji = new JSONObject();
		if (a.accountInfo != null) {
			if (a.accountInfo.sitename != null)
				ji.put("sitename", a.accountInfo.sitename);
			if (a.accountInfo.username != null)
				ji.put("username", a.accountInfo.username);
			if (a.accountInfo.firstname != null)
				ji.put("firstname", a.accountInfo.firstname);
			if (a.accountInfo.lastname != null)
				ji.put("lastname", a.accountInfo.lastname);
			if (a.accountInfo.fullname != null)
				ji.put("fullname", a.accountInfo.fullname);
			if (a.accountInfo.lang != null)
				ji.put("lang", a.accountInfo.lang);
			ji.put("userid", a.accountInfo.userid);
			if (a.accountInfo.siteurl != null)
				ji.put("siteurl", a.accountInfo.siteurl);
			if (a.accountInfo.userpictureurl != null)
				ji.put("userpictureurl", a.accountInfo.userpictureurl);
			ji.put("downloadfiles", a.accountInfo.downloadfiles);
			ji.put("uploadfiles", a.accountInfo.uploadfiles);
			if (a.accountInfo.release != null)
				ji.put("release", a.accountInfo.release);
			if (a.accountInfo.version != null)
				ji.put("version", a.accountInfo.version);
			if (a.accountInfo.mobilecssurl != null)
				ji.put("mobilecssurl", a.accountInfo.mobilecssurl);
			ji.put("usercanmanageownfiles", a.accountInfo.usercanmanageownfiles);
			ji.put("userquota", a.accountInfo.userquota);
			ji.put("usermaxuploadfilesize", a.accountInfo.usermaxuploadfilesize);

			JSONArray jsonFunctions = new JSONArray(); // Function list
			if (a.accountInfo.functionList != null) {
				for (MoodleFunction f : a.accountInfo.functionList) {
					JSONObject jf = new JSONObject();
					jf.put("name", f.name);
					jf.put("version", f.version);
					jsonFunctions.add(jf);
				}
			}
			ji.put("serverfunctions", jsonFunctions);

			JSONArray jsonAdvancedFeatures = new JSONArray(); // Feature list
			if (a.accountInfo.advancedFeaturesList != null) {
				for (MoodleAdvancedFeature f : a.accountInfo.advancedFeaturesList) {
					JSONObject jf = new JSONObject();
					jf.put("name", f.name);
					jf.put("value", f.value);
				}
			}
			ji.put("advancedfeatures", jsonAdvancedFeatures);
		}
		jsonAccount.put("moodleinfo", ji);

		// Course List
		JSONArray jsonCourseList = new JSONArray();
		if (a.courseList != null) {
			for (MoodleCourse course : a.courseList) {
				JSONObject jsonCourse = new JSONObject();
				jsonCourse.put("id", course.id);
				if (course.shortname != null)
					jsonCourse.put("shortname", course.shortname);
				if (course.fullname != null)
					jsonCourse.put("fullname", course.fullname);
				jsonCourse.put("enrolledusercount", course.enrolledusercount);
				if (course.idnumber != null)
					jsonCourse.put("idnumber", course.idnumber);
				jsonCourse.put("visible", course.visible);
				if (course.summary != null)
					jsonCourse.put("summary", course.summary);
				jsonCourse.put("summaryformat", course.summaryformat);
				if (course.format != null)
					jsonCourse.put("format", course.format);
				jsonCourse.put("showgrades", course.showgrades);
				if (course.lang != null)
					jsonCourse.put("lang", course.lang);
				jsonCourse.put("enablecompletion", course.enablecompletion);
				
				// Custom data
				jsonCourse.put("status", course.status);

				// Categories
				JSONArray jsonCategoryList = new JSONArray();
				if (course.categoryList != null) {
					for (MoodleCategory category : course.categoryList) {
						JSONObject jsonCategory = new JSONObject();
						jsonCategory.put("id", category.id);
						if (category.name != null)
							jsonCategory.put("name", category.name);
						jsonCategory.put("visible", category.visible);
						if (category.summary != null)
							jsonCategory.put("summary", category.summary);
						jsonCategory.put("summaryformat", category.summaryformat);
						
						// Custom data
						jsonCategory.put("status", category.status);

						// Modules
						JSONArray jsonModuleList = new JSONArray();
						if (category.moduleList != null) {
							for (MoodleModule module : category.moduleList) {
								JSONObject jsonModule = new JSONObject();
								jsonModule.put("id", module.id);
								if (module.url != null)
									jsonModule.put("url", module.url);
								if (module.name != null)
									jsonModule.put("name", module.name);
								jsonModule.put("instance", module.instance);
								jsonModule.put("visible", module.visible);
								if (module.description != null)
									jsonModule.put("description", module.description);
								if (module.modicon != null)
									jsonModule.put("modicon", module.modicon);
								if (module.modname != null)
									jsonModule.put("modname", module.modname);
								if (module.modplural != null)
									jsonModule.put("modplural", module.modplural);
								jsonModule.put("indent", module.indent);

								// Elements
								JSONArray jsonElementList = new JSONArray();
								if (module.elementList != null) {
									for (MoodleElement element : module.elementList) {
										JSONObject jsonElement = new JSONObject();
										if (element.type != null)
											jsonElement.put("type", element.type);
										if (element.filename != null)
											jsonElement.put("filename", element.filename);
										if (element.filepath != null)
											jsonElement.put("filepath", element.filepath);
										jsonElement.put("filesize", element.filesize);
										if (element.fileurl != null)
											jsonElement.put("fileurl", element.fileurl);
										jsonElement.put("timecreated", element.timecreated);
										jsonElement.put("timemodified", element.timemodified);
										jsonElement.put("sortorder", element.sortorder);
										jsonElement.put("userid", element.userid);
										if (element.author != null)
											jsonElement.put("author", element.author);
										if (element.license != null)
											jsonElement.put("license", element.license);
										jsonElement.put("local", element.isLocal);
										jsonElement.put("checksum", element.checksum);
										if (element.localPath != null)
											jsonElement.put("localpath", element.localPath);
											jsonElement.put("status", element.status);
										jsonElementList.add(jsonElement);
									}
								}
								jsonModule.put("elements", jsonElementList);

								jsonModuleList.add(jsonModule);
							}
						}
						jsonCategory.put("modules", jsonModuleList);

						jsonCategoryList.add(jsonCategory);
					}
				}
				jsonCourse.put("categories", jsonCategoryList);

				jsonCourseList.add(jsonCourse);
			}
		}
		jsonAccount.put("courses", jsonCourseList);

		// Message List
		JSONArray jsonMessageList = new JSONArray();
		if (a.messages != null) {
			for (MoodleMessage m : a.messages) {
				JSONObject jm = new JSONObject();
				jm.put("id", m.id);
				jm.put("useridfrom", m.useridfrom);
				jm.put("useridto", m.useridto);
				if (m.subject != null)
					jm.put("subject", m.subject);
				if (m.text != null)
					jm.put("text", m.text);
				if (m.fullmessage != null)
					jm.put("fullmessage", m.fullmessage);
				jm.put("fullmessageformat", m.fullmessageformat);
				if (m.fullmessagehtml != null)
					jm.put("fullmessagehtml", m.fullmessagehtml);
				if (m.smallmessage != null)
					jm.put("smallmessage", m.smallmessage);
				jm.put("notification", m.notification);
				if (m.contexturl != null)
					jm.put("contexturl", m.contexturl);
				if (m.contexturlname != null)
					jm.put("contexturlname", m.contexturlname);
				jm.put("timecreated", m.timecreated);
				jm.put("timeread", m.timeread);
				if (m.usertofullname != null)
					jm.put("usertofullname", m.usertofullname);
				if (m.userfromfullname != null)
					jm.put("userfromfullname", m.userfromfullname);
				jsonMessageList.add(jm);
			}
		}
		jsonAccount.put("messages", jsonMessageList);

		jsonFile.put("accountdata", jsonAccount);
		saveFile(dataPath, jsonFile.toJSONString());
	}

	public AccountDataHolder loadAccountDataFile(File dataPath) throws ParseException, IOException {
		String fileContents = loadFile(dataPath);
		JSONParser parser = new JSONParser();
		AccountDataHolder a = new AccountDataHolder();
		JSONObject jsonFile = (JSONObject) parser.parse(fileContents);
		JSONObject jsonAccount = (JSONObject) jsonFile.get("accountdata");
		String fileVersion = (String) jsonFile.get("accountdatafileversion");
		if (fileVersion.equals(dataFileVersion)) { // If file versions match
			a.lastUpdateTime = (long) jsonAccount.get("lastupdatetime");

			// HttpClient Settings
			JSONObject jHttpSettings = (JSONObject) jsonAccount.get("httpclientsettings");
			a.useLocalHttpSettings = (boolean) jHttpSettings.get("useaccounthttpsettings");
			if (a.useLocalHttpSettings) {
				HttpClientSettingsHolder httpSettings = new HttpClientSettingsHolder();
				httpSettings.serverURL = (String) jHttpSettings.get("serverurl");
				httpSettings.userAgent = (String) jHttpSettings.get("useragent");
				httpSettings.accept = (String) jHttpSettings.get("accept");
				httpSettings.acceptLanguage = (String) jHttpSettings.get("acceptlanguage");
				httpSettings.bufferSize = (long) jHttpSettings.get("httpbuffersize");
				httpSettings.gzipSupport = (boolean) jHttpSettings.get("gzipsupportenable");
				a.httpClientSettings = httpSettings;
			}

			// Custom data
			a.folderStructure = (long) jsonAccount.get("folderstructure");
			a.customName = (String) jsonAccount.get("displayname");
			a.token = (String) jsonAccount.get("usertoken");

			// Moodle account info
			JSONObject ji = (JSONObject) jsonAccount.get("moodleinfo");
			MoodleInfo info = new MoodleInfo();
			if (!ji.isEmpty()) {
				info.sitename = (String) ji.get("sitename");
				info.username = (String) ji.get("username");
				info.firstname = (String) ji.get("firstname");
				info.lastname = (String) ji.get("lastname");
				info.fullname = (String) ji.get("fullname");
				info.lang = (String) ji.get("lang");
				info.userid = (long) ji.get("userid");
				info.siteurl = (String) ji.get("siteurl");
				info.userpictureurl = (String) ji.get("userpictureurl");
				info.downloadfiles = (long) ji.get("downloadfiles");
				info.uploadfiles = (long) ji.get("uploadfiles");
				info.release = (String) ji.get("release");
				info.version = (String) ji.get("version");
				info.mobilecssurl = (String) ji.get("mobilecssurl");
				info.usercanmanageownfiles = (boolean) ji.get("usercanmanageownfiles");
				info.userquota = (long) ji.get("userquota");
				info.usermaxuploadfilesize = (long) ji.get("usermaxuploadfilesize");
			}

			JSONArray jsonFunctions = (JSONArray) ji.get("serverfunctions"); // Function list
			info.functionList = new ArrayList<MoodleFunction>();
			for (int i = 0; i < jsonFunctions.size(); i++) {
				JSONObject jsonFunction = (JSONObject) jsonFunctions.get(i);
				MoodleFunction function = new MoodleFunction();
				function.name = (String) jsonFunction.get("name");
				function.version = (String) jsonFunction.get("version");
				info.functionList.add(function);
			}

			JSONArray jsonAdvancedFeatures = (JSONArray) ji.get("advancedfeatures"); // Feature list
			info.advancedFeaturesList = new ArrayList<MoodleAdvancedFeature>();
			for (int i = 0; i < jsonAdvancedFeatures.size(); i++) {
				JSONObject jsonFeature = (JSONObject) jsonAdvancedFeatures.get(i);
				MoodleAdvancedFeature feature = new MoodleAdvancedFeature();
				feature.name = (String) jsonFeature.get("name");
				feature.value = (long) jsonFeature.get("value");
				info.advancedFeaturesList.add(feature);
			}

			a.accountInfo = info;

			// Course List
			JSONArray jsonCourseList = (JSONArray) jsonAccount.get("courses");
			a.courseList = new ArrayList<MoodleCourse>();
			for (int i = 0; i < jsonCourseList.size(); i++) {
				JSONObject jsonCourse = (JSONObject) jsonCourseList.get(i);
				MoodleCourse course = new MoodleCourse();
				course.id = (long) jsonCourse.get("id");
				course.shortname = (String) jsonCourse.get("shortname");
				course.fullname = (String) jsonCourse.get("fullname");
				course.enrolledusercount = (long) jsonCourse.get("enrolledusercount");
				course.idnumber = (String) jsonCourse.get("idnumber");
				course.visible = (long) jsonCourse.get("visible");
				course.summary = (String) jsonCourse.get("summary");
				course.summaryformat = (long) jsonCourse.get("summaryformat");
				course.format = (String) jsonCourse.get("format");
				course.showgrades = (boolean) jsonCourse.get("showgrades");
				course.lang = (String) jsonCourse.get("lang");
				course.enablecompletion = (boolean) jsonCourse.get("enablecompletion");
				
				// Custom data
				course.status = (long) jsonCourse.get("status");

				JSONArray jsonCategoryList = (JSONArray) jsonCourse.get("categories");
				course.categoryList = new ArrayList<MoodleCategory>();
				for (int j = 0; j < jsonCategoryList.size(); j++) {
					JSONObject jsonCategory = (JSONObject) jsonCategoryList.get(j);
					MoodleCategory category = new MoodleCategory();
					category.id = (long) jsonCategory.get("id");
					category.name = (String) jsonCategory.get("name");
					category.visible = (long) jsonCategory.get("visible");
					category.summary = (String) jsonCategory.get("summary");
					category.summaryformat = (long) jsonCategory.get("summaryformat");
					
					// Custom data
					category.status = (long) jsonCategory.get("status");

					JSONArray jsonModuleList = (JSONArray) jsonCategory.get("modules");
					category.moduleList = new ArrayList<MoodleModule>();
					for (int k = 0; k < jsonModuleList.size(); k++) {
						JSONObject jsonModule = (JSONObject) jsonModuleList.get(k);
						MoodleModule module = new MoodleModule();
						module.id = (long) jsonModule.get("id");
						module.url = (String) jsonModule.get("url");
						module.name = (String) jsonModule.get("name");
						module.instance = (long) jsonModule.get("instance");
						module.visible = (long) jsonModule.get("visible");
						module.description = (String) jsonModule.get("description");
						module.modicon = (String) jsonModule.get("modicon");
						module.modname = (String) jsonModule.get("modname");
						module.modplural = (String) jsonModule.get("modplural");
						module.indent = (long) jsonModule.get("indent");
						
						// Custom data
						//module.

						JSONArray jsonElementList = (JSONArray) jsonModule.get("elements");
						module.elementList = new ArrayList<MoodleElement>();
						for (int l = 0; l < jsonElementList.size(); l++) {
							JSONObject jsonElement = (JSONObject) jsonElementList.get(l);
							MoodleElement element = new MoodleElement();
							element.type = (String) jsonElement.get("type");
							element.filename = (String) jsonElement.get("filename");
							element.filepath = (String) jsonElement.get("filepath");
							element.filesize = (long) jsonElement.get("filesize");
							element.fileurl = (String) jsonElement.get("fileurl");
							element.timecreated = (long) jsonElement.get("timecreated");
							element.timemodified = (long) jsonElement.get("timemodified");
							element.sortorder = (long) jsonElement.get("sortorder");
							element.userid = (long) jsonElement.get("userid");
							element.author = (String) jsonElement.get("author");
							element.license = (String) jsonElement.get("license");
							element.isLocal = (boolean) jsonElement.get("local");
							element.checksum = (long) jsonElement.get("checksum");
							element.localPath = (String) jsonElement.get("localpath");
							element.status = (long) jsonElement.get("status");
							module.elementList.add(element);
						}

						category.moduleList.add(module);
					}

					course.categoryList.add(category);
				}

				a.courseList.add(course);
			}

			// Message list
			JSONArray jsonMessageList = (JSONArray) jsonAccount.get("messages");
			a.messages = new ArrayList<MoodleMessage>();
			for (int i = 0; i < jsonMessageList.size(); i++) {
				JSONObject jsonMessage = (JSONObject) jsonMessageList.get(i);
				MoodleMessage message = new MoodleMessage();
				message.id = (long) jsonMessage.get("id");
				message.useridto = (long) jsonMessage.get("useridto");
				message.useridfrom = (long) jsonMessage.get("useridfrom");
				message.subject = (String) jsonMessage.get("subject");
				message.text = (String) jsonMessage.get("text");
				message.fullmessage = (String) jsonMessage.get("fullmessage");
				message.fullmessageformat = (long) jsonMessage.get("fullmessageformat");
				message.fullmessagehtml = (String) jsonMessage.get("fullmessagehtml");
				message.smallmessage = (String) jsonMessage.get("smallmessage");
				message.notification = (long) jsonMessage.get("notification");
				message.contexturl = (String) jsonMessage.get("contexturl");
				message.contexturlname = (String) jsonMessage.get("contexturlname");
				message.timecreated = (long) jsonMessage.get("timecreated");
				message.timeread = (long) jsonMessage.get("timeread");
				message.usertofullname = (String) jsonMessage.get("usertofullname");
				message.userfromfullname = (String) jsonMessage.get("userfromfullname");
				a.messages.add(message);
			}
		}
		return a;
	}

	public static File getJarPath() throws URISyntaxException {
		final Class<?> referenceClass = Main.class;
		final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
		try {
			final File jarPath = new File(url.toURI()).getParentFile();
			return jarPath;
		} catch (final URISyntaxException e) {
			throw e;
		}
	}

	public static File getRelativeFile(String filename) throws URISyntaxException {
		return new File(getJarPath().toString() + File.separator + filename);
	}

	public static boolean fileExists(File file) {
		return file.exists() || file.isDirectory();
	}

	public static boolean deleteFile(File file) {
		return file.delete();
	}

	public static boolean createFoldersForPath(File filePath) {
		return filePath.mkdirs();
	}

	public static boolean createFolder(File filePath) {
		return filePath.mkdir();
	}

	public String loadFile(File file) throws FileNotFoundException, IOException {
		BufferedReader bufferedReader = null;
		InputStreamReader fileReader = null;
		try {
			fileReader = new InputStreamReader(new FileInputStream(file), dataEncoding);
			bufferedReader = new BufferedReader(fileReader);
			String fileContents = "";
			char[] buffer = new char[readBufferSize];
			int byteCount = 0;
			while ((byteCount = bufferedReader.read(buffer)) != -1) {
				String readData = String.valueOf(buffer, 0, byteCount);
				fileContents += readData;
			}

			return fileContents;

		} catch (IOException e) {
			throw e;

		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (fileReader != null) {
					fileReader.close();
				}

			} catch (IOException e) {
				throw e;
			}
		}
	}

	public void saveFile(File file, String fileContents) throws IOException {
		BufferedWriter bufferedWriter = null;
		OutputStreamWriter fileWriter = null;
		try {
			fileWriter = new OutputStreamWriter(new FileOutputStream(file), dataEncoding);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(fileContents);

		} catch (IOException e) {
			throw e;

		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}

	public void saveSettingsFile(File settingsPath, SettingsHolder s) {

	}

	public SettingsHolder loadSettingsFile(File settingsPath) {
		SettingsHolder settings = new SettingsHolder();
		settings.httpClientSettings = new HttpClientSettingsHolder();
		return settings;
	}

}

class SettingsHolder {
	public String dataFolder = "files";
	public boolean dataFolderPathRelative = true;
	public String dataFile = "data.json";
	public String locale = "en";
	public boolean timerSystemEnable = false;
	public boolean updateStartup = false;
	public boolean automaticDownloads = false;
	public int maxConcurrentTasks = 2;
	public int metadataReadBufferSize = 4096;
	public int integrityReadBufferSize = 4096;
	public HttpClientSettingsHolder httpClientSettings = new HttpClientSettingsHolder();
	public GUISettingsHolder guiSettings = new GUISettingsHolder();
}

class AccountDataHolder {
	public long lastUpdateTime = -1;
	public boolean useLocalHttpSettings = false; // If httpClient should use the global settings or the defined by
												// this account.
	public long folderStructure = 0; // If files should be stored by course (false)
												// "coursename/coursecategory/...etc" or by moodle's predefined path
												// MoodleFile.filepath (true).
	public HttpClientSettingsHolder httpClientSettings = new HttpClientSettingsHolder();
	public String customName = null;
	public String token = null;

	public MoodleInfo accountInfo = null;
	public List<MoodleCourse> courseList = new ArrayList<>();
	public List<MoodleMessage> messages = new ArrayList<>();
}

class HttpClientSettingsHolder {
	public String serverURL = "https://moodle.udc.es";
	public String userAgent = "Mozilla/5.0";
	public String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	public String acceptLanguage = "en-US,en;q=0.5";
	public boolean gzipSupport = false;
	public long bufferSize = 4096; // Connection buffer size for downloads.
}