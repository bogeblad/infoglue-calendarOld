<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<%@ include file="adminHeader.jsp" %>

<div class="portlet_margin">

	<h1>Error</h1>
	<p style="color: red;">
		<ww:property value="#errorMessage"/>
	</p>
	<p>
		The log files will contain more information on the specific error so please consult them for further guidance.
	</p>
	<p>
		<a href="javascript:history.back();">Tillbaka</a>
	</p>	
<!--
<ww:if test="#error != null">
	<ww:property value="#error.message"/>
</ww:if>
-->
</div>

<%@ include file="adminFooter.jsp" %>