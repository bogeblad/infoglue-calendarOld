<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Events" scope="page"/>
<c:set var="activeEventSubNavItem" value="NewEvent" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<%@ include file="eventSubFunctionMenu.jsp" %>

<div class="portlet_margin">
	<p class="instruction"><ww:property value="this.getLabel('labels.internal.application.chooseLanguageForEditIntro')"/></p>
</div> 

<div class="columnlabelarea">
	<div class="columnLong"><p><ww:property value="this.getLabel('labels.internal.language.name')"/></p></div>
	<div class="columnMedium"><p><ww:property value="this.getLabel('labels.internal.language.isoCode')"/></p></div>
	<div class="clear"></div>
</div>

<ww:set name="skipLanguageTabs" value="skipLanguageTabs" scope="page"/>

<ww:iterator value="availableLanguages" status="rowstatus">
	
	<ww:set name="systemLanguageId" value="id" scope="page"/>
	<ww:set name="eventId" value="eventId" scope="page"/>
	<portlet:renderURL var="editEventUrl">
		<portlet:param name="action" value="ViewEvent!edit"/>
		<portlet:param name="versionLanguageId" value='<%= pageContext.getAttribute("systemLanguageId").toString() %>'/>
		<portlet:param name="eventId" value='<%= pageContext.getAttribute("eventId").toString() %>'/>
		<calendar:evalParam name="skipLanguageTabs" value="${skipLanguageTabs}"/>
	</portlet:renderURL>
	
	<ww:if test="#rowstatus.odd == true">
    	<div class="oddrow">
    </ww:if>
    <ww:else>
		<div class="evenrow">
    </ww:else>

       	<div class="columnLong">
       		<p class="portletHeadline"><a href="<c:out value="${editEventUrl}"/>" title="Välj '<ww:property value="name"/>'"><ww:property value="name"/></a></p>
       	</div>
       	<div class="columnMedium">
       		<p><ww:property value="isoCode"/></p>
       	</div>
       	<div class="columnEnd">
       	</div>
       	<div class="clear"></div>
    </div>
		
</ww:iterator>

<ww:if test="availableLanguages == null || availableLanguages.size() == 0">
	<div class="oddrow">
		<div class="columnLong"><p class="portletHeadline"><ww:property value="this.getLabel('labels.internal.applicationNoItemsFound')"/></a></p></div>
       	<div class="columnMedium"></div>
       	<div class="columnEnd"></div>
       	<div class="clear"></div>
    </div>
</ww:if>


<%@ include file="adminFooter.jsp" %>
