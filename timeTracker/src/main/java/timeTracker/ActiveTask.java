package timeTracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActiveTask extends SavedData {
	private static final String ACTIVE_TASK_FILE = "/activeTask.tmp";
	private Task currentTask = null;
	private  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss");
	
	public ActiveTask() {
		System.out.println(LocalDateTime.now().format(formatter)+" entering ActiveTask constructor");
		//System.out.println(LocalDateTime.now().format(formatter)+" calling loadActive");
		//loadActive();
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
		System.out.println(LocalDateTime.now().format(formatter)+" entering ActiveTask.loadActive");
		if (doesFileExist(ACTIVE_TASK_FILE)) {
			System.out.println(LocalDateTime.now().format(formatter)+" calling SavedData.loadTasks");
			List<Task> activeTasks = loadTasks(ACTIVE_TASK_FILE);
			if (activeTasks.size() > 0) {
				currentTask = activeTasks.get(0);
				currentTask.setStartButtonText("Started");
				currentTask.setStarted(true);
			}else {
				currentTask = null;
			}
		}
		System.out.println(LocalDateTime.now().format(formatter)+" exiting ActiveTask.loadActive");
	}

	public Task getActive() {
		loadActive();
		return currentTask;
	}
}
