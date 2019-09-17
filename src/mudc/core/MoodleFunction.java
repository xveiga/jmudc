package mudc.core;

public class MoodleFunction {
	MoodleFunction() {
	}

	MoodleFunction(String Name, String Version) {
		name = Name;
		version = Version;
	}

	public String name = null;
	public String version = null;

	// Local data
	public int status = 0; //TODO: Add to MetadataStorage
}
