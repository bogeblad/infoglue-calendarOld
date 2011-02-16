<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Languages" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<div class="portlet_margin">

	<portlet:actionURL var="updateLanguageActionUrl">
		<portlet:param name="action" value="UpdateLanguage"/>
	</portlet:actionURL>
	
	<form name="inputForm" method="POST" action="<c:out value="${updateLanguageActionUrl}"/>">
		<input type="hidden" name="systemLanguageId" value="<ww:property value="language.id"/>">
		
		<calendar:textField label="labels.internal.language.name" name="'name'" value="language.name" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.language.isoCode" name="'isoCode'" value="language.isoCode" cssClass="longtextfield"/>
		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.language.updateButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
</div>

<%@ include file="adminFooter.jsp" %>