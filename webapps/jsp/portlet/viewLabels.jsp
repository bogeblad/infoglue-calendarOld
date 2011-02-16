<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Labels" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<portlet:actionURL var="updateLabelsActionUrl">
	<portlet:param name="action" value="ViewLabels!update"/>
</portlet:actionURL>

<portlet:renderURL var="changeLanguageActionUrl">
	<portlet:param name="action" value="ViewLabels"/>
</portlet:renderURL>

<script type="text/javascript">
<!--
	function showDiv(id)
	{
		document.getElementById(id).style.visibility = 'visible';
	}

	function hideDiv(id)
	{
		document.getElementById(id).style.visibility = 'hidden';
	}
	
	function changeLanguage()
	{
		document.inputForm.action = "<c:out value="${changeLanguageActionUrl}"/>";
		document.inputForm.selectedLanguageCode.value = document.inputForm.selectedLanguageCode.value;
		document.inputForm.submit();
	}
	
	function defaultValue()
	{
		<ww:iterator value="labels" status="rowstatus">
			<ww:set name="labelKey" value="top"/>
			<ww:set name="defaultEnglishValue" value="this.getLabel(#labelKey, 'en', true, false).replaceAll('\"', '')"/>
			document.getElementById("<ww:property value="#labelKey"/>").value = "<ww:property value="#defaultEnglishValue" escape="false"/>";
		</ww:iterator>
	}
-->
</script>

<portlet:actionURL var="addLanguageUrl">
	<portlet:param name="action" value="ViewLabels!addLanguage"/>
</portlet:actionURL>

<div id="newLanguageFormLayer" style="border: 1px solid black; background-color: white; LEFT:250px; position:absolute; TOP:250px; visibility:hidden; z-index:1">
	<div id="newLanguageFormLayerPropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Add language</div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('newLanguageFormLayer');" class="white">close</a></div></div>
	<div id="PropertyBody" class="propertiesDivBody">
		<form name="newLanguageForm" action="<c:out value="${addLanguageUrl}"/>" method="POST">
			<input type="hidden" name="propertyId" value="<ww:property value="propertyId"/>">
	
			<label for="languageName">Language Code:</label>
			<input type="textfield" name="selectedLanguageCode" class="longtextfield"/>  
			<br/>
			<input type="submit" value="Save">
			<a href="javascript:hideDiv('newLanguageFormLayer');"><input type="button" value="Cancel"/></a>
		</form>
	</div>
</div>


<div class="portlet_margin">
	<form name="inputForm" method="POST" action="<c:out value="${updateLabelsActionUrl}"/>">
		<input type="hidden" name="propertyId" value="<ww:property value="propertyId"/>">

		<calendar:selectField label="'Languages'" name="'selectedLanguageCode'" multiple="false" value="locales" selectedValue="selectedLanguageCode" cssClass="listBox"/>
		<p>
		<a href="javascript:changeLanguage();">Change Language</a>
		<a href="javascript:showDiv('newLanguageFormLayer');">Add Language</a>
		<a href="javascript:defaultValue();">English values</a>
		</p>

		<ww:set name="count" value="0"/>
		<ww:iterator value="labels" status="rowstatus">
			<ww:set name="labelKey" value="top"/>
			<ww:set name="defaultEnglishValue" value="this.getLabel(#labelKey, 'en', true, false)"/>
			<ww:set name="defaultValue" value="this.getLabel(#labelKey, selectedLanguageCode, true, false)"/>
			<ww:set name="label" value="#labelKey"/>
			<ww:set name="attributeValue" value="this.getLabel(#labelKey, selectedLanguageCode, false, false, false)"/>
			
			<ww:if test="#attributeValue != null">
				<ww:set name="attributeValue" value="#attributeValue.replaceAll('\"', '')"/>			
			</ww:if>
			
			<ww:if test="#defaultEnglishValue != null && #defaultEnglishValue.length() > 80">
	 			<calendar:textAreaField label="#label" name="#labelKey" value="#attributeValue" cssClass="smalltextarea"/>
			</ww:if>
			<ww:else>
 				<calendar:textField label="#label" name="#labelKey" value="#attributeValue" cssClass="longtextfield"/>
			</ww:else>
			<p><span style="color: green; font-weight: bold;">In English:</span> <ww:property value="#defaultEnglishValue.replaceAll('\"', '')" escape="false"/></p>
		
			<ww:set name="count" value="#count + 1"/>
		</ww:iterator>		

		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.applicationSave')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>

</div>

<%@ include file="adminFooter.jsp" %>

