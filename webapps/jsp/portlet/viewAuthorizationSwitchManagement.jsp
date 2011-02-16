<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<c:set var="activeNavItem" value="AuthorizationSwitchManagement" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<div class="portlet_margin">

	<portlet:actionURL var="transferAccessActionUrl">
		<portlet:param name="action" value="ViewAuthorizationSwitchManagement"/>
	</portlet:actionURL>
	
	<form action="<c:out value="${transferAccessActionUrl}" escapeXml="false"/>" method="POST">
	<h3>Users</h3>
	<table>
	
	<ww:set name="oldUserName" value=""/>
	<ww:set name="i" value="0"/>
	<ww:iterator value="users" status="rowstatus">
	
		<ww:if test="top.name != #oldUserName">
			<tr>
				<td>
					<input type="textfield" name="<ww:property value="#i"/>_userName" value="<ww:property value="top.name"/>"/>
					<ww:property value="top.name"/>
				</td>
				<td>
					New user:<br/>
					<select name="<ww:property value="#i"/>_newUserName">
					<option value="">Choose replace name</option>
					<ww:iterator value="infoglueUsers">
						<option value="<ww:property value="top.name"/>"><ww:property value="top.firstName"/> <ww:property value="top.lastName"/></option>
					</ww:iterator>
					</select>
				</td>
			</tr>
			<ww:set name="oldUserName" value="top.name"/>
			<ww:set name="i" value="#i + 1"/>
		</ww:if>
				
	</ww:iterator>
	
	</table>
	
	<h3>Roles</h3>
	<table>
	
	<ww:set name="oldRoleName" value=""/>
	<ww:set name="i" value="0"/>
	<ww:iterator value="roles" status="rowstatus">
	
		<ww:if test="top.name != #oldRoleName">
			<tr>
				<td>
					<input type="textfield" name="<ww:property value="#i"/>_roleName" value="<ww:property value="top.name"/>"/>
					<ww:property value="top.name"/>
				</td>
				<td>
					New role:<br/>
					<select name="<ww:property value="#i"/>_newRoleName">
						<option value="">Choose replace name</option>
					<ww:iterator value="infoglueRoles">
						<option value="<ww:property value="top.name"/>"><ww:property value="top.displayName"/></option>
					</ww:iterator>
					</select>
				</td>
			</tr>
			<ww:set name="oldRoleName" value="top.name"/>
			<ww:set name="i" value="#i + 1"/>
		</ww:if>
				
	</ww:iterator>
	
	</table>

	<h3>Groups</h3>
	<table>
	
	<ww:set name="oldGroupName" value=""/>
	<ww:set name="i" value="0"/>
	<ww:iterator value="groups" status="rowstatus">
	
		<ww:if test="top.name != #oldGroupName">
			<tr>
				<td>
					<input type="textfield" name="<ww:property value="#i"/>_groupName" value="<ww:property value="top.name"/>"/>
					<ww:property value="top.name"/>
				</td>
				<td>
					New role:<br/>
					<select name="<ww:property value="#i"/>_newGroupName">
						<option value="">Choose replace name</option>
					<ww:iterator value="infoglueGroups">
						<option value="<ww:property value="top.name"/>"><ww:property value="top.displayName"/></option>
					</ww:iterator>
					</select>
				</td>
			</tr>
			<ww:set name="oldGroupName" value="top.name"/>
			<ww:set name="i" value="#i + 1"/>
		</ww:if>
				
	</ww:iterator>
	
	</table>
	
	<input type="submit" value="Transfer"/>
	</form>
</div>

<%@ include file="adminFooter.jsp" %>