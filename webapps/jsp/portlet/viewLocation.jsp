<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Locations" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<ww:set name="locationId" value="locationId" scope="page"/>
<ww:set name="language" value="language"/>
<ww:set name="language" value="language" scope="page"/>

<script type="text/javascript">
	
	function setLocalizedValueAndSubmit()
	{
		var languageCode = "<ww:property value="#language.isoCode"/>";
		var name = document.inputForm.name.value;
		var localizedName = document.inputForm.localizedName.value;
		var newName = name;
		//alert("name:" + name);
		//alert("localizedName:" + localizedName);
		//alert("languageCode:" + languageCode);
		var startIndex = name.indexOf(languageCode + "=");
		if(startIndex > -1) 
		{
    		var nextEQIndex = name.indexOf("=", startIndex + languageCode.length + 1);
    		if(nextEQIndex > -1)
    		{
    			endIndex = name.lastIndexOf(",", nextEQIndex);
        	}
    		else
    		{
    			endIndex = -1;
    		}
			
			if(endIndex > -1)
				newName = name.substring(0, startIndex) + languageCode + "=" + localizedName + name.substring(endIndex);				
			else
				newName = name.substring(0, startIndex) + languageCode + "=" + localizedName;
		}
		else
		{
			if(name.indexOf("=") > -1)
				newName = newName + "," + languageCode + "=" + localizedName;
			else
				newName = languageCode + "=" + localizedName;
		}
		
		document.inputForm.name.value = newName;
		document.inputForm.submit();
	}
	
</script>

<br/>

<ul class="languagesTabs">
	<ww:iterator value="availableLanguages" status="rowstatus">
		<ww:set name="currentLanguageId" value="top.id"/>
		<ww:set name="currentLanguageId" value="top.id" scope="page"/>
		
		<portlet:renderURL var="viewLocationUrl">
			<portlet:param name="action" value="ViewLocation"/>
			<calendar:evalParam name="locationId" value="${locationId}"/>
			<calendar:evalParam name="systemLanguageId" value="${currentLanguageId}"/>
		</portlet:renderURL>
			
		<c:choose>
			<c:when test="${systemLanguageId == currentLanguageId || currentLanguageId == language.id}">
				<c:set var="cssClass" value="activeTab"/>
			</c:when>
			<c:otherwise>
				<c:set var="cssClass" value=""/>
			</c:otherwise>
		</c:choose>		
		<li class="<c:out value="${cssClass}"/>">
			<a href="<c:out value="${viewLocationUrl}"/>"><ww:property value="top.name"/></a>
		</li>
		
	</ww:iterator>
</ul>

<div class="portlet_margin">

	<portlet:actionURL var="updateLocationActionUrl">
		<portlet:param name="action" value="UpdateLocation"/>
	</portlet:actionURL>
	
	<form name="inputForm" method="POST" action="<c:out value="${updateLocationActionUrl}"/>">
		<input type="hidden" name="locationId" value="<ww:property value="location.id"/>">
		<input type="hidden" name="systemLanguageId" value="<ww:property value="systemLanguageId"/>">
		<input type="hidden" name="name" value="<ww:property value="location.name"/>">
		
		<calendar:textField label="labels.internal.category.name" name="'localizedName'" value="location.getLocalizedName(#language.isoCode, 'sv')" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.category.description" name="'description'" value="location.description" cssClass="longtextfield"/>
		<div style="height:10px"></div>
		<input type="button" value="<ww:property value="this.getLabel('labels.internal.location.updateButton')"/>" class="button" onclick="setLocalizedValueAndSubmit();"/>
		<!-- 
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.location.updateButton')"/>" class="button">
		 -->
		 <input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
</div>

<%@ include file="adminFooter.jsp" %>