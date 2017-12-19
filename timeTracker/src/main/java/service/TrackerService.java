package service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import timeTracker.ActiveTask;
import timeTracker.SavedData;
import timeTracker.Task;
import timeTracker.Tracker;

public class TrackerService extends SavedData {
	private Tracker tracker;
	private ActiveTask active;
	
	public TrackerService() {
		active = new ActiveTask();
		tracker = new Tracker();
	}
	
	public List<Task> getTrackedTasks(List<Task>taskList) {
		List<Task> result = new ArrayList<Task>();
		result.addAll(Tracker.getAllTracked(taskList));
		Task activeTask = active.getActive();
		if (activeTask != null) {
			result.set(activeTask.getId()-1, activeTask);
		}
		return result;
	}

	public boolean startTask(Task task) {
		if (!task.isStarted()) {
			task.start();
			Task stopped = active.startTask(task);
			if (stopped != null && stopped.isStarted()) {//stop the task, but leave the tmp file alone
				stopped.stop();
				tracker.stopTask(stopped);
			}
		}
		return task.isStarted();
	}

	//called by the stop button. In that case, we need to clear the tmp file
	public boolean stopTask(Task task) {
		if (task.isStarted()) {
			task.stop();
			active.stopTasks();//clear out the tmp file
			tracker.stopTask(task);
		}
		return task.isStarted();
	}

	public TreeMap<LocalDateTime, Map<String, Double>> getTaskHistoryByProject() {
		TreeMap<LocalDateTime, Map<String, Double>> sortedTasksByDay = new TreeMap<LocalDateTime, Map<String, Double>>();
		List<Task> taskSummaryList = new ArrayList<Task>();
		taskSummaryList = tracker.getSavedTasks();
		if (active.getActive() != null) {
			taskSummaryList.add(active.getActive());
		}		
		Map<LocalDateTime, Map<String, Double>> tasksByDay = taskSummaryList.stream()
				.sorted()
				.collect(groupingBy(Task::getStartDay,
						groupingBy(Task::getProject,
								reducing(0.0, Task::getTimeSpentAsDouble, Double::sum))));
		sortedTasksByDay.putAll(tasksByDay);
		return sortedTasksByDay;
	}

	public TreeMap<LocalDateTime, Map<String, Double>> getTaskHistoryByJira() {
		TreeMap<LocalDateTime, Map<String, Double>> sortedTasksByDay = new TreeMap<LocalDateTime, Map<String, Double>>();
		List<Task> taskSummaryList = new ArrayList<Task>();
		taskSummaryList = tracker.getSavedTasks();
		if (active.getActive() != null) {
			taskSummaryList.add(active.getActive());
		}
		Map<LocalDateTime, Map<String, Double>> tasksByDay = taskSummaryList.stream()
				.sorted()
				.collect(groupingBy(Task::getStartDay,
						groupingBy(Task::getProjectAndTask,
								reducing(0.0, Task::getTimeSpentAsDouble, Double::sum))));
		sortedTasksByDay.putAll(tasksByDay);
		return sortedTasksByDay;
	}
	
}
