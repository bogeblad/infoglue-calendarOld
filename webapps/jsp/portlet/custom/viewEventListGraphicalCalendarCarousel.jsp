<!--eri-no-index-->
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.infoglue.calendar.entities.Category"%>
<%@page import="org.infoglue.calendar.actions.CalendarAbstractAction"%>
<%@page import="org.apache.axis.types.Month"%>
<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<%@ page import="org.infoglue.common.util.VisualFormatter, java.util.GregorianCalendar, java.util.Calendar, java.util.Map, java.text.SimpleDateFormat, java.util.Date, java.util.Locale, 
org.infoglue.calendar.entities.Event, org.infoglue.calendar.entities.EventCategory, org.infoglue.calendar.entities.EventTypeCategoryAttribute, org.infoglue.calendar.entities.EventVersion, 
org.infoglue.calendar.entities.Language, java.util.List, java.util.Set, java.util.Iterator"%>
<portlet:defineObjects/>
<ww:set name="eventList" value="events"/>
<ww:set name="daysEventHash" value="this.getDaysEventHash('#eventList', true)" scope="page"/>

<ww:if test="#attr.eventDetailUrl.indexOf('?') > -1">
	<c:set var="delim" value="&"/>
</ww:if>
<ww:else>
	<c:set var="delim" value="?"/>
</ww:else>

<%
VisualFormatter vf = new VisualFormatter();

Calendar now = Calendar.getInstance();
String nowDateTimeString = vf.formatDate(now.getTime(), "yyyy-MM-dd");
//Setting up back and forward dates
Calendar calendarMonthCalendar = Calendar.getInstance();
if(request.getParameter("calendarMonth") != null)
{
	Date monthDate = vf.parseDate(request.getParameter("calendarMonth"), "yyyy-MM");
	calendarMonthCalendar.setTime(monthDate);
}
String calendarMonthDateTimeString = vf.formatDate(calendarMonthCalendar.getTime(), "yyyy-MM");
pageContext.setAttribute("calendarMonthDateTimeString", calendarMonthDateTimeString);

Calendar previousCalendarMonthCalendar = Calendar.getInstance();
previousCalendarMonthCalendar.setTime(calendarMonthCalendar.getTime());
previousCalendarMonthCalendar.add(Calendar.MONTH, -1);
String calendarMonthPreviousDateTimeString = vf.formatDate(previousCalendarMonthCalendar.getTime(), "yyyy-MM");
pageContext.setAttribute("calendarMonthPreviousDateTimeString", calendarMonthPreviousDateTimeString);

Calendar nextCalendarMonthCalendar = Calendar.getInstance();
nextCalendarMonthCalendar.setTime(calendarMonthCalendar.getTime());
nextCalendarMonthCalendar.add(Calendar.MONTH, +1);
String calendarMonthNextDateTimeString = vf.formatDate(nextCalendarMonthCalendar.getTime(), "yyyy-MM");
pageContext.setAttribute("calendarMonthNextDateTimeString", calendarMonthNextDateTimeString);

//Month date
String monthDateString = vf.formatDate(now.getTime(), "yyyy-MM");
pageContext.setAttribute("monthDateString", monthDateString);

//Current date
String currentDateString = vf.formatDate(now.getTime(), "yyyy-MM-dd");
pageContext.setAttribute("currentDateString", currentDateString);

//Seven days ahead span
Calendar weekCalendar = Calendar.getInstance();
String weekStartDateTimeString = vf.formatDate(weekCalendar.getTime(), "yyyy-MM-dd");
pageContext.setAttribute("weekStartDateTimeString", weekStartDateTimeString);
weekCalendar.add(Calendar.DAY_OF_MONTH, 7);
String weekEndDateTimeString = vf.formatDate(weekCalendar.getTime(), "yyyy-MM-dd");
pageContext.setAttribute("weekEndDateTimeString", weekEndDateTimeString);

//Month span
Calendar monthCalendar = Calendar.getInstance();
monthCalendar.set(Calendar.DAY_OF_MONTH, 1); 
String monthStartDateTimeString = vf.formatDate(monthCalendar.getTime(), "yyyy-MM-dd");
pageContext.setAttribute("monthStartDateTimeString", monthStartDateTimeString);
int lastDate = monthCalendar.getActualMaximum(Calendar.DATE);
monthCalendar.set(Calendar.DAY_OF_MONTH, lastDate); 
String monthEndDateTimeString = vf.formatDate(monthCalendar.getTime(), "yyyy-MM-dd");
pageContext.setAttribute("monthEndDateTimeString", monthEndDateTimeString);

Map daysEventHash = (Map)pageContext.getAttribute("daysEventHash");
		
	/** The days in each month. */
	int dom[] = {
		31, 28, 31, 30,  /* jan feb mar apr */
	    31, 30, 31, 31, /* may jun jul aug */
	    30, 31, 30, 31  /* sep oct nov dec */
	};

	/** The number of days to leave blank at the start of this month */
	int leadGap = 0;
	int yy = calendarMonthCalendar.get(Calendar.YEAR);
	int mm = calendarMonthCalendar.get(Calendar.MONTH);
	if(mm == -1){
		yy = yy - 1;
		mm = 11;
	}
%>
<c:set var="selectedDate" value=""/>
<ww:set name="locale" value="this.getLocale()" scope="page"/>
<c:if test="${not empty param.startDateTime && not empty param.endDateTime && param.startDateTime == param.endDateTime}">
	<c:set var="selectedDate" value="${param.startDateTime}"/>
</c:if>		

<table class="GUCalendarCarouselGraphicalTable" cellspacing="0" border="0" cellpadding="0">
	<% 
	Locale locale = (Locale)pageContext.getAttribute("locale");
	GregorianCalendar calendar = new GregorianCalendar(yy, mm, 1); 
	calendar.setFirstDayOfWeek(Calendar.MONDAY);
	Calendar weekDaysCalendar = Calendar.getInstance();
	weekDaysCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);	
	/* Kollar om länk till tidigare månad ska ritas ut */
	Calendar monthDisplayed = Calendar.getInstance();
	monthDisplayed.set(Calendar.MONTH, Integer.parseInt(pageContext.getAttribute("calendarMonthPreviousDateTimeString").toString().substring(5)));
	monthDisplayed.set(Calendar.YEAR, Integer.parseInt(pageContext.getAttribute("calendarMonthPreviousDateTimeString").toString().substring(0,4)));
	Boolean printPreviousMonthLink = new Boolean((monthDisplayed.get(Calendar.MONTH) > now.get(Calendar.MONTH)) || monthDisplayed.get(Calendar.YEAR) > now.get(Calendar.YEAR));
	pageContext.setAttribute("printPreviousMonthLink", printPreviousMonthLink);
	%>
	<ww:if test="#attr.baseUrlCalendarCarousel.indexOf('?') > -1">
		<c:set var="delimGraphic" value="&"/>
	</ww:if>
	<ww:else>
		<c:set var="delimGraphic" value="?"/>
	</ww:else>
	<tr>
		<td class ="GUCalendarCarouselGraphicalTableHead">		
			<c:if test="${printPreviousMonthLink}">
				<%-- &lt;&lt; --%>
				<a id="prevMonth" href="<ww:property value="#attr.baseUrlCalendarCarousel"/><c:out value="${delimGraphic}"/>calendarMonth=<%= pageContext.getAttribute("calendarMonthPreviousDateTimeString").toString() %>">&laquo;</a>
			</c:if>
		</td>
		<td class ="GUCalendarCarouselGraphicalTableHead" colspan="5" align="center"><%= vf.formatDate(calendarMonthCalendar.getTime(), locale, "MMMM").toUpperCase(locale) %> <%= yy %></td>
		<td class ="GUCalendarCarouselGraphicalTableHead">
			<%-- &gt;&gt; --%>
			<a id="nextMonth" href="<ww:property value="#attr.baseUrlCalendarCarousel"/><c:out value="${delimGraphic}"/>calendarMonth=<%= pageContext.getAttribute("calendarMonthNextDateTimeString").toString() %>">&raquo;</a>
		</td>
	</tr>
	<ww:set name="actionObject" value="this" scope="page"/>
	<ww:set name="eventDetailUrl" value="#attr.eventDetailUrl" scope="page"/>
	<ww:set name="calendarPageUrl" value="#attr.calendarPageUrl" scope="page"/>
	<ww:set name="beginsLabel" value="this.getLabel('labels.public.event.begins')" scope="page"/>
	<ww:set name="endsLabel" value="this.getLabel('labels.public.event.ends')" scope="page"/>
	<ww:set name="clockLabel" value="this.getLabel('labels.public.event.klockLabel')" scope="page"/>
	<%
	CalendarAbstractAction calendarAbstractAction = (CalendarAbstractAction)pageContext.getAttribute("actionObject");
	String eventDetailUrl = (String)pageContext.getAttribute("eventDetailUrl");
	String calendarPageUrl = (String)pageContext.getAttribute("calendarPageUrl");
	String delim = (String)pageContext.getAttribute("delim");
	String beginsLabel = (String)pageContext.getAttribute("beginsLabel");
	String endsLabel = (String)pageContext.getAttribute("endsLabel");
	String clockLabel = (String)pageContext.getAttribute("clockLabel");
	if(weekDaysCalendar != null){
		out.print("<tr class='GUCalendarCarouselGraphicalTableWeekdays'>");
		for(int i = 0; i < 7; i++){
			out.print("<th valign='middle'>" + vf.formatDate(weekDaysCalendar.getTime(), locale, "EE").substring(0, 2).toUpperCase(locale) + "</th>");
			weekDaysCalendar.add(Calendar.DAY_OF_WEEK, 1);	
		}
		out.print("</tr>");
	}
	StringBuffer rows = new StringBuffer("");
	
    leadGap = calendar.get(Calendar.DAY_OF_WEEK)-2;
    if(leadGap == -1)
    	leadGap = 6;
 	
    int daysInMonth = dom[mm];
    if (calendar.isLeapYear(calendar.get(Calendar.YEAR)) && mm == 1)
      	++daysInMonth;
 
    rows.append("<tr>");
	 
    // Blank out the labels before 1st day of month
    for (int i = 0; i < leadGap; i++) {
	    rows.append("<td> </td>");
    }
 
    // Fill in numbers for the day of month.
    int endGap = 7;
    String textArrayString = "";

    for (int i = 1; i <= daysInMonth; i++) {
    	String dateTimeString = vf.formatDate(calendar.getTime(), "yyyy-MM-") + (i<10 ? "0" : "") + i;
    	pageContext.setAttribute("currentDateTimeString", dateTimeString);
    	String selectedDate = (String)pageContext.getAttribute("selectedDate");	 	
	    rows.append("<td" + (dateTimeString.equals(nowDateTimeString) ? " class=\"today\"" : "") + (dateTimeString.equals(selectedDate) ? " class=\"current\"" : "") + ">");
      	
      	if(daysEventHash.containsKey("day_" + i)){
      		List<Event> todaysEvents = (List<Event>)daysEventHash.get("day_" + i);
			String title = "";
			String startDate = "";
			String endDate = "";
			String date = "";
			String eventTypes = "";
			String todayEvents = "";
			String shortDescription = "";
			String headerDate ="";
      		for(int j = 0; j < todaysEvents.size(); j++){
      			Event currentEvent = todaysEvents.get(j);
				pageContext.setAttribute("currentEvent", currentEvent);
				EventVersion currentEventVersion = calendarAbstractAction.getEventVersion(currentEvent);	
				
				Set categoryAttributes = currentEvent.getOwningCalendar().getEventType().getCategoryAttributes();
				Iterator it = categoryAttributes.iterator();
                while (it.hasNext())
                {
                	EventTypeCategoryAttribute attribute = (EventTypeCategoryAttribute)it.next();
                	if (attribute.getName().equals("Evenemangstyp") || attribute.getName().equals("Eventtyp"))
                	{
                		 List<Category> selectedCategories = calendarAbstractAction.getEventCategories(currentEvent, attribute);
                		 Iterator<Category> itSelectedCategories = selectedCategories.iterator();
                		 String categoryDelim = "";
                		 while (itSelectedCategories.hasNext())
                		 {
                			 Category selectedCategory = itSelectedCategories.next();
                			 eventTypes += categoryDelim + selectedCategory.getLocalizedName(calendarAbstractAction.getLanguageCode(), "sv");
                			 categoryDelim = ", ";
                		 }
                	}
                } 	
                startDate = calendarAbstractAction.formatDate(currentEvent.getStartDateTime().getTime(), "HH:mm");
                if(startDate.equals("12:34")){
                	startDate = "00:00";
                }
                endDate = calendarAbstractAction.formatDate(currentEvent.getEndDateTime().getTime(), "HH:mm");
	           	date = "<span class=\"smallfont\">"+ beginsLabel + " " + clockLabel + " " + startDate + "</span><span class=\"smallfont\">" + endsLabel + " " + clockLabel + " " + endDate + "</span>";
	    		title = "<h2><a href=\"" + eventDetailUrl + delim + "amp;eventId=" + currentEvent.getId() + "\">"+ StringEscapeUtils.escapeJavaScript(currentEventVersion.getTitle()) +"</a></h2>";
	    		eventTypes = "<span class=\"smallfont\">" + StringEscapeUtils.escapeJavaScript(eventTypes) + "</span>";
	    		todayEvents += "<div class=\"eventBox\">" + eventTypes + title + date + "</div>";// + shortDescription;
	    		eventTypes = "";
      		}
      		headerDate =  "<h2 class=\"infoboxHeaderDate\">" + i + " " + vf.formatDate(calendarMonthCalendar.getTime(), locale, "MMMM") + "</h2>";
      		todayEvents = "textArray[" + i + "] = '"+ headerDate + todayEvents + "';";
		    rows.append("<a data-id=\"" + i + "\" href=\"" + calendarPageUrl + delim + "amp;startDateTime=" + dateTimeString + delim + "amp;endDateTime=" + dateTimeString +"\" class=\"thelink\"><span class=\"dateNumber\">" + i + "</span></a>");
		    textArrayString += todayEvents ;
	  	}
	  	else{
	  	    rows.append("<span class=\"dateNumber\">" + i + "</span>");
		}
  	    rows.append("</td>");
 		endGap--;

      	if ((leadGap + i) % 7 == 0) {    // wrap if end of line.

	  	    rows.append("</tr>");
        	rows.append("<tr>");
        	endGap = 7;
      	}
    }
    
    if(endGap < 7)
    {
    	for(int i=0; i<endGap; i++)
    	{
    		rows.append("<td> </td>");
    	}
   	}
   	rows.append("</tr>");	    		
  		out.print(rows.toString().replaceAll("<tr></tr>", ""));
	%>
</table>
<div id="tipbox"></div>

<script type="text/javascript">
<!--
	var textArray = new Array();
	var isOverDatebox = false;
	var isOverTipbox = false;
	<% out.print(textArrayString); %>
	$(".thelink").hoverIntent(function () {
		/*=====================================================
		  Make sure the popup doesn't end up below the viewport 
		  =====================================================*/
		var position = $(this).position();
		isOverDatebox = true;
		$('#tipbox').html(textArray[$(this).attr("data-id")]);
		var popupHeight = $("#tipbox").height();
		var windowHeight = $(window).height();
		if ((position.top - $(window).scrollTop() + popupHeight) + 80 > windowHeight){
			modY = windowHeight - (position.top - $(window).scrollTop() + popupHeight) - 75;
			modX = 325;
		}
		else{
			modY = 35;
			modX = 270;
		}
		$("#tipbox").fadeIn(100).css("left", (position.left - modX)).css("top", (position.top + modY));
	}, function(){
		isOverDatebox = false;
		setTimeout("hidePopup()", 400);
	});

	$('#tipbox').mouseenter(function(){
		isOverTipbox = true;
	});

	$('#tipbox').mouseleave(function(){
		isOverTipbox = false;
		$('#tipbox').fadeOut(600);
	});
	
	function hidePopup(){
		if(!isOverTipbox && !isOverDatebox){
			$('#tipbox').fadeOut(600);
		}	
	}
	-->
</script>
<!--/eri-no-index-->
 
 