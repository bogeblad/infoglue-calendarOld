package org.infoglue.calendar.webservices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.databeans.CalendarBean;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.common.util.HibernateUtil;


/**
 * This class is responsible for letting an external application call InfoGlue
 * API:s remotely. It handles api:s to manage user properties.
 * 
 * @author Mattias Bogeblad
 */

public class RemoteCalendarServiceImpl extends RemoteInfoGlueService
{
    private final static Logger logger = Logger.getLogger(RemoteCalendarServiceImpl.class.getName());

    /**
     * Gets all roles available.
     */
    
    public List<CalendarBean> getCalendars() 
    {
        List<CalendarBean> calendars = new ArrayList<CalendarBean>();
        
        logger.info("******************************************");
        logger.info("Getting all calendars through webservice..");
        logger.info("******************************************");
        
        try
        {
        	
        	Session session = HibernateUtil.currentSession();
        	Transaction tx = null;
        	try 
        	{
        		tx = session.beginTransaction();

        		Set<Calendar> calendarSet = CalendarController.getController().getCalendarList(session);
        	    Iterator calendarSetIterator = calendarSet.iterator();
        		while(calendarSetIterator.hasNext())
        		{
        			Calendar calendar = (Calendar)calendarSetIterator.next();
        			
        			CalendarBean calendarBean = new CalendarBean();
        			calendarBean.setId(calendar.getId());
        			calendarBean.setName(calendar.getName());
        			calendarBean.setDescription(calendar.getDescription());
        			calendars.add(calendarBean);
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
        		//session.close();
        	}
        }
        catch(Exception e)
        {
        	logger.error("En error occurred when we tried to create a new contentVersion:" + e.getMessage(), e);
        }
        
        return calendars;    
    }



}
