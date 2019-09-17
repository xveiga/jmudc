package mudc.core;

import java.util.List;

public class MoodleInfo {
	public String sitename = null;
	public String username = null;
	public String firstname = null;
	public String lastname = null;
	public String fullname = null;
	public String lang = null;
	public long userid = -1;
	public String siteurl = null;
	public String userpictureurl = null;
	public long downloadfiles = -1;
	public long uploadfiles = -1;
	public String release = null;
	public String version = null;
	public String mobilecssurl = null;
	public boolean usercanmanageownfiles = false;
	public long userquota = -1;
	public long usermaxuploadfilesize = -1;
	public List<MoodleFunction> functionList = null;
	public List<MoodleAdvancedFeature> advancedFeaturesList = null;

	@Override
	public String toString() {
		return username + "@" + siteurl;
	}
}
