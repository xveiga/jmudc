package mudc.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class HttpClient {

	/*
	 * DISCLAIMER: This HTTP client implementation DOES NOT check where cookies come
	 * from. Therefore, when asked by a server, it sends ALL cookies, independently
	 * of which webpage has sent them. Please use different instances to handle
	 * different websites.
	 */

	private String USER_AGENT = "Mozilla/5.0";
	private String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	private String ACCEPT_LANGUAGE = "en-US,en;q=0.5";
	private String ACCEPT_ENCODING = "gzip"; // Fixed value for gzip support.
	private String REFERRER = null;
	private String HOST = null;

	private boolean gzipSupport = false; // Encoding support switch.
	private boolean referrerActive = false; // POST referrer header sending switch.
	private boolean hostActive = false; // POST host header sending switch.
	// private int bufferSize = 8192; // Connection buffer size for downloads.

	private HttpsURLConnection conn;
	private List<String> cookies;
	private int lastStatusCode = -1;

	public HttpClient() {
		// cookies = new ArrayList<String>();
		CookieManager cookies = new CookieManager();
		cookies.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(cookies);
	}

	/*
	 * ## HTML GET ## -This method sends a HTTPS GET request to an URL and gets the
	 * response back. Exceptions: -MalformedURLException (unknown URL format)
	 * -ConnectException (connection refused) -ClassCastException (trying to connect
	 * using HTTP instead of HTTPS)
	 * 
	 */
	public String getHTTPS(String url) throws Exception {

		URL obj = new URL(url); // transform String to URL
		conn = (HttpsURLConnection) obj.openConnection(); // create connection object with URL

		// ## HEADERS ##
		conn.setRequestMethod("GET"); // Set request mode to GET
		conn.setUseCaches(false); // Best turned off to avoid problems (can make proxies cache this type of
									// requests and cause undesired behaviour)
		conn.setRequestProperty("User-Agent", USER_AGENT); // Set user-agent
		conn.setRequestProperty("Accept", ACCEPT); // Set what we are expecting to receive/support
		conn.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE); // Set language list
		if (gzipSupport) { // If gzip support is enabled, send a header to indicate so to the server.
			conn.setRequestProperty("Accept-Encoding", ACCEPT_ENCODING);
		}
		if (cookies != null) { // If we have cookies stored, add them to the header.
			for (String cookie : cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]); // Cookie attributes are separated with a
																			// ";".
			} // Cookie header format: Set-Cookie: <cookie-name>=<cookie-value>;
				// attribute1=value; attribute2. We only need the first part ([0]);
		}

		// ## RESPONSE ##
		String responseString = null; // String containing response data.
		InputStream connInputStream = null; // Input stream for connection.
		int responseCode = conn.getResponseCode(); // When this is executed, the request is sent. Get response code
													// back.
		lastStatusCode = responseCode; // Store response code on global variable.
		if (responseCode >= 200 && responseCode < 300) { // Code 2xx means SUCCESS

			if (gzipSupport && "gzip".equals(conn.getContentEncoding())) { // If the response is gzipped,
				connInputStream = new GZIPInputStream(conn.getInputStream()); // unzip InputStream.
			} else { // If it is a plain text response,
				connInputStream = conn.getInputStream(); // pass the stream to the BufferedReader.
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connInputStream)); // Connection Buffer
			String inputLine; // Temporary variable
			StringBuffer response = new StringBuffer(); // String Buffer

			while ((inputLine = in.readLine()) != null) { // When there's data available,
				response.append(inputLine); // write data to the String Buffer
			}
			in.close(); // Destroy connection buffer
			responseString = response.toString(); // pass String Buffer contents to a String

			cookies = conn.getHeaderFields().get("Set-Cookie");
			// checkCookies(conn.getHeaderFields().get("Set-Cookie")); // Get the header
			// containing new cookies, don't store duplicates, and keep the old ones.
		} else {
			throw new UnexpectedHTTPStatusCode("Server replied with code " + responseCode); // Throw exception if server
																							// doesn't reply with OK
		}

		return responseString; // return HTTP response
	}

	/*
	 * ## HTML POST ## -This method sends a HTTPS POST request to an URL and gets
	 * the response back. Exceptions: -MalformedURLException (unknown URL format)
	 * -ConnectException (connection refused) -ClassCastException (trying to connect
	 * using HTTP instead of HTTPS)
	 * 
	 */
	public String postHTTPS(String url, String postParams, boolean keepAlive) throws Exception {

		URL obj = new URL(url); // transform String to URL
		conn = (HttpsURLConnection) obj.openConnection(); // create connection object with URL

		// ## HEADERS ##

		conn.setRequestMethod("POST"); // Set request mode to POST
		conn.setUseCaches(false); // Best turned off to avoid problems (can make proxies cache this type of
									// requests and cause undesired behaviour)
		conn.setRequestProperty("User-Agent", USER_AGENT); // Set user-agent
		conn.setRequestProperty("Accept", ACCEPT); // Set what we are expecting to receive/support
		conn.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE); // Set language list
		if (hostActive) { // If host is enabled, send the corresponding header. May be required in some
							// proxy configurations.
			conn.setRequestProperty("Host", HOST);
		}
		if (keepAlive) { // Set connection to keep-alive or to close
			conn.setRequestProperty("Connection", "keep-alive");
		} else {
			conn.setRequestProperty("Connection", "close");
		}
		if (referrerActive) { // If referrer is enabled, send the corresponding header. May be required in
								// some pages.
			conn.setRequestProperty("Referer", REFERRER);
		}
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Set content type to http form
																						// response.
		conn.setRequestProperty("Content-Length", Integer.toString(postParams.length())); // Length of post request
		if (cookies != null) { // If we have cookies stored, add them to the header.
			for (String cookie : cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]); // Cookie attributes are separated with a
																			// ";".
			} // Cookie header format: Set-Cookie: <cookie-name>=<cookie-value>;
				// attribute1=value; attribute2. We only need the first part ([0]);
		}
		conn.setDoOutput(true); // Enable outputStream for POST request (Allows to transmit data in body instead
								// of header only).
		conn.setDoInput(true); // Enable inputStream for POST request.

		// ## RESPONSE ##

		String responseString = null; // String containing response data.

		// TODO: Figure out gzip transport for POST requests.
		DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream()); // Get connection outputStream and
																						// buffer it.
		outputStream.writeBytes(postParams); // Write POST request to buffer.
		outputStream.flush(); // Flush buffer.
		outputStream.close(); // Close data stream.

		cookies = conn.getHeaderFields().get("Set-Cookie");
		// checkCookies(conn.getHeaderFields().get("Set-Cookie")); // Get the header
		// containing new cookies, don't store duplicates, and keep the old ones.

		int responseCode = conn.getResponseCode(); // Get response code from server.
		lastStatusCode = responseCode; // Store response code on global variable.
		if (responseCode >= 200 && responseCode < 300) { // Code 2xx means SUCCESS
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream())); // Connection Input
																									// Buffer
			String inputLine; // Temporary variable
			StringBuffer response = new StringBuffer(); // String Buffer

			while ((inputLine = in.readLine()) != null) { // When there's data available,
				response.append(inputLine); // write data to the String Buffer
			}
			in.close(); // Destroy connection buffer
			responseString = response.toString(); // pass String Buffer contents to a String
		} else {
			throw new UnexpectedHTTPStatusCode("Server replied with code " + responseCode); // Throw exception if server
																							// doesn't reply with OK
		}

		return responseString;
	}

	/*
	 * ## encodeParameters ## -This method gets two arrays of parameters and its
	 * values, and encodes them on URL-ready format. Exceptions:
	 * -UnsupportedEncodingException
	 */

	public String encodeParameters(String parameterName[], String parameterValue[]) throws Exception {
		StringBuilder result = null;
		if (parameterName.length == parameterValue.length) {
			result = new StringBuilder();
			for (int i = 0; i < parameterName.length; i++) {
				String param = parameterName[i] + "=" + URLEncoder.encode(parameterValue[i], "UTF-8");
				if (result.length() == 0) {
					result.append(param);
				} else {
					result.append("&" + param);
				}
			}
		} else {
			throw new EncodingException(
					"Encoding Error: Locales[] parameterName and parameterValue are not of the same length.");
		}

		return result.toString();
	}

	/*
	 * public void downloadFileFromURL(String url, String fileName) throws Exception
	 * {
	 * 
	 * URL obj = new URL(url); // transform String to URL conn =
	 * (HttpsURLConnection) obj.openConnection(); // create connection object with
	 * URL
	 * 
	 * // ## RESPONSE ## InputStream connInputStream = null; // Input stream for
	 * connection. int responseCode = conn.getResponseCode(); // When this is
	 * executed, the request is sent. Get response code back. if (responseCode >=
	 * 200 && responseCode < 300) { // Code 2xx means SUCCESS
	 * 
	 * int contentLength = conn.getContentLength(); if
	 * ("gzip".equals(conn.getContentEncoding())) { // If the response is gzipped,
	 * connInputStream = new GZIPInputStream(conn.getInputStream()); // unzip
	 * InputStream. } else { // If it is a plain text response, connInputStream =
	 * conn.getInputStream(); // pass the stream to the BufferedReader. }
	 * 
	 * // Open file in outputStream FileOutputStream outputStream = new
	 * FileOutputStream(fileName);
	 * 
	 * int totalBytesRead = -1; int bytesRead = -1; byte[] buffer = new
	 * byte[bufferSize]; // Create buffer while ((bytesRead =
	 * connInputStream.read(buffer)) != -1) { // While there is data available, and
	 * there's no error outputStream.write(buffer, 0, bytesRead); // Write buffer to
	 * disk totalBytesRead += bytesRead; System.out.println("[" + totalBytesRead +
	 * "/" + contentLength + "]"); } outputStream.close(); connInputStream.close();
	 * } else { throw new UnexpectedHTTPStatusCode("Server replied with code " +
	 * responseCode); // Throw exception if server doesn't reply with OK } }
	 */
	/*
	 * Old code. Doesn't work. Now cookies are handled by CookieManager. Adds new
	 * cookies to cookie list, checks that no cookies are repeated. If they are,
	 * update their values. Also, keeps the old cookies untouched.
	 */
	/*
	 * private void checkCookies (List<String> newCookies) {
	 * System.out.println("old     " + cookies); System.out.println("new     " +
	 * newCookies); if (newCookies != null) { boolean exists = false; for (String
	 * newCookie : newCookies) { for (String oldCookie : cookies) { if
	 * (oldCookie.split("=")[0].contains(newCookie.split("=")[0])) { oldCookie =
	 * newCookie; exists = true; break; } } if (!exists) { cookies.add(newCookie); }
	 * } } System.out.println("old+new " + cookies); }
	 */

	@SuppressWarnings("serial")
	class EncodingException extends Exception {

		public EncodingException() {
		}

		public EncodingException(String message) {
			super(message);
		}
	}

	@SuppressWarnings("serial")
	class UnexpectedHTTPStatusCode extends Exception {

		public UnexpectedHTTPStatusCode() {
		}

		public UnexpectedHTTPStatusCode(String message) {
			super(message);
		}
	}

	// ## Parameter setting methods ##

	public int getLastStatusCode() {
		return lastStatusCode;
	}

	public CookieHandler getCookieManager() {
		return CookieHandler.getDefault();
	}

	public void setCookieManager(CookieHandler cookieHandler) {
		CookieHandler.setDefault(cookieHandler);
		;
	}

	public boolean getReferrerActive() {
		return referrerActive;
	}

	public void setReferrerActive(boolean enabled) {
		referrerActive = enabled;
	}

	public String getReferrer() {
		return REFERRER;
	}

	public void setReferrer(String referrer) {
		REFERRER = referrer;
	}

	public boolean getHostActive() {
		return hostActive;
	}

	public void setHostActive(boolean enabled) {
		hostActive = enabled;
	}

	public String getHost() {
		return HOST;
	}

	public void setHost(String host) {
		HOST = host;
	}

	public boolean getGzipSupport() {
		return gzipSupport;
	}

	public void setGzipSupport(boolean enabled) {
		gzipSupport = enabled;
	}

	public String getUserAgent() {
		return USER_AGENT;
	}

	public void setUserAgent(String userAgent) {
		USER_AGENT = userAgent;
	}

	public String getAcceptHeader() {
		return ACCEPT;
	}

	public void setAcceptHeader(String acceptHeader) {
		ACCEPT = acceptHeader;
	}

	public String getAcceptLanguage() {
		return ACCEPT_LANGUAGE;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		ACCEPT_LANGUAGE = acceptLanguage;
	}
}
