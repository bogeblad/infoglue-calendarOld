<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="EntrySearch" scope="page"/>

<%@ include file="adminHeader.jsp" %>

<script type="text/javascript">
	
	function toggleSearchForm()
	{
		searchFormElement = document.getElementById("searchForm");
		hitListElement = document.getElementById("hitlist");
		if(searchFormElement.style.display == "none")
		{
			searchFormElement.style.display = "block";
			hitListElement.style.display = "none";
		}
		else
		{
			searchFormElement.style.display = "none";
			hitListElement.style.display = "block";
		}
	}
	
	function openSelectedResultFile() {
		var selectBox = document.getElementById( 'exportList' );
		var dest = selectBox.options[ selectBox.selectedIndex ].value;
		window.open( dest, 'documentOpener', '' );
	}
	
</script>

<%@ include file="functionMenu.jsp" %>

<div id="searchForm" class="portlet_margin" style="display: <ww:if test="entries == null">block</ww:if><ww:else>none</ww:else>;">

	<h1><ww:property value="this.getLabel('labels.internal.soba.searchIntro')"/></h1>

	<portlet:renderURL var="searchEntryActionUrl">
		<portlet:param name="action" value="ViewEntrySearch"/>
	</portlet:renderURL>
			
	<form method="post" action="<c:out value="${searchEntryActionUrl}"/>">
	
	<div class="fieldrow">
		<label for="searchEventId"><ww:property value="this.getLabel('labels.internal.soba.events')"/></label><br>
		<select id="searchEventId" name="searchEventId" multiple="true" class="listBox">
			<option value=""/><ww:property value="this.getLabel('labels.internal.soba.anyEvent')"/></option>
			<option value=""/>--------------------</option>
			<ww:iterator value="eventList">
				<ww:set name="event" value="top"/>
				<ww:set name="eventVersion" value="this.getMasterEventVersion('#event')"/>
				<ww:set name="eventVersion" value="this.getMasterEventVersion('#event')" scope="page"/>
				<option value="<ww:property value="id"/>"/><ww:property value="#eventVersion.name"/></option>
			</ww:iterator>
		</select>
	</div>
	
	<calendar:textField label="labels.internal.soba.firstName" name="'searchFirstName'" value="firstName" cssClass="longtextfield"/>
	<calendar:textField label="labels.internal.soba.lastName" name="'searchLastName'" value="lastName" cssClass="longtextfield"/>
	<calendar:textField label="labels.internal.soba.email" name="'searchEmail'" value="email" cssClass="longtextfield"/>

	<ww:iterator value="categoryAttributes" status="rowstatus">
		<ww:set name="categoryAttribute" value="top" scope="page"/>
		<ww:set name="categoryAttributeIndex" value="#rowstatus.index" scope="page"/>
		<input type="hidden" name="categoryAttributeId_<ww:property value="#rowstatus.index"/>" value="<ww:property value="top.id"/>"/>
		<c:set var="categoryAttributeName" value="categoryAttribute_${categoryAttribute.id}_categoryId"/>
		<calendar:selectField label="top.name" name="${categoryAttributeName}" multiple="true" value="top.category.children" selectedValues="getCategoryAttributeValues(top.id)" cssClass="listBox" required="false"/>
	</ww:iterator>

	<calendar:checkboxField label="labels.internal.soba.searchANDOR" name="'andSearch'" valueMap="this.getAndSearch()" selectedValues="false"/>

	<div style="height:10px"></div>
	
	<input type="submit" value="<ww:property value="this.getLabel('labels.internal.soba.searchButton')"/>" class="button"/>
	<input type="button" class="button" onclick="toggleSearchForm();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>"></a>
	</form>
</div>


<!-- ********************* -->
<!-- ******  HITS ******** -->
<!-- ********************* -->
<ww:if test="entries != null">

	<ww:set name="entriesAsList" value="entriesAsList" scope="page"/>
	
	<portlet:renderURL var="searchEntryActionUrl">
		<portlet:param name="action" value="ViewEntrySearch!input"/>
	</portlet:renderURL>
	  
	<div id="hitlist" style="display: <ww:if test="entries == null">none</ww:if><ww:else>block</ww:else>;">
	
	<div class="portlet_margin">
		<h1><ww:property value="this.getLabel('labels.internal.soba.hitListStart')"/> <ww:property value="entries.size()"/> <ww:property value="this.getLabel('labels.internal.soba.hitListEnd')"/></h1>
	</div>
		
	<ww:set name="searchHashCode" value="searchHashCode" scope="page"/>
	
	<portlet:renderURL var="composeEmailUrl">
		<calendar:evalParam name="action" value="ComposeEmail"/>
		<calendar:evalParam name="searchHashCode" value="${searchHashCode}"/>
	</portlet:renderURL>
	
	<portlet:renderURL var="createEntryRenderURL">
		<portlet:param name="action" value="CreateEntry!input"/>
		<portlet:param name="eventId" value="1"/>
	</portlet:renderURL>
		
	<div class="subfunctionarea">
	<span class="left">
		<a href="javascript:toggleSearchForm();"><ww:property value="this.getLabel('labels.internal.soba.newSearch')"/></a>
		<ww:if test="entries != null && entries.size() > 0">
			| <a href="<c:out value="${composeEmailUrl}"/>"><ww:property value="this.getLabel('labels.internal.soba.emailPersons')"/></a>
			<ww:if test="searchResultFiles != null && searchResultFiles.size() > 0">
			| <ww:property value="this.getLabel('labels.internal.soba.exportHitList')"/></label>
				<%--
				<select id="exportList" name="exportList" class="sitedropbox">
				<ww:iterator value="searchResultFiles">
				   <option value="<ww:property value="value"/>"><ww:property value="key"/></option>
				</ww:iterator>
				</select><input type="button" onClick="javascript:openSelectedResultFile();" value="Export"/>
				--%>
				<ww:iterator value="searchResultFiles">
				   <a href="<ww:property value="value"/>"><ww:property value="key"/></a>
				</ww:iterator>
				
			</ww:if>
 		</ww:if>		
	</span>
	<span class="right"></span>	
	<div class="clear"></div>
	</div>

	<div class="columnlabelarea">
		<ww:iterator value="resultValues">
			<%--
		 	<ww:if test="top == 'Id'">
		 		<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.soba.idColumnHeader')"/></p></div>
		 	</ww:if>
		 	--%>
 	    	<ww:if test="top == 'Name'">
 				<div class="columnMediumShort"><p>#<ww:property value="this.getLabel('labels.internal.soba.idColumnHeader')"/> - <ww:property value="this.getLabel('labels.internal.soba.nameColumnHeader')"/></p></div>
	    	</ww:if>
	    	<ww:if test="top == 'Event'">
 				<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.soba.eventColumnHeader')"/></p></div>
	    	</ww:if>
	    	<ww:if test="top == 'Email'">
				<div class="columnMediumShort"><p><ww:property value="this.getLabel('labels.internal.soba.emailColumnHeader')"/></p></div>
	    	</ww:if>
	    	<ww:if test="top == 'Organisation'">
				<div class="columnMediumShort"><p><ww:property value="this.getLabel('labels.internal.soba.organisationColumnHeader')"/></p></div>	
	    	</ww:if>
	    	<ww:if test="top == 'Address'">
				<div class="columnMediumShort"><p><ww:property value="this.getLabel('labels.internal.soba.addressColumnHeader')"/></p></div>
	    	</ww:if>
	    	<ww:if test="top == 'Zipcode'">
				<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.soba.zipcodeColumnHeader')"/></p></div>
	    	</ww:if>
	    	<ww:if test="top == 'City'">
				<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.soba.cityColumnHeader')"/></p></div>
	    	</ww:if>
		</ww:iterator>
		<div class="clear"></div>
	</div>
	
	<portlet:actionURL var="viewListUrl">
		<portlet:param name="action" value="ViewEntrySearch"/>
		<calendar:evalParam name="searchHashCode" value="${searchHashCode}"/>
		<c:if test="${searchEventId != null}">
			<ww:iterator value="searchEventId">
				<ww:if test="top != null">
					<ww:set name="currentSearchEventId" value="top" scope="page"/>
					<portlet:param name="searchEventId" value='<%= pageContext.getAttribute("currentSearchEventId").toString() %>'/>
				</ww:if>
			</ww:iterator>
		</c:if>
		<c:if test="${searchFirstName != null}">
			<portlet:param name="searchFirstName" value='<%= pageContext.getAttribute("searchFirstName").toString() %>'/>
		</c:if>
		<c:if test="${searchLastName != null}">
			<portlet:param name="searchLastName" value='<%= pageContext.getAttribute("searchLastName").toString() %>'/>
		</c:if>
		<c:if test="${searchEmail != null}">
			<portlet:param name="searchEmail" value='<%= pageContext.getAttribute("searchEmail").toString() %>'/>
		</c:if>
		<c:if test="${onlyFutureEvents != null}">
			<portlet:param name="onlyFutureEvents" value='<%= pageContext.getAttribute("onlyFutureEvents").toString() %>'/>
		</c:if>
	</portlet:actionURL>

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

	<ww:iterator value="entries" status="rowstatus">
	    <ww:set name="firstName" value="firstName" scope="page"/>
	    <ww:set name="lastName" value="lastName" scope="page"/>
		<ww:set name="email" value="email" scope="page"/>
		<ww:set name="event" value="event" scope="page"/>
		<ww:set name="organisation" value="organisation" scope="page"/>
		<ww:set name="address" value="address" scope="page"/>
		<ww:set name="zipcode" value="zipcode" scope="page"/>
		<ww:set name="city" value="city" scope="page"/>
		<ww:set name="rowcount" value="rowstatus.count"/>
		<ww:set name="entryId" value="id" scope="page"/>
		<ww:set name="name" value="name" scope="page"/>
		<ww:if test="searchEventId != null">
			<ww:set name="searchEventId" value="searchEventId" scope="page"/>
		</ww:if>
		<ww:if test="searchFirstName != null">
			<ww:set name="searchFirstName" value="searchFirstName" scope="page"/>
		</ww:if>
		<ww:if test="searchLastName != null">
			<ww:set name="searchLastName" value="searchLastName" scope="page"/>
		</ww:if>
		<ww:if test="searchEmail != null">
			<ww:set name="searchEmail" value="searchEmail" scope="page"/>
		</ww:if>
		<ww:if test="onlyFutureEvents != null">
			<ww:set name="onlyFutureEvents" value="onlyFutureEvents" scope="page"/>
		</ww:if>
		<portlet:renderURL var="viewEntryRenderURL">
			<portlet:param name="action" value="ViewEntry"/>
			<calendar:evalParam name="searchHashCode" value="${searchHashCode}"/>
			<c:if test="${entryId != null}">
				<portlet:param name="entryId" value='<%= pageContext.getAttribute("entryId").toString() %>'/>
			</c:if>
			<c:if test="${searchEventId != null}">
				<ww:iterator value="searchEventId">
					<ww:if test="top != null">
						<ww:set name="currentSearchEventId" value="top" scope="page"/>
						<portlet:param name="searchEventId" value='<%= pageContext.getAttribute("currentSearchEventId").toString() %>'/>
					</ww:if>
				</ww:iterator>
			</c:if>
		</portlet:renderURL>
	
		<portlet:actionURL var="deleteUrl">
			<portlet:param name="action" value="DeleteEntry"/>
			<calendar:evalParam name="searchHashCode" value="${searchHashCode}"/>
			<c:if test="${entryId != null}">
				<portlet:param name="entryId" value='<%= pageContext.getAttribute("entryId").toString() %>'/>
			</c:if>
			<c:if test="${searchEventId != null}">
				<ww:iterator value="searchEventId">
					<ww:if test="top != null">
						<ww:set name="currentSearchEventId" value="top" scope="page"/>
						<portlet:param name="searchEventId" value='<%= pageContext.getAttribute("currentSearchEventId").toString() %>'/>
					</ww:if>
				</ww:iterator>
			</c:if>
			<c:if test="${searchFirstName != null}">
				<portlet:param name="searchFirstName" value='<%= pageContext.getAttribute("searchFirstName").toString() %>'/>
			</c:if>
			<c:if test="${searchLastName != null}">
				<portlet:param name="searchLastName" value='<%= pageContext.getAttribute("searchLastName").toString() %>'/>
			</c:if>
			<c:if test="${searchEmail != null}">
				<portlet:param name="searchEmail" value='<%= pageContext.getAttribute("searchEmail").toString() %>'/>
			</c:if>
			<c:if test="${onlyFutureEvents != null}">
				<portlet:param name="onlyFutureEvents" value='<%= pageContext.getAttribute("onlyFutureEvents").toString() %>'/>
			</c:if>			
		</portlet:actionURL>
				
		<ww:if test="#rowstatus.odd == true">
	    	<div class="oddrow">
	    </ww:if>
	    <ww:else>
			<div class="evenrow">
	    </ww:else>
	
		<ww:iterator value="resultValues">
			<%--
		 	<ww:if test="top == 'Id'">
		   		<div class="columnShort">
		   			<p class="portletHeadline"><a href="<c:out value="${viewEntryRenderURL}"/>" title="Redigera '<c:out value="${firstName}"/>'"><ww:property value="#rowstatus.count"/></a></p>
			   	</div>
			</ww:if>
			--%>
			<ww:if test="top == 'Name'">
		   		<div class="columnMediumShort">
		   			<p class="portletHeadline"><a href="<c:out value="${viewEntryRenderURL}"/>" title="Redigera '<c:out value="${firstName}"/>'"><c:out value="${entryId}"/> - <c:out value="${firstName}"/> <c:out value="${lastName}"/></a></p>
			   	</div>
			</ww:if>
			<ww:if test="top == 'Event'">
			   	<div class="columnShort">
			   		<p><c:out value="${event.name}"/></p>
			   	</div>
			</ww:if>
		   	<ww:if test="top == 'Email'">
			   	<div class="columnMediumShort">
			   		<p><c:out value="${email}"/></p>
			   	</div>
			</ww:if>
		   	<ww:if test="top == 'Organisation'">
			   	<div class="columnMediumShort">
			   		<p><c:out value="${organisation}"/></p>
			   	</div>
			</ww:if>
		   	<ww:if test="top == 'Address'">
			   	<div class="columnMediumShort">
			   		<p><c:out value="${address}"/></p>
			   	</div>
			</ww:if>
		   	<ww:if test="top == 'Zipcode'">
			   	<div class="columnShort">
			   		<p><c:out value="${zipcode}"/></p>
			   	</div>
			</ww:if>
		   	<ww:if test="top == 'City'">
			   	<div class="columnShort">
			   		<p><c:out value="${city}"/></p>			   		
			   	</div>
			</ww:if>

		</ww:iterator>
		   	<div class="columnEnd">
		   		<a href="javascript:submitDelete('<c:out value="${deleteUrl}"/>', 'Är du säker på att du vill radera &quot;<ww:property value="#name"/>&quot;');" title="Radera '<ww:property value="entry.firstName"/>'" class="delete"></a>
		   	   	<a href="<c:out value="${viewEntryRenderURL}"/>" title="Redigera '<ww:property value="entry.firstName"/>'" class="edit"></a>
		   	</div>
		   	<div class="clear"></div>
		</div>
	
	</ww:iterator>
</ww:if>

<%--

<ww:if test="entries == null || entries.size() == 0">
	<div class="oddrow">
		<div class="columnLong"><p class="portletHeadline"><ww:property value="this.getLabel('labels.internal.applicationNoItemsFound')"/></a></p></div>
       	<div class="columnMedium"></div>
       	<div class="columnEnd"></div>
       	<div class="clear"></div>
    </div>
</ww:if>
--%>

<%@ include file="adminFooter.jsp" %>