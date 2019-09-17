package mudc.core;

import java.util.List;

public class MoodleCategory {
	public long id = -1;
	public String name = null;
	public long visible = -1;
	public String summary = null;
	public long summaryformat = -1;
	public List<MoodleModule> moduleList = null;

	// Local data
	public long status = 0; //TODO: Add to MetadataStorage

	@Override
	public String toString() {
		return name;
	}
}
