package mudc.core;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Locales {

	public static final String availableLocales[][] = { { "en", "English" }, { "es", "Español" }, { "gl", "Galego" } };

	private static final String bundlePath = "mudc.locales.lang";

	private ResourceBundle labels = null;

	public void setLocale(String language) throws MissingResourceException {
		ResourceBundle.clearCache();
		if (language.equals(new Locale("en").getLanguage())) {
			labels = ResourceBundle.getBundle(bundlePath, new Locale(language));
		} else if (language.equals(new Locale("en").getLanguage())) {
			labels = ResourceBundle.getBundle(bundlePath, new Locale(language));
		} else if (language.equals(new Locale("en").getLanguage())) {
			labels = ResourceBundle.getBundle(bundlePath, new Locale(language));
		} else {
			labels = ResourceBundle.getBundle(bundlePath, new Locale(language));
		}
	}

	public void setDefaultLocale() throws MissingResourceException {
		Locale defaultLocale = Locale.getDefault();
		setLocale(defaultLocale.getLanguage());
	}

	public Locale getLocale() {
		return labels.getLocale();
	}
	
	public String[][] getAvailableLocales() {
		return availableLocales;
	}

	public String getString(String label) throws NullPointerException {
		return labels.getString(label);
	}

}
