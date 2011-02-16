<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<portlet:actionURL var="updateContentTypeDefinitionUrl">
	<portlet:param name="action" value="UpdateContentTypeDefinition"/>
</portlet:actionURL>

<portlet:actionURL var="newAttributeUrl">
	<portlet:param name="action" value="ViewEventType!insertAttribute"/>
</portlet:actionURL>

<portlet:actionURL var="insertAttributeValidatorUrl">
	<portlet:param name="action" value="ViewEventType!insertAttributeValidator"/>
</portlet:actionURL>

<ww:set name="contentTypeDefinitionId" value="eventTypeId" scope="page"/>
<ww:set name="eventTypeId" value="eventTypeId" scope="page"/>

<style type="text/css">
<!--
/*Content type definition editor */

.moveup { 					background-image: url("$cl.getAssetUrl($cl.infoGlueComponent.contentId, "moveup")"); }
.movedown { 					background-image: url("$cl.getAssetUrl($cl.infoGlueComponent.contentId, "movedown")"); }
.checkboxIcon { 					background-image: url("$cl.getAssetUrl($cl.infoGlueComponent.contentId, "checkboxIcon")"); }
.radiobuttonIcon { 					background-image: url("$cl.getAssetUrl($cl.infoGlueComponent.contentId, "radiobuttonIcon")"); }
.selectIcon { 					background-image: url("$cl.getAssetUrl($cl.infoGlueComponent.contentId, "selectIcon")"); }
.textfieldIcon { 					background-image: url("$cl.getAssetUrl($cl.infoGlueComponent.contentId, "textfieldIcon")"); }
.textareaIcon { 					background-image: url("$cl.getAssetUrl($cl.infoGlueComponent.contentId, "textareaIcon")"); }
.hiddenIcon { 					background-image: url("$cl.getAssetUrl($cl.infoGlueComponent.contentId, "hiddenIcon")"); }

#contentTypeEditorContainer {
	width: 100%;
	margin: 0px 0px 0px 0px;
	padding: 10px 10px 10px 10px;
	border: 1px solid green;
}
#attributes {
	aborder: 1px solid red;	
}

.propertiesDiv {
	position:absolute;
	width:700px;
    background-color:#F4F4F4;
	border:1px solid #333333;
	z-index:201;
	left:10px;
	top:100px;
	height:650px;
	}

.propertiesDivHandle {
	padding:2px;
        margin: 2px;
        margin-bottom: 8px;
        width:690px;
	color:white;
	background-color:navy;
	font-family:verdana, sans-serif;
	font-size:10px;
	}

.propertiesDivLeftHandle {
	width: 150px; 
	float: left;
	color:white;
	text-align: left;
	line-height:10px;
	}

.propertiesDivRightHandle {
	color:white;
	text-align: right;
	line-height:10px;	
	}

.propertiesDivBody {
        padding: 4px;
	}

#propertyBody label {
        display: block;
		float: left;
		margin-bottom: 10px;
        padding-right: 5px;
        atext-align: right;
		width: 200px;
}

br {
	clear: left;
}

a.white {
        text-decoration:none;
        color: white;
		font-size:10px;
    }
a.white:hover {
	text-decoration:underline;
	}
a.white:active {
	font-weight: bold;
	}

.validationRow {
  	width: 25%;
  	padding:0.125em 0.75em 0.125em 0.75em;  
}

/* END */

-->
</style>

<script type="text/javascript">
<!--
	function submitNewAttribute()
	{
		if(document.editForm.inputTypeId.selectedIndex > 0)
		{
			document.editForm.action = "<c:out value="${newAttributeUrl}"/>";
			document.editForm.submit();
		}
		else
		{
			alert("You must select a input type first.");
		}
	}

	function syncDropboxes()
	{
		document.editForm.inputTypeId.selectedIndex = document.editForm.inputTypeId2.selectedIndex;
	}

	function showPropertyDiv(id)
	{
		document.getElementById(id).style.visibility = 'visible';
		document.getElementById(id).style.display = 'block';
	}

	function hidePropertyDiv(id)
	{
		document.getElementById(id).style.visibility = 'hidden';
		document.getElementById(id).style.display = 'none';
	}

	function showDiv(id)
	{
		document.getElementById(id).style.visibility = 'visible';
	}

	function hideDiv(id)
	{
		document.getElementById(id).style.visibility = 'hidden';
	}

	function changeViewLanguage()
	{

		window.location.href = "ViewEventType.action?contentTypeDefinitionId=$contentTypeDefinitionId&currentContentTypeEditorViewLanguageCode=" + document.editForm.languageCode.value;
	}
	
	function checkDisplay(value, id)
	{
		if(value == "image")
		{
			document.getElementById(id).style.display = "block";
		}
		else
		{
			document.getElementById(id).style.display = "none";
		}
	}
	
	function showAddValidatorFormDiv(attributeName)
	{
		document.newValidatorForm.attributeName.value = attributeName;
		document.newValidatorForm.attributeToExpand.value = attributeName;
		showDiv('newValidatorFormLayer');
	}
	
	/****************************
	 * Hook method to get informed when a drag starts
 	 ****************************/

	function dragStarted(object)
	{
		//alert("dragStarted:" + object.id);
		isDragged = true;
	} 

	/****************************
	 * Hook method to get informed when a drag ends
	 ****************************/
	function dragEnded(object, left, top)
	{
		//alert("dragEnded:" + object.id);
	}
	
-->
</script>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/script/componentEditor.js"></script>--%>
<script type="text/javascript" src="<%=request.getContextPath()%>/script/dom-drag.js"></script>

<div id="newValidatorFormLayer" style="border: 1px solid black; background-color: white; LEFT:250px; position:absolute; TOP:250px; visibility:hidden; z-index:1">
	<div id="newValidatorFormLayerPropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Add validation rule</div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('newValidatorFormLayer');" class="white">close</a></div></div>
	<div id="PropertyBody" class="propertiesDivBody">
		<form name="newValidatorForm" action="<c:out value="${insertAttributeValidatorUrl}"/>" method="POST">
			<input type="hidden" name="eventTypeId" value="<ww:property value="contentTypeDefinitionId"/>">
			<input type="hidden" name="contentTypeDefinitionId" value="<ww:property value="contentTypeDefinitionId"/>">
			<input type="hidden" name="attributeName" value="<ww:property value="#attribute.name"/>">
			<input type="hidden" name="attributeToExpand" value="<ww:property value="#attribute.name"/>">
	
			<label for="validatorName">Type:</label>
			<select size="1" name="validatorName" class="sitedropbox">
			    <option value="required">Required</option>
			    <option value="requiredif">Required If</option>
			    <option value="matchRegexp">Match Regexp</option>
			</select>
			<br/>
			<input type="submit" value="Save">
			<a href="javascript:hideDiv('newValidatorFormLayer');"><input type="button" value="Cancel"/></a>
		</form>
	</div>
</div>


<ww:iterator value="contentTypeAttributes" status="rowstatus">
	<ww:set name="attribute" value="top"/>
	<ww:set name="attributeName" value="#attribute.name" scope="page"/>
	<ww:set name="inputType" value="attribute.inputType"/>
	<ww:set name="title" value="top.getContentTypeAttribute('title').getContentTypeAttributeParameterValue().getLocalizedValueByLanguageCode('label', currentContentTypeEditorViewLanguageCode)" scope="page"/>

	<ww:iterator value="#attribute.validators" status="rowstatus">
		<ww:set name="validator" value="top"/>
		
		<portlet:actionURL var="updateAttributeValidatorArgumentsUrl">
			<portlet:param name="action" value="ViewEventType!updateAttributeValidatorArguments"/>
		</portlet:actionURL>
		
		<div id="<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>_layer" style="border: 1px solid black; background-color: white; LEFT:100px; position:absolute; TOP:250px; visibility:hidden; z-index:1">
			<div id="<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>PropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Validator</div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>_layer');" class="white">close</a></div></div>
			<div id="PropertyBody" class="propertiesDivBody">
				<form name="<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>ArgumentsForm" action="<c:out value="${updateAttributeValidatorArgumentsUrl}"/>" method="POST">
					<input type="hidden" name="contentTypeDefinitionId" value="<ww:property value="eventTypeId"/>">
					<input type="hidden" name="eventTypeId" value="<ww:property value="eventTypeId"/>">
					<input type="hidden" name="attributeName" value="<ww:property value="#attribute.name"/>">
					<input type="hidden" name="attributeValidatorName" value="<ww:property value="#validator.name"/>">
					<!--
					<input type="hidden" name="attributeParameterValueLocale" value="$!currentContentTypeEditorViewLanguageCode">
					<input type="hidden" name="currentContentTypeEditorViewLanguageCode" value="$!currentContentTypeEditorViewLanguageCode">
					-->
					<input type="hidden" name="attributeToExpand" value="<ww:property value="#attribute.name"/>">
	
					<ww:set name="index" value="0"/>
					<ww:iterator value="#validator.arguments.keySet()" status="rowstatus">
						<ww:set name="key" value="top"/>
						<input type="hidden" name="<ww:property value="#index"/>_argumentName" value="<ww:property value="#key"/>">
						
						<label for="<ww:property value="#index"/>_argumentValue"><ww:property value="#key"/>:</label>
						<input type="textfield" name="<ww:property value="#index"/>_argumentValue" value="<ww:property value="#validator.arguments.get(#key)"/>" class="normaltextfield">
				
						<ww:set name="index" value="#index + 1"/>
					</ww:iterator>
					<ww:if test="#index == 0">
						<label for="no_argumentValue">No properties for validation rule</label>
						<input type="hidden" name="no_argumentValue">
					</ww:if>
					<br/>
					<input type="submit" value="Save">
					<a href="javascript:hideDiv('<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>_layer');"><input type="button" value="Cancel"/></a>
	
				</form>
			</div>
		</div>
	</ww:iterator>

	<ww:iterator value="#attribute.contentTypeAttributeParameters" status="rowstatus">
		<ww:set name="parameter" value="top"/>
		<ww:set name="values" value="#parameter.value.contentTypeAttributeParameterValues"/>

		<ww:iterator value="#values" status="rowstatus">
			<ww:set name="value" value="top"/>

			<ww:if test="#parameter.value.type == 1">

				<portlet:actionURL var="updateAttributeParameterValueUrl">
					<portlet:param name="action" value="ViewEventType!updateAttributeParameterValue"/>
				</portlet:actionURL>
			
				<div id="<ww:property value="#attribute.name"/><ww:property value="#parameter.key"/><ww:property value="#value.key"/>PropertyLayer" style="border: 1px solid black; background-color: white; LEFT:250px; position:absolute; TOP:250px; visibility:hidden; z-index:1">

					<div id="<ww:property value="#attribute.name"/>_<ww:property value="#parameter.key"/>_<ww:property value="#value.key"/>PropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Values</div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('<ww:property value="#attribute.name"/><ww:property value="#parameter.key"/><ww:property value="#value.key"/>PropertyLayer');" class="white">close</a></div></div>
					<div id="PropertyBody" class="propertiesDivBody">
						<form name="<ww:property value="#attribute.name"/>_<ww:property value="#parameter.key"/>ArgumentsForm" action="<c:out value="${updateAttributeParameterValueUrl}"/>" method="POST">
							<input type="hidden" name="contentTypeDefinitionId" value="<ww:property value="eventTypeId"/>">
							<input type="hidden" name="eventTypeId" value="<ww:property value="eventTypeId"/>">
							<input type="hidden" name="attributeName" value="<ww:property value="#attribute.name"/>">
							<input type="hidden" name="attributeParameterId" value="<ww:property value="#parameter.key"/>">
							<input type="hidden" name="attributeParameterValueId" value="<ww:property value="#value.key"/>">
							<input type="hidden" name="attributeParameterValueLocale" value="<ww:property value="currentContentTypeEditorViewLanguageCode"/>">
							<!--
							<input type="hidden" name="attributeParameterValueLocale" value="$!currentContentTypeEditorViewLanguageCode">
							<input type="hidden" name="currentContentTypeEditorViewLanguageCode" value="$!currentContentTypeEditorViewLanguageCode">
							-->
							<input type="hidden" name="attributeToExpand" value="<ww:property value="#attribute.name"/>">
			
							<label for="attributeParameterValueLabel">Label:</label>
							<input type="textfield" name="attributeParameterValueLabel" value="<ww:property value="#value.value.getLocalizedValueByLanguageCode('label', currentContentTypeEditorViewLanguageCode)"/>" class="normaltextfield">
							<br/>
		
							<label for="newAttributeParameterValueId">Internal value:</label>
							<input type="textfield" name="newAttributeParameterValueId" value="<ww:property value="#value.value.getLocalizedValueByLanguageCode('id', currentContentTypeEditorViewLanguageCode)"/>" class="normaltextfield">
							<br/>
							<input type="submit" value="Save">
							<a href="javascript:hideDiv('<ww:property value="#attribute.name"/><ww:property value="#parameter.key"/><ww:property value="#value.key"/>PropertyLayer');"><input type="button" value="Cancel"/></a>
			
						</form>
					</div>
				</div>

			</ww:if>
		</ww:iterator>
	</ww:iterator>


<%--
<c:if test="${activatedName == ''">
	<c:set var="attributeToExpand" value="${activatedName[0]}"/> 
</c:if>
--%>

<c:set var="visibility" value="hidden"/>
<ww:if test="attributeToExpand == #attribute.name">
	<c:set var="visibility" value="visible"/>
</ww:if>
<c:set var="display" value="none"/>
<ww:if test="attributeToExpand == #attribute.name">
	<c:set var="display" value="block"/>
</ww:if>

<portlet:actionURL var="updateAttributePropertyUrl">
	<portlet:param name="action" value="ViewEventType!updateAttribute"/>
</portlet:actionURL>
<div id="<ww:property value="#attribute.name"/>PropertyLayer" class="propertiesDiv" style="border: 1px solid black; background-color: white; position:absolute; left:20px; top:20px; display:<c:out value="${display}"/>; visibility:<c:out value="${visibility}"/>; z-index:0; overflow: auto;">
	<div id="<ww:property value="#attribute.name"/>PropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Properties for attribute <ww:property value="#attribute.name"/></div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('<ww:property value="#attribute.name"/>PropertyLayer');" class="white">close</a></div></div>
	<div id="PropertyBody" class="propertiesDivBody">
	<form id="<ww:property value="#attribute.name"/>PropertiesForm" name="<ww:property value="#attribute.name"/>PropertiesForm" action="<c:out value="${updateAttributePropertyUrl}"/>" method="POST">
		<input type="hidden" name="eventTypeId" value="<c:out value="${eventTypeId}"/>">
		<input type="hidden" name="contentTypeDefinitionId" value="<c:out value="${contentTypeDefinitionId}"/>">
		<input type="hidden" name="currentContentTypeEditorViewLanguageCode" value="<ww:property value="currentContentTypeEditorViewLanguageCode"/>">
		<input type="hidden" name="attributeName" value="<ww:property value="#attribute.name"/>">
		<input type="hidden" name="attributeToExpand" value="<ww:property value="#attribute.name"/>">
		
	    <div class="actionrow">
			<label for="newAttributeName">Name</label>
			<input type="textfield" name="newAttributeName" value="<ww:property value="#attribute.name"/>" class="longtextfield"><br/>
	
			<calendar:selectField label="'Type'" name="'inputTypeId'" multiple="false" value="attributeTypes" selectedValue="inputType" headerItem="Choose element type" cssClass="listBox" skipContainer="true" skipLineBreak="true"/>
		</div>	
	<br/>
	
	<div class="columnlabelarea">
		<div class="columnShort"><p>Validation</p></div>
		<div class="columnLong"><p>&nbsp;</p></div>
		<div class="columnEnd"><p>&nbsp;</p></div>
		<div class="clear"></div>
	</div>
	
	<ww:set name="index" value="0"/>
	<ww:iterator value="#attribute.validators" status="rowstatus">
		<ww:set name="validator" value="top"/>
		<ww:set name="contentTypeDefinitionId" value="eventTypeId" scope="page"/>
		<ww:set name="eventTypeId" value="eventTypeId" scope="page"/>
		<ww:set name="attributeName" value="#attribute.name" scope="page"/>
		<ww:set name="validatorName" value="#validator.name" scope="page"/>
		
		<portlet:actionURL var="deleteAttributeValidatorUrl">
			<portlet:param name="action" value="ViewEventType!deleteAttributeValidator"/>
			<calendar:evalParam name="contentTypeDefinitionId" value="${contentTypeDefinitionId}"/>
			<calendar:evalParam name="eventTypeId" value="${contentTypeDefinitionId}"/>
			<calendar:evalParam name="attributeName" value="${attributeName}"/>
			<calendar:evalParam name="attributeValidatorName" value="${validatorName}"/>
			<calendar:evalParam name="attributeToExpand" value="${attributeName}"/>
		</portlet:actionURL>
				
		<ww:if test="#rowstatus.odd == true">
	    	<div class="oddrow">
	    </ww:if>
	    <ww:else>
			<div class="evenrow">
	    </ww:else>
		   	<div class="columnShort"><p><ww:property value="#validator.name"/></p></div>
		   	<div class="columnEnd">
				<a href="<c:out value="${deleteAttributeValidatorUrl}"/>" class="delete"></a>
				<a href="javascript:showDiv('<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>_layer');" class="edit"></a>
			</div>
			<div class="clear"></div>
		</div
		<ww:set name="index" value="#index + 1"/>
	</ww:iterator>

	<ww:if test="#index == 0">
		<div class="actionrow">
			<div class="columnLong"><p>No validation rules assigned</p></div>
			<div class="clear"></div>
		</div>
	</ww:if>

	<div class="actionrow">
		<div class="columnLong"><p><a href="javascript:showAddValidatorFormDiv('<ww:property value="#attribute.name"/>');">Add new validation rule</a></p></div>
		<div class="clear"></div>
	</div>
	
	<br/>
	
	<div class="columnlabelarea">
		<div class="columnShort"><p>Extra parameters</p></div>
		<div class="columnLong"><p>&nbsp;</p></div>
		<div class="columnEnd"><p>&nbsp;</p></div>
		<div class="clear"></div>
	</div>
	
	<ww:iterator value="#attribute.contentTypeAttributeParameters" status="rowstatus">
		<ww:set name="parameter" value="top"/>
		<ww:set name="parameterKey" value="#parameter.key" scope="page"/>
		
	    <div class="actionrow">
	       		<input type="hidden" name="parameterNames" value="<ww:property value="#parameter.key"/>">
				
				<ww:set name="values" value="#parameter.value.contentTypeAttributeParameterValues"/>

				<ww:if test="#parameter.value.type == 0">
					<label for="<ww:property value="#parameter.key"/>"><ww:property value="#parameter.key"/>:</label>
					<input type="textfield" name="<ww:property value="#parameter.key"/>" value="<ww:property value="#parameter.value.getContentTypeAttributeParameterValue().getLocalizedValueByLanguageCode('label', currentContentTypeEditorViewLanguageCode)"/>" class="longtextfield">
					<div class="clear"></div>
				</ww:if>
				<ww:else>
					
					<div class="columnlabelarea">
						<div class="columnShort"><p><ww:property value="#parameter.key"/></p></div>
						<div class="columnShort"><p>Label</p></div>
						<div class="columnLong"><p>Internal name</p></div>
						<div class="columnEnd"><p>&nbsp;</p></div>
						<div class="clear"></div>
					</div>
					
					<ww:iterator value="#values" status="rowstatus">
						<ww:set name="value" value="top"/>
						<ww:set name="valueId" value="#value.key" scope="page"/>
	
						<portlet:actionURL var="deleteAttributeParameterValueUrl">
							<portlet:param name="action" value="ViewEventType!deleteAttributeParameterValue"/>
							<calendar:evalParam name="contentTypeDefinitionId" value="${contentTypeDefinitionId}"/>
							<calendar:evalParam name="eventTypeId" value="${contentTypeDefinitionId}"/>
							<calendar:evalParam name="title" value="${title}"/>
							<calendar:evalParam name="attributeName" value="${attributeName}"/>
							<calendar:evalParam name="attributeParameterId" value="${parameterKey}"/>
							<calendar:evalParam name="attributeParameterValueId" value="${valueId}"/>
							<calendar:evalParam name="attributeToExpand" value="${attributeName}"/>
						</portlet:actionURL>
							
						<ww:if test="#rowstatus.odd == true">
					    	<div class="oddrow">
					    </ww:if>
					    <ww:else>
							<div class="evenrow">
					    </ww:else>
						   	<div class="columnShort"><p>&nbsp;</p></div>
						   	<div class="columnShort"><p><ww:property value="#value.value.getLocalizedValueByLanguageCode('label', currentContentTypeEditorViewLanguageCode)"/></p></div>
						   	<div class="columnLong"><p><ww:property value="#value.value.getLocalizedValueByLanguageCode('id', currentContentTypeEditorViewLanguageCode)"/></p></div>
						   	<div class="columnEnd">
								<a href="<c:out value="${deleteAttributeParameterValueUrl}"/>" class="delete"></a>
								<a href="javascript:showDiv('<ww:property value="#attribute.name"/><ww:property value="#parameter.key"/><ww:property value="#value.key"/>PropertyLayer');" class="edit"></a>
							</div>
							<div class="clear"></div>
						</div
					</ww:iterator>
					
					<portlet:actionURL var="insertAttributeParameterValueUrl">
						<portlet:param name="action" value="ViewEventType!insertAttributeParameterValue"/>
						<calendar:evalParam name="contentTypeDefinitionId" value="${contentTypeDefinitionId}"/>
						<calendar:evalParam name="eventTypeId" value="${contentTypeDefinitionId}"/>
						<calendar:evalParam name="title" value="${title}"/>
						<calendar:evalParam name="attributeName" value="${attributeName}"/>
						<calendar:evalParam name="attributeParameterId" value="${parameterKey}"/>
						<calendar:evalParam name="attributeToExpand" value="${attributeName}"/>
					</portlet:actionURL>
					
					<div class="actionrow">
						<div class="columnLong"><p><a href="<c:out value="${insertAttributeParameterValueUrl}"/>">Add value</a></p></div>
						<div class="clear"></div>
					</div>
				</ww:else>
		</div>
	</ww:iterator>

	<br/>
	<div class="actionrow">
		<input type="submit" value="Save"/>
		<a href="javascript:hideDiv('<ww:property value="#attribute.name"/>PropertyLayer');"><input type="button" value="Cancel"/></a>
	</div>
	</form>
	</div>
</div>

<script type="text/javascript">		
	var theHandle = document.getElementById("<ww:property value="#attribute.name"/>PropertyHandle");		
	var theRoot   = document.getElementById("<ww:property value="#attribute.name"/>PropertyLayer");		
	Drag.init(theHandle, theRoot, 50, 50, 0, 1000);    
	floatDiv("<ww:property value="#attribute.name"/>PropertyLayer", 50, 50).flt();
</script>

</ww:iterator>


<form name="editForm" method="POST" action="<c:out value="${updateContentTypeDefinitionUrl}"/>">
<input type="hidden" name="eventTypeId" value="<ww:property value="contentTypeDefinitionId"/>">
<input type="hidden" name="contentTypeDefinitionId" value="<ww:property value="contentTypeDefinitionId"/>">
<input type="hidden" name="currentContentTypeEditorViewLanguageCode" value="<ww:property value="${currentContentTypeEditorViewLanguageCode}"/>">
<input type="hidden" name="schemaValue" value="$formatter.escapeHTML($!schemaValue)">
<div id="menu">

<div class="columnlabelarea">
	<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.eventType.attributes')"/></p></div>
	<div class="columnLong">
		<select size="1" name="inputTypeId" class="sitedropbox">
		    <option value="" selected>Choose element type</option>
		    <!--<option value="label">Label</option>-->
		    <option value="textfield">TextField</option>
		    <option value="textarea">TextArea</option>
		    <option value="checkbox">CheckBox</option>
		    <option value="radiobutton">RadioButton</option>
		    <option value="select">SelectBox</option>
		    <option value="hidden">Hidden</option>
		    <!--<option value="password">Password</option>-->
		    <!--<option value="image">SubmitImage</option>-->
		    <!--<option value="submit">SubmitButton</option>-->
		    <!--<option value="clear">ClearButton</option>-->
		</select>
		<a href="javascript:submitNewAttribute();"><input type="button" value="Add attribute" style=""/></a>
	</div>
	<div class="columnShort">
		<p>
			<a href="javascript:showDiv('simpleEditor');"><ww:property value="this.getLabel('labels.internal.eventType.simple')"/></a>
		</p>
	</div>
	<div class="columnEnd">
		<select onChange="javascript:changeViewLanguage();" size="1" name="languageCode" class="sitedropbox">
		    <option value="">default</option>
		    <%-- 
			#foreach ($languageVO in $availableLanguages)
				#if($languageVO.languageCode == $!currentContentTypeEditorViewLanguageCode)
				<option value="$languageVO.getLanguageCode()" selected>$languageVO</option>
				#else
				<option value="$languageVO.getLanguageCode()">$languageVO</option>
				#end
			#end
			--%>
		</select>
	
	</div>
	<div class="clear"></div>
</div>

<div id="attributes">
	<c:set var="count" value="0"/>
	<ww:iterator value="contentTypeAttributes" status="rowstatus">
		<ww:set name="attribute" value="top" scope="page"/>
		<ww:set name="attribute" value="top"/>
		<ww:set name="title" value="top.getContentTypeAttribute('title').getContentTypeAttributeParameterValue().getLocalizedValueByLanguageCode('label', currentContentTypeEditorViewLanguageCode)" scope="page"/>
		
		<portlet:actionURL var="deleteAttributeUrl">
			<portlet:param name="action" value="ViewEventType!deleteAttribute"/>
			<calendar:evalParam name="eventTypeId" value="${contentTypeDefinitionId}"/>
			<calendar:evalParam name="title" value="${title}"/>
			<calendar:evalParam name="attributeName" value="${attribute.name}"/>
		</portlet:actionURL>
		
		<ww:if test="#rowstatus.odd == true">
	    	<div class="oddrow">
	    </ww:if>
	    <ww:else>
			<div class="evenrow">
	    </ww:else>

		   	<div class="columnShort">
				<a href="ViewEventType!moveAttributeUp.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&attributeName=$attribute.name" class="moveup"></a>
				<a href="ViewEventType!moveAttributeDown.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&attributeName=$attribute.name" class="moveDown"></a>
				<a href="#" title="<c:out value="${attribute.inputType}"/>" class="<c:out value="${attribute.inputType}"/>Icon"></a>
			</div
			<div class="columnLong">
				<a name="<ww:property value="#attribute.name"/>" href="javascript:showPropertyDiv('<ww:property value="#attribute.name"/>PropertyLayer');">
				<ww:property value="#attribute.name"/> (<c:out value="${title}"/>) of type <c:out value="${attribute.inputType}"/></a>
			</div>
			<div class="columnEnd">
				<a href="<c:out value="${deleteAttributeUrl}"/>" class="delete"></a>
				<a href="javascript:showPropertyDiv('<ww:property value="#attribute.name"/>PropertyLayer');" class="edit"></a>
			</div>
			<div class="clear"></div>
		</div>
		<ww:set name="count" value="${count + 1)"/>
	</ww:iterator>
</div>
<div id="menu">
	<ww:if test="count > 15">

		<select size="1" name="inputTypeId2" onChange="syncDropboxes();" class="sitedropbox">
		    <option value="" selected>Choose element type</option>
		    <!--<option value="label">Label</option>-->
		    <option value="textfield">TextField</option>
		    <option value="textarea">TextArea</option>
		    <option value="checkbox">CheckBox</option>
		    <option value="radiobutton">RadioButton</option>
		    <option value="select">SelectBox</option>
		    <option value="hidden">Hidden</option>
		    <!--<option value="password">Password</option>-->
		    <!--<option value="image">SubmitImage</option>-->
		    <!--<option value="submit">SubmitButton</option>-->
		    <!--<option value="clear">ClearButton</option>-->
		</select>

		<a href="javascript:submitNewAttribute();"><input type="button" value="Add attribute" style=""/></a>

	</ww:if>
</div>

</form>
</div>

<!-- HERE IS THE SIMPLE EDITOR DIV -->
<div id="simpleEditor" style="visibility: hidden; position: absolute; top: 200px; left: 20px; width: 90%;">
<form>
<textarea name="schemaValue" style="width: 100%; height: 500px;"><ww:property value="contentTypeDefinition.schemaValue"/></textarea>
<input type="submit" value="Save"/>
</form>
</div>

<script type="text/javascript">
	#if($activatedName.size() > 0)
		document.location.href = document.location.href + "#$activatedName.get(0)"; 
		showDiv("${activatedName.get(0)}PropertyLayer");
	#end
</script>