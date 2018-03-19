package service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import timeTracker.ActiveTask;
import timeTracker.SavedData;
import timeTracker.Task;
import timeTracker.Tracker;

public class TrackerService extends SavedData {
	private Tracker tracker;
	private ActiveTask active;
	private  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss");
	
	public TrackerService() {
		active = new ActiveTask();
		tracker = new Tracker();
	}
	
	public List<Task> getTrackedTasks(List<Task>taskList) {
		
		System.out.println(LocalDateTime.now().format(formatter)+" entering getTrackedTasks");
		//List<Task> result = new ArrayList<Task>();
		//result.addAll(Tracker.getAllTracked(taskList));
		
		Task activeTask = active.getActive();
		
		if(null == activeTask) {
			System.out.println(LocalDateTime.now().format(formatter)+" active task file was empty. trying again...");
			activeTask = active.getActive();
		}
		
		if (activeTask != null) {
			System.out.println(LocalDateTime.now().format(formatter)+" found active task in the tmp file. Looking for a match in the taskList");
			Collections.sort(taskList);
			//using an index counter, rather than relying on the taskId, in case the file isn't sorted ascending. 
			//this ensures that the active task is placed correctly back in the list regardless of sort 
			int i = -1; 
			for(Task t : taskList) {//using a for-loop instead of stream. sometimes it's just better that way
				i++;
				if(activeTask.getId() == t.getId()) {
					System.out.println(LocalDateTime.now().format(formatter)+" setting active task at index: "+i);
					taskList.set(i, activeTask);
				}
			}
		}
		System.out.println(LocalDateTime.now().format(formatter)+" exiting getTrackedTasks");
		return taskList;
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

	public NavigableMap<LocalDateTime, Map<String, Double>> getTaskHistoryByProject() {
		TreeMap<LocalDateTime, Map<String, Double>> sortedTasksByDay = new TreeMap<LocalDateTime, Map<String, Double>>();
		List<Task> taskSummaryList = new ArrayList<Task>();
		taskSummaryList = tracker.getSavedTasks();
		if (active.getActive() != null) {
			taskSummaryList.add(active.getActive());
		}		
		Map<LocalDateTime, Map<String, Double>> tasksByDay = taskSummaryList.stream()
				.sorted()
				.filter(t ->t.getStartDay().isAfter(LocalDateTime.now().minusDays(32L)))
				.collect(groupingBy(Task::getStartDay,
						groupingBy(Task::getProject,
								reducing(0.0, Task::getTimeSpentAsDouble, Double::sum))));
		sortedTasksByDay.putAll(tasksByDay);
		return sortedTasksByDay.descendingMap();
	}

	public NavigableMap<LocalDateTime, Map<String, Double>> getTaskHistoryByJira() {
		TreeMap<LocalDateTime, Map<String, Double>> sortedTasksByDay = new TreeMap<LocalDateTime, Map<String, Double>>();
		List<Task> taskSummaryList = new ArrayList<Task>();
		taskSummaryList = tracker.getSavedTasks();
		if (active.getActive() != null) {
			taskSummaryList.add(active.getActive());
		}
		Map<LocalDateTime, Map<String, Double>> tasksByDay = taskSummaryList.stream()
				.sorted()
				.filter(t ->t.getStartDay().isAfter(LocalDateTime.now().minusDays(32L)))
				.collect(groupingBy(Task::getStartDay,
						groupingBy(Task::getProjectAndTask,
								reducing(0.0, Task::getTimeSpentAsDouble, Double::sum))));
		sortedTasksByDay.putAll(tasksByDay);
		return sortedTasksByDay.descendingMap();
	}
	
}
