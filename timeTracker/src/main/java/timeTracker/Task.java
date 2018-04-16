package timeTracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task implements Comparable<Task> {
	private static final String NULL = "null";
	private int id;
	private String project;
	private String jiraTask;
	private boolean started;
	private boolean current=true;
	private LocalDateTime start;
	private LocalDateTime end;
	private String startButtonText = "Start";
	private String JIRA_URL_BASE = "http://bjcjira/browse/";
	private String JIRA_DASHBOARD_URL = "http://bjcjira/secure/Dashboard.jspa";
	
	public String getStartButtonText() {
		return startButtonText;
	}

	public void setStartButtonText(String startButtonText) {
		this.startButtonText = startButtonText;
	}

	public Task(int id, String project, String jiraTask) {
		this.project = project;
		this.jiraTask = jiraTask;
		this.started = false;
		this.id = id;
	}

	public Task(String[] fields) {
		this.id = Integer.parseInt(fields[0]);
		this.project = fields[1];
		this.jiraTask = fields[2];
		this.current = ("true".equalsIgnoreCase(fields[3]));
		if (fields.length > 4) {
			// assumes these come from reading in timeTracking.txt or activeTask.tmp
			this.start = LocalDateTime.parse(fields[4]);
			if (!fields[5].equals(NULL)) {
				this.end = LocalDateTime.parse(fields[5]);
			}
		}
	}

	public Task() {
	}
	
	public Task getInstance() {
		return this;
	}

 	public void start() {
		this.setEnd(null);
		this.setStart(LocalDateTime.now());
		this.setStarted(true);
		this.setStartButtonText("Started");
	}

	public void stop() {
		this.setEnd(LocalDateTime.now());
		this.setStarted(false);
		this.setStartButtonText("Start");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getJiraTask() {
		return jiraTask;
	}

	public String getJiraLink() {
		String linkValue = "";
		Pattern p = Pattern.compile("^([A-Za-z0-9]+(-[0-9]+ ))");
		Matcher m = p.matcher(jiraTask);
		String jiraTaskValue = m.find()?m.group(1):"";
		System.out.println("jiraTaskValue from regex: "+jiraTaskValue);
		if(null != jiraTaskValue && !"".equals(jiraTaskValue)) {
			linkValue=JIRA_URL_BASE+jiraTaskValue;
//			boolean isInvalidTask = false;
//		     HttpURLConnection myURLConnection;
//				try {
//					URL myURL = new URL(linkValue);
//					myURLConnection = (HttpURLConnection) myURL.openConnection();
//
//				     try (BufferedReader reader = new BufferedReader(
//				           new InputStreamReader(myURLConnection.getInputStream()))) {
//				    	 	isInvalidTask = reader.lines().anyMatch((s) -> s.contains("Project Does Not Exist"));
//				     } catch (IOException e) {
//						System.out.println(e.getMessage());
//						isInvalidTask = true;
//					}
//				} catch (IOException e1) {
//					System.out.println(e1.getMessage());
//					isInvalidTask = true;
//				}			
			
//				linkValue = !isInvalidTask?linkValue:JIRA_DASHBOARD_URL;	
		}else {
			linkValue = JIRA_DASHBOARD_URL;
		}
		return linkValue;
	}
	
	public String getProjectTaskAndJiraLink() {
		return project + "~" + jiraTask+"^"+getJiraLink();
	}
	
	public String getProjectAndTask() {
		return project + "~" + jiraTask;
	}

	public void setJiraTask(String jiraTask) {
		this.jiraTask = jiraTask;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean getCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public LocalDateTime getStart() {
		return start;
	}
	
	public String getFormattedStart() {
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss");
		 return (null != start)?start.format(formatter):"";
	}
	
	public String getFormattedEnd() {
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss");
		 return (null != end)?end.format(formatter):"";
	}

	public LocalDateTime getStartDay() {
		return start.truncatedTo(java.time.temporal.ChronoUnit.DAYS);
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public boolean isRegularTask() {
		return !(this.getProject().equalsIgnoreCase("new task:project"));
	}
	
	public Long getTimeSpent() {
		Long time = 0L;
		if (null != getStart() && null != getEnd()) {
			time = java.time.Duration.between(getStart(), getEnd()).toMinutes();
		} else if (null != getStart() && null == getEnd()) {
			time = java.time.Duration.between(getStart(), LocalDateTime.now()).toMinutes();
		}
		return time;
	}

	public Double getTimeSpentAsDouble() {
		// return (null !=timeSpent)?(timeSpent.doubleValue()/1000/60/60):0;
		Double timeSpent = 0.0;
		if (null != getStart() && null != getEnd()) {
			timeSpent = new Double(java.time.Duration.between(getStart(), getEnd()).toMillis()) / 1000 / 60 / 60;
		} else if (null != getStart() && null == getEnd()) {
			timeSpent = new Double(java.time.Duration.between(getStart(), LocalDateTime.now()).toMillis()) / 1000 / 60
					/ 60;
		}
		timeSpent = (double) (Math.round(timeSpent*4)/4f);
		return timeSpent;
	}

	@Override
	public String toString() {
		// formatted for output to a csv file
		return id + "," + project + "," + jiraTask + "," + current + "," + start + "," + end;
	}
	
	public String getUrlId() {
		return Base64.getEncoder().encodeToString(toString().getBytes());
	}
	
	public boolean isUrlId(String urlId) {
		return getUrlId().equals(urlId);
	}

	public String saveString() {
		// formatted for tasks file
		return id + "," + project + "," + jiraTask + "," + current;
	}

	@Override
	public int compareTo(Task t1) {
		int result = 0;
		result = this.id>t1.id?1:0;
		return result;
	}
}
