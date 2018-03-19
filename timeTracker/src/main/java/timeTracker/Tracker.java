package timeTracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

public class Tracker extends SavedData {
	private static final String TIME_TRACKING_TXT = "/timeTracking.txt";
	private List<Task> tasks;
	private  static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss");
	
	public int updateJira(Task task) {
	    int returnStatus = 0;
		String jiraTaskValue = task.getJiraTask().substring(0, task.getJiraTask().contains("-")?task.getJiraTask().contains(" ")?task.getJiraTask().indexOf(" "):task.getJiraTask().length():0);
		if(null != jiraTaskValue && !"".equals(jiraTaskValue)) {
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("<username>", "<passowrd>");
			credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		    HttpClient client =  HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
		    HttpPost httpPost = new HttpPost("https://gcjiramain.unisysdevops.com/rest/api/2/issue/"+jiraTaskValue+"/worklog");
		    String json = "{\"started\":\""+task.getStart()+"\",\"timeSpent\":\""+task.getTimeSpent()+"m\"}";
	
		    try {
					StringEntity entity = new StringEntity(json);
				    httpPost.setEntity(entity);
				    httpPost.setHeader("Accept", "application/json");
				    httpPost.setHeader("Content-type", "application/json");	 
				    HttpResponse response = client.execute(httpPost);
				    returnStatus = response.getStatusLine().getStatusCode();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					client = null;
				}
		}
		return returnStatus;
	}
		
	public Tracker() {
		System.out.println(LocalDateTime.now().format(formatter)+" entering Tracker constructor");
//		System.out.println(LocalDateTime.now().format(formatter)+" calling Tracker.parseFile");
//		tasks = parseFile();
	}
	
	public static List<Task> getAllTracked() {
		System.out.println(LocalDateTime.now().format(formatter)+" entering Tracker.getAllTracked");
		System.out.println(LocalDateTime.now().format(formatter)+" calling Tracker.rebuildTracked, using timeTracking.txt");
		return rebuildTracked(parseFile());
	}
	
	public static List<Task> getAllTracked(List<Task> seed) {
		System.out.println(LocalDateTime.now().format(formatter)+" entering Tracker.getAllTracked");
		List<Task> combined = new ArrayList<Task>(seed);
		System.out.println(LocalDateTime.now().format(formatter)+" calling Tracker.parseFile, using timeTracker.txt");
		combined.addAll(parseFile());
		System.out.println(LocalDateTime.now().format(formatter)+" calling Tracker.rebuildTracked, using combined task list");
		return rebuildTracked(combined);
	}

	private static List<Task> parseFile() {
		System.out.println(LocalDateTime.now().format(formatter)+" entering Tracker.parseFile");
		System.out.println(LocalDateTime.now().format(formatter)+" calling SavedData.loadTasks, using timeTracker.txt");
		return loadTasks(TIME_TRACKING_TXT);
	}
	
	private static List<Task> rebuildTracked(List<Task> src) {
		//src.stream().map(Task::saveString).distinct().forEach(System.out::println);
		System.out.println(LocalDateTime.now().format(formatter)+" entering Tracker.rebuildTracked");
		System.out.println(LocalDateTime.now().format(formatter)+" creating new task list from streamed file");
		List<Task> tasks= src.stream().map(Task::saveString).distinct().map(s -> s.split(DELIM)).map(Task::new).collect(Collectors.toList());
		System.out.println(LocalDateTime.now().format(formatter)+" exiting Tracker.rebuildTracked");
		return tasks;
	}	

	public List<Task> getAllTasks() {
		return tasks;
	}
	
	public List<Task> getSavedTasks() {
		System.out.println(LocalDateTime.now().format(formatter)+" entering Tracker.getSavedTasks");
		System.out.println(LocalDateTime.now().format(formatter)+" calling Tracker.parseFile, using timeTracker.txt");		
		return parseFile();
	}

	public void stopTask(Task task) {
		if (task != null) {	
			writeToFile(task, TIME_TRACKING_TXT, task::toString);
			//int updateStatus = updateJira(task);
			//System.out.println(LocalDateTime.now().format(formatter)+" jira update status: "+updateStatus);
			tasks = getSavedTasks();
		}
	}
}
