<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Home" scope="page"/>

<%@ include file="adminHeader.jsp" %>

<div class="portlet_margin">
	<h1><%= java.net.URLDecoder.decode(request.getParameter("confirmTitle"), "utf-8") %></h1>
    <p><%= java.net.URLDecoder.decode(request.getParameter("confirmMessage"), "utf-8") %></p>
    <input onclick="document.location.href='<%= java.net.URLDecoder.decode(request.getParameter("okUrl"), "utf-8") %>';" type="submit" value="<ww:property value="this.getLabel('labels.internal.applicationDelete')"/>" title="Radera "postens namn""/>
	<input onclick="document.location.href='<%= java.net.URLDecoder.decode(request.getParameter("cancelUrl"), "utf-8") %>';" type="submit" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" title="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/> "postens namn""/>
	
	<!--		
	<a href="<%= java.net.URLDecoder.decode(request.getParameter("okUrl"), "utf-8") %>"><ww:property value="this.getLabel('labels.internal.applicationOK')"/></a>	
	<a href="<%= java.net.URLDecoder.decode(request.getParameter("cancelUrl"), "utf-8") %>"><ww:property value="this.getLabel('labels.internal.applicationCancel')"/></a>	
	</p>
	-->
</div>

<%@ include file="adminFooter.jsp" %>
