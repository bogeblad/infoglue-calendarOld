<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="sv">

	<head>
		<title>InfoGlue Calendar 1.0</title>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<ww:if test="CSSUrl != null">
			<style type="text/css" media="screen">@import url(<ww:property value="CSSUrl"/>);</style>
		</ww:if>
		<ww:else>
			<style type="text/css" media="screen">@import url(/infoglueCalendar/css/calendarPortlet.css);</style>
		</ww:else>
		
	</head>

<body>

<div class="calApp">

<div class="portlet">

<div class="head">
	<span class="left">
		InfoGlue Calendar 1.0
	</span>	
	<span class="right">&nbsp;</span>
    <div class="clear"></div>
</div>

<div class="portlet_margin">
<h1>Database configuration error</h1>
<p>
	The portlet has no valid database connection - either you have just installed the portlet or your database/connection have been reconfigured.
</p>
<p>
	Check out the hibernate.cfg.xml-file in your WEB-INF/classes-directory if you want to reconfigure the connection. 
</p>
<p>
	Click <a href="http://www.infoglue.org">here</a> and navigate to the Calendar pages to find full installation / configuration instructions.
</p>
</div>

<%@ include file="adminFooter.jsp" %>
