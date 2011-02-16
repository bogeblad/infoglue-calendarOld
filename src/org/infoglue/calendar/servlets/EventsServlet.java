package org.infoglue.calendar.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.entities.Event;
import org.infoglue.common.util.HibernateUtil;
import org.infoglue.common.util.RemoteCacheUpdater;
import org.infoglue.common.util.VisualFormatter;

/**
 * Servlet implementation class CalendarServlet
 */

public class EventsServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
    private final static Logger logger = Logger.getLogger(EventsServlet.class.getName());

	private VisualFormatter vf = new VisualFormatter();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		StringBuffer sb = new StringBuffer();

		String siteNodeId 			= request.getParameter("siteNodeId");
		String calendarId 			= request.getParameter("calendarId");
		String freeText 			= request.getParameter("freeText");
		String startDateTime 		= request.getParameter("startDateTime");
		String endDateTime 			= request.getParameter("endDateTime");
		String categoryAttribute 	= request.getParameter("categoryAttribute");
		String categoryNames 		= request.getParameter("categoryNames");
		String calendarMonth 		= request.getParameter("calendarMonth");
		String includedLanguages 	= request.getParameter("includedLanguages");
		
        try
        {
    		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    		sb.append("<events>");

    		Session session = HibernateUtil.currentSession();
        	Transaction tx = null;
        	try 
        	{
        		tx = session.beginTransaction();
        		
                java.util.Calendar startCalendar = null;
                java.util.Calendar endCalendar = null;
                java.util.Calendar calendarMonthCalendar = null;
                
            	if(startDateTime != null && startDateTime.length() > 0)
                    startCalendar = getCalendar(startDateTime, "yyyy-MM-dd", true); 
                else
                {
                    startCalendar = java.util.Calendar.getInstance();
                    startCalendar.set(java.util.Calendar.DAY_OF_MONTH, 1); 
                }
                
                if(endDateTime != null && endDateTime.length() > 0)
                	endCalendar = getCalendar(endDateTime, "yyyy-MM-dd", true); 
                else
                {
                	endCalendar = java.util.Calendar.getInstance();
                    int lastDate = endCalendar.getActualMaximum(java.util.Calendar.DATE);
                    endCalendar.set(java.util.Calendar.DAY_OF_MONTH, lastDate); 
                }

                if(calendarMonth != null && calendarMonth.length() > 0)
                {
                	calendarMonthCalendar = getCalendar(calendarMonth, "yyyy-MM", true); 
                	if(startDateTime == null || startDateTime.length() == 0)
                	{
                		startCalendar.setTime(calendarMonthCalendar.getTime());
                		startCalendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                	}
                	if(endDateTime == null || endDateTime.length() == 0)
                	{
                		endCalendar.setTime(calendarMonthCalendar.getTime());
                		int lastDate = endCalendar.getActualMaximum(java.util.Calendar.DATE);
                        endCalendar.set(java.util.Calendar.DAY_OF_MONTH, lastDate); 
                	}
                }	
                else
                {
                	calendarMonthCalendar = java.util.Calendar.getInstance();
                	calendarMonthCalendar.set(java.util.Calendar.DAY_OF_MONTH, 1); 
                }

                
                startCalendar.set(java.util.Calendar.HOUR_OF_DAY, 1);
                endCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
                
                String[] calendarIds = calendarId.split(",");
                String[] categoryNamesArray = null;
                if(categoryNames != null)
                	categoryNamesArray = categoryNames.split(",");
                            	        
                Set events = EventController.getController().getEventList(calendarIds, categoryAttribute, categoryNamesArray, includedLanguages, startCalendar, endCalendar, freeText, session);
                
                if(siteNodeId != null && !siteNodeId.equals(""))
                	RemoteCacheUpdater.setUsage(new Integer(siteNodeId), calendarIds);
                
        	    Iterator eventsIterator = events.iterator();
        		while(eventsIterator.hasNext())
        		{
        			Event event = (Event)eventsIterator.next();
        			sb.append("    <event id=\"" + event.getId() + "\" startDate=\"" + vf.formatDate(event.getStartDateTime().getTime(), "yyyy-MM-dd") + "\" endDate=\"" + vf.formatDate(event.getEndDateTime().getTime(), "yyyy-MM-dd") + "\"/>");
        		}
        		
        		tx.commit();
        	}
        	catch (Exception e) 
        	{
        		if (tx!=null) tx.rollback();
        	    throw e;
        	}
        	finally 
        	{
        		HibernateUtil.closeSession();
        	}
            sb.append("</events>");
            
    		response.setContentType("text/xml");

        }
        catch(Exception e)
        {
    		response.setContentType("text/html");
    		//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    		sb = new StringBuffer();

        	sb.append("siteNodeId:" + siteNodeId + "\n");
        	sb.append("calendarId:" + calendarId + "\n");
        	sb.append("freeText:" + freeText + "\n");
        	sb.append("startDateTime:" + startDateTime + "\n");
        	sb.append("endDateTime:" + endDateTime + "\n");
        	sb.append("categoryAttribute:" + categoryAttribute + "\n");
        	sb.append("categoryNames:" + categoryNames + "\n");
        	sb.append("calendarMonth:" + calendarMonth + "\n");
        	sb.append("includedLanguages:" + includedLanguages + "\n");

        	logger.error("En error occurred when we tried to generate eventlist:" + e.getMessage(), e);
        	logger.error(sb);
        }
		
		PrintWriter pw = response.getWriter();
		
		pw.println(sb.toString());
		pw.flush();
		pw.close();
	}

    /**
     * Gets a calendar object with date and hour
     * 
     * @param dateString
     * @param pattern
     * @param hour
     * @return
     */
    
    public java.util.Calendar getCalendar(String dateString, String pattern, boolean fallback)
    {	
    	java.util. Calendar calendar = java.util.Calendar.getInstance();
        if(dateString == null)
        {
            return calendar;
        }
        
        Date date = new Date();    
        
        try
        {
	        // Format the current time.
	        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	        date = formatter.parse(dateString);
	        calendar.setTime(date);
	        //calendar.set(Calendar.HOUR_OF_DAY, hour.intValue());
        }
        catch(Exception e)
        {
            if(!fallback)
                return null;
        }
        
        return calendar;
    }

}
