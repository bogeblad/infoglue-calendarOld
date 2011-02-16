<portlet:renderURL var="createEventUrl">
	<portlet:param name="action" value="ViewCalendarList!choose"/>
</portlet:renderURL>
<portlet:renderURL var="viewLinkedPublishedEventsUrl">
	<portlet:param name="action" value="ViewLinkedPublishedEventList"/>
</portlet:renderURL>
<portlet:renderURL var="viewPublishedEventsUrl">
	<portlet:param name="action" value="ViewPublishedEventList"/>
</portlet:renderURL>
<portlet:renderURL var="viewWaitingEventsUrl">
	<portlet:param name="action" value="ViewWaitingEventList"/>
</portlet:renderURL>
<portlet:renderURL var="viewEventSubscriptionListUrl">
	<portlet:param name="action" value="ViewEventSubscriptionList"/>
</portlet:renderURL>
<portlet:renderURL var="viewEventSearchFormUrl">
	<portlet:param name="action" value="ViewEventSearch!input"/>
</portlet:renderURL>
	
<div class="subfunctionarea">
	
	<calendar:hasRole id="calendarAdministrator" roleName="CalendarAdministrator"/>
	<calendar:hasRole id="calendarOwner" roleName="CalendarOwner"/>
	<calendar:hasRole id="eventPublisher" roleName="EventPublisher"/>
	
	<a id="newEventLink" href="<c:out value="${createEventUrl}"/>" <c:if test="${activeEventSubNavItem == 'NewEvent'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.event.addEvent')"/></a> |

	<c:if test="${calendarOwner == true}">
		<a href="<c:out value="${viewLinkedPublishedEventsUrl}"/>" <c:if test="${activeEventSubNavItem == 'LinkedPublishedEvents'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationLinkedPublishedEvents')"/></a> |
	   	<a href="<c:out value="${viewPublishedEventsUrl}"/>" <c:if test="${activeEventSubNavItem == 'PublishedEvents'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationPublishedComingEvents')"/></a> |
	   	<a href="<c:out value="${viewWaitingEventsUrl}"/>" <c:if test="${activeEventSubNavItem == 'WaitingEvents'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationWaitingEvents')"/></a> |
	</c:if>
	<a href="<c:out value="${viewMyWorkingEventsUrl}"/>" <c:if test="${activeEventSubNavItem == 'MyWorkingEvents'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationMyWorkingEvents')"/></a> |
	<c:if test="${calendarOwner == true}">
		<a href="<c:out value="${viewEventSubscriptionListUrl}"/>" <c:if test="${activeEventSubNavItem == 'EventSubscriptions'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationEventSubscriptions')"/></a> |
		<a href="<c:out value="${viewEventSearchFormUrl}"/>" <c:if test="${activeEventSubNavItem == 'EventSearch'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationSearchEvents')"/></a>
	</c:if>	
	&nbsp;

</div>