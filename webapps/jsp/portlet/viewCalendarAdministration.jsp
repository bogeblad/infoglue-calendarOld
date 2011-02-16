<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<c:set var="activeNavItem" value="Home" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<ww:if test="infoGlueRemoteUser == 'eventPublisher'">
	<ww:set name="anonymousCalendars" value="this.getAnonymousCalendars()"/>
	<ww:if test="#anonymousCalendars.size > 0">
	    <ww:set name="calendarId" value="#anonymousCalendars.get(0).id"/>
	</ww:if>
</ww:if>

<ww:if test="#calendarId == null || #calendarId == ''">
	<portlet:renderURL var="createEventUrl">
		<portlet:param name="action" value="ViewCalendarList!choose"/>
	</portlet:renderURL>
</ww:if>
<ww:else>
	<ww:set name="calendarId" value="#calendarId" scope="page"/>
	<portlet:renderURL var="createEventUrl">
		<portlet:param name="action" value="CreateEvent!input"/>
		<portlet:param name="calendarId" value='<%= pageContext.getAttribute("calendarId").toString() %>'/>
	</portlet:renderURL>
</ww:else>

<div class="portlet_margin">
<h1><ww:property value="this.getLabel('labels.internal.applicationSubHeader')"/></h1>
<p>
	<ww:property value="this.getLabel('labels.internal.applicationIntroduction')"/>
</p>
<p><a id="newEventLink" href="<c:out value="${createEventUrl}"/>"><ww:property value="this.getLabel('labels.internal.event.addEvent')"/></a></p>
</div>

<%@ include file="adminFooter.jsp" %>
