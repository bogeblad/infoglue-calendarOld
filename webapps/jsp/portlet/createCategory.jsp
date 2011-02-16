<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Categories" scope="page"/>
<c:set var="activeSubNavItem" value="NewCategory" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<portlet:renderURL var="createCategoryUrl">
	<portlet:param name="action" value="CreateCategory!input"/>
</portlet:renderURL>

<div class="subfunctionarea">
<span class="left"></span>	
<span class="right">
	<a href="<c:out value="${createCategoryUrl}"/>" <c:if test="${activeSubNavItem == 'NewCategory'}">class="current"</c:if> title="Skapa ny post"><ww:property value="this.getLabel('labels.internal.category.addCategory')"/></a>
</span>	
<div class="clear"></div>
</div>

<div class="portlet_margin">
	<portlet:actionURL var="createCategoryActionUrl">
		<portlet:param name="action" value="CreateCategory"/>
	</portlet:actionURL>
	
	<form name="inputForm" method="POST" action="<c:out value="${createCategoryActionUrl}"/>">
		<input type="hidden" name="parentCategoryId" value="<ww:property value="parentCategoryId"/>"/>
	
		<calendar:textField label="labels.internal.category.internalName" name="'internalName'" value="category.internalName" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.category.name" name="'name'" value="category.name" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.category.description" name="'description'" value="category.description" cssClass="longtextfield"/>
		<calendar:checkboxField label="labels.internal.category.isActive" name="'active'" valueMap="yesNoMap"/>

		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.category.createButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
</div>

<%@ include file="adminFooter.jsp" %>
