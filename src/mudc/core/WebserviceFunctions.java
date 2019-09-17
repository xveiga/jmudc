package mudc.core;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WebserviceFunctions {
	
	private static final String webservicePath = "/webservice/rest/server.php";
	
	private static final MoodleFunction[] supportedFunctions = {
			new MoodleFunction("core_webservice_get_site_info", "2015111603.05"),
			new MoodleFunction("core_course_get_contents", "2015111603.05"),
			new MoodleFunction("core_course_get_courses", "2015111603.05"),
			new MoodleFunction("core_message_get_messages", "2015111603.05") };

	private HttpClient httpClient = null;;

	WebserviceFunctions(HttpClient client) {
		httpClient = client;
	}

	public MoodleInfo core_webservice_get_site_info(String server, String token) throws Exception {

		// Parameters for core_webservice_get_site_info
		String[] parameters = { "wsfunction", "moodlewsrestformat", "wstoken" };
		String[] values = { "core_webservice_get_site_info", "json", token };
		String params = httpClient.encodeParameters(parameters, values); // Encode params

		String result = httpClient.getHTTPS(server + webservicePath + "?" + params); // Request page to server

		JSONParser parser = new JSONParser(); // Parse received json
		Object obj = parser.parse(result);
		JSONObject jsonObject = (JSONObject) obj;

		// If server reports an exception:
		String exception = (String) jsonObject.get("exception");
		if (exception != null) {
			String errorcode = (String) jsonObject.get("errorcode");
			String message = (String) jsonObject.get("message");
			throw new Moodle_ServerException(exception + " (" + errorcode + "). " + message);
		}

		else { // No exception, get info data
			MoodleInfo moodleinfo = new MoodleInfo();
			moodleinfo.sitename = (String) jsonObject.get("sitename");
			moodleinfo.username = (String) jsonObject.get("username");
			moodleinfo.firstname = (String) jsonObject.get("firstname");
			moodleinfo.lastname = (String) jsonObject.get("lastname");
			moodleinfo.fullname = (String) jsonObject.get("fullname");
			moodleinfo.lang = (String) jsonObject.get("lang");
			moodleinfo.userid = (long) jsonObject.get("userid");
			moodleinfo.siteurl = (String) jsonObject.get("siteurl");
			moodleinfo.userpictureurl = (String) jsonObject.get("userpictureurl");
			moodleinfo.downloadfiles = (long) jsonObject.get("downloadfiles");
			moodleinfo.uploadfiles = (long) jsonObject.get("uploadfiles");
			moodleinfo.release = (String) jsonObject.get("release");
			moodleinfo.version = (String) jsonObject.get("version");
			moodleinfo.mobilecssurl = (String) jsonObject.get("mobilecssurl");
			moodleinfo.usercanmanageownfiles = (Boolean) jsonObject.get("usercanmanageownfiles");
			moodleinfo.userquota = (long) jsonObject.get("userquota");
			moodleinfo.usermaxuploadfilesize = (long) jsonObject.get("usermaxuploadfilesize");
			moodleinfo.functionList = new ArrayList<MoodleFunction>();
			moodleinfo.advancedFeaturesList = new ArrayList<MoodleAdvancedFeature>();

			JSONArray jsonFunctions = (JSONArray) jsonObject.get("functions");
			for (int i = 0; i < jsonFunctions.size(); i++) {
				JSONObject jsonFunction = (JSONObject) jsonFunctions.get(i);
				MoodleFunction function = new MoodleFunction();
				function.name = (String) jsonFunction.get("name");
				function.version = (String) jsonFunction.get("version");
				moodleinfo.functionList.add(function);
			}

			JSONArray jsonFeatures = (JSONArray) jsonObject.get("advancedfeatures");
			for (int i = 0; i < jsonFeatures.size(); i++) {
				JSONObject jsonFeature = (JSONObject) jsonFeatures.get(i);
				MoodleAdvancedFeature feature = new MoodleAdvancedFeature();
				feature.name = (String) jsonFeature.get("name");
				feature.value = (long) jsonFeature.get("value");
				moodleinfo.advancedFeaturesList.add(feature);
			}

			return moodleinfo;
		}
	}

	public List<MoodleCourse> core_course_get_courses(String server, String token, long userid) throws Exception {

		// Parameters for core_enrol_get_users_courses
		String[] parameters = { "wsfunction", "moodlewsrestformat", "wstoken", "userid" };
		String[] values = { "core_enrol_get_users_courses", "json", token, Long.toString(userid) };
		String params = httpClient.encodeParameters(parameters, values); // Encode params

		String result = httpClient.getHTTPS(server + webservicePath + "?" + params); // Request page to server

		JSONParser parser = new JSONParser(); // Parse received json
		Object obj = parser.parse(result);

		try { // Try to receive object (returned if server finds exception)
			JSONObject jsonObject = (JSONObject) obj;
			String exception = (String) jsonObject.get("exception");
			String errorcode = (String) jsonObject.get("errorcode");
			String message = (String) jsonObject.get("message");
			throw new Moodle_ServerException(exception + " (" + errorcode + "). " + message);
		} catch (ClassCastException e) { // If no exception, get array
			JSONArray jsonArray = (JSONArray) obj;
			List<MoodleCourse> courseList = new ArrayList<MoodleCourse>(); // Create course list

			for (int i = 0; i < jsonArray.size(); i++) { // Parse each element
				JSONObject jsonCourse = (JSONObject) jsonArray.get(i);
				MoodleCourse course = new MoodleCourse();

				course.id = (long) jsonCourse.get("id");
				course.shortname = (String) jsonCourse.get("shortname");
				course.fullname = (String) jsonCourse.get("fullname");
				course.enrolledusercount = (long) jsonCourse.get("enrolledusercount");
				course.idnumber = (String) jsonCourse.get("idnumber");
				course.visible = (long) jsonCourse.get("visible");
				course.summary = (String) jsonCourse.get("shortname");
				course.summaryformat = (long) jsonCourse.get("summaryformat");
				course.format = (String) jsonCourse.get("shortname");
				course.showgrades = (Boolean) jsonCourse.get("showgrades");
				course.lang = (String) jsonCourse.get("lang");
				course.enablecompletion = (Boolean) jsonCourse.get("enablecompletion");

				courseList.add(course); // Add to list
			}

			return courseList;
		}
	}

	public List<MoodleCategory> core_course_get_contents(String server, String token, long courseid) throws Exception {

		// Parameters for core_course_get_contents
		String[] parameters = { "wsfunction", "moodlewsrestformat", "wstoken", "courseid" };
		String[] values = { "core_course_get_contents", "json", token, Long.toString(courseid) };
		String params = httpClient.encodeParameters(parameters, values); // Encode params

		String result = httpClient.getHTTPS(server + webservicePath + "?" + params); // Request page to server

		JSONParser parser = new JSONParser(); // Parse received json
		Object obj = parser.parse(result);

		try { // Try to receive object (returned if server finds exception)
			JSONObject jsonObject = (JSONObject) obj;
			String exception = (String) jsonObject.get("exception");
			String errorcode = (String) jsonObject.get("errorcode");
			String message = (String) jsonObject.get("message");
			throw new Moodle_ServerException(exception + " (" + errorcode + "). " + message);
		} catch (ClassCastException e) { // If no exception, get array

			/*
			 * Moodle Course file structure:
			 * 
			 * MoodleCourse | |-> MoodleCategory | |-> MoodleModule | |-> MoodleElement
			 */

			List<MoodleCategory> categoryList = new ArrayList<MoodleCategory>();

			JSONArray jsonCategories = (JSONArray) obj;
			for (int i = 0; i < jsonCategories.size(); i++) { // Parse each category
				JSONObject jsonCategory = (JSONObject) jsonCategories.get(i);

				MoodleCategory category = new MoodleCategory();

				category.id = (long) jsonCategory.get("id");
				category.name = (String) jsonCategory.get("name");
				category.visible = (long) jsonCategory.get("visible");
				category.summary = (String) jsonCategory.get("summary");
				category.summaryformat = (long) jsonCategory.get("summaryformat");

				List<MoodleModule> moduleList = new ArrayList<MoodleModule>();

				JSONArray jsonModules = (JSONArray) jsonCategory.get("modules");
				for (int j = 0; j < jsonModules.size(); j++) { // Parse each module
					JSONObject jsonModule = (JSONObject) jsonModules.get(j);

					MoodleModule module = new MoodleModule();

					module.id = (long) jsonModule.get("id");
					module.url = (String) jsonModule.get("url");
					module.name = (String) jsonModule.get("name");
					module.instance = (long) jsonModule.get("instance");
					module.description = (String) jsonModule.get("description");
					module.visible = (long) jsonModule.get("visible");
					module.modicon = (String) jsonModule.get("modicon");
					module.modname = (String) jsonModule.get("modname");
					module.modplural = (String) jsonModule.get("modplural");
					module.indent = (long) jsonModule.get("indent");

					List<MoodleElement> elementList = new ArrayList<MoodleElement>();

					JSONArray jsonElements = (JSONArray) jsonModule.get("contents");
					if (jsonElements != null) {
						for (int k = 0; k < jsonElements.size(); k++) { // Parse each element
							JSONObject jsonElement = (JSONObject) jsonElements.get(k);

							MoodleElement element = new MoodleElement();

							element.type = (String) jsonElement.get("type");
							element.filename = (String) jsonElement.get("filename");
							element.filepath = (String) jsonElement.get("filepath");
							try {
								element.filesize = (long) jsonElement.get("filesize");
							} catch (NullPointerException nullPointerEx) {
								element.filesize = -1;
							}
							element.fileurl = (String) jsonElement.get("fileurl");
							try {
								element.timecreated = (long) jsonElement.get("timecreated");
							} catch (NullPointerException nullPointerEx) {
								element.timecreated = -1;
							}
							try {
								element.timemodified = (long) jsonElement.get("timemodified");
							} catch (NullPointerException nullPointerEx) {
								element.timemodified = -1;
							}
							try {
								element.sortorder = (long) jsonElement.get("sortorder");
							} catch (NullPointerException nullPointerEx) {
								element.sortorder = -1;
							}
							try {
								element.userid = (long) jsonElement.get("userid");
							} catch (NullPointerException nullPointerEx) {
								element.sortorder = -1;
							}
							element.author = (String) jsonElement.get("author");
							element.license = (String) jsonElement.get("license");

							elementList.add(element);
						}
					}

					module.elementList = elementList;

					moduleList.add(module); // Add to list
				}
				category.moduleList = moduleList; // Add to category
				categoryList.add(category); // Add to list
			}
			return categoryList;
		}
	}

	public List<MoodleMessage> core_message_get_messages(String server, String token, long useridto, long useridfrom)
			throws Exception {
		// Parameters for core_message_get_messages
		String[] parameters = { "wsfunction", "moodlewsrestformat", "wstoken", "useridto", "useridfrom" };
		String[] values = { "core_message_get_messages", "json", token, Long.toString(useridto),
				Long.toString(useridfrom) };
		String params = httpClient.encodeParameters(parameters, values); // Encode params

		String result = httpClient.getHTTPS(server + webservicePath + "?" + params); // Request page to server

		JSONParser parser = new JSONParser(); // Parse received json
		Object obj = parser.parse(result);

		JSONObject jsonObject = (JSONObject) obj;

		String exception = (String) jsonObject.get("exception");
		if (exception != null) {
			String errorcode = (String) jsonObject.get("errorcode");
			String message = (String) jsonObject.get("message");
			throw new Moodle_ServerException(exception + " (" + errorcode + "). " + message);
		} else {
			JSONArray jsonElements = (JSONArray) jsonObject.get("messages");
			List<MoodleMessage> messageList = new ArrayList<MoodleMessage>();

			for (int i = 0; i < jsonElements.size(); i++) {
				JSONObject jsonMessage = (JSONObject) jsonElements.get(i);
				MoodleMessage message = new MoodleMessage();
				message.id = (long) jsonMessage.get("id");
				message.useridfrom = (long) jsonMessage.get("useridfrom");
				message.useridto = (long) jsonMessage.get("useridto");
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
				messageList.add(message);
			}

			return messageList;
		}
	}

	/*
	 * public void xml_core_webservice_get_site_info (String serverURL, String
	 * wstoken) { String[] names = {"wstoken", "moodlewsrestformat", "wsfunction"};
	 * String[] params = {wstoken, "xml", "core_webservice_get_site_info"}; try {
	 * String response = getHTTPrequest(serverURL, names, params); Document
	 * xmlResponse = getXmlFromHTTPresponse(response); XPath xPath =
	 * XPathFactory.newInstance().newXPath(); String expression =
	 * "/RESPONSE/SINGLE/KEY"; NodeList nodeList = (NodeList)
	 * xPath.compile(expression).evaluate(xmlResponse, XPathConstants.NODESET); for
	 * (int i = 0; i < nodeList.getLength(); i++) {
	 * System.out.println(nodeList.item(i).getAttributes().item(0)); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	/*
	 * private Document getXmlFromHTTPresponse (String response) throws
	 * SAXException, IOException, ParserConfigurationException {
	 * DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	 * DocumentBuilder builder = null; builder =
	 * builderFactory.newDocumentBuilder(); Document document = builder.parse(new
	 * ByteArrayInputStream(response.getBytes())); return document; }
	 * 
	 * private String getHTTPrequest(String serverURL, String[] names, String[]
	 * params) throws Exception { String requestParams =
	 * httpClient.encodeParameters(names, params); return
	 * httpClient.getHTTPS(serverURL + webservicePath + "?" + requestParams); }
	 */

	public HttpClient gethttpClient() {
		return httpClient;
	}

	public void sethttpClient(HttpClient client) {
		httpClient = client;
	}

	public MoodleFunction[] getSupportedFunctions() {
		return supportedFunctions;
	}

	@SuppressWarnings("serial")
	public class Moodle_ServerException extends Exception {

		public Moodle_ServerException() {

		}

		public Moodle_ServerException(String message) {
			super(message);
		}

		public Moodle_ServerException(Throwable cause) {
			super(cause);
		}

		public Moodle_ServerException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}

/*
 * TODO: gradereport_user_get_grades_table params: wstoken, wsfunction,
 * courseid, userid
 * 
 * core_group_get_course_user_groups params: wstoken, wsfunction, courseid,
 * userid
 * 
 * moodle_user_get_users_by_courseid params: wstoken, wsfunction, courseid
 * 
 * core_message_send_instant_messages post request
 * 
 * calendar events idea: use core_user_add_user_private_files to save app
 * information "on the cloud".
 *
 * html viewer for .html files in a separate window
 */