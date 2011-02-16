/* ===============================================================================
 *
 * Part of the InfoGlue Content Management Platform (www.infoglue.org)
 *
 * ===============================================================================
 *
 *  Copyright (C)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2, as published by the
 * Free Software Foundation. See the file LICENSE.html for more information.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
 * Place, Suite 330 / Boston, MA 02111-1307 / USA.
 *
 * ===============================================================================
 */

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.EventType;

import junit.framework.TestCase;

/**
 * @author Mattias Bogeblad
 */
public class CalendarControllerTestCases extends TestCase
{
    private static Log log = LogFactory.getLog(CalendarControllerTestCases.class);

    private static SessionFactory sessionFactory = null;
    
    
    /**
     * This method returns a sessionFactory
     * @author Mattias Bogeblad
     */
    
    public static Session getSession() throws Exception 
    {
        if(sessionFactory == null)
        {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        
        return sessionFactory.openSession();
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Constructor for CalendarControllerTestCases.
     * @param arg0
     */
    public CalendarControllerTestCases(String arg0)
    {
        super(arg0);
    }

    public void testCreateCalendar() throws Exception
    {
        Session session = getSession();
        
        Transaction tx = null;
        
        try 
        {
            tx = session.beginTransaction();
  
	        try
	        {
		        log.info("testCreateCalendar...");
		        CalendarController calendarController = CalendarController.getController();
		        
		        Calendar calendar = calendarController.createCalendar("TestCalendar" + System.currentTimeMillis(), "This is the testcalendar...", null, null, new Long(1), session);
	        } 
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        tx.commit();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            
            if (tx!=null) 
                tx.rollback();
        }
        finally 
        {
            session.close();
        }

    }

    public void testDeleteCalendar() throws Exception
    {
        Session session = getSession();
        
        Transaction tx = null;
        
        try 
        {
            tx = session.beginTransaction();
  
	        try
	        {
		        log.info("testCreateCalendar...");
		        CalendarController calendarController = CalendarController.getController();
		        
		        Set calendars = calendarController.getCalendarList(session);
		        //if(calendars.size() > 0)
		        //    calendarController.deleteCalendar(((Calendar)((LinkedHashSet)calendars)(0)).getId(), session);
		    } 
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        tx.commit();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            
            if (tx!=null) 
                tx.rollback();
        }
        finally 
        {
            session.close();
        }

    }

    public void testGetCalendarList() throws Exception
    {
        Session session = getSession();
        
        Transaction tx = null;
        
        try 
        {
            tx = session.beginTransaction();
  
	        try
	        {
		        log.info("testCreateCalendar...");
		        CalendarController calendarController = CalendarController.getController();
		        Set calendars = calendarController.getCalendarList(session);
		        log.info("calendars:" + calendars);
	        } 
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        tx.commit();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            
            if (tx!=null) 
                tx.rollback();
        }
        finally 
        {
            session.close();
        }

    }
    
    public void testUpdateCalendar() throws Exception
    {
        Session session = getSession();
        
        Transaction tx = null;
        
        try 
        {
            tx = session.beginTransaction();
  
	        try
	        {
		        log.info("testUpdateCalendar...");
		        CalendarController calendarController = CalendarController.getController();
		        
		        Set calendars = calendarController.getCalendarList(session);
		        if(calendars.size() > 0)
		        {
		            //Calendar calendar = (Calendar)calendars.get(0);
		            //EventType eventType = (EventType)EventTypeController.getController().getEventTypeList(session).get(0);

		            //calendarController.updateCalendar(calendar, "UpdatedCalendar", "This action updated the calendar", null, null, eventType, session);
		        }
		    } 
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        tx.commit();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            
            if (tx!=null) 
                tx.rollback();
        }
        finally 
        {
            session.close();
        }

    }

}
