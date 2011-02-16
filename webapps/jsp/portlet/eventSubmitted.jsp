<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Home" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<portlet:renderURL var="homeActionUrl">
	<portlet:param name="action" value="ViewCalendarAdministration"/>
</portlet:renderURL>

<div class="portlet_margin">
	<p>
		<ww:property value="this.getLabel('labels.internal.event.eventSubmittedText')"/>
		<br>
		<a href="<c:out value="${homeActionUrl}"/>"><ww:property value="this.getLabel('labels.internal.event.eventSubmittedHome')"/></a>	
	</p>
</div>

<%@ include file="adminFooter.jsp" %>
