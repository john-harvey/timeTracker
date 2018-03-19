/* Copyright © 2015 Oracle and/or its affiliates. All rights reserved. */
package timeTracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import service.TrackerService;

public class TaskList extends SavedData {
	private static final String TASKS_TXT = "/tasks.txt";
	private List<Task> taskList = new ArrayList<Task>();
	private TrackerService service = null;
	private static TaskList instance = null;
	private  static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss");
	
	private TaskList() {
		service = new TrackerService();
		System.out.println(LocalDateTime.now().format(formatter)+" taskList constructor (no longer calling loadTasks)");
//		loadTasks();
	}

	public synchronized static TaskList getInstance() {
		if (instance == null) {
			instance = new TaskList();
		}
		return instance;
	}

	private static List<Task> parseFile() {
		System.out.println(LocalDateTime.now().format(formatter)+" entering TaskList.parseFile");
		System.out.println(LocalDateTime.now().format(formatter)+" calling SavedData.loadTasks");
		return loadTasks(TASKS_TXT);
	}
	
	private void addDefaultTask() {
		System.out.println(LocalDateTime.now().format(formatter)+" entering TaskList.addDefaultTask");
		taskList.add(new Task(taskList.size() + 1, "new task:project", "new task:Jira/RFA"));
		System.out.println(LocalDateTime.now().format(formatter)+" exiting addDefaultTask");
	}

	public void saveTask(Task t) {
		writeToFile(t, TASKS_TXT, t::saveString);
	}
	
	public void saveAllTasks(List<Task> tasks) {
		System.out.println(LocalDateTime.now().format(formatter)+" entering TaskList.saveAllTasks");	
		truncateFile(TASKS_TXT);
		System.out.println(LocalDateTime.now().format(formatter)+" sorting task list before saving");
		Collections.sort(tasks);
		System.out.println(LocalDateTime.now().format(formatter)+" streaming task list to file");
		tasks.stream().filter(Task::isRegularTask).forEach(t -> writeToFile(t, TASKS_TXT, t::saveString));
		System.out.println(LocalDateTime.now().format(formatter)+" exiting TaskList.saveAllTasks");
	}	

	public List<Task> loadTasks() {
		System.out.println(LocalDateTime.now().format(formatter)+" entering TaskList.loadTasks");
		taskList.clear();
		
		taskList.addAll(service.getTrackedTasks(parseFile()));
		addDefaultTask();
		System.out.println(LocalDateTime.now().format(formatter)+" exiting TaskList.loadTasks");
		return taskList;
	}
}
