<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<div class="inputDiv">
		
	<h1><ww:property value="this.getLabel('labels.internal.entry.createNewEntryThankYou')"/></h1>
	<hr/>
	<div id="portlet_margin">
	
		<ww:set name="eventId" value="eventId" scope="page"/>
		<portlet:renderURL var="eventUrl">
			<portlet:param name="action" value="ViewEvent"/>
			<portlet:param name="eventId" value='<%= pageContext.getAttribute("eventId").toString() %>'/>
		</portlet:renderURL>
		
		<span class="label"><ww:property value="this.getLabel('labels.internal.entry.createNewEntryThankYouText')"/></span> 
		<br/><br/>
		<a href="<c:out value="${eventUrl}"/>">Back to event</a>
	</div>

</div>
