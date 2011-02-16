<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Calendars" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<ww:set name="calendarId" value="calendar.id" scope="page"/>

<portlet:renderURL var="viewSubscriptionsUrl">
	<portlet:param name="action" value="ViewSubscriptionList"/>
	<portlet:param name="calendarId" value='<%= pageContext.getAttribute("calendarId").toString() %>'/>
</portlet:renderURL>

<div class="subfunctionarea">
<span class="left"></span>	
<span class="right">
	<a href="<c:out value="${viewSubscriptionsUrl}"/>" title="Skapa ny post"><ww:property value="this.getLabel('labels.internal.calendar.viewSubscriptions')"/></a>
</span>	
<div class="clear"></div>
</div>

<div class="portlet_margin">

	<portlet:actionURL var="updateCalendarActionUrl">
		<portlet:param name="action" value="UpdateCalendar"/>
	</portlet:actionURL>

	<portlet:renderURL var="viewCalendarUrl">
		<portlet:param name="action" value="ViewCalendar!gui"/>
		<portlet:param name="calendarId" value='<%= pageContext.getAttribute("calendarId").toString() %>'/>
		<portlet:param name="mode" value="day"/>
	</portlet:renderURL>
			
	<form name="inputForm" method="POST" action="<c:out value="${updateCalendarActionUrl}"/>">
		<input type="hidden" name="calendarId" value="<ww:property value="calendar.id"/>">
		
		<calendar:textField label="labels.internal.calendar.name" name="'name'" value="calendar.name" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.calendar.description" name="'description'" value="calendar.description" cssClass="longtextfield"/>
	    <%--<calendar:selectField label="labels.internal.calendar.owner" name="owner" multiple="false" value="infogluePrincipals" selectedValue="calendar.owner" cssClass="listBox"/>--%>
	    
	  	<ww:set name="owningRoles" value="calendar.owningRoles"/>
	  	<ww:set name="owningGroups" value="calendar.owningGroups"/>
			
	    <calendar:selectField label="labels.internal.calendar.roles" name="roles" multiple="true" value="infoglueRoles" selectedValueSet="#owningRoles" cssClass="listBox"/>
	    <calendar:selectField label="labels.internal.calendar.groups" name="groups" multiple="true" value="infoglueGroups" selectedValueSet="#owningGroups" cssClass="listBox"/>
	    
	    <calendar:selectField label="labels.internal.calendar.eventType" name="'eventTypeId'" multiple="false" value="eventTypes" selectedValue="calendar.eventType.id" cssClass="listBox"/>

	    <calendar:selectField label="labels.internal.calendar.eventType" name="'systemLanguageId'" multiple="false" value="languages" selectedValue="calendar.languages.id" cssClass="listBox"/>

		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.calendar.updateButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
		<!--
		<input onclick="document.location.href='<c:out value="${viewCalendarUrl}"/>';" type="button" value="<ww:property value="this.getLabel('labels.internal.calendar.viewGUICalendarButton')"/>" class="button">
		-->
	</form>

<%--	
	<portlet:renderURL var="accessRightsActionUrl">
		<portlet:param name="action" value="ViewAccessRights"/>
		<portlet:param name="interceptionPointCategory" value="Calendar"/>
		<portlet:param name="extraParameters" value='<%= pageContext.getAttribute("calendarId").toString() %>'/>
	</portlet:renderURL>
	
	<a href="<c:out value="${accessRightsActionUrl}"/>">Access rights</a>
--%>
	
</div>

<%@ include file="adminFooter.jsp" %>