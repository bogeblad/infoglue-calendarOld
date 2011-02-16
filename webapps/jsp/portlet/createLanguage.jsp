<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Languages" scope="page"/>
<c:set var="activeSubNavItem" value="NewLanguage" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<portlet:renderURL var="createLanguageUrl">
	<portlet:param name="action" value="CreateLanguage!input"/>
</portlet:renderURL>

<div class="subfunctionarea">
<span class="left"></span>	
<span class="right">
	<a href="<c:out value="${createLanguageUrl}"/>" <c:if test="${activeSubNavItem == 'NewLanguage'}">class="current"</c:if> title="Skapa ny post"><ww:property value="this.getLabel('labels.internal.language.addLanguage')"/></a>
</span>	
<div class="clear"></div>
</div>

<div class="portlet_margin">
	<portlet:actionURL var="createLanguageActionUrl">
		<portlet:param name="action" value="CreateLanguage"/>
	</portlet:actionURL>
	
	<ww:property value="this.getLabel('labels.internal.language.createButton')"/>
	
	<form name="inputForm" method="POST" action="<c:out value="${createLanguageActionUrl}"/>">

		<calendar:textField label="labels.internal.language.name" name="'name'" value="name" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.language.isoCode" name="'isoCode'" value="isoCode" cssClass="longtextfield"/>
	
		<div style="height:10px"></div>
		
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.language.createButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
</div>

<%@ include file="adminFooter.jsp" %>
