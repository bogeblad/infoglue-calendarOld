<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<ww:set name="languageCode" value="this.getLanguageCode()"/>

<ww:iterator value="events" status="rowstatus">
	<ww:if test="#rowstatus.count <= numberOfItems">
		<ww:set name="event" value="top"/>
		<ww:set name="eventVersion" value="this.getEventVersion('#event')"/>
		<ww:set name="eventVersion" value="this.getEventVersion('#event')" scope="page"/>
	
		<div class="record">
			<ww:if test="#attr.detailUrl.indexOf('?') > -1">
				<c:set var="delim" value="&"/>
			</ww:if>
			<ww:else>
				<c:set var="delim" value="?"/>
			</ww:else>
	
			<p class="dateformat leftpadding">[<ww:property value="this.formatDate(top.startDateTime.getTime(), 'yyyy-MM-dd')"/>]</p>
	       	<p class="unpaddedtext"><a href="<ww:property value="#attr.detailUrl"/><c:out value="${delim}"/>eventId=<ww:property value="top.id"/>" class="bulleted" title="<ww:property value="#eventVersion.title"/>"><ww:property value="#eventVersion.name"/></a></p>
		</div>
	
	</ww:if>
</ww:iterator>
