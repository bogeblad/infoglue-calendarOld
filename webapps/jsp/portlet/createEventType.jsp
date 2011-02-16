<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="EventTypes" scope="page"/>
<c:set var="activeSubNavItem" value="NewEventType" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<portlet:renderURL var="createEventTypeUrl">
	<portlet:param name="action" value="CreateEventType!input"/>
</portlet:renderURL>

<div class="subfunctionarea">
<span class="left"></span>	
<span class="right">
	<a href="<c:out value="${createEventTypeUrl}"/>" <c:if test="${activeSubNavItem == 'NewEventType'}">class="current"</c:if> title="Skapa ny post"><ww:property value="this.getLabel('labels.internal.eventType.addEventType')"/></a>
</span>	
<div class="clear"></div>
</div>

<div class="portlet_margin">
	<portlet:actionURL var="createEventTypeActionUrl">
		<portlet:param name="action" value="CreateEventType"/>
	</portlet:actionURL>
	
	<form name="inputForm" method="POST" action="<c:out value="${createEventTypeActionUrl}"/>">

		<calendar:textField label="labels.internal.eventType.name" name="'name'" value="eventType.name" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.eventType.description" name="'description'" value="eventType.description" cssClass="longtextfield"/>
		<calendar:selectField label="labels.internal.eventType.type" name="'type'" multiple="false" value="eventTypes" selectedValue="eventType.type" headerItem="Choose type" cssClass="listBox"/>
		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.eventType.createButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
</div>

<%@ include file="adminFooter.jsp" %>
