<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<c:catch var="exception1">

<ww:set name="languageCode" value="this.getLanguageCode()"/>
<ww:set name="events" value="events" scope="page"/>
<ww:if test="#languageCode == 'en'">
	<ww:set name="dateFormat" value="'M/d/yyyy'"/>
	<ww:set name="timeFormat" value="'h:mm aaa'"/>
</ww:if>
<ww:else>
	<ww:set name="dateFormat" value="'yyyy-MM-dd'"/>
	<ww:set name="timeFormat" value="'HH:mm'"/>
</ww:else>

<calendar:setToList id="eventList" set="${events}"/>

<c:set var="eventsItems" value="${eventList}"/>
<ww:if test="events != null && events.size() > 0">
	<ww:set name="numberOfItems" value="numberOfItems" scope="page"/>
	<c:if test="${empty numberOfItems}">
		<c:set var="numberOfItems" value="10"/>
	</c:if>
	<c:set var="currentSlot" value="${param.currentSlot}"/>
	<c:if test="${currentSlot == null}">
		<c:set var="currentSlot" value="1"/>
	</c:if>
	<calendar:slots visibleElementsId="eventsItems" visibleSlotsId="indices" lastSlotId="lastSlot" elements="${eventList}" currentSlot="${currentSlot}" slotSize="${numberOfItems}" slotCount="10"/>
</ww:if>

</c:catch>

<c:if test="${exception1 != null}">
	An error occurred: <c:out value="${exception1}"/>
</c:if>

<c:catch var="exception2">

<H1><ww:property value="this.getLabel('labels.public.calendar.calendarLabel')"/></H1>
<!-- Calendar start -->
<div class="calendar">   

	<ww:if test="#attr.detailUrl.indexOf('?') > -1">
		<c:set var="delim" value="&"/>
	</ww:if>
	<ww:else>
		<c:set var="delim" value="?"/>
	</ww:else>

	<ww:iterator value="#attr.eventsItems" status="rowstatus">
	
		<ww:set name="event" value="top"/>
		<ww:set name="eventId" value="id" scope="page"/>
		<ww:set name="eventVersion" value="this.getEventVersion('#event')"/>
		<ww:set name="eventVersion" value="this.getEventVersion('#event')" scope="page"/>
		
		<portlet:renderURL var="eventDetailUrl">
			<portlet:param name="action" value="ViewEvent!publicGU"/>
			<portlet:param name="eventId" value='<%= pageContext.getAttribute("eventId").toString() %>'/>
		</portlet:renderURL>
   		                 
		<!-- Record Start -->
		<div class="recordLine">
			<span class="categoryLabelSmall">
				<ww:iterator value="top.owningCalendar.eventType.categoryAttributes">
					<ww:if test="top.name == 'Evenemangstyp' || top.name == 'Eventtyp'">
						<ww:set name="selectedCategories" value="this.getEventCategories('#event', top)"/>
						<ww:iterator value="#selectedCategories" status="rowstatus">
							<ww:property value="top.getLocalizedName(#languageCode, 'sv')"/><ww:if test="!#rowstatus.last">, </ww:if>
						</ww:iterator>
					</ww:if>
		   		</ww:iterator>
			</span>
			<h3><a href="<ww:property value="#attr.detailUrl"/><c:out value="${delim}"/>eventId=<ww:property value="top.id"/>" title="<ww:property value="#eventVersion.title"/>"><ww:property value="#eventVersion.name"/></a></h3>
	
			<p><span class="calFactLabel"><ww:property value="this.getLabel('labels.public.event.timeLabel')"/></span> <ww:property value="this.formatDate(top.startDateTime.getTime(), #dateFormat)"/> 
			<ww:if test="this.formatDate(top.startDateTime.time, 'HH:mm') != '12:34'">
			<ww:property value="this.getLabel('labels.public.event.klockLabel')"/> <ww:property value="this.formatDate(top.startDateTime.getTime(), #timeFormat)"/>
			</ww:if>
			<br />
			<span class="calFactLabel"><ww:property value="this.getLabel('labels.public.event.locationLabel')"/>:</span>
			<ww:if test="#eventVersion.alternativeLocation != null && #eventVersion.alternativeLocation != ''">
				<ww:property value="#eventVersion.alternativeLocation"/>
			</ww:if>
			<ww:else>
				<ww:iterator value="top.locations" status="rowstatus">
					<ww:property value="top.getLocalizedName(#languageCode,'sv')"/><ww:if test="!#rowstatus.last">, </ww:if>
				</ww:iterator>
			</ww:else>
			<ww:if test="#eventVersion.customLocation != null && #eventVersion.customLocation != ''">
				- <ww:property value="#eventVersion.customLocation"/>
			</ww:if>
			
			<br /></p>
	        <ww:set name="puffImage" value="this.getResourceUrl(event, 'PuffBild')"/>
			<ww:if test="#puffImage != null">
			<img src="<ww:property value="#puffImage"/>" class="img_calendar_event"/>
			</ww:if>
			<p><ww:property value="#eventVersion.shortDescription"/></p>
			<ww:if test="#eventVersion.lecturer != null && #eventVersion.lecturer != ''">
			<p><span class="calFactLabel"><ww:property value="this.getLabel('labels.public.event.lecturerLabel')"/>:</span> <ww:property value="#eventVersion.lecturer"/></p>
			</ww:if>
		</div>
		<!-- Record End -->
	</ww:iterator>

<ww:if test="events != null && events.size() > 0">
	<br/>
	<p><strong><ww:property value="this.getLabel('labels.public.event.slots.pageLabel')"/> <c:out value="${currentSlot}"/> <ww:property value="this.getLabel('labels.public.event.slots.ofLabel')"/> <c:out value="${lastSlot}"/></strong>&nbsp;</p>                       
	
	<!-- slot navigator -->
	<c:if test="${lastSlot != 1}">
		<div class="prev_next">
			<c:if test="${currentSlot gt 1}">
				<c:set var="previousSlotId" value="${currentSlot - 1}"/>
				<portlet:renderURL var="firstUrl">
					<portlet:param name="action" value="ViewEventList!listSlottedGU"/>
					<portlet:param name="currentSlot" value="1"/>
				</portlet:renderURL>
				<portlet:renderURL var="previousSlot">
					<portlet:param name="action" value="ViewEventList!listSlottedGU"/>
					<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("previousSlotId").toString() %>'/>
				</portlet:renderURL>
				
				<a href="<c:out value='${firstUrl}'/>" class="number" title="<ww:property value="this.getLabel('labels.public.event.slots.firstPageTitle')"/>"><ww:property value="this.getLabel('labels.public.event.slots.firstPageLabel')"/></a>
				<a href="<c:out value='${previousSlot}'/>" title="<ww:property value="this.getLabel('labels.public.event.slots.previousPageTitle')"/>" class="number">&laquo;</a>
			</c:if>
			<c:forEach var="slot" items="${indices}" varStatus="count">
				<c:if test="${slot == currentSlot}">
					<span class="number"><c:out value="${slot}"/></span>
				</c:if>
				<c:if test="${slot != currentSlot}">
					<c:set var="slotId" value="${slot}"/>
					<portlet:renderURL var="url">
						<portlet:param name="action" value="ViewEventList!listSlottedGU"/>
						<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("slotId").toString() %>'/>
					</portlet:renderURL>

					<a href="<c:out value='${url}'/>" title="<ww:property value="this.getLabel('labels.public.event.slots.pageLabel')"/> <c:out value='${slot}'/>" class="number"><c:out value="${slot}"/></a>
				</c:if>
			</c:forEach>
			<c:if test="${currentSlot lt lastSlot}">
				<c:set var="nextSlotId" value="${currentSlot + 1}"/>
				<portlet:renderURL var="nextSlotUrl">
					<portlet:param name="action" value="ViewEventList!listSlottedGU"/>
					<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("nextSlotId").toString() %>'/>
				</portlet:renderURL>
						
				<a href="<c:out value='${nextSlotUrl}'/>" title="<ww:property value="this.getLabel('labels.public.event.slots.nextPageTitle')"/>" class="number">&raquo;</a>
			</c:if>
		</div>
	</c:if>

</ww:if>
<ww:else>
	
	<ww:if test="events == null || events.size() == 0">
		<p><ww:property value="this.getLabel('labels.public.event.slots.noMatchesLabel')"/> <!--"<ww:property value="#visibleCategoryName"/>"--></p>
	</ww:if>

</ww:else>
	
</div>

</c:catch>

<c:if test="${exception2 != null}">
	An error occurred: <c:out value="${exception2}"/>
</c:if>
<!-- Calendar End -->  
