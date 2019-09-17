package mudc.core.casudc;

import java.util.ArrayList;
import java.util.List;

import mudc.core.HttpClient;
import mudc.core.PatternFinder;

public class CASlogin {
	
	/**
	 *  This class contains methods to allow login and logout functions on UDC's Central Authentication System (CAS).
	 *  It requires a HttpClient instance, which is used to navigate through CAS's web page. The instance remains authenticated afterwards.
	 */
	
	private static final String baseURL = "https://cas.udc.es";
	private static final String logoutConfirmationPhrase = "<h2>A súa sesión foi pechada correctamente</h2>";
	
	private HttpClient httpClient;
	private PatternFinder finder;

	/** 
	 *  Access is granted to an unique Core_HttpClient instance.
	 * @param client
	 */
	
	public CASlogin(HttpClient client) {
		httpClient = client;
		finder = new PatternFinder();
	}
	
	/**
	 * Logs into CAS with the given username and password.
	 * @param username
	 * @param password
	 * @throws Exception 
	 */
	
	public boolean login(String loginParams, String confirmationPhrase, String username, char[] password) throws Exception {
		
		// Let the spaghetti code adventure begin! I know, "Don't ever parse HTML with regex".
		System.out.println("[CASlogin] Connecting to CAS website");
		String loginPage = httpClient.getHTTPS(baseURL + "/login" + loginParams); // Connect to CAS login page.
		System.out.println("[CASlogin] Parsing CAS website");
		List<String> forms = finder.findElementsByPattern(loginPage, "<form(.*?)<\\/form>");  // Find login HTML form.
		if (forms.size() == 1) {  // If found, get it.
			String loginForm = forms.get(0); 
			List <String> formLine = finder.findElementsByPattern(loginForm, "<form(.*?)>"); // Find the form header line.
			if (formLine.size() == 1) { // If the header exists, get it.
				String formHeader = formLine.get(0);
				List<String> formURL = finder.findElementsByPattern(formHeader, "action=\"(.*?)\""); // Find the form submission URL.
				if (formURL.size() == 1) {
					String postURL = formURL.get(0).replace("action=", "").replaceAll("\"", ""); // Remove the surrounding text to get the pure URL (can contain parameters).
					if (!formLine.contains("method=\"post\"")) { // Make sure it requires POST to submit. If not, the webpage is not as we expect it to be.
						List<String> inputFields = finder.findElementsByPattern(loginForm, "<input(.*?)>"); // Then find the input fields (this is needed as the webpage includes the number of tries, and can change on different logins).
						List<String> inputNames = new ArrayList<String>(); // Create variables to store the name-value pairs on the form.
						List<String> inputValues = new ArrayList<String>();
						for (String field : inputFields) { // For each field, try to get the name and the value.
							List<String> names = finder.findElementsByPattern(field, "name=\"(.*?)\"");
							List<String> values = finder.findElementsByPattern(field, "value=\"(.*?)\"");
							if (names.size() == 1 && values.size() == 1) { // If none or more than one are found, the webpage is not behaving as expected, and cannot be parsed.
								inputNames.add(names.get(0).replace("name=", "").replaceAll("\"", "")); // Get the first and only ocurrence of each pair, and store it for later.
								inputValues.add(values.get(0).replace("value=", "").replaceAll("\"", ""));
							}
							else {
								throw new HtmlParseError("The CAS login page could not be recognized. The form parameters are missing or not correct."); 
							}
						}
						/* We've finished parsing the webpage, and we can prepare the response.
						 * We need to replace the values on the username and password fields by our own ones.
						 * This could be done by finding the index where each field is located.
						 * But the webpage should be static, and if it changes, this code may not work anyway.
						 * So it is done directly. The user field is assumed to be the first one, and the password the second.
						 * If there's an error the webpage shoud redirect to another page that can be catched.
						*/
						
						inputValues.set(0, username); // Set provided username.
						inputValues.set(1, String.valueOf(password)); // Set provided password. 
						/* TODO: For security purposes the password should NEVER be converted to a String.
						 * It should stay as a char array until it is used, then the array should be overwritten
						 * by setting each character to zero or another value.
						 */
						
						// Now, we prepare and send a POST request with our values:
						
						String[] nameArray = new String[inputNames.size()]; // Convert Lists to Arrays for encoding
						String[] valueArray = new String[inputValues.size()]; 
						for (int i=0; i<inputNames.size(); i++) {
							nameArray[i] = inputNames.get(i);
						}
						for (int i=0; i<inputValues.size(); i++) {
							valueArray[i] = inputValues.get(i);
						}
						
						String parameters = httpClient.encodeParameters(nameArray, valueArray); // Encode parameters for sending through HTTP.
				
						System.out.println("[CASlogin] Sending login request");
						String loginResponse = httpClient.postHTTPS(baseURL + postURL, parameters, true); // SEND POST REQUEST
						
						if (loginResponse.contains(confirmationPhrase)) { // If the confirmation phrase is received, we are logged in!
							System.out.println("[CASlogin] Login successful.");
							return true;
						}
						
						else if (loginResponse.contains("As credenciais proporcionadas non parecen correctas.")) { // If a "wrong password" message is received, return false.
							System.out.println("[CASlogin] Login failed, incorrect credentials.");
							return false;
						}
						
						else { // If neither, something failed.
							System.err.println("[CASlogin] Login failed, unknown error. Server response: " + loginResponse);
							throw new HtmlParseError("The login operation failed. The CAS server did not report a successful login.");
						}
						
					}
					else {
						throw new HtmlParseError("The CAS login page could not be recognized. The webpage is not reporting to use an HTTP POST request to login.");
					}
				}
				else {
					throw new HtmlParseError("The CAS login page could not be recognized. The webpage is not reporting back the form submission URL correctly.");
				}
			}
			else {
				throw new HtmlParseError("The CAS login page could not be recognized. The login form header line could not be grabbed, but the form was found. Most likely this is a one time error.");
			}
		}
		else {
			throw new HtmlParseError("The CAS login page could not be recognized. The login form cannot be found."); 
		}
	}
	
	public boolean logout() throws Exception {
		System.out.println("[CASlogin] Logging out");
		String logoutPage = httpClient.getHTTPS(baseURL + "/logout");
		if (logoutPage.contains(logoutConfirmationPhrase)) {
			System.out.println("[CASlogin] Logout successful.");
			return true;
		}
		else {
			System.out.println("[CASlogin] Logout failed.");
			return false;
		}
		//return logoutPage.contains(logoutConfirmationPhrase);
	}
	
	public HttpClient getHttpClient () {
		return httpClient;
	}
	
	public class HtmlParseError extends Exception {
		
		private static final long serialVersionUID = 1L;

		public HtmlParseError() {}
		
		public HtmlParseError(String message) {
			super(message);
		}
	}
}
