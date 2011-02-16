<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="EventTypes" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<div class="portlet_margin">

	<portlet:actionURL var="updateEventTypeCategoryAttributeActionUrl">
		<portlet:param name="action" value="UpdateEventTypeCategoryAttribute"/>
	</portlet:actionURL>
	
	<form name="inputForm" method="POST" action="<c:out value="${updateEventTypeCategoryAttributeActionUrl}"/>">
		<input type="hidden" name="eventTypeCategoryAttributeId" value="<ww:property value="eventTypeCategoryAttribute.id"/>">
		
		<calendar:textField label="labels.internal.category.internalName" name="'internalName'" value="eventTypeCategoryAttribute.internalName" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.category.name" name="'name'" value="eventTypeCategoryAttribute.name" cssClass="longtextfield"/>
		<calendar:selectField label="labels.internal.eventTypeCategoryAttribute.BaseCategory" name="categoryId" multiple="false" value="categories" selectedValue="eventTypeCategoryAttribute.category.id" cssClass="listBox"/>
		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.eventType.updateButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
</div>

<%@ include file="adminFooter.jsp" %>