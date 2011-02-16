<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="EventTypes" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<ww:set name="languageCode" value="this.getLanguageCode()"/>

<portlet:renderURL var="createAttributeCategoryUrl">
	<portlet:param name="action" value="CreateEventTypeCategoryAttribute!input"/>
	<calendar:evalParam name="eventTypeId" value="${param.eventTypeId}"/>
</portlet:renderURL>

<portlet:renderURL var="viewListUrl">
	<portlet:param name="action" value="ViewEventType"/>
	<calendar:evalParam name="eventTypeId" value="${param.eventTypeId}"/>
</portlet:renderURL>

<portlet:renderURL var="confirmUrl">
	<portlet:param name="action" value="Confirm"/>
</portlet:renderURL>

<script type="text/javascript">
	function submitDelete(okUrl, confirmMessage)
	{
		//alert("okUrl:" + okUrl);
		document.confirmForm.okUrl.value = okUrl;
		document.confirmForm.confirmMessage.value = confirmMessage;
		document.confirmForm.submit();
	}
</script>
<form name="confirmForm" action="<c:out value="${confirmUrl}"/>" method="post">
	<input type="hidden" name="confirmTitle" value="Radera - bekräfta"/>
	<input type="hidden" name="confirmMessage" value="Fixa detta"/>
	<input type="hidden" name="okUrl" value=""/>
	<input type="hidden" name="cancelUrl" value="<c:out value="${viewListUrl}"/>"/>	
</form>

<div class="subfunctionarea">
<span class="left"></span>	
<span class="right">
	<a href="<c:out value="${createAttributeCategoryUrl}"/>" title="Skapa ny post"><ww:property value="this.getLabel('labels.internal.eventType.addAvailableCategory')"/></a>
</span>	
<div class="clear"></div>
</div>

<div class="portlet_margin">

	<portlet:actionURL var="updateEventTypeActionUrl">
		<portlet:param name="action" value="UpdateEventType"/>
	</portlet:actionURL>
	
	<form name="inputForm" method="POST" action="<c:out value="${updateEventTypeActionUrl}"/>">
		<input type="hidden" name="eventTypeId" value="<ww:property value="eventType.id"/>">
		
		<calendar:textField label="labels.internal.eventType.name" name="'name'" value="eventType.name" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.eventType.description" name="'description'" value="eventType.description" cssClass="longtextfield"/>
		<calendar:selectField label="labels.internal.eventType.type" name="'type'" multiple="false" value="eventTypes" selectedValue="eventType.type" headerItem="Choose type" cssClass="listBox"/>

		<div style="height:10px"></div>
	
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.eventType.updateButton')"/>" class="button">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
</div>

<%@ include file="viewContentTypeDefinitionEditor.jsp" %>

<div class="columnlabelarea">
	<div class="columnMedium"><p><ww:property value="this.getLabel('labels.internal.eventType.AvailableCategories')"/></p></div>
	<div class="columnLong"><p></p></div>
	<div class="clear"></div>
</div>


<ww:iterator value="eventType.categoryAttributes" status="rowstatus">
		
	<ww:set name="eventTypeCategoryAttributeId" value="id" scope="page"/>
	<ww:set name="name" value="top.getLocalizedName(#languageCode, 'sv')" scope="page"/>
	<ww:set name="eventTypeId" value="eventTypeId" scope="page"/>
	<portlet:renderURL var="attributeCategoryUrl">
		<portlet:param name="action" value="ViewEventTypeCategoryAttribute"/>
		<portlet:param name="eventTypeCategoryAttributeId" value='<%= pageContext.getAttribute("eventTypeCategoryAttributeId").toString() %>'/>
	</portlet:renderURL>
	
	<portlet:actionURL var="deleteUrl">
		<portlet:param name="action" value="DeleteEventTypeCategoryAttribute"/>
		<portlet:param name="eventTypeCategoryAttributeId" value='<%= pageContext.getAttribute("eventTypeCategoryAttributeId").toString() %>'/>
		<portlet:param name="eventTypeId" value='<%= pageContext.getAttribute("eventTypeId").toString() %>'/>
	</portlet:actionURL>
	
	<ww:if test="#rowstatus.odd == true">
    	<div class="oddrow">
    </ww:if>
    <ww:else>
		<div class="evenrow">
    </ww:else>

       	<div class="columnMedium">
       		<p class="portletHeadline"><a href="<c:out value="${attributeCategoryUrl}"/>" title="Visa kategori"><ww:property value="name"/> (<ww:property value="internalName"/>)</a></p>
       	</div>
       	<div class="columnLong">
       		<p><ww:property value="description"/></p>
       	</div>
       	<div class="columnEnd">
       		<a href="javascript:submitDelete('<c:out value="${deleteUrl}"/>', 'Är du säker på att du vill radera &quot;<ww:property value="name"/>&quot;');" title="Radera kategori" class="delete"></a>
       	   	<a href="<c:out value="${attributeCategoryUrl}"/>" title="Redigera kategori" class="edit"></a>
       	</div>
       	<div class="clear"></div>
    </div>
	
</ww:iterator>
	
<ww:if test="eventType.categoryAttributes == null || eventType.categoryAttributes.size() == 0">
	<div class="oddrow">
		<div class="columnLong"><p class="portletHeadline"><ww:property value="this.getLabel('labels.internal.applicationNoItemsFound')"/></a></p></div>
       	<div class="columnMedium"></div>
       	<div class="columnEnd"></div>
       	<div class="clear"></div>
    </div>
</ww:if>

<%@ include file="adminFooter.jsp" %>