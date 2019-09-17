package mudc.core;

import java.util.List;

public class MoodleCourse {
	public long id = -1;
	public String shortname = null;
	public String fullname = null;
	public long enrolledusercount = -1;
	public String idnumber = null;
	public long visible = -1;
	public String summary = null;
	public long summaryformat = -1;
	public String format = null;
	public boolean showgrades = false;
	public String lang = null;
	public boolean enablecompletion = false;
	public List<MoodleCategory> categoryList = null;

	// Local data
	public long status = 0;
	public String displayName = null;

	@Override
	public String toString() {
		if (displayName == null) return fullname;
		else return displayName;
	}
}
