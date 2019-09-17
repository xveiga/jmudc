package mudc.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import mudc.core.Locales;
import mudc.core.casudc.MoodleToken;
import mudc.core.casudc.MoodleTokenData;

public class PanelAccounts extends JPanel {

	private static final long serialVersionUID = -4920180746448310822L;
	
	private static final int animationSteps = 48; // TODO: Move to settings
	private static final int animationStepDelay = 5;
	private static final float animationAcceleration = 1f;
	
	private GUI gui = null;
	private Window window = null;
	private JPanel[] panels = null;
	private AssistantSlideshowAnimator slideshow = null;

	public PanelAccounts(GUI g, Window w, Locales str) {
		gui = g;
		window = w;
		setLayout(null);
		panels = new JPanel[4];
		panels[0] = new PanelAccountsLogin(this, g, w, str);
		panels[1] = new PanelLoading("Logging in...");
		panels[2] = new PanelAccountsView(this, g, w, str);
		panels[3] = new PanelLoading("Retrieving server information...");;
		slideshow = new AssistantSlideshowAnimator(panels);
		add(slideshow);
		gui.registerAccountsPanel(this);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				slideshow.setBounds(0, 0, getWidth(), getHeight());
				slideshow.onResize(getWidth(), getHeight());
			}
		});
		PanelAccountsLogin p = (PanelAccountsLogin) panels[0];
		w.setOnVisibleRunnable(new Runnable() {
			@Override
			public void run() {
				p.requestFocus();
			}
		});
	}
	
	public void getTokens() {
		PanelAccountsLogin p = (PanelAccountsLogin) panels[0];
		PanelLoading a = (PanelLoading) panels[1];
		gui.getTokenForUser(p.getUsername(), p.getPassword(), false);
		nextSlide();
		a.setAnimationEnabled(true);
		System.out.println("[PanelAccounts] Logging in to UDC's CAS, retrieving Moodle security keys.");
		//TODO: If window is closed, stop getToken operation.
	}
	
	public void onTokenReturn(MoodleTokenData tokens) {
		PanelAccountsLogin l = (PanelAccountsLogin) panels[0];
		PanelLoading a = (PanelLoading) panels[1];
		PanelAccountsView v = (PanelAccountsView) panels[2];
		if (tokens != null) {
			System.out.println("[PanelAccounts] " + tokens.getTokens().size() + " Moodle security keys found.");
			v.setTokenData(tokens);
			v.requestFocus();
			nextSlide();
		}
		else {
			String error = gui.getAccountsError();
			l.setError(error);
			prevSlide();
		}
		a.setAnimationEnabled(false);
	}
	
	public void setToken(MoodleToken token) {
		System.out.println("[PanelAccounts] Access token is now \"" + token.getServiceName() + "\"");
		PanelLoading a = (PanelLoading) panels[3];
		a.setAnimationEnabled(true);
		nextSlide();
		gui.setToken(token);
		gui.updateMoodleInfo();
	}
	
	public void onMoodleInfoRefresh() {
		if (gui.getInfo() == null) {
			prevSlide();
			gui.setToken(null);
		}
		else {
			window.dispose();
		}
		PanelLoading a = (PanelLoading) panels[3];
		a.setAnimationEnabled(false);
	}
	
	public void nextSlide() {
		slideshow.slideRight(animationSteps, animationStepDelay, animationAcceleration);
	}
	
	public void prevSlide() {
		slideshow.slideLeft(animationSteps, animationStepDelay, animationAcceleration);
	}

}
