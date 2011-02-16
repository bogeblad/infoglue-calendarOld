<%@ page import="javax.portlet.PortletURL,
				 java.util.Map,
				 java.util.Iterator,
				 java.util.List,
				 java.util.Locale,
				 java.util.ResourceBundle,
				 org.infoglue.common.util.ResourceBundleHelper"%>

<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>


<portlet:defineObjects/>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/calendar.css" />
<link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/applications/jscalendar/calendar-system.css" title="system" />

<div id="inputForm">
	
	<div id="contentListHeader">
		<%= resourceBundle.getString("labels.public.entry.newEntryHeader") %>
	</div>

	<div id="contentList">

		<portlet:actionURL var="createEntryActionUrl">
			<portlet:param name="action" value="CreateEntry!public"/>
		</portlet:actionURL>

		<form name="inputForm" method="POST" action="<c:out value="${createEntryActionUrl}"/>">
			<input type="hidden" name="eventId" value="<ww:property value="eventId"/>">
			<p>
				<calendar:textField label="labels.public.entry.firstName" name="firstName" value="entry.firstName" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textField label="labels.public.entry.lastName" name="lastName" value="entry.lastName" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textField label="labels.public.entry.email" name="email" value="entry.email" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textField label="labels.public.entry.organisation" name="organisation" value="entry.organisation" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textField label="labels.public.entry.address" name="address" value="entry.address" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textField label="labels.public.entry.zipcode" name="zipcode" value="entry.zipcode" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textField label="labels.public.entry.city" name="city" value="entry.city" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textField label="labels.public.entry.phone" name="phone" value="entry.phone" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textField label="labels.public.entry.fax" name="fax" value="entry.fax" cssClass="longtextfield"/>
			</p>
			<p>
				<calendar:textAreaField label="labels.public.entry.message" name="message" value="entry.message" cssClass="longtextfield"/>
			</p>
				
			<input type="submit" value="<ww:property value="this.get('labels.public.entry.createButton')"/>" class="button">
			<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
		</form>
	</div>

</div>
