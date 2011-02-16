<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Settings" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

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
	
	function changeVariation()
	{
		document.variationForm.selectedVariationCode.value = document.inputForm.selectedVariationCode.value;
		document.variationForm.submit();
	}
	
	function defaultValue()
	{
		<ww:iterator value="settings" status="rowstatus">
			<ww:set name="labelKey" value="top"/>
			<ww:set name="defaultValue" value="this.getVisualFormatter().escapeForAdvancedJavascripts(this.getSetting(#labelKey, true, false))"/>
			document.getElementById("<ww:property value="#labelKey"/>").value = "<ww:property value="#defaultValue"/>";
		</ww:iterator>				
	}
-->
</script>

<portlet:actionURL var="addVariationUrl">
	<portlet:param name="action" value="ViewSettings!addVariation"/>
</portlet:actionURL>

<div id="newVariationFormLayer" style="border: 1px solid black; background-color: white; LEFT:250px; position:absolute; TOP:250px; visibility:hidden; z-index:1">
	<div id="newVariationFormLayerPropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Add variation</div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('newVariationFormLayer');" class="white">close</a></div></div>
	<div id="PropertyBody" class="propertiesDivBody">
		<form name="newVariationForm" action="<c:out value="${addVariationUrl}"/>" method="POST">
			<input type="hidden" name="propertyId" value="<ww:property value="propertyId"/>">
	
			<label for="variationName">Variation Code:</label>
			<input type="textfield" name="selectedVariationCode" class="longtextfield"/>  
			<br/>
			<input type="submit" value="Save">
			<a href="javascript:hideDiv('newVariationFormLayer');"><input type="button" value="Cancel"/></a>
		</form>
	</div>
</div>


<div class="portlet_margin">

	<portlet:actionURL var="updateSettingsActionUrl">
		<portlet:param name="action" value="ViewSettings!update"/>
	</portlet:actionURL>

	<portlet:renderURL var="changeVariationActionUrl">
		<portlet:param name="action" value="ViewSettings"/>
	</portlet:renderURL>

	<form name="variationForm" method="POST" action="<c:out value="${changeVariationActionUrl}"/>">
		<input type="hidden" name="selectedVariationCode" value="<ww:property value="selectedVariationCode"/>">
	</form>

	<form name="inputForm" method="POST" action="<c:out value="${updateSettingsActionUrl}"/>">
		<input type="hidden" name="propertyId" value="<ww:property value="propertyId"/>">
		
		<calendar:selectField label="'Variations'" name="'selectedVariationCode'" multiple="false" value="locales" selectedValue="selectedVariationCode" cssClass="listBox"/>
		<p>
		<a href="javascript:changeVariation();">Change Variation</a>
		<a href="javascript:showDiv('newVariationFormLayer');">Add Variation</a>
		<a href="javascript:defaultValue();">English values</a>
		</p>
		
		<ww:set name="count" value="0"/>
		<ww:iterator value="settings" status="rowstatus">
			<ww:set name="labelKey" value="top"/>
			<ww:set name="defaultValue" value="this.getSetting(#labelKey, true, false)"/>
			<ww:set name="label" value="#labelKey"/>
			<ww:set name="attributeValue" value="this.getSetting(#labelKey, selectedVariationId, false, false)"/>

			<ww:if test="#attributeValue != null && #attributeValue.length() > 80">
	 			<calendar:textAreaField label="#label" name="#labelKey" value="#attributeValue" cssClass="smalltextarea"/>
			</ww:if>
			<ww:else>
 				<calendar:textField label="#label" name="#labelKey" value="#attributeValue" cssClass="longtextfield"/>
			</ww:else>
			<p><span style="color: green; font-weight: bold;">In English:</span> <ww:property value="#defaultEnglishValue"/></p>
		
			<ww:set name="count" value="#count + 1"/>
		</ww:iterator>		

		<div style="height:10px"></div>
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.applicationSave')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>

</div>

<%@ include file="adminFooter.jsp" %>

