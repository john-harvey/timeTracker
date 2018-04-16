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
  	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.4.1/js/bootstrap-datepicker.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.4.1/css/bootstrap-datepicker3.css"/>              
    </head>
    <script>
		$(document).ready(function(){	
		    $(".form_date").datepicker({
		        format: "yyyy-mm-dd",
		        	immediateUpdates: true,
		            autoclose: true,
		            todayBtn: 'linked',
		            todayHighlight: true,
		            pickerPosition: "bottom-left"
		    }).on('changeDate', function(e) {
		    	// var $input = $(this).find("input[name=submittedDate]");
		    	//console.log("new date selected: "+$input.val());
		    	document.listSummaryForm.submit();
		    });		
		})
			
    </script>
    <body>          
        <div class="container" style="margin-left: 50px">
            <h2>Task Summary</h2>
            <form action="/tasks" method="get" id="listTaskForm" role="form">
                <button type="submit" class="btn btn-info">
                    <span class="glyphicon glyphicon-th"></span>  Task Tracking
                </button>
                <br></br>
            </form>
            <form action="/summary" method="post" id="listSummaryForm" name="listSummaryForm" role="form">
                <div><label class="control-label">Pay Period Start Date:</label></div>
				<div class="input-group input-append date form_date" style="width: 300px; min-width: 300px;">			
				    <input size="16" class="form-control" type="date" name="submittedDate" value=${startDate} readonly>
				    <span class="input-group-addon add-on"><span class="glyphicon glyphicon-calendar"></span></span>
<!-- 				 <button type="submit" class="btn btn-info">
                    <span class="glyphicon glyphicon-th"></span>  Update List
                </button> -->
				</div>		
            </form> 
        </div>       
    <div class="panel panel-default" style="margin-right: 50px;">
   		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapseClarity">Time Sheet Totals</a>
			</h4>
		</div>      
         <div id="collapseClarity" class="table-responsive panel-collapse collapse in" style="margin-left: 50px;">                                     
                <c:choose>
                    <c:when test="${not empty dailyTotalsByJira}">
						  <div style="display: inline-block; width:300px;"><strong>Project</strong></div>
						  <div style="display: inline-block; width:300px;"><strong>Task</strong></div> 
						   <c:forEach var="weekDate" items="${weekDates}">
						  		<div style="display: inline-block; width:90px;"><strong>${weekDate}</strong></div>  
						  </c:forEach>
						  <div style="display: inline-block; width:80px;"><strong>Total Hours</strong></div>  							  
						  <div class="table-responsive panel-group">
							<c:forEach var="task" items="${dailyTotalsByJira}"> 
							<c:set var="taskTotal" value="${0.0}"></c:set>
								<div class="table-responsive panel panel-default">  
									<div class="table-responsive panel-body"> 
											<div style="display: inline-block; width:300px; max-width:500px;">${fn:substringBefore(task.key,"~")}</div>
											<div style="display: inline-block; width:300px; max-width:1000px;" >
												<a href="${fn:split(fn:substringAfter(task.key,'^'), ' ')[0]}" target="_blank">
													${fn:split(fn:substringAfter(task.key,'~'), '^')[0]}
												</a>
											</div>
											<c:forEach var="weekDate" items="${weekDates}">
												<c:set var="matched" value='false'></c:set>
												<c:forEach var="day" items="${task.value}">
													 	<c:if test="${weekDate == fn:substringBefore(day.key,'T')}">
													 		<div class="${weekDate}" style="display: inline-block; width:90px;">${day.value}</div>
													 		<c:set var="taskTotal" value="${taskTotal+day.value}"></c:set>
													 		<c:set var="matched" value='true'></c:set>
													 	</c:if>	
												 </c:forEach>
												 <c:if test = "${matched=='false'}">
													<div  style="display: inline-block; width:90px;">0.0</div>	
												</c:if> 
											</c:forEach>
												<div  style="display: inline-block; width:90px;"><strong>${taskTotal}</strong></div>
									</div>           
							   </div>  
							</c:forEach>
							<div style="display: inline-block; width:600px;"><strong>Daily Totals:</strong></div>
						   <c:forEach var="weekDate" items="${weekDates}">
						  		<div style="display: inline-block; width:90px;"><input type="text" name="${weekDate}_Total" readonly style="border: none;"/></div>  
						  </c:forEach>
						  <div style="display: inline-block; width:90px;"><input type="text" name="grand_Total" readonly style="border: none;"/></div>							
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
  </div><!-- panel group -->    
 
   <div class="panel panel-default" style="margin-right: 50px;">
   		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapseSpreadsheet">Summary Detail</a>
			</h4>
		</div>      
         <div id="collapseSpreadsheet" class="table-responsive panel-collapse collapse" style="margin-left: 50px;">                                     
                <c:choose>
                    <c:when test="${not empty tasksByJira}">
						  <div style="display: inline-block; width:300px;"><strong>Project</strong></div>
						  <div style="display: inline-block; width:300px;"><strong>Task</strong></div>  
						  <div style="display: inline-block; width:200px;"><strong>Date Worked</strong></div>  
						  <div style="display: inline-block; width:200px;"><strong>Hours Worked</strong></div>  							  
						  <div class="table-responsive panel-group">
							<c:forEach var="day" items="${tasksByJira}"> 
									<div class="table-responsive panel panel-default">  
											<div class="table-responsive panel-body"> 
													<c:forEach var="task" items="${day.value}">
														 <div style="display: inline-block; width:300px; max-width:500px;">${fn:substringBefore(task.key,"~")}</div>
														 <div style="display: inline-block; width:300px; max-width:1000px;" >
															<a href="${fn:split(fn:substringAfter(task.key,'^'), ' ')[0]}" target="_blank">
																${fn:split(fn:substringAfter(task.key,'~'), '^')[0]}
															</a>
														</div>  
														<div style="display: inline-block; width:200px; max-width:200px;">${fn:substringBefore(day.key, "T")}</div>
														<div style="display: inline-block; width:200px; max-width:200px;"><fmt:formatNumber type="number" maxIntegerDigits="2" value="${task.value}" /></div>
														<div></div>
													</c:forEach>
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
  </div><!-- panel group -->    
    </body>
</html>