<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Categories" scope="page"/>

<%@ include file="adminHeader.jsp" %>

<ww:set name="category" value="category" scope="page"/>
<ww:if test="category.parent != null">
	<portlet:renderURL var="viewBackUrl">
		<portlet:param name="action" value="ViewCategory"/>
		<calendar:evalParam name="categoryId" value="${category.parent.id}"/>
	</portlet:renderURL>
</ww:if>
<ww:else>
	<portlet:renderURL var="viewBackUrl">
		<portlet:param name="action" value="ViewCategoryList"/>
	</portlet:renderURL>
</ww:else>

<%@ include file="functionMenu.jsp" %>

<portlet:renderURL var="createCategoryUrl">
	<portlet:param name="action" value="CreateCategory!input"/>
	<calendar:evalParam name="parentCategoryId" value="${param.categoryId}"/>
</portlet:renderURL>

<div class="subfunctionarea">
<span class="left"></span>	
<span class="right">
	<a href="<c:out value="${createCategoryUrl}"/>" title="Skapa ny post"><ww:property value="this.getLabel('labels.internal.category.addCategory')"/></a>
</span>	
<div class="clear"></div>
</div>

<ww:set name="categoryId" value="categoryId" scope="page"/>
<ww:set name="language" value="language"/>
<ww:set name="language" value="language" scope="page"/>
<ww:set name="languageCode" value="this.getLanguageCode()"/>

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
			var endIndex = name.indexOf(",", startIndex);
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

	function submitDelete(okUrl, confirmMessage)
	{
		//alert("okUrl:" + okUrl);
		document.confirmForm.okUrl.value = okUrl;
		document.confirmForm.confirmMessage.value = confirmMessage;
		document.confirmForm.submit();
	}
	
</script>

<portlet:renderURL var="viewListUrl">
	<portlet:param name="action" value="ViewCategory"/>
	<calendar:evalParam name="categoryId" value="${param.categoryId}"/>
</portlet:renderURL>

<portlet:renderURL var="confirmUrl">
	<portlet:param name="action" value="Confirm"/>
</portlet:renderURL>

<form name="confirmForm" action="<c:out value="${confirmUrl}"/>" method="post">
	<input type="hidden" name="confirmTitle" value="Radera - bekräfta"/>
	<input type="hidden" name="confirmMessage" value="Fixa detta"/>
	<input type="hidden" name="okUrl" value=""/>
	<input type="hidden" name="cancelUrl" value="<c:out value="${viewListUrl}"/>"/>	
</form>

<br/>

<ul class="languagesTabs">
	<ww:iterator value="availableLanguages" status="rowstatus">
		<ww:set name="currentLanguageId" value="top.id"/>
		<ww:set name="currentLanguageId" value="top.id" scope="page"/>
		
		<portlet:renderURL var="viewCategoryUrl">
			<portlet:param name="action" value="ViewCategory"/>
			<calendar:evalParam name="categoryId" value="${categoryId}"/>
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
			<a href="<c:out value="${viewCategoryUrl}"/>"><ww:property value="top.name"/></a>
		</li>
		
	</ww:iterator>
</ul>

<div class="portlet_margin">

	<portlet:actionURL var="updateCategoryActionUrl">
		<portlet:param name="action" value="UpdateCategory"/>
	</portlet:actionURL>
	
	<form name="inputForm" method="POST" action="<c:out value="${updateCategoryActionUrl}"/>">
		<input type="hidden" name="updateCategoryId" value="<ww:property value="category.id"/>">
		<input type="hidden" name="name" value="<ww:property value="category.name"/>">
		
		<calendar:textField label="labels.internal.category.internalName" name="'internalName'" value="category.internalName" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.category.name" name="'localizedName'" value="category.getLocalizedName(#language.isoCode, 'sv')" cssClass="longtextfield"/>
		<calendar:textField label="labels.internal.category.description" name="'description'" value="category.description" cssClass="longtextfield"/>
		<calendar:checkboxField label="labels.internal.category.isActive" name="'active'" valueMap="yesNoMap" selectedValues="category.active"/>
		
		<div style="height:10px"></div>
		<!-- 
		<input type="submit" value="<ww:property value="this.getLabel('labels.internal.category.updateButton')"/>" class="button">
		-->
		<input type="button" value="<ww:property value="this.getLabel('labels.internal.category.updateButton')"/>" class="button" onclick="setLocalizedValueAndSubmit();">
		<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
	</form>
</div>


<div class="columnlabelarea">
	<div class="columnLong"><p><ww:property value="this.getLabel('labels.internal.category.childCategories')"/></p></div>
	<div class="columnMedium"><p><ww:property value="this.getLabel('labels.internal.category.description')"/></p></div>
	<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.category.isActive')"/></p></div>
	<div class="clear"></div>
</div>

<ww:set name="sortedChildren" value="this.getSortedChildren(category.children)"/>

<%--
<ww:iterator value="category.children" status="rowstatus">
--%>
<ww:iterator value="#sortedChildren" status="rowstatus">

	<ww:set name="categoryId" value="id" scope="page"/>
	<ww:set name="category" value="top"/>
	<ww:set name="category" value="top" scope="page"/>
	<ww:set name="languageCode" value="this.getLanguageCode()"/>
	<ww:set name="name" value="#category.getLocalizedName(#languageCode, 'sv')"/>

	<portlet:renderURL var="categoryUrl">
		<portlet:param name="action" value="ViewCategory"/>
		<portlet:param name="categoryId" value='<%= pageContext.getAttribute("categoryId").toString() %>'/>
	</portlet:renderURL>
	
	<portlet:actionURL var="deleteUrl">
		<portlet:param name="action" value="DeleteCategory"/>
		<portlet:param name="deleteCategoryId" value='<%= pageContext.getAttribute("categoryId").toString() %>'/>
	</portlet:actionURL>

	<ww:if test="#rowstatus.odd == true">
    	<div class="oddrow">
    </ww:if>
    <ww:else>
		<div class="evenrow">
    </ww:else>

       	<div class="columnLong">
       		<p class="portletHeadline"><a href="<c:out value="${categoryUrl}"/>" title="Redigera '<ww:property value="#name"/>'"><ww:property value="#name"/> (<ww:property value="internalName"/>)</a></p>
       	</div>
       	<div class="columnMedium">
       		<p><ww:property value="description"/></p>
       	</div>
       	<div class="columnShort">
       		<p><ww:property value="active"/></p>
       	</div>
       	<div class="columnEnd">
       		<a href="javascript:submitDelete('<c:out value="${deleteUrl}"/>', 'Är du säker på att du vill radera &quot;<ww:property value="#name"/>&quot;');" title="Radera '<ww:property value="#name"/>'" class="delete"></a>
       	   	<a href="<c:out value="${categoryUrl}"/>" title="Redigera '<ww:property value="#name"/>'" class="edit"></a>
       	</div>
       	<div class="clear"></div>
    </div>
</ww:iterator>

<ww:if test="category.children == null || category.children.size() == 0">
	<div class="oddrow">
		<div class="columnLong"><p class="portletHeadline"><ww:property value="this.getLabel('labels.internal.applicationNoItemsFound')"/></a></p></div>
       	<div class="columnMedium"></div>
       	<div class="columnEnd"></div>
       	<div class="clear"></div>
    </div>
</ww:if>


<%@ include file="adminFooter.jsp" %>