<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Calendars" scope="page"/>
<c:set var="activeSubNavItem" value="NewCalendar" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<portlet:renderURL var="createCalendarUrl">
	<portlet:param name="action" value="CreateCalendar!input"/>
</portlet:renderURL>

<div class="subfunctionarea">
<span class="left"></span>	
<span class="right">
	<a href="<c:out value="${createCalendarUrl}"/>" <c:if test="${activeSubNavItem == 'NewCalendar'}">class="current"</c:if> title="Skapa ny post"><ww:property value="this.getLabel('labels.internal.calendar.addCalendar')"/></a>
</span>	
<div class="clear"></div>
</div>

<div class="portlet_margin">

	<portlet:actionURL var="createCalendarActionUrl">
		<portlet:param name="action" value="CreateCalendar"/>
	</portlet:actionURL>
	
	<form name="inputForm" method="POST" action="<c:out value="${createCalendarActionUrl}"/>">

		<calendar:textField label="labels.internal.calendar.name" name="'name'" value="calendar.name" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.calendar.description" name="'description'" value="calendar.description" cssClass="longtextfield"/>
	    
	    <calendar:selectField label="labels.internal.calendar.roles" name="roles" multiple="true" value="infoglueRoles" cssClass="listBox"/>
	    <calendar:selectField label="labels.internal.calendar.groups" name="groups" multiple="true" value="infoglueGroups" cssClass="listBox"/>
	    
	    <calendar:selectField label="labels.internal.calendar.eventType" name="'eventTypeId'" multiple="false" value="eventTypes" selectedValue="calendar.eventType" cssClass="listBox"/>
		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.calendar.createButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
	
</div>

<%@ include file="adminFooter.jsp" %>
