package mudc.core;

import java.util.List;

public class MoodleModule {
	public long id = -1;
	public String url = null;
	public String name = null;
	public long instance = -1;
	public String description = null;
	public long visible = -1;
	public String modicon = null;
	public String modname = null;
	public String modplural = null;
	public long indent = -1;
	public List<MoodleElement> elementList = null;

	// Local data
	public int status = 0;//TODO: Add to MetadataStorage

	@Override
	public String toString() {
		return name;
	}
}
