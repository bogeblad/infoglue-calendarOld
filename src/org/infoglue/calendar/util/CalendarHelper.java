package org.infoglue.calendar.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.actions.CalendarAbstractAction;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;

public class CalendarHelper
{
	private static Log log = LogFactory.getLog(CalendarHelper.class);

    public static List getDates(SyndEntry entry, String languageCode)
    {
    	List dates = new ArrayList();
    	
    	try
		{	            		
	        if(entry != null && entry.getContents() != null && entry.getContents().size() > 0)
	        {
		        SyndContent metaData = (SyndContent)entry.getContents().get(0);
		        String content = metaData.getValue();
		        
		        int indexStart = content.indexOf("<startDateTime>") + "<startDateTime>".length();
		        if(indexStart > -1)
		        {
		            int indexEnd = content.indexOf("</startDateTime>", indexStart);
		            if(indexEnd > -1)
		            {
		                String startDateTimeString = content.substring(indexStart, indexEnd);
		                dates.add(parseDate(startDateTimeString, "yyyy-MM-dd HH:mm", languageCode));
		    	    }
		        }
		
		        indexStart = content.indexOf("<endDateTime>") + "<endDateTime>".length();
		        if(indexStart > -1)
		        {
		            int indexEnd = content.indexOf("</endDateTime>", indexStart);
		            if(indexEnd > -1)
		            {
		                String endDateTimeString = content.substring(indexStart, indexEnd);
		                dates.add(parseDate(endDateTimeString, "yyyy-MM-dd HH:mm", languageCode));
		    	    }
		        }
	        }	
	        
	        if(dates.size() < 2)
	        {
		        dates.add(new Date());
		    	dates.add(new Date());
	        }	        
		} 
    	catch (Exception e)
		{
    		e.printStackTrace();
		}
        
        return dates;
    }
    
    public static Date parseDate(String dateString, String pattern, String languageCode)
    {	
        if(dateString == null)
            return new Date();
        
        Date date = new Date();    
        
        try
        {
	        // Format the current time.
	        SimpleDateFormat formatter = new SimpleDateFormat(pattern, new Locale(languageCode));
	        date = formatter.parse(dateString);
        }
        catch(Exception e)
        {
            log.info("Could not parse date:" + e.getMessage() + " - defaulting to now...");
        }
        
        return date;
    }

    public static Date parseDate(String dateString, String pattern, Locale locale)
    {	
        if(dateString == null)
            return new Date();
        
        Date date = new Date();    
        
        try
        {
	        // Format the current time.
	        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
	        date = formatter.parse(dateString);
        }
        catch(Exception e)
        {
            log.info("Could not parse date:" + e.getMessage() + " - defaulting to now...");
        }
        
        return date;
    }

    
}
