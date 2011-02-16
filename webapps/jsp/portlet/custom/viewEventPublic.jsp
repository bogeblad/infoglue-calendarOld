<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<ww:set name="event" value="event"/>
<ww:set name="event" value="event" scope="page"/>
<ww:set name="eventVersion" value="this.getEventVersion('#event')"/>
<ww:set name="eventVersion" value="this.getEventVersion('#event')" scope="page"/>
<ww:set name="languageCode" value="this.getLanguageCode()"/>
<ww:set name="startDate" value="this.formatDate(event.startDateTime.time, 'yyyy-MM-dd')"/>
<ww:set name="endDate" value="this.formatDate(event.endDateTime.time, 'yyyy-MM-dd')"/>
<ww:set name="detailImage" value="this.getResourceUrl(event, 'DetaljBild')"/>

<ww:iterator value="event.owningCalendar.eventType.categoryAttributes">
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

<div class="sectionheader"><ww:property value="#eventVersion.name"/> - <ww:property value="#area"/>, <ww:property value="#city"/><img alt="" class="leftpadding" src="<ww:property value="this.getStringAttributeValue('balkImageUrl')"/>"/></div>

<p class="dateformat">[
	<ww:if test="#startDate != #endDate">		
   		<ww:property value="#startDate"/>
   		<ww:if test="this.formatDate(event.startDateTime.time, 'HH:mm') != '12:34'">
 			<ww:property value="this.getLabel('labels.public.event.klockLabel')"/> <ww:property value="this.formatDate(event.startDateTime.time, 'HH:mm')"/> till <ww:property value="#endDate"/> <ww:property value="this.getLabel('labels.public.event.klockLabel')"/> <ww:property value="this.formatDate(event.endDateTime.time, 'HH:mm')"/>
 		</ww:if>			
	</ww:if>
	<ww:else>	
	  	<ww:property value="#startDate"/>
		<ww:if test="this.formatDate(event.startDateTime.time, 'HH:mm') != '12:34'">
	 		<ww:property value="this.getLabel('labels.public.event.timeLabel')"/>: <ww:property value="this.formatDate(event.startDateTime.time, 'HH:mm')"/> <ww:if test="this.formatDate(event.endDateTime.time, 'HH:mm') != '23:59'">- <ww:property value="this.formatDate(event.endDateTime.time, 'HH:mm')"/></ww:if>
	 	</ww:if>			
	</ww:else>
]</p>

<div>
	<div class="eventCellLeft">Beskrivning:</div>
	<div class="eventCellRight">
		<ww:if test="#detailImage != null">
			<img src="<ww:property value="#detailImage"/>" class="mediumRight"/>
		</ww:if>
		<ww:property value="#eventVersion.decoratedLongDescription"/>				
	</div>
</div>

<ww:if test="#eventVersion.organizerName != null && #eventVersion.organizerName != ''">
 	<div>
		<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.organizerLabel')"/>: </div>
   		<div class="eventCellRight"><ww:property value="#eventVersion.organizerName"/></div>
   	</div>
</ww:if>
	
<div>
	<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.locationLabel')"/>: </div>
    <div class="eventCellRight">
		<ww:if test="#eventVersion.alternativeLocation != null && #eventVersion.alternativeLocation != ''">
			<ww:property value="#eventVersion.alternativeLocation"/>		
		</ww:if>
		<ww:else>
 				<ww:iterator value="event.locations">
	      		<ww:set name="location" value="top"/>
 				<ww:property value="#location.getLocalizedName(#languageCode, 'sv')"/><br/>		
      		</ww:iterator>
		</ww:else>
		<ww:property value="#eventVersion.customLocation"/>
	</div>
</div>
	
<ww:set name="startDate" value="this.formatDate(event.startDateTime.time, 'yyyy-MM-dd')"/>
<ww:set name="endDate" value="this.formatDate(event.endDateTime.time, 'yyyy-MM-dd')"/>

<ww:if test="#startDate != #endDate">
	<div>
		<div class="eventCellLeft">Tid:</div>
	    <div class="eventCellRight">
	   		<ww:property value="#startDate"/>
	   		<ww:if test="this.formatDate(event.startDateTime.time, 'HH:mm') != '12:34'">
	 			<ww:property value="this.getLabel('labels.public.event.klockLabel')"/> <ww:property value="this.formatDate(event.startDateTime.time, 'HH:mm')"/> till <ww:property value="#endDate"/> <ww:property value="this.getLabel('labels.public.event.klockLabel')"/> <ww:property value="this.formatDate(event.endDateTime.time, 'HH:mm')"/>
	 		</ww:if>
		 </div>
	</div>
</ww:if>
<ww:else>
	<div>
		<div class="eventCellLeft">Tid:</div>
    	<div class="eventCellRight">
    		<ww:property value="#startDate"/>
			<ww:if test="this.formatDate(event.startDateTime.time, 'HH:mm') != '12:34'">
		 		<ww:property value="this.getLabel('labels.public.event.timeLabel')"/>: </span><ww:property value="this.formatDate(event.startDateTime.time, 'HH:mm')"/> <ww:if test="this.formatDate(event.endDateTime.time, 'HH:mm') != '23:59'">- <ww:property value="this.formatDate(event.endDateTime.time, 'HH:mm')"/></ww:if>
		 	</ww:if>
		</div>
	</div>	
</ww:else>
    
<ww:if test="event.resources.size() > 0">
	<div>
		<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.additionalInfoLabel')"/>:</div>
		<div class="eventCellRight">
			<ww:iterator value="event.resources">
			  	<ww:if test="top.assetKey == 'BifogadFil'">
					<ww:set name="resourceId" value="top.id" scope="page"/>
					<calendar:resourceUrl id="url" resourceId="${resourceId}"/>
					<ww:if test="fileName.indexOf('.pdf') > -1">
						<ww:set name="resourceClass" value="'pdficon'"/>
					</ww:if>
					<ww:if test="fileName.indexOf('.doc') > -1">
						<ww:set name="resourceClass" value="'wordicon'"/>
					</ww:if>
					<ww:if test="fileName.indexOf('.xls') > -1">
						<ww:set name="resourceClass" value="'excelicon'"/>
					</ww:if>
					<ww:if test="fileName.indexOf('.ppt') > -1">
						<ww:set name="resourceClass" value="'powerpointicon'"/>
					</ww:if>					
						<a href="<c:out value="${url}"/>" target="_blank" class="<ww:property value="#resourceClass"/>"><ww:property value="shortendFileName"/></a><br/>					
			  	</ww:if>
			</ww:iterator>			
		</div>
	</div>
</ww:if>

<ww:if test="event.lastRegistrationDateTime != null">
 	<div>
		<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.lastRegistrationDateLabel')"/>: </div>
   	 	<div class="eventCellRight"><ww:property value="this.formatDate(event.lastRegistrationDateTime.time, 'yyyy-MM-dd')"/> <ww:property value="this.getLabel('labels.public.event.klockLabel')"/>. <ww:property value="this.formatDate(event.lastRegistrationDateTime.time, 'HH')"/>.</div>
	</div>
</ww:if>
	
<ww:if test="event.price != null && event.price != ''">
  	<div>
		<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.feeLabel')"/>: </div>
   		<div class="eventCellRight"><ww:property value="event.price"/></div>
   </div>
</ww:if>
<ww:else>
 	<div>
		<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.feeLabel')"/>: </div>
   		<div class="eventCellRight"><ww:property value="this.getLabel('labels.public.event.noFeeLabel')"/></div>	
   	</div>	
</ww:else>
	
<ww:if test="event.contactEmail != null && event.contactEmail != ''">
	<ww:if test="event.contactName != null && event.contactName != ''">
		<div>
			<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.contactPersonLabel')"/>: </div>
  			<div class="eventCellRight"><a href="mailto:<ww:property value="event.contactEmail"/>"><ww:property value="event.contactName"/></a></div>
  		</div>
	</ww:if>
	<ww:else>
		<div>
			<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.contactPersonLabel')"/>:</div>
	  		<div class="eventCellRight"><a href="mailto:<ww:property value="event.contactEmail"/>"><ww:property value="event.contactEmail"/></a></div>
  		</div>
	</ww:else>
</ww:if>
<ww:else>
	<ww:if test="event.contactName != null && event.contactName != ''">
		<div>
			<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.contactPersonLabel')"/>: </div>
  			<div class="eventCellRight"><ww:property value="event.contactName"/></div>
  		</div>
	</ww:if>
</ww:else>
	
<ww:if test="event.contactPhone != null && event.contactPhone != ''">
	<div>
		<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.phoneLabel')"/>: </div>
		<div class="eventCellRight"><ww:property value="event.contactPhone"/></div>
	</div>
</ww:if>
   
<ww:set name="count" value="0"/>
  
<ww:iterator value="attributes" status="rowstatus">
	<ww:set name="attribute" value="top" scope="page"/>
	<ww:set name="title" value="top.getContentTypeAttribute('title').getContentTypeAttributeParameterValue().getLocalizedValueByLanguageCode('label', currentContentTypeEditorViewLanguageCode)" scope="page"/>
	<ww:set name="attributeName" value="this.concat('attribute_', top.name)"/>
	<ww:set name="attributeValue" value="this.getAttributeValue(event.attributes, top.name)"/>
	<div>
		<div class="eventCellLeft"><calendar:textValue label="${title}" value="#attributeValue" labelCssClass="label"/></div>
	</div>
	<ww:set name="count" value="#count + 1"/>
</ww:iterator>
  
<ww:if test="#eventVersion.eventUrl != null && #eventVersion.eventUrl != ''">
   <div>
		<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.eventURLLabel')"/>: </div>
       <div class="eventCellRight"><a href="<ww:property value="#eventVersion.eventUrl"/>" target="_blank">Läs mer om <ww:property value="#eventVersion.name"/></a></div>
   </div>
</ww:if>

<ww:if test="event.maximumParticipants != null && event.maximumParticipants != 0">
<div>
	<div class="eventCellLeft"><ww:property value="this.getLabel('labels.public.event.numberOfSeatsLabel')"/>: </div>
   	<div class="eventCellRight"><ww:property value="event.maximumParticipants"/> (<ww:property value="this.getLabel('labels.public.event.ofWhichLabel')"/> <ww:property value="event.entries.size()"/> <ww:property value="this.getLabel('labels.public.event.isBookedLabel')"/>)</div>
   </div>
</ww:if>

<ww:if test="event.lastRegistrationDateTime != null">
	<div>
		<div class="eventCellRight">
			<ww:if test="event.lastRegistrationDateTime.time.time > now.time.time">
				<ww:if test="event.maximumParticipants == null || event.maximumParticipants > event.entries.size()">
					<ww:set name="eventId" value="eventId" scope="page"/>
					<portlet:renderURL var="createEntryRenderURL">
						<calendar:evalParam name="action" value="CreateEntry!inputPublicCustom"/>
						<calendar:evalParam name="eventId" value="${eventId}"/>
					</portlet:renderURL>
					<a href="<c:out value="${createEntryRenderURL}"/>"><ww:property value="this.getLabel('labels.public.event.signUp')"/></a>
				</ww:if>
				<ww:else>
					<ww:property value="this.getLabel('labels.internal.event.signUpForThisOverbookedEvent')"/><br/>
					<ww:set name="eventId" value="eventId" scope="page"/>
					<portlet:renderURL var="createEntryRenderURL">
						<calendar:evalParam name="action" value="CreateEntry!inputPublicGU"/>
						<calendar:evalParam name="eventId" value="${eventId}"/>
					</portlet:renderURL>
					<a href="<c:out value="${createEntryRenderURL}"/>"><ww:property value="this.getLabel('labels.public.event.signUp')"/></a>
				</ww:else>
			</ww:if>
			<ww:else>
				<ww:property value="this.getLabel('labels.public.event.registrationExpired')"/>
			</ww:else>
		</div>
	</div>
</ww:if>
