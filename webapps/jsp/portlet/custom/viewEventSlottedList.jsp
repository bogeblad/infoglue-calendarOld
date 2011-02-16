<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<ww:set name="languageCode" value="this.getLanguageCode()"/>
<ww:set name="events" value="events" scope="page"/>
<calendar:setToList id="eventList" set="${events}"/>

<c:set var="eventsItems" value="${eventList}"/>
<ww:if test="events != null && events.size() > 0">
	<ww:set name="numberOfItems" value="numberOfItems" scope="page"/>

	<c:set var="currentSlot" value="${param.currentSlot}"/>
	<c:if test="${currentSlot == null}">
		<c:set var="currentSlot" value="1"/>
	</c:if>
	<calendar:slots visibleElementsId="eventsItems" visibleSlotsId="indices" lastSlotId="lastSlot" elements="${eventList}" currentSlot="${currentSlot}" slotSize="${numberOfItems}" slotCount="10"/>
</ww:if>

<!-- Calendar start -->
<div>   
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
			<portlet:param name="action" value="ViewEvent!publicCustom"/>
			<portlet:param name="eventId" value='<%= pageContext.getAttribute("eventId").toString() %>'/>
		</portlet:renderURL>
	                     
		<!-- Record Start -->

		<div>	
			<ww:set name="puffImage" value="this.getResourceUrl(#event, 'PuffBild')"/>
		    <ww:if test="#puffImage != null">
				<img class="newsListThumb" src="<ww:property value="#puffImage"/>"/>
			</ww:if>
			
			<h2><a href="<ww:property value="#attr.detailUrl"/><c:out value="${delim}"/>eventId=<ww:property value="top.id"/>"><ww:property value="#eventVersion.name"/></a></h2>	
			
			<p class="dateformat2">[
				<ww:set name="startDate" value="this.formatDate(top.startDateTime.time, 'yyyy-MM-dd')"/>
				<ww:set name="endDate" value="this.formatDate(top.endDateTime.time, 'yyyy-MM-dd')"/>
				<ww:if test="#startDate != #endDate">
					<ww:property value="#startDate"/>
					<ww:if test="this.formatDate(top.startDateTime.time, 'HH:mm') != '12:34'">
				 	<ww:property value="this.getLabel('labels.public.event.klockLabel')"/> <ww:property value="this.formatDate(top.startDateTime.time, 'HH:mm')"/> till <ww:property value="#endDate"/> <ww:property value="this.getLabel('labels.public.event.klockLabel')"/> <ww:property value="this.formatDate(top.endDateTime.time, 'HH:mm')"/>
				 	</ww:if>
				</ww:if>
				<ww:else>
					<ww:property value="#startDate"/>
					<ww:if test="this.formatDate(top.startDateTime.time, 'HH:mm') != '12:34'">
				 	<ww:property value="this.getLabel('labels.public.event.timeLabel')"/>: <ww:property value="this.formatDate(top.startDateTime.time, 'HH:mm')"/> <ww:if test="this.formatDate(top.endDateTime.time, 'HH:mm') != '23:59'">- <ww:property value="this.formatDate(top.endDateTime.time, 'HH:mm')"/></ww:if>
				 	</ww:if>
				</ww:else>
				]
			</p>
						
			<ww:iterator value="top.owningCalendar.eventType.categoryAttributes">
				<ww:if test="top.internalName == 'eventType'">
					<ww:set name="selectedCategories" value="this.getEventCategories('#event', top)"/>
					<ww:iterator value="#selectedCategories" status="rowstatus">				
						<ww:set name="area" value="top.getLocalizedName(#languageCode, 'sv')"/>
					</ww:iterator>
				</ww:if>
										
				<ww:if test="top.internalName == 'city'">
					<ww:set name="selectedCategories" value="this.getEventCategories('#event', top)"/>
					<ww:iterator value="#selectedCategories" status="rowstatus">				
						<ww:set name="city" value="top.getLocalizedName(#languageCode, 'sv')"/>
					</ww:iterator>
				</ww:if>				
		  	</ww:iterator>				
			
			<p class="unpaddedtext"><ww:property value="#area"/> - <ww:property value="#city"/></p>
			
			<p>				        			
				<ww:property value="#eventVersion.shortDescription"/>
			</p>
		</div>
		<br/>
				
		<!-- Record End -->
	</ww:iterator>

<ww:if test="events != null && events.size() > 0">
	<br/>
	<p><strong>Sida <c:out value="${currentSlot}"/> av <c:out value="${lastSlot}"/></strong>&nbsp;</p>                       
	
	<!-- slot navigator -->
	<c:if test="${lastSlot != 1}">
		<div class="prev_next">
			<c:if test="${currentSlot gt 1}">
				<c:set var="previousSlotId" value="${currentSlot - 1}"/>
				<portlet:renderURL var="firstUrl">
					<portlet:param name="action" value="ViewEventList!listSlottedCustom"/>
					<portlet:param name="currentSlot" value="1"/>
				</portlet:renderURL>
				<portlet:renderURL var="previousSlot">
					<portlet:param name="action" value="ViewEventList!listSlottedCustom"/>
					<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("previousSlotId").toString() %>'/>
				</portlet:renderURL>
				
				<a href="<c:out value='${firstUrl}'/>" class="number" title="F&ouml;rsta sidan">F&ouml;rsta</a>
				<a href="<c:out value='${previousSlot}'/>" title="F&ouml;reg&aring;ende sida" class="number">&laquo;</a>
			</c:if>
			<c:forEach var="slot" items="${indices}" varStatus="count">
				<c:if test="${slot == currentSlot}">
					<span class="number"><c:out value="${slot}"/></span>
				</c:if>
				<c:if test="${slot != currentSlot}">
					<c:set var="slotId" value="${slot}"/>
					<portlet:renderURL var="url">
						<portlet:param name="action" value="ViewEventList!listSlottedCustom"/>
						<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("slotId").toString() %>'/>
					</portlet:renderURL>

					<a href="<c:out value='${url}'/>" title="Sida <c:out value='${slot}'/>" class="number"><c:out value="${slot}"/></a>
				</c:if>
			</c:forEach>
			<c:if test="${currentSlot lt lastSlot}">
				<c:set var="nextSlotId" value="${currentSlot + 1}"/>
				<portlet:renderURL var="nextSlotUrl">
					<portlet:param name="action" value="ViewEventList!listSlottedCustom"/>
					<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("nextSlotId").toString() %>'/>
				</portlet:renderURL>
						
				<a href="<c:out value='${nextSlotUrl}'/>" title="N&auml;sta sida" class="number">&raquo;</a>
			</c:if>
		</div>
	</c:if>

</ww:if>
<ww:else>
	
	<ww:if test="events == null || events.size() == 0">
		<p>För tillfället finns inga aktuella kalenderhändelser inlagda i  kategorin <!--"<ww:property value="#visibleCategoryName"/>"--></p>
	</ww:if>

</ww:else>
	
</div>
<!-- Calendar End -->  
