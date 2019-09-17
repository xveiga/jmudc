package mudc.core.dataelements;

public class MoodleMessage {
	public long id = -1;
	public long useridfrom = -1;
	public long useridto = -1;
	public String subject = null;
	public String text = null;
	public String fullmessage = null;
	public long fullmessageformat = 1;
	public String fullmessagehtml = null;
	public String smallmessage = null;
	public long notification = -1;
	public String contexturl = null;
	public String contexturlname = null;
	public long timecreated = -1;
	public long timeread = -1;
	public String usertofullname = null;
	public String userfromfullname = null;

	// Local data
	public int status = 0; //TODO: Add to MetadataStorage

	@Override
	public String toString() {
		return "[" + useridfrom + " -> " + useridto + "]: " + subject;
	}
}
