package mudc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class IntegrityChecker {
	
	private int bufferSize = 32768;
	
	public IntegrityChecker() {
		
	}
	
	public boolean checkFileIntegrity(File file, long expectedChecksum, ProgressUpdater progress) throws IOException {
		long checksum = getCRCFile(file, progress);
		return (checksum == expectedChecksum);
	}
	
	private long getCRCFile(File file, ProgressUpdater progress) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		CRC32 checksum = new CRC32();
		long totalBytesRead = -1;
		int bytesRead = -1;
		byte[] buffer = new byte[bufferSize];
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			checksum.update(buffer);
			totalBytesRead += bytesRead;
			if (progress != null) progress.setProgress(totalBytesRead);
		}
		inputStream.close();
		return checksum.getValue();
	}
	
	public void setBufferSize(int size) {
		bufferSize = size;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

}
