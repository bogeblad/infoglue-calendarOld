<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Events" scope="page"/>
<c:set var="activeEventSubNavItem" value="EventSearch" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<%@ include file="eventSubFunctionMenu.jsp" %>

<div class="portlet_margin">

	<h1><ww:property value="this.getLabel('labels.internal.event.searchIntro')"/></h1>
	
	<portlet:renderURL var="searchEntryActionUrl">
		<portlet:param name="action" value="ViewEventSearch"/>
	</portlet:renderURL>
		
	<form name="register" method="post" action="<c:out value="${searchEntryActionUrl}"/>">
	
		<calendar:textField label="labels.internal.event.name" name="'name'" value="name" cssClass="longtextfield"/>

		<ww:if test="this.isActiveEventField('organizerName')">
			<calendar:textField label="labels.internal.event.organizerName" name="'organizerName'" value="organizerName" cssClass="longtextfield"/>
		</ww:if>
		<ww:if test="this.isActiveEventField('lecturer')">
			<calendar:textField label="labels.internal.event.lecturer" name="'lecturer'" value="lecturer" cssClass="longtextfield"/>
		</ww:if>
		<ww:if test="this.isActiveEventField('customLocation')">
			<calendar:textField label="labels.internal.event.customLocation" name="'customLocation'" value="customLocation" cssClass="longtextfield"/>
		</ww:if>
		<ww:if test="this.isActiveEventField('alternativeLocation')">
			<calendar:textField label="labels.internal.event.alternativeLocation" name="'alternativeLocation'" value="alternativeLocation" cssClass="longtextfield"/>
		</ww:if>
		<ww:if test="this.isActiveEventField('contactName')">
			<calendar:textField label="labels.internal.event.contactName" name="'contactName'" value="contactName" cssClass="longtextfield"/>
		</ww:if>

		<span class="errorMessage"><ww:property value="#fieldErrors.startDateTime"/></span>
		<div class="fieldrow">
			<label for="startDateTime"><ww:property value="this.getLabel('labels.internal.event.searchStartDate')"/></label><br />
			<input id="startDateTime" name="startDateTime" value="<ww:property value="startDateTime"/>" class="datefield" type="textfield">
			<img src="<%=request.getContextPath()%>/images/calendar.gif" id="trigger_startDateTime" style="border: 0px solid black; cursor: pointer;" title="Date selector">
			<input name="startTime" value="<ww:property value="startTime"/>" class="hourfield" type="textfield">					
		</div>

		<span class="errorMessage"><ww:property value="#fieldErrors.endDateTime"/></span>
		<div class="fieldrow">
			<label for="endDateTime"><ww:property value="this.getLabel('labels.internal.event.searchEndDate')"/></label><br />
			<input id="endDateTime" name="endDateTime" value="<ww:property value="endDateTime"/>" class="datefield" type="textfield">
			<img src="<%=request.getContextPath()%>/images/calendar.gif" id="trigger_endDateTime" style="border: 0px solid black; cursor: pointer;" title="Date selector">
			<input name="endTime" value="<ww:property value="endTime"/>" class="hourfield" type="textfield">					
		</div>

		<calendar:selectField label="labels.internal.calendar.eventType" name="'categoryId'" headerItem="Filtrera på evenemangstyp" multiple="false" value="categoriesList" selectedValue="categoryId" cssClass="listBox"/>

		<calendar:selectField label="labels.internal.event.owningCalendar" name="'calendarId'" headerItem="Filtrera på huvudkalender" multiple="false" value="calendarList" selectedValue="calendarId" cssClass="listBox"/>

		<calendar:selectField label="labels.internal.event.location" name="'locationId'" headerItem="Filtrera på plats" multiple="false" value="locationList" selectedValue="locationId" cssClass="listBox"/>

		<calendar:selectField label="labels.internal.event.searchItemsPerPage" name="'itemsPerPage'" headerItem="Antal poster per sida" multiple="false" value="itemsPerPageMap" selectedValue="itemsPerPage" cssClass="listBox"/>

		<calendar:checkboxField label="Export to Excel" name="'exportResult'" valueMap="yesNoMap" selectedValues="false"/>

	<div style="height:10px"></div>
	<input type="submit" value="<ww:property value="this.getLabel('labels.internal.soba.searchButton')"/>" class="button"/>
	</form>
</div>

<script type="text/javascript">
    Calendar.setup({
        inputField     :    "startDateTime",     // id of the input field
        ifFormat       :    "%Y-%m-%d",      // format of the input field
        button         :    "trigger_startDateTime",  // trigger for the calendar (button ID)
        align          :    "BR",           // alignment (defaults to "Bl")
        singleClick    :    true,
        firstDay  	   : 	1    
    });
</script>

<script type="text/javascript">
    Calendar.setup({
        inputField     :    "endDateTime",     // id of the input field
        ifFormat       :    "%Y-%m-%d",      // format of the input field
        button         :    "trigger_endDateTime",  // trigger for the calendar (button ID)
        align          :    "BR",           // alignment (defaults to "Bl")
        singleClick    :    true,
        firstDay  	   : 	1    
    });
</script>

<%@ include file="adminFooter.jsp" %>