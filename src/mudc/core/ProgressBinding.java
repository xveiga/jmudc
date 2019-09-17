package mudc.core;

public class ProgressBinding {

	private Runnable run = null;
	
	public ProgressBinding(Runnable onProgressUpdate) {
		run = onProgressUpdate;
	}
	
	public void setProgress(float progress) {
		if (run != null) {
			run.run();
		}
	}
	
}
