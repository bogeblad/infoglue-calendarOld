<%@ taglib uri="webwork" prefix="ww"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="calendar" prefix="calendar"%>

<portlet:defineObjects />

<ww:set name="languageCode" value="this.getLanguageCode()" />

<!-- Eventlist start -->

<ww:set name="entries" value="this.getAggregatedEntries()" />
<ww:set name="numberOfItems" value="this.getNumberOfItems()"/>

<ww:iterator value="#entries" status="rowstatus">
	
	<ww:if test="#rowstatus.count <= numberOfItems">
		
		<ww:set name="entry" value="top" />
	
		<h2><a href="<ww:property value="#entry.uri"/>"><ww:property value="#entry.title" /></a></h2>
		<p class="unpaddedtext"><ww:property value="#entry.description.value" /></p>
		
		<ww:set name="dates" value="this.getDates('#entry')" />
		
		<ww:set name="startDateTime" value="#dates[0]" />
		<ww:set name="endDateTime" value="#dates[1]" />
		
		<p class="dateformat">[<ww:set name="startDate" value="this.formatDate(#startDateTime, 'yyyy-MM-dd')" /> 
			<ww:set name="endDate" value="this.formatDate(#endDateTime, 'yyyy-MM-dd')" /> 
			<ww:if test="#startDate != #endDate">
				<ww:property value="#startDate" />
				<ww:if
					test="this.formatDate(#startDateTime, 'HH:mm') != '12:34'">
					<ww:property value="this.getLabel('labels.public.event.klockLabel')" />
					<ww:property value="this.formatDate(#startDateTime, 'HH:mm')" /> till <ww:property
						value="#endDate" />
					<ww:property value="this.getLabel('labels.public.event.klockLabel')" />
					<ww:property value="this.formatDate(#endDateTime, 'HH:mm')" />
				</ww:if>
			</ww:if> 
			<ww:else>
				<ww:property value="#startDate" />
				<ww:if
					test="this.formatDate(#startDateTime, 'HH:mm') != '12:34'">
					<ww:property value="this.getLabel('labels.public.event.timeLabel')" />: <ww:property
						value="this.formatDate(#startDateTime, 'HH:mm')" />
					<ww:if
						test="this.formatDate(#endDateTime, 'HH:mm') != '23:59'">- <ww:property
							value="this.formatDate(#endDateTime, 'HH:mm')" />
					</ww:if>
				</ww:if>
			</ww:else>]
		</p>
		
		<ww:set name="area"	value="this.getCategoryValue('#entry', 'eventType')" /> 
		<ww:set name="city"	value="this.getCategoryValue('#entry', 'city')" />
	
		<p class="unpaddedtext"><ww:property value="#area" /><ww:if test="#city != null"> - <ww:property value="#city" /></ww:if></p>
	
	</ww:if>
	
</ww:iterator>

<ww:if test="#entries == null || #entries.size() == 0">
	<p class="unpaddedtext">För tillfället finns inga aktuella kalenderhändelser inlagda i kategorin</p>
</ww:if>

<!-- Eventlist End -->

