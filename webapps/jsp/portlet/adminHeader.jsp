<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="sv">

	<head>
		<title><ww:property value="this.getLabel('labels.internal.applicationTitle')"/></title>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<ww:if test="CSSUrl != null">
			<style type="text/css" media="screen">@import url(<ww:property value="CSSUrl"/>);</style>
		</ww:if>
		<ww:else>
			<style type="text/css" media="screen">@import url(/infoglueCalendar/css/calendarPortlet.css);</style>
		</ww:else>
		
		<link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/applications/jscalendar/skins/aqua/theme.css" title="aqua" />
		<link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/applications/jscalendar/calendar-system.css" title="system" />
		
		<script type="text/javascript" src="<%=request.getContextPath()%>/script/dom-drag.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/script/infoglueCalendar.js"></script>
		
		<style type="text/css">
			.errorMessage {
			    color: red;
			}
			.columnMediumShort {
				width:18%;
				float:left; 
			}
			
			.languagesTabs {
				height: 20px;	
				margin: 0;	
				padding-left: 10px;
				border-bottom: 1px solid #ccc;
			}
			
			.languagesTabs li {
				margin: 0; 
				padding: 0;
				display: inline;	
				list-style-type: none;
			}
			
			.languagesTabs a:link, .languagesTabs a:visited {	
				float: left;
				background: #f3f3f3;
				font-size: 10px;	
				line-height: 14px;	
				padding: 2px 10px 2px 10px;
				margin-right: 4px;	
				border: 1px solid #ccc;	
				border-bottom: 0px solid white;
				text-decoration: none;	
				color: #666;
			}
			
			.languagesTabs a:link.active, .languagesTabs a:visited.active {
				border-bottom: 1px solid #fff;
				background: #fff;	
				color: #000;
			}
			
			.languagesTabs a:hover	{
				background: #fff;
			}
			
			.activeTab {
				color: #000;
				font-weight: bold;
			}

			.activeTab a:link, .activeTab a:link.visited {
				color: #000;
				font-weight: bold;
			}
		
		</style>

		<script type="text/javascript">
		
			function linkEvent(calendarId)
			{
				document.getElementById("calendarId").value = calendarId;
				document.linkForm.submit();
			}
		
			function createEventFromCopy(action)
			{
				document.updateForm.action = action;
				document.updateForm.submit();
			} 

			function deleteResource(resourceId)
			{
				document.deleteResourceForm.resourceId.value = resourceId;
				document.deleteResourceForm.submit();
			} 
		
			function includeScript(url)
			{
			  document.write('<script type="text/javascript" src="' + url + '"></scr' + 'ipt>'); 
			}

		</script>

	</head>

<body>

	<script type="text/javascript">
		//alert("Calendar:" + typeof(Calendar));
		if(typeof(Calendar) == 'undefined')
		{
			//alert("No calendar found - let's include it..");
			includeScript("<%=request.getContextPath()%>/applications/jscalendar/calendar.js");
			includeScript("<%=request.getContextPath()%>/applications/jscalendar/lang/calendar-en.js");
			includeScript("<%=request.getContextPath()%>/applications/jscalendar/calendar-setup.js");
		}
	</script>

<div class="calApp">

<div class="portlet">

<portlet:renderURL var="viewCalendarAdministrationUrl">
	<portlet:param name="action" value="ViewCalendarAdministration"/>
</portlet:renderURL>

<div class="head">
	<span class="left">
		<a href="<c:out value="${viewCalendarAdministrationUrl}"/>"><ww:property value="this.getLabel('labels.internal.applicationTitle')"/></a>
	</span>	
	<span class="right">	
		<ww:property value="this.getInfoGluePrincipal().firstName"/> <ww:property value="this.getInfoGluePrincipal().lastName"/> | <a href="<ww:property value="logoutUrl"/>">Logga ut</a>
		<%--
		Request: <c:out value="${request.remoteUser}"/><br/>
		Request: <c:out value="${request.remoteHost}"/><br/>
		<%=request.getContextPath()%><br/>
		Host: <%=request.getRemoteHost()%><br/>
		Class: <%=request.getClass().getName()%><br/>
		Host: <%=request.getRemoteHost()%><br/>
		<%=request.getRemoteUser()%><br/>
		<%=request.isUserInRole("admin")%><br/>
		<%=request.isUserInRole("cmsUser")%><br/>
		<%=request.getUserPrincipal().toString()%><br/>
		RemoteUser: <ww:property value="this.getRequestClass()"/><br/>
		--%>
	</span>
    <div class="clear"></div>
</div>
