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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.InterceptionPoint;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

public class InterceptionPointController extends BasicController
{    
    private static Log log = LogFactory.getLog(InterceptionPointController.class);
        
    /**
     * Factory method to get InterceptionPointController
     * 
     * @return InterceptionPointController
     */
    
    public static InterceptionPointController getController()
    {
        return new InterceptionPointController();
    }
        
    /**
     * This method is used to create a new InterceptionPoint object in the database inside a transaction.
     */
    
    public InterceptionPoint createInterceptionPoint(String name, String description, String category, Boolean usesExtraDataForAccessControl, Session session) throws HibernateException, Exception 
    {
        InterceptionPoint interceptionPoint = new InterceptionPoint();
        interceptionPoint.setName(name);
        interceptionPoint.setDescription(description);
        interceptionPoint.setCategory(category);
        interceptionPoint.setUsesExtraDataForAccessControl(usesExtraDataForAccessControl);
        
        session.save(interceptionPoint);
        
        return interceptionPoint;
    }
    
    /**
     * Updates an interceptionPoint.
     * 
     * @throws Exception
     */
    
    public void updateInterceptionPoint(Long id, String name, String description, String category, Boolean usesExtraDataForAccessControl, Session session) throws Exception 
    {
		InterceptionPoint interceptionPoint = getInterceptionPoint(id, session);
		updateInterceptionPoint(interceptionPoint, name, description, category, usesExtraDataForAccessControl, session);
    }
    
    /**
     * Updates an interceptionPoint inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateInterceptionPoint(InterceptionPoint interceptionPoint, String name, String description, String category, Boolean usesExtraDataForAccessControl, Session session) throws Exception 
    {
        interceptionPoint.setName(name);
        interceptionPoint.setDescription(description);
	
		session.update(interceptionPoint);
	}
    
 
    
    /**
     * This method returns a InterceptionPoint based on it's primary key inside a transaction
     * @return InterceptionPoint
     * @throws Exception
     */
    
    public InterceptionPoint getInterceptionPoint(Long id, Session session) throws Exception
    {
        InterceptionPoint interceptionPoint = (InterceptionPoint)session.load(InterceptionPoint.class, id);
		
		return interceptionPoint;
    }
    
    
    
    /**
     * Gets a list of all interceptionPoints available sorted by primary key.
     * @return List of InterceptionPoint
     * @throws Exception
     */
    
    public List getInterceptionPointList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from InterceptionPoint interceptionPoint order by interceptionPoint.id");
   
        result = q.list();
        
        return result;
    }

    /**
     * Gets a list of all interceptionPoints available sorted by primary key.
     * @return List of InterceptionPoint
     * @throws Exception
     */
    
    public List getInterceptionPointList(String category, Session session) throws Exception 
    {
        List result = null;
        
        Criteria criteria = session.createCriteria(InterceptionPoint.class);
        criteria.add(Expression.eq("category", category));
        criteria.addOrder(Order.asc("id"));
   
        result = criteria.list();
        
        return result;
    }

    /**
     * Gets a list of interceptionPoints fetched by name.
     * @return List of InterceptionPoint
     * @throws Exception
     */
    
    public List getInterceptionPoint(String name, Session session) throws Exception 
    {
        List interceptionPoints = null;
        
        interceptionPoints = session.createQuery("from InterceptionPoint as interceptionPoint where interceptionPoint.name = ?").setString(0, name).list();
        
        return interceptionPoints;
    }
    
    
    /**
     * Deletes a interceptionPoint object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteInterceptionPoint(Long id, Session session) throws Exception 
    {
        InterceptionPoint interceptionPoint = this.getInterceptionPoint(id, session);
        
        session.delete(interceptionPoint);
    }
    
}