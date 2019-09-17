package mudc.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternFinder {

	public PatternFinder() {
	}

	/**
	 * This method takes a string and finds substrings that match a certain pattern.
	 * Intended pattern usage: "aaaa(.*?)bbbb". The section in parenthesis is the
	 * unknown portion. "aaaa" and "bbbb" are start and end known patterns. One of
	 * them can be empty. It returns an exception if no elements are found, or if a
	 * parsing error occurs. Of course, other regex expressions can also be used.
	 */
	public List<String> findElementsByPattern(String input, String elementPattern) throws Exception {
		boolean matchFound = false; // Boolean to know if any pattern has been found.
		List<String> elements = new ArrayList<String>(); // List containing unknown portions that are found
		Pattern pattern = Pattern.compile(elementPattern); // Pattern to search compilation
		Matcher matcher = pattern.matcher(input); // Create pattern finder
		while (matcher.find()) { // While any patterns are found
			matchFound = true; // Remember we found at least one
			elements.add(matcher.group(0)); // Save the whole pattern.
		}
		if (!matchFound) { // If no matches are found,
			elements = null; // Return no elements
			throw new MatchNotFoundException(
					"No elements matching the expression \"" + elementPattern + "\"were found"); // and throw an
																									// exception
																									// indicating so
		}
		return elements; // return result
	}

	public class MatchNotFoundException extends Exception {

		private static final long serialVersionUID = 1L;

		public MatchNotFoundException() {
		}

		public MatchNotFoundException(String message) {
			super(message);
		}
	}
}
