<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<%@ page import="org.infoglue.common.util.VisualFormatter, java.util.GregorianCalendar, java.util.Calendar, java.util.Map"%>

<portlet:defineObjects/>

<ww:set name="eventList" value="events"/>
<ww:set name="daysEventHash" value="this.getDaysEventHash('#eventList')" scope="page"/>

<ww:set name="languageCode" value="this.getLanguageCode()"/>
<ww:if test="#languageCode == 'en'">
	<ww:set name="dateFormat" value="'M/d/yyyy'"/>
	<ww:set name="timeFormat" value="'h:mm aaa'"/>
</ww:if>
<ww:else>
	<ww:set name="dateFormat" value="'yyyy-MM-dd'"/>
	<ww:set name="timeFormat" value="'HH:mm'"/>
</ww:else>

<span class="colHeader"><structure:componentLabel mapKeyName="monthLabel"/></span>
<div style="clear:both"></div>
<div class="monthCalendar">
	<%
	  /** The names of the months */
	  String[] months = {
	    "January", "February", "March", "April",
	    "May", "June", "July", "August",
	    "September", "October", "November", "December"
	  };
	 
	  /** The days in each month. */
	  int dom[] = {
	      31, 28, 31, 30,  /* jan feb mar apr */
	      31, 30, 31, 31, /* may jun jul aug */
	      30, 31, 30, 31  /* sep oct nov dec */
	  };

	  	/** The number of days to leave blank at the start of this month */
	  	int leadGap = 0;
	  	int yy = 2008, mm = 9;
		
	  	VisualFormatter vf = new VisualFormatter();
	  	Map daysEventHash = (Map)pageContext.getAttribute("daysEventHash");
	%>
	<table border=1>
		<tr><th colspan=7><%= months[mm] %>  <%= yy %></tr>
		<% 
		GregorianCalendar calendar = new GregorianCalendar(yy, mm, 1); 
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		%>
		<tr><td>Mo<td>Tu</td><td>We</td><td>Th</td><td>Fr</td><td>Sa</td><td>Su</td></tr>
		<%
		    leadGap = calendar.get(Calendar.DAY_OF_WEEK)-2;
		 	
		    int daysInMonth = dom[mm];
		    if (calendar.isLeapYear(calendar.get(Calendar.YEAR)) && mm == 1)
		      ++daysInMonth;
		 
		    out.print("<tr>");
		 
		    // Blank out the labels before 1st day of month
		    for (int i = 0; i < leadGap; i++) {
		      out.print("<td>&nbsp;</td>");
		    }
		 
		    // Fill in numbers for the day of month.
		    for (int i = 1; i <= daysInMonth; i++) {
		    	String dateTimeString = vf.formatDate(calendar.getTime(), "yyyy-MM-") + (i<10 ? "0" : "") + i;
		    	pageContext.setAttribute("currentDateTimeString", dateTimeString);
		    	%>
				<portlet:renderURL var="eventDetailUrl">
					<portlet:param name="action" value="ViewEvent!publicGU"/>
					<portlet:param name="eventId" value='<%= pageContext.getAttribute("eventId").toString() %>'/>
				</portlet:renderURL>

<%--
			 	<common:urlBuilder id="calendarDateTimeFilterCurrentUrl" excludedQueryStringParameters="siteNodeId,languageId,categoryAttribute,categoryNames,startDateTime,endDateTime">
					<common:parameter name="siteNodeId" value="${pc.siteNodeId}"/>
					<common:parameter name="languageId" value="${pc.languageId}"/>
					<common:parameter name="startDateTime" value="${currentDateTimeString}"/>
					<common:parameter name="endDateTime" value="${currentDateTimeString}"/>
				</common:urlBuilder>
--%>
				<ww:set name="dayEvents" value="this.getDayEvents('#eventList', i)"/>
				<%		 	
		      	out.print("<td>");
				if(daysEventHash.containsKey("day_" + i))
				{
					out.print("<a href=\"" + pageContext.getAttribute("calendarDateTimeFilterCurrentUrl") + "\"><b>" + i + "</b><a>");
				}
				else
			      	out.print("<a href=\"" + pageContext.getAttribute("calendarDateTimeFilterCurrentUrl") + "\">" + i + "<a>");
				
		      	out.print("</td>");
		 
		      	if ((leadGap + i) % 7 == 0) {    // wrap if end of line.
		        	out.println("</tr>");
		        	out.print("<tr>");
		      	}
		    }
		%>
		</tr>
		<tr><td><--</td><td colspan="5"><%= mm %></td><td>--></td></tr>
	</table>
</div>

<br clear="all" />

<!-- Calendar End -->  
