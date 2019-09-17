package mudc.core;

public class MoodleAdvancedFeature {
	MoodleAdvancedFeature() {
	}

	MoodleAdvancedFeature(String Name, long Value) {
		name = Name;
		value = Value;
	}

	public String name = null;
	public long value = -1;

	// Local data
	public int status = 0; //TODO: Add to MetadataStorage
}
