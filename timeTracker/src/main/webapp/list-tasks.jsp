<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<html>
<head>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container" style="width: 90%; margin-left: 50px">
		<h2>Task Tracking</h2>
		<div style="float: left">
			<form action="/tasks" method="post" id="listTaskForm" role="form">
				<button type="submit" name="reload" value="true"
					class="btn btn-info">
					<span class="glyphicon glyphicon-refresh"></span> Reload Tasks
				</button>
			</form>
		</div>
		<div style="float: left">&nbsp;</div>
		<div>
			<form action="/summary" method="get" id="listSummaryForm" role="form">
				<button type="submit" class="btn btn-info">
					<span class="glyphicon glyphicon-tasks"></span> Task Summary
				</button>
				<br></br>
			</form>
		</div>
		<form action="/tasks" method="post" id="taskForm" role="form">
			<input type="hidden" name="taskList" value="${taskList}" />
			<c:choose>
				<c:when test="${not empty taskList}">
					<div class="panel-group">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" href="#collapseCurrent">Active
										Tasks</a>
								</h4>
							</div>
							<div id="collapseCurrent" class=" table-responsive panel-collapse collapse in"
								style="margin-left: 50px">
								<table class="table table-striped">
									<thead>
										<tr style="padding: 0.2rem;">
											<th>#</th>
											<th style="width: 20%;">Project</th>
											<th style="width: 65%;">Jira Task</th>
											<th  style="width: 15%;" colspan="3">Actions</th>
										</tr>
									</thead>
									<c:forEach var="task" items="${taskList}">
										<c:if test="${task.project eq 'new task:project'}">
											<tr style="padding: 0.2rem;">
												<td><input type="hidden" name="newTaskId"
													value="${task.id}">${task.id}</td>
												<td><input type="text" name="newTaskProject"
													value="${task.project}"></td>
												<td><input type="text" name="newTaskJira"
													value="${task.jiraTask}"></td>
												<td>
													<button type="submit" name="add" value="true"
														class="btn btn-primary"
														style="padding-top: 0rem; padding-bottom: 0rem">
														<span class="glyphicon glyphicon-copy"></span> Add Task
													</button>
												</td>
												<td></td>
											</tr>
										</c:if>
										<c:if
											test="${task.project != 'new task:project' && task.current}">
											<tr style="padding: 0.2rem;">
												<td >${task.id}</td>
												<td>${task.project}</td>
												<td><a href="${task.jiraLink}" target="_blank">
														${task.jiraTask} </a></td>
												<td>
													<button type="submit" name="start" value="${task.urlId}"
														<c:if test="${task.started}">disabled</c:if>
														class="btn btn-success"
														style="padding-top: 0rem; padding-bottom: 0rem">
														<span class="glyphicon glyphicon-play"></span>
														${task.startButtonText}
													</button>
												</td>
												<td><c:if test="${task.started}">
														<button type="submit" name="stop" value="${task.urlId}"
															class="btn btn-danger">
															<span class="glyphicon glyphicon-stop"></span> Stop
														</button>
													</c:if>
												</td>
												<td>
													<button type="submit" name="setidle" value="${task.urlId}"
														class="btn btn-info"
														style="padding-top: 0rem; padding-bottom: 0rem">
														<span class="glyphicon glyphicon-save"></span>
														Set to idle
													</button>
												</td>												
											</tr>
										</c:if>
									</c:forEach>
								</table>
							</div>
						</div>
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" href="#collapseIdle">Idle Tasks</a>
								</h4>
							</div>
							<div id="collapseIdle" class="table-responsive panel-collapse collapse"
								style="margin-left: 50px">

								<table class="table table-striped">
									<thead>
										<tr style="padding: 0.2rem;">
											<th>#</th>
											<th style="width: 20%;">Project</th>
											<th style="width: 65%;">Jira Task</th>
											<th style="width: 15%;" colspan="3">Actions</th>
										</tr>
									</thead>
									<c:forEach var="task" items="${taskList}">
										<c:if
											test="${task.project != 'new task:project' && !task.current}">
											<tr style="padding: 0.2rem;">
												<td>${task.id}</td>
												<td>${task.project}</td>
												<td><a href="${task.jiraLink}" target="_blank">
														${task.jiraTask} </a></td>
												<td>
													<button type="submit" name="start" value="${task.urlId}"
														<c:if test="${task.started}">disabled</c:if>
														class="btn btn-success"
														style="padding-top: 0rem; padding-bottom: 0rem">
														<span class="glyphicon glyphicon-play"></span>
														${task.startButtonText}
													</button>
												</td>
												<td><c:if test="${task.started}">
														<button type="submit" name="stop" value="${task.urlId}"
															class="btn btn-danger">
															<span class="glyphicon glyphicon-stop"></span> Stop
														</button>
													</c:if>
												</td>
												<td>
													<button type="submit" name="setcurrent" value="${task.urlId}"
														class="btn btn-info"
														style="padding-top: 0rem; padding-bottom: 0rem">
														<span class="glyphicon glyphicon-open"></span>
														Set to Current
													</button>
												</td>													
											</tr>
										</c:if>
									</c:forEach>
								</table>
							</div>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<br>
					<div class="alert alert-info">No tasks found</div>
				</c:otherwise>
			</c:choose>
		</form>
	</div>
</body>
</html>