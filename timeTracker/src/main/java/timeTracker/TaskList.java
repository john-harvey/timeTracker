/* Copyright © 2015 Oracle and/or its affiliates. All rights reserved. */
package timeTracker;

import java.util.ArrayList;
import java.util.List;

import service.TrackerService;

public class TaskList extends SavedData {
	private static final String TASKS_TXT = "/tasks.txt";
	private List<Task> taskList = new ArrayList<Task>();
	private TrackerService service = null;
	private static TaskList instance = null;

	private TaskList() {
		service = new TrackerService();
		loadTasks();
	}

	public synchronized static TaskList getInstance() {
		if (instance == null) {
			instance = new TaskList();
		}
		return instance;
	}

	private static List<Task> parseFile() {
		return loadTasks(TASKS_TXT);
	}
	
	private void addDefaultTask() {
		taskList.add(new Task(taskList.size() + 1, "new task:project", "new task:Jira/RFA"));
	}

	public void saveTask(Task t) {
		writeToFile(t, TASKS_TXT, t::saveString);
	}

	public List<Task> loadTasks() {
		taskList.clear();
		taskList.addAll(service.getTrackedTasks(parseFile()));
		addDefaultTask();
		return taskList;
	}
}
