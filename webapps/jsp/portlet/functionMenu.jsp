<portlet:renderURL var="viewCalendarAdministrationUrl">
	<portlet:param name="action" value="ViewCalendarAdministration"/>
</portlet:renderURL>
<portlet:renderURL var="viewEventsUrl">
	<portlet:param name="action" value="ViewWaitingEventList"/>
</portlet:renderURL>

<portlet:renderURL var="viewAuthorizationSwitchManagementUrl">
	<portlet:param name="action" value="ViewAuthorizationSwitchManagement!input"/>
</portlet:renderURL>
<portlet:renderURL var="viewApplicationStateUrl">
	<portlet:param name="action" value="ViewApplicationState"/>
</portlet:renderURL>
<portlet:renderURL var="viewCalendarListUrl">
	<portlet:param name="action" value="ViewCalendarList"/>
</portlet:renderURL>
<portlet:renderURL var="viewLocationListUrl">
	<portlet:param name="action" value="ViewLocationList"/>
</portlet:renderURL>
<portlet:renderURL var="viewCategoryUrl">
	<portlet:param name="action" value="ViewCategory"/>
</portlet:renderURL>
<portlet:renderURL var="viewEventTypeListUrl">
	<portlet:param name="action" value="ViewEventTypeList"/>
</portlet:renderURL>
<portlet:renderURL var="viewMyWorkingEventsUrl">
	<portlet:param name="action" value="ViewMyWorkingEventList"/>
</portlet:renderURL>
<portlet:renderURL var="viewWaitingEventsUrl">
	<portlet:param name="action" value="ViewWaitingEventList"/>
</portlet:renderURL>
<portlet:renderURL var="viewEventSearchFormUrl">
	<portlet:param name="action" value="ViewEventSearch!input"/>
</portlet:renderURL>
<portlet:renderURL var="viewEntrySearchUrl">
	<portlet:param name="action" value="ViewEntrySearch!input"/>
</portlet:renderURL>
<portlet:renderURL var="viewSettingsUrl">
	<portlet:param name="action" value="ViewSettings"/>
</portlet:renderURL>
<portlet:renderURL var="viewLabelsUrl">
	<portlet:param name="action" value="ViewLabels"/>
</portlet:renderURL>
<portlet:renderURL var="viewLanguagesUrl">
	<portlet:param name="action" value="ViewLanguageList"/>
</portlet:renderURL>

<calendar:hasRole id="calendarSuperUser" roleName="CalendarSuperUser"/>
<calendar:hasRole id="calendarAdministrator" roleName="CalendarAdministrator"/>
<calendar:hasRole id="calendarOwner" roleName="CalendarOwner"/>
<calendar:hasRole id="eventPublisher" roleName="EventPublisher"/>
	
<div class="functionarea">
  <span class="left">	
	<c:if test="${calendarOwner == true}">
	
		<a href="<c:out value="${viewEventsUrl}"/>" <c:if test="${activeNavItem == 'Events'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationHome')"/></a> |
		<a href="<c:out value="${viewEntrySearchUrl}"/>" <c:if test="${activeNavItem == 'EntrySearch'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationSearchEntries')"/></a>
	
	</c:if>
  </span>	
	
  <span class="right">	
	
	<!--User: <ww:property value="infoGlueRemoteUserRoles"/> <ww:property value="infoGlueRemoteUserGroups"/>-->
	
	<!--
	<a href="<c:out value="${viewWaitingEventsUrl}"/>" <c:if test="${activeNavItem == 'WaitingEvents'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationWaitingEvents')"/></a> |
	<a href="<c:out value="${viewEventSearchFormUrl}"/>" <c:if test="${activeNavItem == 'EventSearch'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationSearchEvents')"/></a> |
	<b><ww:property value="this.getLabel('labels.internal.applicationAdministrate')"/></b> 
	-->
	
	
	<c:if test="${calendarAdministrator == true}">
		<a href="<c:out value="${viewCategoryUrl}"/>" <c:if test="${activeNavItem == 'Categories'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationAdministerCategories')"/></a> |
		<a href="<c:out value="${viewCalendarListUrl}"/>" <c:if test="${activeNavItem == 'Calendars'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationAdministerCalendars')"/></a> |
		<a href="<c:out value="${viewEventTypeListUrl}"/>" <c:if test="${activeNavItem == 'EventTypes'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationAdministerEventTypes')"/></a> |
	</c:if>
	
	<c:if test="${calendarSuperUser == true}">
		<a href="<c:out value="${viewAuthorizationSwitchManagementUrl}"/>" <c:if test="${activeNavItem == 'AuthorizationSwitchManagement'}">class="current"</c:if>>Auth transfer</a> |
		<a href="<c:out value="${viewApplicationStateUrl}"/>" <c:if test="${activeNavItem == 'ViewApplicationState'}">class="current"</c:if>>AppState</a> |
		<a href="<c:out value="${viewSettingsUrl}"/>" <c:if test="${activeNavItem == 'Settings'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationAdministerSettings')"/></a> |
		<a href="<c:out value="${viewLabelsUrl}"/>" <c:if test="${activeNavItem == 'Labels'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationAdministerLabels')"/></a> |
		<a href="<c:out value="${viewLanguagesUrl}"/>" <c:if test="${activeNavItem == 'Languages'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationAdministerLanguages')"/></a> |
	</c:if>
	
	<c:if test="${calendarAdministrator == true}">
		<a href="<c:out value="${viewLocationListUrl}"/>" <c:if test="${activeNavItem == 'Locations'}">class="current"</c:if>><ww:property value="this.getLabel('labels.internal.applicationAdministerLocations')"/></a>
	</c:if>
			
  </span>
  <div class="clear"></div>
</div>	