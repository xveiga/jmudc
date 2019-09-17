package mudc.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;

public class HttpDownloader {

	/*
	 * TODO: Calculate magic number of file (first 4 bytes). Check if magic number
	 * matches expected file extension. If it does not, prompt user to open file
	 * based on file extension, or based on magic number.
	 */

	private int bufferSize = 32768; // Connection buffer size for downloads. (Default 4kb, inspired by disk sector
									// size. Traditional is 512 bytes, modern is 4096 bytes).
	//TODO: Test bigger buffer sizes to avoid high CPU usage.

	public FileDownloadReturnData downloadFileFromURL(String url, File fileName, ProgressUpdater progressUpdater)
			throws Exception {

		URL obj = new URL(url); // transform String to URL
		HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection(); // create connection object with URL

		// ## RESPONSE ##
		InputStream connInputStream = null; // Input stream for connection.
		int responseCode = conn.getResponseCode(); // When this is executed, the request is sent. Get response code
													// back.
		if (responseCode >= 200 && responseCode < 300) { // Code 2xx means SUCCESS

			//int contentLength = conn.getContentLength();
			if ("gzip".equals(conn.getContentEncoding())) { // If the response is gzipped,
				connInputStream = new GZIPInputStream(conn.getInputStream()); // unzip InputStream.
			} else { // If it is a plain text response,
				connInputStream = conn.getInputStream(); // pass the stream to the BufferedReader.
			}

			// Prepare CRC calculation
			CRC32 checksum = new CRC32();

			// Open file in outputStream
			FileOutputStream outputStream = new FileOutputStream(fileName);

			int totalBytesRead = -1;
			int bytesRead = -1;
			byte[] buffer = new byte[bufferSize]; // Create buffer
			while ((bytesRead = connInputStream.read(buffer)) != -1) { // While there is data available, and there's no
																		// error
				checksum.update(buffer); // Calculate checksum of file on the fly.
				outputStream.write(buffer, 0, bytesRead); // Write buffer to disk
				totalBytesRead += bytesRead;
				if (progressUpdater != null) progressUpdater.setProgress(totalBytesRead);
			}

			// Close streams
			connInputStream.close();
			outputStream.close();
			FileDownloadReturnData returnData = new FileDownloadReturnData(); // Return new info
			returnData.byteSize = totalBytesRead;
			returnData.checksum = checksum.getValue();
			returnData.mimeType = null;
			returnData.reportedName = null;
			return returnData;
		} else {
			throw new UnexpectedHTTPSDownloadStatusCode("Server replied with code " + responseCode); // Throw exception
																										// if server
																										// doesn't reply
																										// with OK
		}
	}

	@SuppressWarnings("serial")
	class UnexpectedHTTPSDownloadStatusCode extends Exception {

		public UnexpectedHTTPSDownloadStatusCode() {
		}

		public UnexpectedHTTPSDownloadStatusCode(String message) {
			super(message);
		}
	}
}

class FileDownloadReturnData {
	long checksum = -1;
	long byteSize = -1;
	String mimeType = null;
	String reportedName = null;
}
