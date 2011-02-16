<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Calendars" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<%--
<ww:set name="calendarId" value="calendar.id" scope="page"/>
--%>

<div class="portlet_margin">

<%--
	<portlet:actionURL var="updateCalendarActionUrl">
		<portlet:param name="action" value="UpdateCalendar"/>
	</portlet:actionURL>

	<portlet:renderURL var="viewCalendarUrl">
		<portlet:param name="action" value="ViewCalendar!gui"/>
		<portlet:param name="calendarId" value='<%= pageContext.getAttribute("calendarId").toString() %>'/>
		<portlet:param name="mode" value="day"/>
	</portlet:renderURL>

--%>
	<ww:property value="interceptionPointList"/>
	
	<table border="0" width="95%" cellpadding="0" cellspacing="0">
	<tr class="darkgreen">
		<td class="smalllabel">Role</td>
		<td><img src="images/trans.gif" width="20" height="1"></td>
		<ww:set name="colspan" value="1"/>
		<ww:iterator value="interceptionPointList" status="rowstatus">
			<ww:if test="top.usesExtraDataForAccessControl">
				<ww:set name="accessRightId" value="-1"/>
				<ww:set name="accessRightId" value="$this.getAccessRightId(top.id, extraParameters)"/>
				<td><ww:property value="top.name"/>
				<ww:if test="accessRightId > -1"/>
					<ww:set name="accessRightGroups" value="this.getAccessRightGroups(accessRightId)"/>
					<ww:if test="#accessRightGroups.size > 0">
						<script type="text/javascript">hasGroupLimitation = true;</script>
						<a href="javascript:showDiv('groups<ww:property value="accessRightId"/>');" title="Limited by groups also - click to view."><img src="images/groups.gif" border="0"/></a>
					</ww:if>
					<ww:else>
						<a href="javascript:showDiv('groups<ww:property value="accessRightId"/>');" title="Not limited by groups at the moment"><img src="images/groupsInactive.gif" border="0"/></a>
					</ww:else>
				
				</td>
				<td><img src="images/trans.gif" width="20" height="1"></td>
				<ww:set name="colspan" value="#colspan + 3"/>
			</ww:if>
		</ww:iterator>
		<td></td>
	</tr>
	
	<tr>
		<td bgcolor="#EEF7DC" colspan="<ww:property value="#colspan"/>" height="1"><img src="images/trans.gif" width="1" height="1"></td>
	</tr>
	<tr>
		<td bgcolor="#C7D1B3" colspan="<ww:property value="#colspan"/>" height="1"><img src="images/trans.gif" width="1" height="1"></td>
	</tr>
	
	<ww:set name="roleIndex" value="0"/>
	<ww:iterator value="roleList" status="rowstatus">
		<ww:set name="role" value="top"/>
				
		<tr>
			<td>
				<ww:property value="name"/>
				<input type="hidden" name="<ww:property value="roleIndex"/>_roleName" value="<ww:property value="name"/>">
			</td>
			<td></td>
			<ww:set name="interceptionPointIndex" value="0"/>
			<ww:iterator value="interceptionPointList" status="rowstatus">
				<ww:if test="top.usesExtraDataForAccessControl">
					<td align="center" nowrap>
						<%--
						#set($checked = "")
						#if($this.getHasAccessRight($interceptionPointVO.id, $extraParameters, $role.name))
							#set($checked = "checked")
							#set($accessRightId = $this.getAccessRightId($interceptionPointVO.id, $extraParameters))
						#end 
						--%>
																							
						<input type="hidden" name="<ww:property value="interceptionPointIndex"/>_InterceptionPointId" value="<ww:property value="id"/>">
						<input type="hidden" name="<ww:property value="id"/>_<ww:property value="roleIndex"/>_roleName" value="<ww:property value="#role.name"/>">
						<input type="checkbox" id="<ww:property value="id"/>_<ww:property value="#role.name"/>_hasAccess" name="${interceptionPointVO.id}_<ww:property value="#role.name"/>_hasAccess" value="true" $checked>
						
					</td>
					<td></td>
					<ww:set name="interceptionPointIndex" value="#interceptionPointIndex + 1"/>
				</ww:if>	
			</ww:iterator>	
		</tr>
		<ww:set name="roleIndex" value="#roleIndex + 1"/>
	</ww:iterator>	


<%--	
	#foreach ($role in $roleList)
		<tr>
			<td>
				$role.name
				<input type="hidden" name="${roleIndex}_roleName" value="$role.name">
			</td>
			<td></td>
			#set($interceptionPointIndex = 0)
			#foreach($interceptionPointVO in $interceptionPointVOList)
				#if(($interceptionPointVO.getUsesExtraDataForAccessControl() && $extraParameters && $extraParameters != "") || (!$interceptionPointVO.getUsesExtraDataForAccessControl() && (!$extraParameters || $extraParameters == "")))
					<td align="center" nowrap>
						#set($checked = "")
						#if($this.getHasAccessRight($interceptionPointVO.id, $extraParameters, $role.name))
							#set($checked = "checked")
							#set($accessRightId = $this.getAccessRightId($interceptionPointVO.id, $extraParameters))
						#end 
						
						#if($interceptionPointVO.name == "Content.Read" || $interceptionPointVO.name == "SiteNodeVersion.Read")
							#set($anonymousRolesCount = 0)
							#foreach($anonymousRole in $anonymousPrincipal.roles)
								#if($anonymousRole.name == $role.name)
									<script type="text/javascript">
										//alert("Anonymous role found $role.name");
										roles[$anonymousRolesCount] = "${interceptionPointVO.id}_${role.name}_hasAccess";
									</script>
									#set($anonymousRolesCount = $anonymousRolesCount + 1)
								#end
							#end
						#end
																	
						<input type="hidden" name="${interceptionPointIndex}_InterceptionPointId" value="$interceptionPointVO.id">
						<input type="hidden" name="${interceptionPointVO.id}_${roleIndex}_roleName" value="$role.name">
						<input type="checkbox" id="${interceptionPointVO.id}_${role.name}_hasAccess" name="${interceptionPointVO.id}_${role.name}_hasAccess" value="true" $checked>
						
					</td>
					<td></td>
					#set($interceptionPointIndex = $interceptionPointIndex + 1)
				#end
			#end
		</tr>
		#set($roleIndex = $roleIndex + 1)
	#end
--%>
	
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>	
		<td colspan="<ww:property value="interceptionPointIndex"/>">
			<a href="javascript:confirmSave();"><img src="$ui.getString("images.managementtool.buttons.save")" width="50" height="25" border="0"></a>
			<!--<a href="javascript:saveAndExit(document.editForm, 'UpdateAccessRights!saveAndExit.action');"><img src="$ui.getString("images.managementtool.buttons.saveAndExit")" width="80" height="25" border="0"></a>-->
			<a href="$returnAddress"><img border="0" src="$ui.getString("images.managementtool.buttons.cancel")" width="50" height="25"></a>
		</td>
	</tr>			
	<tr>
		<td>&nbsp;</td>
	</tr>
</form>
	
	
<%--				
	<form name="inputForm" method="POST" action="<c:out value="${updateCalendarActionUrl}"/>">
		<input type="hidden" name="calendarId" value="<ww:property value="calendar.id"/>">
		
		<calendar:textField label="labels.internal.calendar.name" name="name" value="calendar.name" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.calendar.description" name="description" value="calendar.description" cssClass="longtextfield"/>
	    
	  	<ww:set name="owningRoles" value="calendar.owningRoles"/>
	  	<ww:set name="owningGroups" value="calendar.owningGroups"/>
			
	    <calendar:selectField label="labels.internal.calendar.roles" name="roles" multiple="true" value="infoglueRoles" selectedValueSet="#owningRoles" cssClass="listBox"/>
	    <calendar:selectField label="labels.internal.calendar.groups" name="groups" multiple="true" value="infoglueGroups" selectedValueSet="#owningGroups" cssClass="listBox"/>
	    
	    <calendar:selectField label="labels.internal.calendar.eventType" name="eventTypeId" multiple="false" value="eventTypes" selectedValue="calendar.eventType" cssClass="listBox"/>
		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.calendar.updateButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
--%>

</div>

<%@ include file="adminFooter.jsp" %>