package servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.TrackerService;
import timeTracker.Task;

@WebServlet(name = "TaskSummaryServlet", urlPatterns = { "/summary" })

public class TaskSummaryServlet extends HttpServlet {

	private static final long serialVersionUID = -6663182174738777554L;
	private TrackerService trackerService = new TrackerService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		forwardListTasks(req, resp, trackerService.getTaskHistoryByProject(), trackerService.getTaskHistoryByJira());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	private void forwardListTasks(HttpServletRequest req, HttpServletResponse resp,
			Map<LocalDateTime, Map<String, Double>> tasksByProject, Map<LocalDateTime, Map<String, Double>> tasksByJira)
			throws ServletException, IOException {
		String nextJSP = "/task-summary.jsp";
		RequestDispatcher dispatcher = req.getRequestDispatcher(nextJSP);
		req.setAttribute("tasksByProject", tasksByProject);
		req.setAttribute("tasksByJira", tasksByJira);
		dispatcher.forward(req, resp);
	}

}
