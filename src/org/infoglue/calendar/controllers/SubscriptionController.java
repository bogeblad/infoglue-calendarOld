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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Subscriber;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class SubscriptionController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(SubscriptionController.class);
        
    
    /**
     * Factory method to get SubscriberController
     * 
     * @return SubscriberController
     */
    
    public static SubscriptionController getController()
    {
        return new SubscriptionController();
    }
        
    
    /**
     * This method is used to create a new Subscriber object in the database inside a transaction.
     */
    
    public Subscriber createSubscriber(String email, Calendar calendar, Session session) throws HibernateException, Exception 
    {
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail(email);
        subscriber.setCalendar(calendar);
        
        session.save(subscriber);
        
        return subscriber;
    }
    
    
    /**
     * Updates an subscriber.
     * 
     * @throws Exception
     */
    
    public void updateSubscriber(Long id, String email, Session session) throws Exception 
    {
		Subscriber subscriber = getSubscriber(id, session);
		updateSubscriber(subscriber, email, session);
    }
    
    /**
     * Updates an subscriber inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateSubscriber(Subscriber subscriber, String email, Session session) throws Exception 
    {
        subscriber.setEmail(email);
	
		session.update(subscriber);
	}
    
 
    /**
     * This method returns a Subscriber based on it's primary key inside a transaction
     * @return Subscriber
     * @throws Exception
     */
    
    public Subscriber getSubscriber(Long id, Session session) throws Exception
    {
        Subscriber subscriber = (Subscriber)session.load(Subscriber.class, id);
		
		return subscriber;
    }
    
    
    
    /**
     * Gets a list of all subscribers available sorted by primary key.
     * @return List of Subscriber
     * @throws Exception
     */
    
    public List getSubscriberList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Subscriber subscriber order by subscriber.id");
   
        result = q.list();
        
        return result;
    }

    /**
     * Gets a list of all calendars available which has a subscriber with email - sorted by primary key.
     * @return List of Calendar
     * @throws Exception
     */
    
    public Set getSubscriberList(String email, Session session) throws Exception 
    {
        List subscribers = null;
        
        subscribers = session.createQuery("from Subscriber subscriber where subscriber.email = ?").setString(0, email).list();
   
        Set set = new LinkedHashSet();
        set.addAll(subscribers);

        return set;
    }  
    
    /**
     * Deletes a subscriber object in the database. Also cascades all calendars associated to it.
     * @throws Exception
     */
    
    public void deleteSubscriber(Long id, Session session) throws Exception 
    {
        Subscriber subscriber = this.getSubscriber(id, session);
        session.delete(subscriber);
    }
    
}