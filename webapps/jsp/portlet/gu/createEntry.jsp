<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<portlet:actionURL var="createEntryActionUrl">
	<portlet:param name="action" value="CreateEntry!publicGU"/>
</portlet:actionURL>

<ww:set name="event" value="event"/>
<ww:set name="eventVersion" value="this.getEventVersion('#event')"/>

<div class="calendar">
	
	<form name="inputForm" method="POST" action="<c:out value="${createEntryActionUrl}"/>">
		<input type="hidden" name="eventId" value="<ww:property value="eventId"/>">
		<input type="hidden" name="languageCode" value="<ww:property value="languageCode"/>">
		<input type="hidden" name="returnAddress" value="<ww:property value="returnAddress"/>">
			
		<h1><ww:property value="this.getLabel('labels.internal.entry.createNewEntry')"/></h1>
		<h3>"<ww:property value="#eventVersion.name"/>"</h3>
		
		<calendar:textField label="labels.internal.entry.firstName" name="'firstName'" value="entry.firstName" required="true" labelCssClass="label" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.entry.lastName" name="'lastName'" value="entry.lastName" required="true" labelCssClass="label" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.entry.email" name="'email'" value="entry.email" required="true" labelCssClass="label" cssClass="longtextfield"/>
		
		<ww:if test="this.isActiveEntryField('organisation')">
			<calendar:textField label="labels.internal.entry.organisation" name="'organisation'" value="entry.organisation" labelCssClass="label" cssClass="longtextfield"/>
		</ww:if>
		
		<ww:if test="this.isActiveEntryField('address')">
			<calendar:textField label="labels.internal.entry.address" name="'address'" value="entry.address" labelCssClass="label" cssClass="longtextfield"/>
		</ww:if>
		
		<ww:if test="this.isActiveEntryField('zipcode')">
			<calendar:textField label="labels.internal.entry.zipcode" name="'zipcode'" value="entry.zipcode" labelCssClass="label" cssClass="shorttextfield"/>
		</ww:if>
		
		<ww:if test="this.isActiveEntryField('city')">
		<calendar:textField label="labels.internal.entry.city" name="'city'" value="entry.city" labelCssClass="label" cssClass="longtextfield"/>
		</ww:if>
		
		<ww:if test="this.isActiveEntryField('phone')">
			<calendar:textField label="labels.internal.entry.phone" name="'phone'" value="entry.phone" labelCssClass="label" cssClass="shorttextfield"/>
		</ww:if>
		
		<ww:if test="this.isActiveEntryField('fax')">
			<calendar:textField label="labels.internal.entry.fax" name="'fax'" value="entry.fax" labelCssClass="label" cssClass="shorttextfield"/>
		</ww:if>
		
		<ww:if test="this.isActiveEntryField('message')">
			<calendar:textAreaField label="labels.internal.entry.message" name="message" value="entry.message" labelCssClass="label" cssClass="fieldfullwidth"/>
		</ww:if>

		<calendar:gapcha id="catpchaImageUrl" textVariableName="catpchaText" numberOfCharacters="4" fontName="Arial" fontSize="24" fontStyle="0" fgColor="10:10:10:255" bgColor="255:255:255:255" padTop="4" padBottom="4" renderWidth="107" padLeft="5" twirlAngle="0.2" marbleXScale="0.8"  marbleYScale="0.8" marbleTurbulence="0.5" marbleAmount="1.2"/>
		
		<p>
			<input type="hidden" name="captchaTextVariableName" value="<c:out value="${catpchaText}" escapeXml="false"/>"/>
			<calendar:textField label="labels.internal.entry.captcha" name="'captcha'" value="entry.captcha" required="true" cssClass="shorttextfield"/>
			<img src="<c:out value="${catpchaImageUrl}" escapeXml="false"/>" class="captchaImg"/>
		</p>

		<ww:set name="count" value="0"/>
		<ww:iterator value="attributes" status="rowstatus">
			<ww:set name="attribute" value="top"/>
			<ww:set name="title" value="top.getContentTypeAttribute('title').getContentTypeAttributeParameterValue().getLocalizedValueByLanguageCode('label', currentContentTypeEditorViewLanguageCode)" scope="page"/>
			<ww:set name="attributeName" value="this.concat('attribute_', top.name)"/>
			<ww:set name="attributeValue" value="this.getAttributeValue(#errorEntry.attributes, top.name)"/>

			<c:set var="required" value="false"/>
			<ww:iterator value="#attribute.validators" status="rowstatus">
				<ww:set name="validator" value="top"/>
				<ww:set name="validatorName" value="#validator.name"/>
				<ww:if test="#validatorName == 'required'">
					<c:set var="required" value="true"/>
				</ww:if>
			</ww:iterator>

			<input type="hidden" name="attributeName_<ww:property value="#count"/>" value="attribute_<ww:property value="top.name"/>"/>
			
			<ww:if test="#attribute.inputType == 'textfield'">
				<calendar:textField label="${title}" name="#attributeName" value="#attributeValue" required="${required}" cssClass="longtextfield"/>
			</ww:if>		

			<ww:if test="#attribute.inputType == 'textarea'">
				<calendar:textAreaField label="${title}" name="#attributeName" value="#attributeValue" required="${required}" cssClass="smalltextarea"/>
			</ww:if>		

			<ww:if test="#attribute.inputType == 'select'">
				<ww:set name="attributeValues" value="#attributeValue"/>
				<ww:if test="#attributeValue != null">
					<ww:set name="attributeValues" value="#attributeValue.split(',')"/>
				</ww:if>
				<calendar:selectField label="${title}" name="#attributeName" multiple="true" value="#attribute.contentTypeAttributeParameterValues" selectedValues="#attributeValues" required="${required}" cssClass="listBox"/>
			</ww:if>		

			<ww:if test="#attribute.inputType == 'radiobutton'">
				<calendar:radioButtonField label="${title}" name="#attributeName" valueMap="#attribute.contentTypeAttributeParameterValuesAsMap" selectedValue="#attributeValue" required="${required}"/>
			</ww:if>		

			<ww:if test="#attribute.inputType == 'checkbox'">
				<ww:set name="attributeValues" value="#attributeValue"/>
				<ww:if test="#attributeValue != null">
					<ww:set name="attributeValues" value="#attributeValue.split(',')"/>
				</ww:if>
				<calendar:checkboxField label="${title}" name="#attributeName" valueMap="#attribute.contentTypeAttributeParameterValuesAsMap" selectedValues="#attributeValues" required="${required}"/>
			</ww:if>		

			<ww:if test="#attribute.inputType == 'hidden'">
				<calendar:hiddenField name="#attributeName" value="#attributeValue"/>
			</ww:if>		

			<ww:set name="count" value="#count + 1"/>
		</ww:iterator>
				
		<br /><span class="fieldrow"><span class="redstar">*</span><label>obligatoriska f&auml;lt</label></span>  
		<br />
		<input id="submit" type="submit" value="<ww:property value="this.getLabel('labels.internal.entry.createButton')"/>" title="<ww:property value="this.getLabel('labels.internal.entry.createButton')"/>">
	</form>

</div>

