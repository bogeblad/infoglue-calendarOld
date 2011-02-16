<%@ taglib uri="webwork" prefix="ww"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="calendar" prefix="calendar"%>

<portlet:defineObjects />

<ww:set name="languageCode" value="this.getLanguageCode()" />

<!-- Eventlist start -->
<ww:if test="#attr.detailUrl.indexOf('?') > -1">
	<c:set var="delim" value="&" />
</ww:if>
<ww:else>
	<c:set var="delim" value="?" />
</ww:else>

<ww:iterator value="events" status="rowstatus">

	<ww:set name="event" value="top" />
	<ww:set name="eventVersion" value="this.getEventVersion('#event')" />
	<ww:set name="eventVersion" value="this.getEventVersion('#event')"
		scope="page" />
	<ww:set name="eventId" value="id" scope="page" />
	<portlet:renderURL var="eventDetailUrl">
		<portlet:param name="action" value="ViewEvent!publicCustom" />
		<portlet:param name="eventId" value='<%= pageContext.getAttribute("eventId").toString() %>' />
	</portlet:renderURL>

	<h2><a
		href="<ww:property value="#attr.detailUrl"/><c:out value="${delim}"/>eventId=<ww:property value="top.id"/>"><ww:property
		value="#eventVersion.name" /></a></h2>

	<p class="dateformat">[<ww:set name="startDate" value="this.formatDate(top.startDateTime.time, 'yyyy-MM-dd')" /> 
		<ww:set name="endDate" value="this.formatDate(top.endDateTime.time, 'yyyy-MM-dd')" /> 
		<ww:if test="#startDate != #endDate">
			<ww:property value="#startDate" />
			<ww:if
				test="this.formatDate(top.startDateTime.time, 'HH:mm') != '12:34'">
				<ww:property value="this.getLabel('labels.public.event.klockLabel')" />
				<ww:property value="this.formatDate(top.startDateTime.time, 'HH:mm')" /> till <ww:property
					value="#endDate" />
				<ww:property value="this.getLabel('labels.public.event.klockLabel')" />
				<ww:property value="this.formatDate(top.endDateTime.time, 'HH:mm')" />
			</ww:if>
		</ww:if> 
		<ww:else>
			<ww:property value="#startDate" />
			<ww:if
				test="this.formatDate(top.startDateTime.time, 'HH:mm') != '12:34'">
				<ww:property value="this.getLabel('labels.public.event.timeLabel')" />: <ww:property
					value="this.formatDate(top.startDateTime.time, 'HH:mm')" />
				<ww:if
					test="this.formatDate(top.endDateTime.time, 'HH:mm') != '23:59'">- <ww:property
						value="this.formatDate(top.endDateTime.time, 'HH:mm')" />
				</ww:if>
			</ww:if>
		</ww:else>]
	</p>

	<ww:iterator value="top.owningCalendar.eventType.categoryAttributes">
		<ww:if test="top.internalName == 'eventType'">
			<ww:set name="selectedCategories" value="this.getEventCategories('#event', top)" />
			<ww:iterator value="#selectedCategories" status="rowstatus">
				<ww:set name="area"	value="top.getLocalizedName(#languageCode, 'sv')" />
			</ww:iterator>
		</ww:if>

		<ww:if test="top.internalName == 'city'">
			<ww:set name="selectedCategories" value="this.getEventCategories('#event', top)" />
			<ww:iterator value="#selectedCategories" status="rowstatus">
				<ww:set name="city"	value="top.getLocalizedName(#languageCode, 'sv')" />
			</ww:iterator>
		</ww:if>
	</ww:iterator>
	
	<p class="unpaddedtext"><ww:property value="#area" /> - <ww:property value="#city" /></p>
	
</ww:iterator>

<ww:if test="events == null || events.size() == 0">
	<p class="unpaddedtext">För tillfället finns inga aktuella
	kalenderhändelser inlagda i kategorin <!--"<ww:property value="#visibleCategoryName"/>"-->
	</p>
</ww:if>

<!-- Eventlist End -->
