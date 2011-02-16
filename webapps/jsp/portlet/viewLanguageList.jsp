<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Languages" scope="page"/>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<portlet:renderURL var="createLanguageUrl">
	<portlet:param name="action" value="CreateLanguage!input"/>
</portlet:renderURL>

<portlet:renderURL var="viewListUrl">
	<portlet:param name="action" value="ViewLanguageList"/>
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
	<a href="<c:out value="${createLanguageUrl}"/>" title="Skapa ny post"><ww:property value="this.getLabel('labels.internal.language.addLanguage')"/></a>
</span>	
<div class="clear"></div>
</div>

<div class="columnlabelarea">
	<div class="columnLong"><p><ww:property value="this.getLabel('labels.internal.language.name')"/></p></div>
	<div class="columnMedium"><p><ww:property value="this.getLabel('labels.internal.language.isoCode')"/></p></div>
	<div class="clear"></div>
</div>

<ww:iterator value="languages" status="rowstatus">

	<ww:set name="systemLanguageId" value="id" scope="page"/>
	<ww:set name="name" value="name" scope="page"/>
	<portlet:renderURL var="languageUrl">
		<portlet:param name="action" value="ViewLanguage"/>
		<portlet:param name="systemLanguageId" value='<%= pageContext.getAttribute("systemLanguageId").toString() %>'/>
	</portlet:renderURL>
	
	<portlet:actionURL var="deleteLanguageUrl">
		<portlet:param name="action" value="DeleteLanguage"/>
		<portlet:param name="systemLanguageId" value='<%= pageContext.getAttribute("systemLanguageId").toString() %>'/>
	</portlet:actionURL>

	<ww:if test="#rowstatus.odd == true">
    	<div class="oddrow">
    </ww:if>
    <ww:else>
		<div class="evenrow">
    </ww:else>

       	<div class="columnLong">
       		<p class="portletHeadline"><a href="<c:out value="${languageUrl}"/>" title="Redigera '<ww:property value="name"/>'"><ww:property value="name"/></a></p>
       	</div>
       	<div class="columnMedium">
       		<p><ww:property value="isoCode"/></p>
       	</div>
       	<div class="columnEnd">
       		<a href="javascript:submitDelete('<c:out value="${deleteUrl}"/>', 'Är du säker på att du vill radera &quot;<ww:property value="name"/>&quot;');" title="Radera '<ww:property value="name"/>'" class="delete"></a>
       	   	<a href="<c:out value="${languageUrl}"/>" title="Redigera '<ww:property value="name"/>'" class="edit"></a>
       	</div>
       	<div class="clear"></div>
    </div>

</ww:iterator>

<ww:if test="languages == null || languages.size() == 0">
	<div class="oddrow">
		<div class="columnLong"><p class="portletHeadline"><ww:property value="this.getLabel('labels.internal.applicationNoItemsFound')"/></a></p></div>
       	<div class="columnMedium"></div>
       	<div class="columnEnd"></div>
       	<div class="clear"></div>
    </div>
</ww:if>

<%@ include file="adminFooter.jsp" %>
