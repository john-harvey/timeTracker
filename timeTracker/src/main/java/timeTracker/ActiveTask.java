package timeTracker;

import java.util.List;

public class ActiveTask extends SavedData {
	private static final String ACTIVE_TASK_FILE = "/activeTask.tmp";
	private Task currentTask = null;
	
	public ActiveTask() {
		loadActive();
	}
	
	public Task startTask(Task starter) {
		Task stopped = currentTask;
		currentTask = starter;
		new Thread(() -> writeToFile(currentTask, ACTIVE_TASK_FILE, currentTask::toString, false)).start();
		return stopped;
	}
	
	public void stopTasks() {
		new Thread(() -> writeToFile(currentTask, ACTIVE_TASK_FILE, ""::toString, false)).start();
		currentTask=null;//wipe out the current task instance. 
	}
	
	private void loadActive() {
		if (doesFileExist(ACTIVE_TASK_FILE)) {
			List<Task> activeTasks = loadTasks(ACTIVE_TASK_FILE);
			if (activeTasks.size() > 0) {
				currentTask = activeTasks.get(0);
				currentTask.setStartButtonText("Started");
				currentTask.setStarted(true);
			}else {
				currentTask = null;
			}
		}
	}

	public Task getActive() {
		loadActive();
		return currentTask;
	}
}
