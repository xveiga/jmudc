package mudc.core.casudc;

import java.util.ArrayList;
import java.util.List;

import mudc.core.HttpClient;
import mudc.core.PatternFinder;

public class MoodleTokenObtainer {
	
	private static final String moodleLoginParams = "?service=https%3A%2F%2Fmoodle.udc.es%2Flogin%2Findex.php%3FauthCAS%3DCAS"; // Login params. For the UDC's Moodle the service parameter is needed as there are random auth problems without it. 
	private static final String pageContainsOnLoginSucess = "<a class=\"logo\" href=\"//moodle.udc.es\" title=\""; // Substring that the redirected page after login has to have (to confirm correct login).
	
	private HttpClient httpClient; // HttpClient instance that will be granted access.
	private PatternFinder finder;  // Pattern finder for HTML parsing.

	public MoodleTokenObtainer() {
		httpClient = new HttpClient(); // Init httpClient.
	}

	public MoodleTokenData getTokenForUser(String username, char[] password, Boolean stayLoggedIn) throws Exception {
		System.out.println("[MoodleTokenObtainer] Logging in as \"" + username + "\"");
		CASlogin cas = new CASlogin (httpClient); // Create CASlogin instance.
		try {
			if (cas.login(moodleLoginParams, pageContainsOnLoginSucess, username, password)) { // If we are logged in, then find the token.
				//System.out.println("[UDC CAS login] Logged in sucessfully");
				httpClient = cas.getHttpClient(); // Get the logged in httpClient instance.
				finder = new PatternFinder();  // Init patternFinder. We'll need it in a moment.
				
				System.out.println("[CASlogin] Navigating to preferences page");
				// Now we head straight to the preferences page. We need to find the "Security Keys" link, as it includes a safety session key that cannot be obtained other way.
				String moodlePreferencesPage = httpClient.getHTTPS("https://moodle.udc.es/user/preferences.php");
				System.out.println("[CASlogin] Parsing preferences webpage");
				List<String> loginLinkOcurrences = finder.findElementsByPattern(moodlePreferencesPage, "href=\"https://moodle.udc.es/user/managetoken.php\\?sesskey=(.*?)\""); // Grab the key.
				if (loginLinkOcurrences.size() == 1) { // Check that only one ocurrence exists.
					String loginLink = loginLinkOcurrences.get(0).replaceAll("href=", "").replace("\"", ""); // Trim the surrounding parts to get the link only.
					
					System.out.println("[CASlogin] Navigating to security keys page");
					String moodleKeysPage = httpClient.getHTTPS(loginLink); // Navigate to the Security Keys Webpage
					
					System.out.println("[CASlogin] Parsing preferences webpage");
					List<String> keys = finder.findElementsByPattern(moodleKeysPage, "(?s)<td class=\"cell c0\"(.*?)</td>"); // Then extract the table cells. Cell c0 is the token.
					List<String> names = finder.findElementsByPattern(moodleKeysPage, "(?s)<td class=\"cell c1\"(.*?)</td>"); // Cell c1 is the service name for the token. (Tokens may have different permissions).
					List<String> resetURLs = finder.findElementsByPattern(moodleKeysPage, "(?s)<td class=\"cell c4 lastcol\"(.*?)</td>"); // Cell c4 is the reset link
					
					if (keys.size() == names.size() && keys.size() == resetURLs.size()) { // Check all fields were found.
						for (int i=0; i<keys.size(); i++) { // For each key row, do some trimming to get the data we want.
							keys.set(i, finder.findElementsByPattern(keys.get(i), ">(.*?)<").get(0).replace(">", "").replace("<", "")); // Get only the key inside the table cell
							names.set(i, finder.findElementsByPattern(names.get(i), ">(.*?)<").get(0).replace(">", "").replace("<", "")); // Same for the service name
							resetURLs.set(i, finder.findElementsByPattern(resetURLs.get(i), "href=\"https://moodle.udc.es/user/managetoken.php(.*?)\">").get(0).replace("href=\"", "").replace("\">", "")); // Trim reset url
						}
						
						List<MoodleToken> tokendata = new ArrayList<MoodleToken>(); // Create Moodle Token list to store information.
						for (int i=0; i<keys.size(); i++) {
							tokendata.add(new MoodleToken(keys.get(i), names.get(i), resetURLs.get(i)));
						}

						MoodleTokenData moodleTokens = new MoodleTokenData(tokendata);
						
						if (!stayLoggedIn) {
							if (!cas.logout()) {
								throw new UnexpectedResponseException("The server did not reply as expected to the logout request.");
							}
						}
						
						return moodleTokens; // Return everything.
					}
					else {
						throw new HtmlParseError("The Moodle page could not be recognized. The security keys table was not laid as expected.");
					}
				}
				else {
					throw new HtmlParseError("The Moodle page could not be recognized. The security keys link cannot be found.");
				}
			}
			else {
				throw new InvalidCredentialsException("Could not log in. The credentials are invalid.");
			}
		} catch (HtmlParseError e) {
			throw new HtmlParseError ("Cannot access Moodle Webpage: " + e.toString());
		}
	}
	
	// Future expansion: Add token reset capabilities. There's a POST submit form on the security keys webpage.
	
	public class UnexpectedResponseException extends Exception {
		private static final long serialVersionUID = 1L;

		public UnexpectedResponseException() {}
		
		public UnexpectedResponseException(String message) {
			super(message);
		}
	}
	
	public class InvalidCredentialsException extends Exception {
		private static final long serialVersionUID = 1L;

		public InvalidCredentialsException() {}
		
		public InvalidCredentialsException(String message) {
			super(message);
		}
	}
	
	public class HtmlParseError extends Exception {
		
		private static final long serialVersionUID = 1L;

		public HtmlParseError() {}
		
		public HtmlParseError(String message) {
			super(message);
		}
	}
}
