package mudc.core;

import java.util.ArrayList;
import java.util.List;

public class ProgressUpdater {

	private long finalSize = 0;
	private float progress = 0.0f;
	private List<ProgressBinding> bindings = new ArrayList<>();

	public ProgressUpdater(long filesize) {
		finalSize = filesize;
	}

	public void setProgress(long currentValue) {
		progress = (float) currentValue / (float) finalSize;
		for (ProgressBinding b : bindings) {
			if (b != null) {
				b.setProgress(progress);
			}
			else {
				//Remove binding
			}
		}
	}
	
	public float getProgress() {
		return progress;
	}
	
	public void registerBinding(ProgressBinding binding) {
		if (bindings.indexOf(binding) == -1) {
			bindings.add(binding);
		}
	}
	
	public void unregisterBinding(ProgressBinding binding) {
		bindings.remove(binding);
	}
	
}
