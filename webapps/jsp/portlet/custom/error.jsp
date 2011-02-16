<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<div class="calendar"> 	
Ett fel inträffade: <ww:property value="#message"/><br/>

<!--
<ww:if test="#error != null">
	<ww:property value="#error.message"/>
</ww:if>
-->
</div>
