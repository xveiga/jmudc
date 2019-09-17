package mudc.core.casudc;

public class MoodleToken {
	
	private String token = null;
	private String serviceName = null;
	private String resetLink = null;
	
	public MoodleToken (String Token, String ServiceName, String ResetLink) {
		token = Token;
		serviceName = ServiceName;
		resetLink = ResetLink;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public String getResetLink() {
		return resetLink;
	}
	
}
