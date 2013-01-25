<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Events" scope="page"/>
<c:set var="activeEventSubNavItem" value="PublishedEvents" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<%@ include file="eventSubFunctionMenu.jsp" %>

<script type="text/javascript">
<!--
	function toggleDiv(id)
	{
		element = document.getElementById(id);
		if(element.style.display == "none")
			element.style.display = "block";
		else
			element.style.display = "none";
	}
-->
</script>

<div class="columnlabelarea">
	<div class="columnMedium"><p><ww:property value="this.getLabel('labels.internal.event.name')"/></p></div>
	<div class="columnMedium"><p><ww:property value="this.getLabel('labels.internal.event.description')"/></p></div>
	<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.event.owningCalendar')"/></p></div>
	<div class="columnDate"><p><ww:property value="this.getLabel('labels.internal.event.startDate')"/></p></div>
	<div class="columnEnd"><p><a href="javascript:toggleDiv('columnFilterArea');">Filtrera</a></p></div>
	<div class="clear"></div>
</div>

<portlet:renderURL var="filterUrl">
	<portlet:param name="action" value="ViewPublishedEventList"/>
</portlet:renderURL>

<portlet:renderURL var="viewListUrl">
	<portlet:param name="action" value="ViewPublishedEventList"/>
</portlet:renderURL>

<portlet:renderURL var="confirmUrl">
	<portlet:param name="action" value="Confirm"/>
</portlet:renderURL>

<script type="text/javascript">
	function submitDelete(okUrl, confirmMessage)
	{
		//alert("okUrl:" + okUrl);
		document.confirmForm.okUrl.value = okUrl;
		document.confirmForm.confirmMessage.value = confirmMessage;
		document.confirmForm.submit();
	}
</script>
<form name="confirmForm" action="<c:out value="${confirmUrl}"/>" method="post">
	<input type="hidden" name="confirmTitle" value="Radera - bekr�fta"/>
	<input type="hidden" name="confirmMessage" value="Fixa detta"/>
	<input type="hidden" name="okUrl" value=""/>
	<input type="hidden" name="cancelUrl" value="<c:out value="${viewListUrl}"/>"/>	
</form>

<ww:if test="categoryId == null">
<div id="columnFilterArea" class="columnlabelarea" style="display:none;">
</ww:if>
<ww:else>
<div id="columnFilterArea" class="columnlabelarea" style="display:block;">
</ww:else>
<form action="<c:out value="${filterUrl}"/>" method="POST">
	<div class="columnMedium"><p><calendar:selectField label="labels.internal.calendar.eventType" name="'categoryId'" headerItem="Filtrera p� evenemangstyp" multiple="false" value="categoriesList" selectedValue="categoryId" cssClass="listBox"/></p></div>
	<div class="columnMedium"><p>&nbsp;</p></div>
	<div class="columnShort"><p>&nbsp;</p></div>
	<div class="columnDate"><p>&nbsp;</p></div>
	<div class="columnEnd"><p>&nbsp;</p><input type="submit" value="Filtrera"/></div>
	<div class="clear"></div>
</form>
</div>

<ww:set name="languageCode" value="this.getLanguageCode()"/>
<ww:set name="events" value="events" scope="page"/>
<calendar:setToList id="eventList" set="${events}"/>

<c:set var="eventsItems" value="${eventList}"/>
<ww:if test="events != null && events.size() > 0">
	<ww:set name="numberOfItems" value="numberOfItems" scope="page"/>
	<c:if test="${numberOfItems == null || numberOfItems == '' || numberOfItems == 'Undefined'}">
		<c:set var="numberOfItems" value="10"/>
	</c:if>
	<c:set var="currentSlot" value="${param.currentSlot}"/>
	<c:if test="${currentSlot == null}">
		<c:set var="currentSlot" value="1"/>
	</c:if>
	<calendar:slots visibleElementsId="eventsItems" visibleSlotsId="indices" lastSlotId="lastSlot" elements="${eventList}" currentSlot="${currentSlot}" slotSize="${numberOfItems}" slotCount="10"/>
</ww:if>

<ww:iterator value="#attr.eventsItems" status="rowstatus">

	<ww:set name="event" value="top"/>
	<ww:set name="eventVersion" value="this.getMasterEventVersion('#event')"/>
	<ww:set name="eventVersion" value="this.getMasterEventVersion('#event')" scope="page"/>
	<ww:set name="eventId" value="id" scope="page"/>
	<ww:set name="name" value="name" scope="page"/>

	<portlet:renderURL var="eventUrl">
		<portlet:param name="action" value="ViewEvent"/>
		<portlet:param name="eventId" value='<%= pageContext.getAttribute("eventId").toString() %>'/>
	</portlet:renderURL>
	
	<portlet:actionURL var="deleteUrl">
		<portlet:param name="action" value="DeleteEvent!published"/>
		<calendar:evalParam name="eventId" value="${eventId}"/>
	</portlet:actionURL>
		
	<ww:if test="#rowstatus.odd == true">
    	<div class="oddrow">
    </ww:if>
    <ww:else>
		<div class="evenrow">
    </ww:else>

	   	<div class="columnMedium">
	   		<p class="portletHeadline"><a href="<c:out value="${eventUrl}"/>" title="Visa '<ww:property value="#eventVersion.name"/>'"><ww:property value="#eventVersion.name"/><ww:if test="#eventVersion == null"><ww:property value="#event.id"/></ww:if></a>
		   		<ww:iterator value="owningCalendar.eventType.categoryAttributes">
					<ww:if test="top.name == 'Evenemangstyp' || top.name == 'Eventtyp'">
						<ww:set name="selectedCategories" value="this.getEventCategories('#event', top)"/>
						<ww:iterator value="#selectedCategories" status="rowstatus">
							<ww:property value="top.getLocalizedName(#languageCode, 'sv')"/><ww:if test="!#rowstatus.last">, </ww:if>
						</ww:iterator>
					</ww:if>
		   		</ww:iterator>
	   		</p>
	   	</div>
	   	<div class="columnMedium">
	   		<p><ww:property value="#eventVersion.shortDescription"/>&nbsp;</p>
	   	</div>
	   	<div class="columnShort">
	   		<p><ww:property value="owningCalendar.name"/></p>
	   	</div>
	   	<div class="columnDate">
	   		<p style="white-space: nowrap;"><ww:property value="this.formatDate(startDateTime.time, 'yyyy-MM-dd')"/></p>
	   	</div>
	   	<div class="columnEnd">
	   		<ww:if test="this.getInfoGluePrincipal().name != 'eventPublisher'">
	   		<a href="javascript:submitDelete('<c:out value="${deleteUrl}"/>', '�r du s�ker p� att du vill radera &quot;<ww:property value="#eventVersion.name"/>&quot;');" title="Radera '<ww:property value="#eventVersion.name"/>'" class="delete"></a>
	   	   	<a href="<c:out value="${eventUrl}"/>" title="Redigera '<ww:property value="#eventVersion.name"/>'" class="edit"></a>
	   		</ww:if>
	   	</div>
	   	<div class="clear"></div>
	</div>

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
					<portlet:param name="action" value="ViewPublishedEventList"/>
					<portlet:param name="currentSlot" value="1"/>
				</portlet:renderURL>
				<portlet:renderURL var="previousSlot">
					<portlet:param name="action" value="ViewPublishedEventList"/>
					<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("previousSlotId").toString() %>'/>
				</portlet:renderURL>
				
				<a href="<c:out value='${firstUrl}'/>" class="number" title="F&ouml;rsta sidan">F&Ouml;RSTA</a>
				<a href="<c:out value='${previousSlot}'/>" title="F&ouml;reg&aring;ende sida" class="number">&laquo;</a>
			</c:if>
			<c:forEach var="slot" items="${indices}" varStatus="count">
				<c:if test="${slot == currentSlot}">
					<span class="number"><c:out value="${slot}"/></span>
				</c:if>
				<c:if test="${slot != currentSlot}">
					<c:set var="slotId" value="${slot}"/>
					<portlet:renderURL var="url">
						<portlet:param name="action" value="ViewPublishedEventList"/>
						<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("slotId").toString() %>'/>
					</portlet:renderURL>
	
					<a href="<c:out value='${url}'/>" title="Sida <c:out value='${slot}'/>" class="number"><c:out value="${slot}"/></a>
				</c:if>
			</c:forEach>
			<c:if test="${currentSlot lt lastSlot}">
				<c:set var="nextSlotId" value="${currentSlot + 1}"/>
				<portlet:renderURL var="nextSlotUrl">
					<portlet:param name="action" value="ViewPublishedEventList"/>
					<portlet:param name="currentSlot" value='<%= pageContext.getAttribute("nextSlotId").toString() %>'/>
				</portlet:renderURL>
						
				<a href="<c:out value='${nextSlotUrl}'/>" title="N&auml;sta sida" class="number">&raquo;</a>
			</c:if>
		</div>
	</c:if>

</ww:if>
<ww:else>

	<ww:if test="events == null || events.size() == 0">
		<div class="oddrow">
			<div class="columnLong"><p class="portletHeadline"><ww:property value="this.getLabel('labels.internal.applicationNoItemsFound')"/></a></p></div>
	       	<div class="columnMedium"></div>
	       	<div class="columnEnd"></div>
	       	<div class="clear"></div>
	    </div>
	</ww:if>

</ww:else>

<%@ include file="adminFooter.jsp" %>
