package servlet;

import java.io.IOException;
import java.util.ArrayList;
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
	private List<Task> tasks = new ArrayList<Task>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		tasks = TaskList.getInstance().loadTasks();
		forwardListTasks(req, resp, tasks);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String taskStart = req.getParameter("start");
		String taskStop = req.getParameter("stop");
		String reloadTasks = req.getParameter("reload");
		String addTask = req.getParameter("add");

		Task thisTask = new Task();
		if (null == tasks || tasks.isEmpty()) {
			tasks = TaskList.getInstance().loadTasks();
		}
		if (taskStart != null) {
			thisTask = tasks.stream().filter(t -> t.isUrlId(taskStart)).findFirst().orElse(null);
			if (thisTask != null) {
				System.out.println("starting Task " + thisTask.getId());
				trackerService.startTask(thisTask);
				//tasks = TaskList.getInstance().loadTasks();
			}
		}
		if (taskStop != null) {
			thisTask = tasks.stream().filter(t -> t.isUrlId(taskStop)).findFirst().orElse(null);
			if (thisTask != null) {
				System.out.println("stopping Task " + thisTask.getId());
				trackerService.stopTask(thisTask);
				//tasks = TaskList.getInstance().loadTasks();
			}
		}
//		if ("true".equals(reloadTasks)) {
//			tasks = TaskList.getInstance().loadTasks();
//		}
		if ("true".equals(addTask)) {
			String taskAdd = req.getParameter("newTaskId");
			String newTaskProject = req.getParameter("newTaskProject");
			String newTaskJira = req.getParameter("newTaskJira");
			int newTaskId = (null != taskAdd) ? Integer.parseInt(taskAdd) : -1;
			thisTask = new Task(newTaskId, newTaskProject, newTaskJira);
			if (!newTaskProject.equalsIgnoreCase("new task:project")
					&& !newTaskJira.equalsIgnoreCase("new task:Jira/RFA")) {
				TaskList.getInstance().saveTask(thisTask);
				//tasks = TaskList.getInstance().loadTasks();
			}
		}
		tasks = TaskList.getInstance().loadTasks();
		forwardListTasks(req, resp, tasks);
	}

	private void forwardListTasks(HttpServletRequest req, HttpServletResponse resp, List<Task> taskList)
			throws ServletException, IOException {
		String nextJSP = "/list-tasks.jsp";
		RequestDispatcher dispatcher = req.getRequestDispatcher(nextJSP);
		req.setAttribute("taskList", taskList);
		dispatcher.forward(req, resp);
	}
}
