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

package org.infoglue.calendar.controllers;

import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.Group;
import org.infoglue.calendar.entities.Role;
import org.infoglue.calendar.entities.Subscriber;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Subqueries;

public class CalendarController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(CalendarController.class);
        
    
    /**
     * Factory method to get CalendarController
     * 
     * @return CalendarController
     */
    
    public static CalendarController getController()
    {
        return new CalendarController();
    }
        
    
    /**
     * This method is used to create a new Calendar object in the database.
     */
    /*
    public Calendar createCalendar(String name, String description, String owner) throws HibernateException, Exception 
    {
        Calendar calendar = null;
        
        Session session = getSession();
        
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			calendar = createCalendar(name, description, owner, session);
			tx.commit();
		}
		catch (Exception e) 
		{
		    if (tx!=null) 
		        tx.rollback();
		    throw e;
		}
		finally 
		{
		    session.close();
		}
		
        return calendar;
    }
    */

    
    /**
     * This method is used to create a new Calendar object in the database inside a transaction.
     */
    
    public Calendar createCalendar(String name, String description, String[] roles, String[] groups, Long eventTypeId, Session session) throws HibernateException, Exception 
    {
        EventType eventType = EventTypeController.getController().getEventType(eventTypeId, session);
        
        Calendar calendar = new Calendar();
        calendar.setName(name);
        calendar.setDescription(description);
        
        calendar.setEventType(eventType);
        session.save(calendar);
       
        for(int i=0; i < roles.length; i++)
        {
            Role role = new Role();
            String roleName = roles[i];
            role.setName(roleName);
            role.setCalendar(calendar);
            session.save(role);
            calendar.getOwningRoles().add(role);
        }

        for(int i=0; i < groups.length; i++)
        {
            Group group = new Group();
            String groupName = groups[i];
            group.setName(groupName);
            group.setCalendar(calendar);
            session.save(group);
            calendar.getOwningGroups().add(group);
        }
                
        return calendar;
    }
    
    
    /**
     * Updates an calendar.
     * 
     * @throws Exception
     */
    
    public void updateCalendar(Long id, String name, String description, String[] roles, String[] groups, Long eventTypeId, Session session) throws Exception 
    {
		Calendar calendar = getCalendar(id, session);
        EventType eventType = EventTypeController.getController().getEventType(eventTypeId, session);

		updateCalendar(calendar, name, description, roles, groups, eventType, session);
    }
    
    /**
     * Updates an calendar inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateCalendar(Calendar calendar, String name, String description, String[] roles, String groups[], EventType eventType, Session session) throws Exception 
    {
        calendar.setName(name);
        calendar.setDescription(description);
        calendar.setEventType(eventType);

        Iterator oldRolesIterator = calendar.getOwningRoles().iterator();
        while(oldRolesIterator.hasNext())
        {
            Role role = (Role)oldRolesIterator.next();
            session.delete(role);
            oldRolesIterator.remove();
        }

        Iterator oldGroupsIterator = calendar.getOwningGroups().iterator();
        while(oldGroupsIterator.hasNext())
        {
            Group group = (Group)oldGroupsIterator.next();
            session.delete(group);
            oldGroupsIterator.remove();
        }

        if(roles != null)
        {
	        for(int i=0; i < roles.length; i++)
	        {
	            Role role = new Role();
	            String roleName = roles[i];
	            role.setCalendar(calendar);
	            role.setName(roleName);
	            session.save(role);
	            calendar.getOwningRoles().add(role);
	        }
        }
        
        if(groups != null)
        {
	        for(int i=0; i < groups.length; i++)
	        {
	            Group group = new Group();
	            String groupName = groups[i];
	            group.setCalendar(calendar);
	            //log.debug("ID: " + calendar.getId());
	            group.setName(groupName);
	            session.save(group);
	            calendar.getOwningGroups().add(group);
	        }
        }

        session.update(calendar);
	}
    
 
    /**
     * This method returns a Calendar based on it's primary key
     * @return Calendar
     * @throws Exception
     */
    /*
    public Calendar getCalendar(Long id) throws Exception
    {
        Calendar calendar = null;
        
        Session session = getSession();
        
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			calendar = getCalendar(id, session);
			tx.commit();
		}
		catch (Exception e) 
		{
		    if (tx!=null) 
		        tx.rollback();
		    throw e;
		}
		finally 
		{
		    session.close();
		}
		
		return calendar;
    }
    */
    
    /**
     * This method returns a Calendar based on it's primary key inside a transaction
     * @return Calendar
     * @throws Exception
     */
    
    public Calendar getCalendar(Long id, Session session) throws Exception
    {
        Calendar calendar = (Calendar)session.load(Calendar.class, id);
		
		return calendar;
    }
    

    /**
     * Gets a list of all calendars available sorted by primary key.
     * @return List of Calendar
     * @throws Exception
     */
    /*
    public List getCalendarList() throws Exception 
    {
        List calendars = null;

        Session session = getSession();
        
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			calendars = getCalendarList(session);
			tx.commit();
		}
		catch (Exception e) 
		{
		    if (tx!=null) 
		        tx.rollback();
		    throw e;
		}
		finally 
		{
		    session.close();
		}
        
        return calendars;
    }
    */

    /**
     * Gets a list of all calendars available sorted by primary key.
     * @return List of Calendar
     * @throws Exception
     */
    
    public Set getCalendarList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Calendar calendar order by calendar.name");
   
        result = q.list();
        
        Set set = new LinkedHashSet();
        set.addAll(result);
        
        return set;
    }

    
    /**
     * Gets a list of all calendars not subscribed to sorted by primary key.
     * @return List of Calendar
     * @throws Exception
     */
    
    public Set getUnsubscribedCalendarList(String email, Session session) throws Exception 
    {
        List result = null;
   
        Set subscriptions = SubscriptionController.getController().getSubscriberList(email, session);
        List subscriptionsList = new ArrayList();
        
        Iterator i = subscriptions.iterator();
        while(i.hasNext())
        {
            Subscriber subscriber = (Subscriber)i.next();
            subscriptionsList.add(subscriber.getCalendar().getId());
        }
        
        Criteria criteria = session.createCriteria(Calendar.class);
        if(subscriptionsList.size() > 0)
            criteria.add(Expression.not(Expression.in("id", subscriptionsList.toArray())));
        criteria.addOrder(Order.asc("name"));

        Set set = new LinkedHashSet();
        set.addAll(criteria.list());
        
        return set;
    }

    /**
     * Gets a list of all calendars available sorted by primary key.
     * @return List of Calendar
     * @throws Exception
     */
    
    public Set getCalendarList(List roles, List groups, Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Calendar calendar order by calendar.id");

        Criteria criteria = session.createCriteria(Calendar.class);
        criteria.createCriteria("owningRoles").add(Expression.in("name", roles.toArray()));
        if(groups.size() > 0)
            criteria.createCriteria("owningGroups").add(Expression.in("name", groups.toArray()));
        criteria.addOrder(Order.asc("name"));

        //result = criteria.list();
        //return result;
        Set set = new LinkedHashSet();
        set.addAll(criteria.list());	
        
        return set;
    }

    /**
     * Gets a list of calendars fetched by name.
     * @return List of Calendar
     * @throws Exception
     */
    /*
    public List getCalendar(String name) throws Exception 
    {
        List calendars = null;
        
        Session session = getSession();
        
        Transaction tx = null;
        
        try 
        {
            tx = session.beginTransaction();
            
            calendars = session.createQuery("from Calendar as calendar where calendar.name = ?").setString(0, name).list();
                
            tx.commit();
        }
        catch (Exception e) 
        {
            if (tx!=null) 
                tx.rollback();
            throw e;
        }
        finally 
        {
            session.close();
        }
        
        return calendars;
    }
    */

    public List getCalendar(String name, Session session) throws Exception 
    {
        List calendars = null;
        
        calendars = session.createQuery("from Calendar as calendar where calendar.name = ?").setString(0, name).list();
        
        return calendars;
    }

    /**
     * Deletes a calendar object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
 /*
    public void deleteCalendar(Long id) throws Exception 
    {
        Session session = getSession();
        
        Transaction tx = null;
        
        try 
        {
            tx = session.beginTransaction();
            
            Calendar calendar = this.getCalendar(id);
            session.delete(calendar);
            
            tx.commit();
        }
        catch (Exception e) 
        {
            if (tx!=null) 
                tx.rollback();
            throw e;
        }
        finally 
        {
            session.close();
        }
    }
*/ 
    public void deleteCalendar(Long id, Session session) throws Exception 
    {
        Calendar calendar = this.getCalendar(id, session);
        session.delete(calendar);
    }
    
}