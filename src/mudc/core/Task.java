package mudc.core;

public class Task {
	public String name;
	public String description;
	public int group = 0; // Tasks on the same group are not run at the same time.
	public boolean run = false;
	public boolean stalled = false;
	public boolean isStoppable = false;
	public Runnable onStartRunnable = null;
	public Runnable onStopRunnable = null;
	public ProgressUpdater progress = null;
	public Runnable actionRunnable = null;

	@Override
	public String toString() {
		return name + ": " + description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task task = (Task) obj; 
			return (task.hashCode() == this.hashCode());
		}
		return false;
	}
}