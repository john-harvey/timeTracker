<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<html>
    <head>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">	
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>                 
    </head>
    <script>
		$(document).ready(function(){})
    </script>
    <body>          
        <div class="container" style="margin-left: 50px">
            <h2>Task Summary</h2>
            <form action="/tasks" method="get" id="listTaskForm" role="form">
                <button type="submit" class="btn btn-info">
                    <span class="glyphicon glyphicon-time"></span>  Task Tracking
                </button>
                <br></br>
            </form> 
        </div>
<div class="panel-group">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapseDeltek">Summary for Deltek</a>                    					                     
			</h4>
		</div> 
         <div id="collapseDeltek" class="panel-collapse collapse" style="margin-left: 50px">                                     
                <c:choose>
                    <c:when test="${not empty tasksByProject}">
                                    <div><strong>Day</strong></div>
                                    <div style="display: inline-block; width:300px;"><strong>Project</strong></div>
                                    <div style="display: inline-block;"><strong>Hours Worked</strong></div>                              
					          <div class="panel-group">
					            <c:forEach var="day" items="${tasksByProject}"> 
					                <div class="panel panel-default">
					                      <div class="panel-heading">
					        				<h5 class="panel-title">
					                             <a data-toggle="collapse" href="#collapse${fn:substringBefore(day.key, 'T')}">${fn:substringBefore(day.key, "T")}</a>                    					                     
					                        </h5>
					                      </div>   
					                     <div id="collapse${fn:substringBefore(day.key, 'T')}" class="panel-collapse collapse">  
					                        <div class="panel-body"> 
					                             	<c:forEach var="task" items="${day.value}">
							                               <div style="display: inline-block; width:300px;">${task.key}</div>
							                              <div style="display: inline-block;" ><fmt:formatNumber type="number" maxIntegerDigits="2" value="${task.value}" /></div>  
							                              <div></div>
					                                </c:forEach>
											</div>  
					                   </div>          
					               </div>                                                                      
					            </c:forEach> 
					         </div>       
                    </c:when>                    
                    <c:otherwise>
                        <br>           
                        <div class="alert alert-info">
                            No tasks found
                        </div>
                    </c:otherwise>
                </c:choose>                                
        </div>
  </div><!-- panel --> 
  
   <div class="panel panel-default">
   		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapseSpreadsheet">Summary for Spreadsheet</a>                    					                     
			</h4>
		</div>      
         <div id="collapseSpreadsheet" class="panel-collapse collapse" style="margin-left: 50px">                                     
                <c:choose>
                    <c:when test="${not empty tasksByJira}">
                        <table  class="table table-striped">
                            <thead>
                                <tr style="padding: 0.2rem;">
                                    <td style="min-width: 300px;"><h5>Project Code</h5></td>
                                    <td style="min-width: 1200px;"><h5>Jira/RFA/PR#</h5></td>
                                    <td style="min-width: 200px;"><h5>Date Worked</h5></td>  
                                    <td style="min-width: 200px;"><h5>Hours Worked</h5></td>                               
                                </tr>
                            </thead>
                            <c:forEach var="day" items="${tasksByJira}"> 
                             	<c:forEach var="task" items="${day.value}">
	                                <tr style="padding: 0.2rem;">
	                               		<td>${fn:substringBefore(task.key,"~")}</td>
	                                    <td>
	                                    	<a href="https://gcjiramain.unisysdevops.com/browse/${fn:split(fn:substringAfter(task.key,'~'), ' ')[0]}" target="_blank">
	                                    		${fn:substringAfter(task.key,"~")}
	                                    	</a>
	                                    </td>
	                                    <td>${fn:substringBefore(day.key, "T")}</td> 
	                                    <td><fmt:formatNumber type="number" maxIntegerDigits="2" value="${task.value}" /></td>                     					                    
	                                </tr>
                                </c:forEach>                                                       
                            </c:forEach>        
                        </table>  
                    </c:when>                    
                    <c:otherwise>
                        <br>           
                        <div class="alert alert-info">
                            No tasks found
                        </div>
                    </c:otherwise>
                </c:choose>                                
        </div>
     </div><!-- panel -->   
  </div><!-- panel group -->                
    </body>
</html>