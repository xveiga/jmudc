package mudc.core;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class TaskSystem {

	private List<Task> taskList = null;
	private ExecutorService executor = null;
	private int runningTaskNumber = 0;
	private int maxConcurrentThreads = 1;
	private boolean isSystemRunning = false;
	private Runnable onTaskListUpdate = null;

	public TaskSystem() {
		taskList = new ArrayList<Task>();
		executor = Executors.newCachedThreadPool();
	}

	public void shutdown() {
		executor.shutdown();
		isSystemRunning = !executor.isShutdown();
		if (isSystemRunning) {
			System.out.println("[Task System] Shutdown started. Waiting for remaining tasks to complete.");
		}
		else {
			taskList.clear();
			System.out.println("[Task System] Shutdown successful.");
		}
	}

	public void forceShutdown() {
		System.out.println("[Task System] Executing force shutdown on tasks.");
		executor.shutdownNow();
		isSystemRunning = false;
	}

	public void addTask(Task task) {
		taskList.add(task);
		if (onTaskListUpdate != null)
			onTaskListUpdate.run();
		System.out.println("[Task System] Task \"" + task.name + "\" has been added.");
	}

	public void startTask(Task task) throws NullPointerException, RejectedExecutionException, InterruptedException, ExecutionException {
		int index = taskList.indexOf(task);
		if (index != -1) {
			Task localTask = taskList.get(index);
			int group = localTask.group;
			// If task is of the same group as other that's running, mark it as stalled.
			Task sameGroupTask = isRunningTaskSameGroup(localTask);
			if (sameGroupTask != null) {
				localTask.stalled = true;
				if (onTaskListUpdate != null)
					onTaskListUpdate.run();
				System.out.println("[Task System] \"" + localTask.name + "\" has been stalled until other tasks on group \"" + sameGroupTask.group + "\" finish.");
			}
			else {
				if (runningTaskNumber < maxConcurrentThreads) {
					if (!localTask.run) {
						runningTaskNumber += 1;
						localTask.run = true;
						isSystemRunning = true;
						Runnable runnable = new Runnable() {
							@Override
							public void run() {
								if (localTask.onStartRunnable != null) {
									localTask.onStartRunnable.run();
								}
								if (localTask.actionRunnable != null) {
									localTask.actionRunnable.run();
								}
								task.run = false;
								runningTaskNumber -= 1;
								System.out.println("[Task System] \"" + localTask.name + "\" has finished.");
								removeTask(task);
								
								if (onTaskListUpdate != null)
									onTaskListUpdate.run();
								
								// When a task finishes, find the first stalled task belonging to the same
								// group and execute it.
								for (int i=0; i<taskList.size(); i++) {
									Task loopTask = taskList.get(i);
									if (loopTask.stalled && loopTask.group == group) {
										loopTask.stalled = false;
										try {
											startTask(loopTask);
										} catch (RejectedExecutionException | NullPointerException | InterruptedException | ExecutionException e) {
											loopTask.stalled = true;
											e.printStackTrace(); //TODO: Find a way to return exception
										}
										break;
									}
								}
							}
						};
						try {
							/*Future future = executor.submit(runnable);
							future.get();*/
							executor.execute(runnable);
							if (onTaskListUpdate != null)
								onTaskListUpdate.run();
							System.out.println("[Task System] \"" + localTask.name + "\" has started.");
						} catch (RejectedExecutionException e) {
							localTask.run = false;
							runningTaskNumber -= 1;
							throw new RejectedExecutionException(
									"Could not execute task \"" + task.name + "\":" + e.toString());
						} catch (NullPointerException e) {
							localTask.run = false;
							runningTaskNumber -= 1;
							throw e;
						}
						/*} catch (InterruptedException e) {
							localTask.run = false;
							runningTaskNumber -= 1;
							throw e;
						} catch (ExecutionException e) {
							localTask.run = false;
							runningTaskNumber -= 1;
							throw e;
						}*/
					} else {
						throw new RejectedExecutionException("\"" + localTask.name + "\" is already running.");
					}
				} else {
					localTask.run = false;
					throw new RejectedExecutionException("Maximum number of concurrent tasks reached"); //TODO: Auto-queuing system to start tasks once there are free threads
				}
			}
		} else {
			throw new NullPointerException("Task was not found in TaskSystem's list");
		}
	}
	
	private Task isRunningTaskSameGroup(Task task) {
		if (task.group != 0) {
			for (Task localTask : taskList) {
				if (!localTask.equals(task)) {
					if (localTask.run) {
						if (localTask.group == task.group) {
							return localTask;
						}
					}
				}
			}
		}
		return null;
	}

	public void stopTask(Task task) throws NullPointerException {
		stopTaskIndex(taskList.indexOf(task));
	}

	private void stopTaskIndex(int index) throws NullPointerException {
		if (index != -1) {
			Task localTask = taskList.get(index);
			if (localTask.stalled) {
				localTask.stalled = false;
				System.out.println("[Task System] \"" + localTask.name + "\" was stalled and has been stopped.");
				if (onTaskListUpdate != null)
					onTaskListUpdate.run();
			}
			else {
				if (localTask.run) {
					if (localTask.isStoppable) {
						localTask.run = false;
						runningTaskNumber -= 1;
					} else {
						throw new RejectedExecutionException(
								"\"" + localTask.name + "\" is running and can not be stopped.");
					}
				} else {
					throw new RejectedExecutionException("Task \"" + localTask.name + "\" is not running.");
				}
				System.out.println("[Task System] \"" + localTask.name + "\" has stopped.");
				if (onTaskListUpdate != null)
					onTaskListUpdate.run();
			}
		}
	}

	public void removeTask(Task task) {
		int index = taskList.indexOf(task);
		if (index != -1) {
			Task localTask = taskList.get(index);
			if (localTask.run) {
				if (localTask.isStoppable) {
					stopTaskIndex(index);
					taskList.remove(index);
					if (onTaskListUpdate != null)
						onTaskListUpdate.run();
				} else {
					throw new RejectedExecutionException(
							"\"" + localTask.name + "\" is running and can not be stopped.");
				}
			} else {
				taskList.remove(index);
			}
			System.out.println("[Task System] \"" + localTask.name + "\" has been removed.");
		}
	}

	public void setMaxTaskNumber(int threadNumber) {
		maxConcurrentThreads = threadNumber;
	}

	public int getMaxTaskNumber() {
		return maxConcurrentThreads;
	}

	public void setOnUpdateRunnable(Runnable runnable) {
		onTaskListUpdate = runnable;
	}

	public Runnable getOnUpdateRunnable() {
		return onTaskListUpdate;
	}

	public int getRunningTaskNumber() {
		return runningTaskNumber;
	}

	public int getCurrentTaskNumber() {
		return taskList.size();
	}

	public List<Task> getCurrentTasks() {
		return taskList;
	}

	public boolean isSystemActive() {
		return isSystemRunning;
	}

}
