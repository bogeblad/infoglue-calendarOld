<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Event" scope="page"/>

<%@ include file="adminHeader.jsp" %>

<ww:set name="event" value="event" scope="page"/>
<ww:set name="eventId" value="event.id" scope="page"/>
<ww:set name="calendarId" value="calendarId" scope="page"/>
<ww:set name="versionLanguageId" value="versionLanguageId" scope="page"/>

<%@ include file="functionMenu.jsp" %>

<div id="inputDiv">
	
	<ww:set name="eventId" value="event.id" scope="page"/>
	<ww:set name="versionLanguageId" value="versionLanguageId" scope="page"/>
	<ww:set name="eventVersion" value="eventVersion" scope="page"/>
	<ww:set name="name" value="eventVersion.name" scope="page"/>
	
	<portlet:actionURL var="deleteUrl">
		<portlet:param name="action" value="DeleteEvent"/>
		<calendar:evalParam name="eventId" value="${eventId}"/>
	</portlet:actionURL>

	<portlet:actionURL var="deleteVersionUrl">
		<portlet:param name="action" value="DeleteEventVersion"/>
		<calendar:evalParam name="eventId" value="${eventId}"/>
		<calendar:evalParam name="eventVersionId" value="${eventVersion.id}"/>
	</portlet:actionURL>

	<portlet:renderURL var="viewListUrl">
		<portlet:param name="action" value="ViewEvent"/>
		<calendar:evalParam name="eventId" value="${eventId}"/>
		<calendar:evalParam name="versionLanguageId" value="${versionLanguageId}"/>
	</portlet:renderURL>

	<portlet:renderURL var="translateEventRenderURL">
		<calendar:evalParam name="action" value="ViewEvent!chooseLanguageForEdit"/>
		<calendar:evalParam name="eventId" value="${eventId}"/>
		<calendar:evalParam name="calendarId" value="${calendarId}"/>
		<portlet:param name="skipLanguageTabs" value="true"/>	
	</portlet:renderURL>

	<portlet:renderURL var="confirmUrl">
		<portlet:param name="action" value="Confirm"/>
	</portlet:renderURL>

	<form name="deleteLinkForm" method="POST" action="<c:out value="${confirmUrl}"/>">
		<input type="hidden" name="confirmTitle" value="Radera - bekräfta"/>
		<input type="hidden" name="confirmMessage" value="Är du säker på att du vill radera &quot;<c:out value="${name}"/>&quot;"/>
		<input type="hidden" name="okUrl" value="<%= java.net.URLEncoder.encode(pageContext.getAttribute("deleteUrl").toString(), "utf-8") %>"/>
		<input type="hidden" name="cancelUrl" value="<%= java.net.URLEncoder.encode(pageContext.getAttribute("viewListUrl").toString(), "utf-8") %>"/>

		<input type="hidden" name="eventId" value="<ww:property value="event.id"/>"/>
		<input type="hidden" name="calendarId" value="<ww:property value="calendarId"/>"/>
		<input type="hidden" name="startDateTime" value="<ww:property value="this.formatDate(event.startDateTime.time, 'yyyy-MM-dd')"/>">
		<input type="hidden" name="endDateTime" value="<ww:property value="this.formatDate(event.endDateTime.time, 'yyyy-MM-dd')"/>">
	</form>	

	<form name="deleteVersionLinkForm" method="POST" action="<c:out value="${confirmUrl}"/>">
		<input type="hidden" name="confirmTitle" value="Radera - bekräfta"/>
		<input type="hidden" name="confirmMessage" value="Är du säker på att du vill radera den <c:out value="${eventVersion.language.name}"/> versionen av &quot;<c:out value="${name}"/>&quot;"/>
		<input type="hidden" name="okUrl" value="<%= java.net.URLEncoder.encode(pageContext.getAttribute("deleteVersionUrl").toString(), "utf-8") %>"/>
		<input type="hidden" name="cancelUrl" value="<%= java.net.URLEncoder.encode(pageContext.getAttribute("viewListUrl").toString(), "utf-8") %>"/>

		<input type="hidden" name="eventId" value="<ww:property value="event.id"/>"/>
		<input type="hidden" name="calendarId" value="<ww:property value="calendarId"/>"/>
		<input type="hidden" name="startDateTime" value="<ww:property value="this.formatDate(event.startDateTime.time, 'yyyy-MM-dd')"/>">
		<input type="hidden" name="endDateTime" value="<ww:property value="this.formatDate(event.endDateTime.time, 'yyyy-MM-dd')"/>">
	</form>	
			
	<div class="portlet_margin">
	
		<ww:if test="skipLanguageTabs != true">
		
			<ww:if test="event.versions.size() > 1">
				<p>
					<ww:property value="this.getLabel('labels.internal.application.languageTranslationTabsIntro')"/>
					<ww:if test="event.versions.size() < availableLanguages.size()">
						<ww:property value="this.getParameterizedLabel('labels.internal.application.languageTranslationNewVersionText', #attr.translateEventRenderURL)"/>
					</ww:if>
				</p>
				<ul class="languagesTabs">
					<ww:iterator value="event.versions" status="rowstatus">
						<ww:set name="currentLanguageId" value="top.language.id"/>
						<ww:set name="currentLanguageId" value="top.language.id" scope="page"/>
						
						<portlet:renderURL var="viewEventVersionUrl">
							<portlet:param name="action" value="ViewEvent"/>
							<calendar:evalParam name="eventId" value="${eventId}"/>
							<calendar:evalParam name="versionLanguageId" value="${currentLanguageId}"/>
						</portlet:renderURL>
							
						<c:choose>
							<c:when test="${versionLanguageId == currentLanguageId}">
								<c:set var="cssClass" value="activeTab"/>
							</c:when>
							<c:otherwise>
								<c:set var="cssClass" value=""/>
							</c:otherwise>
						</c:choose>		
						<li class="<c:out value="${cssClass}"/>">
							<a href="<c:out value="${viewEventVersionUrl}"/>"><ww:property value="top.language.name"/></a>
						</li>
						
					</ww:iterator>
				</ul>
			</ww:if>
			<ww:else>
				<ww:if test="event.versions.size() < availableLanguages.size()">
				<p>
					<ww:property value="this.getParameterizedLabel('labels.internal.application.languageTranslationNewVersionText', #attr.translateEventRenderURL)"/>
				</p>
				</ww:if>
			</ww:else>
		
		</ww:if>
		
		<p>
			<calendar:textValue label="labels.internal.event.name" value="eventVersion.name" labelCssClass="label"/>
		</p>
		<p>
			<calendar:textValue label="labels.internal.event.title" value="eventVersion.title" labelCssClass="label"/>
		</p>
		<p>
			<span class="label"><ww:property value="this.getLabel('labels.internal.event.startDate')"/></span><br />
			<ww:property value="this.formatDate(event.startDateTime.time, 'yyyy-MM-dd')"/> 
			<ww:if test="this.formatDate(event.startDateTime.time, 'HH:mm') != '12:34'">
			<ww:property value="this.getLabel('labels.public.event.klockLabel')"/>. <ww:property value="this.formatDate(event.startDateTime.time, 'HH:mm')"/>
			</ww:if>
		</p>

		<p>
			<span class="label"><ww:property value="this.getLabel('labels.internal.event.endDate')"/></span><br />
			<ww:property value="this.formatDate(event.endDateTime.time, 'yyyy-MM-dd')"/> 
			<ww:if test="this.formatDate(event.endDateTime.time, 'HH:mm') != '13:34'">
			<ww:property value="this.getLabel('labels.public.event.klockLabel')"/>. <ww:property value="this.formatDate(event.endDateTime.time, 'HH:mm')"/>
			</ww:if>
		</p>
		
		<p>
			<calendar:textValue label="labels.internal.event.shortDescription" value="eventVersion.shortDescription" labelCssClass="label"/>
		</p>
		<p>
			<calendar:textValue label="labels.internal.event.longDescription" value="eventVersion.longDescription" labelCssClass="label"/>
		</p>
		<ww:if test="this.isActiveEventField('isInternal')">
		<p>
			<calendar:textValue label="labels.internal.event.isInternal" value="event.isInternal" valueMap="internalEventMap" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('isOrganizedByGU')">
		<p>
			<calendar:textValue label="labels.internal.event.isOrganizedByGU" value="event.isOrganizedByGU" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('organizerName')">
		<p>
			<calendar:textValue label="labels.internal.event.organizerName" value="eventVersion.organizerName" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('lecturer')">
		<p>
			<calendar:textValue label="labels.internal.event.lecturer" value="eventVersion.lecturer" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('customLocation')">
		<p>
			<calendar:textValue label="labels.internal.event.customLocation" value="eventVersion.customLocation" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('alternativeLocation')">
		<p>
			<calendar:textValue label="labels.internal.event.alternativeLocation" value="eventVersion.alternativeLocation" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('eventUrl')">
		<p>
			<calendar:textValue label="labels.internal.event.eventUrl" value="eventVersion.eventUrl" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('contactName')">
		<p>
			<calendar:textValue label="labels.internal.event.contactName" value="event.contactName" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('contactEmail')">
		<p>
			<calendar:textValue label="labels.internal.event.contactEmail" value="event.contactEmail" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('contactPhone')">
		<p>
			<calendar:textValue label="labels.internal.event.contactPhone" value="event.contactPhone" labelCssClass="label"/>
		</p>
		</ww:if>
		<ww:if test="this.isActiveEventField('price')">
		<p>
			<calendar:textValue label="labels.internal.event.price" value="event.price" labelCssClass="label"/>
		</p>
		</ww:if>
		<p>
			<calendar:textValue label="labels.internal.event.maximumParticipants" value="event.maximumParticipants" labelCssClass="label"/>
		</p>

		<p>
			<span class="label"><ww:property value="this.getLabel('labels.internal.event.lastRegistrationDate')"/></span><br />
			<ww:if test="event.lastRegistrationDateTime == null">
				<ww:property value="this.getLabel('labels.internal.event.noLastRegistrationDateTime')"/>
			</ww:if>
			<ww:else>
				<ww:property value="this.formatDate(event.lastRegistrationDateTime.time, 'yyyy-MM-dd')"/> <ww:property value="this.getLabel('labels.public.event.klockLabel')"/>. <ww:property value="this.formatDate(event.lastRegistrationDateTime.time, 'HH:mm')"/>
			</ww:else>
		</p>

		<!-- END NEW -->

  		<p>
  			<calendar:textValue label="labels.internal.event.location" value="event.locations" labelCssClass="label"/>
		</p>

		<ww:set name="count" value="0"/>
		<ww:iterator value="attributes" status="rowstatus">
			<ww:set name="attribute" value="top" scope="page"/>
			<ww:set name="title" value="top.getContentTypeAttribute('title').getContentTypeAttributeParameterValue().getLocalizedValueByLanguageCode('label', currentContentTypeEditorViewLanguageCode)" scope="page"/>
			<ww:set name="attributeName" value="this.concat('attribute_', top.name)"/>
			<ww:set name="attributeValue" value="this.getAttributeValue(eventVersion.attributes, top.name)"/>
			<p>
				<calendar:textValue label="${title}" value="#attributeValue" labelCssClass="label"/>
			</p>
			<ww:set name="count" value="#count + 1"/>
		</ww:iterator>

		<ww:iterator value="event.owningCalendar.eventType.categoryAttributes" status="rowstatus">
		<p>
			<ww:set name="categoryAttribute" value="top" scope="page"/>
			<ww:set name="categoryAttributeIndex" value="#rowstatus.index" scope="page"/>
			<ww:set name="selectedCategories" value="this.getEventCategories(top)"/>
			<c:set var="categoryAttributeName" value="categoryAttribute_${categoryAttribute.id}_categoryId"/>
			<ww:property value="#categoryAttribute"/>
			<input type="hidden" name="categoryAttributeId_<ww:property value="#rowstatus.index"/>" value="<ww:property value="top.id"/>"/>
			<calendar:textValue label="top.name" value="#selectedCategories" labelCssClass="label"/>
   		</p>
		</ww:iterator>
			
		<p>  		
  			<calendar:textValue label="labels.internal.event.participants" value="event.participants" labelCssClass="label"/>
		</p>
		
		<p>
			<span class="label"><ww:property value="this.getLabel('labels.internal.event.attachedFiles')"/></span><br>
			<ww:iterator value="event.resources">
			
				<ww:set name="resourceId" value="top.id" scope="page"/>
				<calendar:resourceUrl id="url" resourceId="${resourceId}"/>
				
				<portlet:actionURL var="deleteResourceUrl">
					<portlet:param name="action" value="DeleteResource"/>
					<portlet:param name="deleteResourceId" value='<%= pageContext.getAttribute("resourceId").toString() %>'/>
				</portlet:actionURL>
							
				<span class=""><a href="<c:out value="${url}"/>"><ww:property value='fileName'/> (<ww:property value='assetKey'/>)</a></span>&nbsp;
				<ww:if test="this.getIsEventCreator(event) || this.getIsEventOwner(event)">
					<a href="<c:out value="${deleteResourceUrl}"/>"><img src="<%=request.getContextPath()%>/images/delete.gif" border="0"></a>
      			</ww:if>
      			<br/>
      		</ww:iterator>
      		
			<ww:if test="event.resources == null || event.resources.size() == 0">
				<span class="calendarValue"><ww:property value="this.getLabel('labels.internal.event.noAttachments')"/></span><br/>
			</ww:if>
		</p>

		<ww:if test="event.versions.size() < availableLanguages.size()">
		<p>
			<ww:property value="this.getParameterizedLabel('labels.internal.application.languageTranslationNewVersionText', #attr.translateEventRenderURL)"/>
		</p>
		</ww:if>
		
		<ww:set name="eventId" value="eventId" scope="page"/>
		<ww:if test="event.lastRegistrationDateTime != null">
			<ww:if test="event.stateId == 3 && event.lastRegistrationDateTime.time.time > now.time.time">
				<portlet:renderURL var="createEntryRenderURL">
					<calendar:evalParam name="action" value="CreateEntry!input"/>
					<calendar:evalParam name="eventId" value="${eventId}"/>
					<calendar:evalParam name="calendarId" value="${calendarId}"/>
				</portlet:renderURL>
	
	      		<a href="<c:out value="${createEntryRenderURL}"/>"><input type="button" value="<ww:property value="this.getLabel('labels.internal.event.signUpForThisEvent')"/>" class="button"></a>
			</ww:if>
		</ww:if>
		
		<portlet:renderURL var="editEventRenderURL">
			<calendar:evalParam name="action" value="ViewEvent!edit"/>
			<calendar:evalParam name="eventId" value="${eventId}"/>
			<calendar:evalParam name="calendarId" value="${calendarId}"/>
			<calendar:evalParam name="versionLanguageId" value="${versionLanguageId}"/>
		</portlet:renderURL>

		<portlet:renderURL var="uploadFormURL">
			<calendar:evalParam name="action" value="UpdateEvent!uploadForm"/>
			<calendar:evalParam name="eventId" value="${eventId}"/>
		</portlet:renderURL>
		
		<calendar:hasRole id="calendarAdministrator" roleName="CalendarAdministrator"/>
		
		<%
		org.infoglue.calendar.entities.Event event = (org.infoglue.calendar.entities.Event)pageContext.getAttribute("event");
		String languageName = ((org.infoglue.calendar.entities.EventVersion)event.getVersions().toArray()[0]).getLanguage().getName();
		pageContext.setAttribute("languageName", languageName);
		%>

		<ww:if test="this.getIsEventCreator(event) || this.getIsEventOwner(event) || calendarAdministrator == true">

	  		<input onclick="document.location.href='<c:out value="${uploadFormURL}"/>';" type="button" value="<ww:property value="this.getLabel('labels.internal.event.attachFile')"/>" class="button">
			<input onclick="document.location.href='<c:out value="${editEventRenderURL}"/>';" type="button" value="<ww:property value="this.getLabel('labels.internal.event.editButton')"/>" class="button">
			
			<ww:if test="event.versions.size() > 1">
				<input onclick="document.deleteVersionLinkForm.submit();" type="button" value="<ww:property value="this.getParameterizedLabel('labels.internal.event.deleteVersionButton', eventVersion.language.name)"/>" class="button"></a>
			</ww:if>
			<input onclick="document.deleteLinkForm.submit();" type="button" value="<ww:property value="this.getLabel('labels.internal.event.deleteButton')"/>" class="button"></a>
		
			<%
			Object requestObject = request.getAttribute("javax.portlet.request");
			javax.portlet.PortletRequest renderRequestIG = (javax.portlet.PortletRequest)requestObject;
			String hostName = (String)renderRequestIG.getProperty("host");
			pageContext.setAttribute("hostName", hostName);
			%>		
			
			<ww:if test="event.stateId == 2">
				<portlet:renderURL var="publishedEventUrl">
					<portlet:param name="action" value="ViewEvent"/>
					<portlet:param name="eventId" value="{eventId}"/>
				</portlet:renderURL>
							
				<portlet:actionURL var="publishEventActionUrl">
					<calendar:evalParam name="action" value="UpdateEvent!publishEvent"/>
					<calendar:evalParam name="eventId" value="${eventId}"/>
					<calendar:evalParam name="calendarId" value="${calendarId}"/>
					<calendar:evalParam name="publishedEventUrl" value="http://${hostName}${publishedEventUrl}"/>
				</portlet:actionURL>
				<input onclick="document.location.href='<c:out value="${publishEventActionUrl}"/>';" type="button" value="<ww:property value="this.getLabel('labels.internal.event.publishEvent')"/>" class="button"/>
			</ww:if>
	
			<ww:if test="event.stateId == 0">
				<portlet:renderURL var="publishEventUrl">
					<portlet:param name="action" value="ViewEvent"/>
					<portlet:param name="eventId" value="{eventId}"/>
				</portlet:renderURL>
				
				<portlet:actionURL var="submitForPublishEventActionUrl">
					<calendar:evalParam name="action" value="UpdateEvent!submitForPublishEvent"/>
					<calendar:evalParam name="eventId" value="${eventId}"/>
					<calendar:evalParam name="calendarId" value="${calendarId}"/>
					<calendar:evalParam name="publishEventUrl" value="http://${hostName}${publishEventUrl}"/>
				</portlet:actionURL>
				       	
				<ww:if test="event.versions.size() > 1">
					<ww:set name="publishButtonLabel" value="this.getLabel('labels.internal.event.submitAllForPublishEvent')"/>
				</ww:if>
				<ww:else>
					<ww:set name="publishButtonLabel" value="this.getParameterizedLabel('labels.internal.event.submitLanguageVersionForPublishEvent', #attr.languageName)"/>
				</ww:else>
				
				<input onclick="document.location.href='<c:out value="${submitForPublishEventActionUrl}"/>';" type="button" value="<ww:property value="#publishButtonLabel"/>" class="button"/>
			</ww:if>
			
		</ww:if>
		
		<ww:if test="event.stateId == 3">
			<portlet:renderURL var="createEventAsCopyActionUrl">
				<calendar:evalParam name="action" value="ViewCalendarList!chooseCopyTarget"/>
				<calendar:evalParam name="eventId" value="${eventId}"/>
			</portlet:renderURL>
			<input onclick="document.location.href='<c:out value="${createEventAsCopyActionUrl}"/>';" type="button" value="<ww:property value="this.getLabel('labels.internal.event.createNewEvent')"/>" class="button"/>

			<portlet:renderURL var="linkEventActionUrl">
				<calendar:evalParam name="action" value="ViewCalendarList!chooseLinkTarget"/>
				<calendar:evalParam name="eventId" value="${eventId}"/>
			</portlet:renderURL>
			<input onclick="document.location.href='<c:out value="${linkEventActionUrl}"/>';" type="button" value="<ww:property value="this.getLabel('labels.internal.event.linkEvent')"/>" class="button"/>

		</ww:if>
				
		<ww:if test="event.stateId == 3">
			<portlet:renderURL var="searchEntryActionUrl">
				<portlet:param name="action" value="ViewEntrySearch"/>				
				<calendar:evalParam name="searchEventId" value="${eventId}"/>
				<calendar:evalParam name="onlyFutureEvents" value="false"/>
			</portlet:renderURL>
			
			<ww:if test="this.getIsEventOwner(event)">
				<input onclick="document.location.href='<c:out value="${searchEntryActionUrl}"/>';" type="button" value="<ww:property value="this.getLabel('labels.internal.event.entriesButton')"/>" class="button">
			</ww:if>
		</ww:if>
	</div>
	
	
</div>

<%@ include file="adminFooter.jsp" %>