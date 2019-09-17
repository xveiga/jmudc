package mudc.core;

public class MoodleElement {
	public String type = null;
	public String filename = null;
	public String filepath = null;
	public long filesize = -1;
	public String fileurl = null;
	public long timecreated = -1;
	public long timemodified = -1;
	public long sortorder = -1;
	public long userid = -1;
	public String author = null;
	public String license = null;

	// Local data TODO: save on data.json
	public boolean isLocal = false; // If file has been downloaded or not (only used for "file" type)
	public long checksum = -1; // File checksum at download instant
	public String localPath = null; // Path where file was downloaded
	public long status = 0; //TODO: Add to MetadataStorage
	
	// GUI data
	public float progress = -1;

	@Override
	public String toString() {
		return filename;
	}
}
