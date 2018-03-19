package servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.TrackerService;
import timeTracker.Task;
import timeTracker.TaskList;

@WebServlet(name = "TaskServlet", urlPatterns = { "/tasks" })

public class TaskServlet extends HttpServlet {
	private static final long serialVersionUID = -6663182174738777553L;
	private TrackerService trackerService = new TrackerService();
	private TaskList taskListInstance = TaskList.getInstance();
	private List<Task> tasks =taskListInstance.loadTasks();
	private  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println(LocalDateTime.now().format(formatter)+" entering doGet");
		if (null == tasks || tasks.isEmpty()) {
			System.out.println(LocalDateTime.now().format(formatter)+" taskList is empty - calling TaskList.loadTasks");
			tasks = taskListInstance.loadTasks();
		}
		forwardListTasks(req, resp, tasks);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println(LocalDateTime.now().format(formatter)+" entering doPost");
		String taskStart = req.getParameter("start");
		String taskStop = req.getParameter("stop");
		String reloadTasks = req.getParameter("reload");
		String addTask = req.getParameter("add");
		String setIdle = req.getParameter("setidle");
		String setCurrent = req.getParameter("setcurrent");
		
		Task thisTask = new Task();
		if (null == tasks || tasks.isEmpty()) {
			System.out.println(LocalDateTime.now().format(formatter)+" taskList is empty - calling TaskList.loadTasks");
			tasks = taskListInstance.loadTasks();
		}
		if (taskStart != null) {
			thisTask = tasks.stream().filter(t -> t.isUrlId(taskStart)).findFirst().orElse(null);
			if (thisTask != null) {
				System.out.println(LocalDateTime.now().format(formatter)+" starting Task " + thisTask.getId());
				trackerService.startTask(thisTask);
			}
		}
		if (taskStop != null) {
			thisTask = tasks.stream().filter(t -> t.isUrlId(taskStop)).findFirst().orElse(null);
			if (thisTask != null) {
				System.out.println(LocalDateTime.now().format(formatter)+" stopping Task " + thisTask.getId());
				trackerService.stopTask(thisTask);
			}
		}
		if (setIdle != null) {
			thisTask = tasks.stream().filter(t -> t.isUrlId(setIdle)).findFirst().orElse(null);
			if (thisTask != null) {
				System.out.println(LocalDateTime.now().format(formatter)+" setting Task " + thisTask.getId()+" to idle");
				thisTask.setCurrent(false);
				taskListInstance.saveAllTasks(tasks);
			}
		}
		if (setCurrent != null) {
			thisTask = tasks.stream().filter(t -> t.isUrlId(setCurrent)).findFirst().orElse(null);
			if (thisTask != null) {
				System.out.println(LocalDateTime.now().format(formatter)+" setting Task " + thisTask.getId()+" to current");
				thisTask.setCurrent(true);
				taskListInstance.saveAllTasks(tasks);
			}
		}	

		if ("true".equals(addTask)) {
			String taskAdd = req.getParameter("newTaskId");
			String newTaskProject = req.getParameter("newTaskProject");
			String newTaskJira = req.getParameter("newTaskJira");
			int newTaskId = (null != taskAdd) ? Integer.parseInt(taskAdd) : -1;
			thisTask = new Task(newTaskId, newTaskProject, newTaskJira);
			if (!newTaskProject.equalsIgnoreCase("new task:project")
					&& !newTaskJira.equalsIgnoreCase("new task:Jira/RFA")) {
				tasks.add(thisTask);			
				taskListInstance.saveAllTasks(tasks);
			}
		}
		System.out.println(LocalDateTime.now().format(formatter)+" reloading tasks");
		tasks = taskListInstance.loadTasks();
		forwardListTasks(req, resp, tasks);
	}

	private void forwardListTasks(HttpServletRequest req, HttpServletResponse resp, List<Task> taskList)
			throws ServletException, IOException {
		System.out.println(LocalDateTime.now().format(formatter)+" entering forwardListTasks");
		String nextJSP = "/list-tasks.jsp";
		System.out.println(LocalDateTime.now().format(formatter)+" re-sorting the taskList for the jsp");
		Collections.sort(taskList);
		RequestDispatcher dispatcher = req.getRequestDispatcher(nextJSP);
		req.setAttribute("taskList", taskList);
		System.out.println(LocalDateTime.now().format(formatter)+" forwarding to the jsp");
		dispatcher.forward(req, resp);
	}
}
