package mudc.core.casudc;

import java.util.List;

public class MoodleTokenData {
	
	private List<MoodleToken> tokens;
	
	public MoodleTokenData (List<MoodleToken> tokenList) {
		tokens = tokenList;
	}
	
	public List<MoodleToken> getTokens() {
		return tokens;
	}
}