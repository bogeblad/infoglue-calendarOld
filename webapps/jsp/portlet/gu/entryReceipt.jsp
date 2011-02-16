<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<ww:set name="event" value="event"/>
<ww:set name="eventVersion" value="this.getEventVersion('#event')"/>

<ww:if test="#attr.detailUrl.indexOf('?') > -1">
	<c:set var="delim" value="&"/>
</ww:if>
<ww:else>
	<c:set var="delim" value="?"/>
</ww:else>

<!-- Anm&auml;lan - kvitto -->	
<ww:if test="isReserve">
	<H1><ww:property value="this.getLabel('labels.public.entryReceiptReserve.headline')"/></H1>
</ww:if>
<ww:else>
	<H1><ww:property value="this.getLabel('labels.public.entryReceipt.headline')"/></H1>
</ww:else>

<div class="contaktform_receipt">
	<ww:if test="isReserve">
		<h2><ww:property value="this.getLabel('labels.public.entryReceiptReserve.intro')"/>:</h2>
	</ww:if>
	<ww:else>
		<h2><ww:property value="this.getLabel('labels.public.entryReceipt.intro')"/>:</h2>
	</ww:else>
	<h3>"<ww:property value="#eventVersion.name"/>"</h3>
	<h3><ww:property value="this.getLabel('labels.public.entryReceipt.bookingIdLabel')"/>:</h3>
	<p><ww:property value="entry.id"/></p>
	<h3><ww:property value="this.getLabel('labels.public.entryReceipt.nameLabel')"/>:</h3>
	<p><ww:property value="entry.firstName"/> <ww:property value="entry.lastName"/></p>
	<h3><ww:property value="this.getLabel('labels.public.entryReceipt.emailLabel')"/>:</h3>
	<p><ww:property value="entry.email"/></p>
	<p><ww:property value="this.getLabel('labels.public.entryReceipt.verificationText')"/> <ww:property value="entry.email"/></p>
	<p><ww:property value="this.getLabel('labels.public.entryReceipt.welcomeText')"/></p>
	<p><a href="<ww:property value="#attr.detailUrl"/><c:out value="${delim}"/>eventId=<ww:property value="eventId"/>" title="<ww:property value="this.getLabel('labels.public.entryReceipt.backToEntryText')"/>">&laquo; <ww:property value="this.getLabel('labels.public.entryReceipt.backToEntryText')"/></a></p>	
</div>
<!-- Anm&auml;lan - kvitto Slut --> 