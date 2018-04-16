package servlet;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
	private  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String submittedDate = req.getParameter("submittedDate");
		LocalDate startDate = (LocalDate) req.getAttribute("startDate");
		
		if(null== startDate || "".equals(startDate)) {
			if(null != submittedDate && !"".equals(submittedDate)) {
				startDate = LocalDate.parse(submittedDate);
			}else {
				startDate = LocalDate.now();
			}
		}
		req.setAttribute("startDate", startDate);
		req.setAttribute("weekDates", getWeekDates(startDate));
		LocalDateTime filterDate = startDate.atStartOfDay();
		forwardListTasks(req, resp, trackerService.getTaskHistoryByProject(filterDate), trackerService.getTaskHistoryByJira(filterDate), trackerService.getDailyTotalsByJira(filterDate));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	private void forwardListTasks(HttpServletRequest req, HttpServletResponse resp,
			Map<LocalDateTime, Map<String, Double>> tasksByProject, Map<LocalDateTime, Map<String, Double>> tasksByJira, Map<String, Map<LocalDateTime, Double>> dailyTotalsByJira)
			throws ServletException, IOException {
		String nextJSP = "/task-summary.jsp";
		RequestDispatcher dispatcher = req.getRequestDispatcher(nextJSP);
		req.setAttribute("tasksByProject", tasksByProject);
		req.setAttribute("tasksByJira", tasksByJira);
		req.setAttribute("dailyTotalsByJira", dailyTotalsByJira);	
		dispatcher.forward(req, resp);
	}
	
	private List<String> getWeekDates(LocalDate startDate){
		List<String>weekDates = new ArrayList<String>();
		boolean test = DayOfWeek.SUNDAY.equals(startDate.getDayOfWeek());
		LocalDate weekDay = (DayOfWeek.SUNDAY.equals(startDate.getDayOfWeek()))?startDate:startDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
		for(int i=0; i<7;i++) {
			weekDates.add(weekDay.plusDays(i).toString());
		}
		return weekDates;
	}

}
