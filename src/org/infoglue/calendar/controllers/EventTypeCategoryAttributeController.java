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
import org.infoglue.calendar.entities.Category;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventTypeCategoryAttribute;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class EventTypeCategoryAttributeController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(EventTypeCategoryAttributeController.class);
        
    
    /**
     * Factory method to get EventTypeController
     * 
     * @return EventTypeController
     */
    
    public static EventTypeCategoryAttributeController getController()
    {
        return new EventTypeCategoryAttributeController();
    }
        
        
    /**
     * This method is used to create a new EventTypeCategoryAttribute object in the database inside a transaction.
     */
    
    public EventTypeCategoryAttribute createEventTypeCategoryAttribute(Long eventTypeId, Long categoryId, String internalName, String name, Session session) throws HibernateException, Exception 
    {
        EventTypeCategoryAttribute eventTypeCategoryAttribute = new EventTypeCategoryAttribute();
        eventTypeCategoryAttribute.setInternalName(internalName);
        eventTypeCategoryAttribute.setName(name);
        
        Category category = CategoryController.getController().getCategory(categoryId, session);
        EventType eventType = EventTypeController.getController().getEventType(eventTypeId, session);
        
        eventTypeCategoryAttribute.setCategory(category);
        eventTypeCategoryAttribute.setEventType(eventType);
        
        session.save(eventTypeCategoryAttribute);
        
        return eventTypeCategoryAttribute;
    }
    
    
    /**
     * Updates an eventType.
     * 
     * @throws Exception
     */
    
    public EventTypeCategoryAttribute updateEventTypeCategoryAttribute(Long id, String internalName, String name, Long categoryId, Session session) throws Exception 
    {
        EventTypeCategoryAttribute eventTypeCategoryAttribute = getEventTypeCategoryAttribute(id, session);
		Category category = CategoryController.getController().getCategory(categoryId, session);
        return updateEventTypeCategoryAttribute(eventTypeCategoryAttribute, internalName, name, category, session);
    }
    
    /**
     * Updates an eventTypeCategoryAttribute inside an transaction.
     * 
     * @throws Exception
     */
    
    public EventTypeCategoryAttribute updateEventTypeCategoryAttribute(EventTypeCategoryAttribute eventTypeCategoryAttribute, String internalName, String name, Category category, Session session) throws Exception 
    {
        eventTypeCategoryAttribute.setInternalName(internalName);
        eventTypeCategoryAttribute.setName(name);
        eventTypeCategoryAttribute.setCategory(category);
        
		session.update(eventTypeCategoryAttribute);
		
		return eventTypeCategoryAttribute;
	}
    
 
    
    /**
     * This method returns a EventTypeCategoryAttribute based on it's primary key inside a transaction
     * @return EventType
     * @throws Exception
     */
    
    public EventTypeCategoryAttribute getEventTypeCategoryAttribute(Long id, Session session) throws Exception
    {
        EventTypeCategoryAttribute eventTypeCategoryAttribute = (EventTypeCategoryAttribute)session.load(EventTypeCategoryAttribute.class, id);
		
		return eventTypeCategoryAttribute;
    }
    
    
    
    /**
     * Gets a list of all eventTypesCategoryAttribute available sorted by primary key.
     * @return List of EventTypeCategoryAttribute
     * @throws Exception
     */
    
    public List getEventTypeCategoryAttributeList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from EventTypeCategoryAttribute eventTypeCategoryAttribute order by eventTypeCategoryAttribute.internalName");
   
        result = q.list();
        
        return result;
    }
    
    /**
     * Gets a list of eventTypeCategoryAttributes fetched by name.
     * @return List of EventTypeCategoryAttribute
     * @throws Exception
     */
    
    public List getEventTypeCategoryAttribute(String internalName, Session session) throws Exception 
    {
        List eventTypeCategoryAttributes = null;
        
        eventTypeCategoryAttributes = session.createQuery("from EventTypeCategoryAttribute as eventTypeCategoryAttribute where eventTypeCategoryAttribute.internalName = ?").setString(0, internalName).list();
        
        return eventTypeCategoryAttributes;
    }
    
    
    /**
     * Deletes a eventType object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteEventTypeCategoryAttribute(Long id, Session session) throws Exception 
    {
        EventTypeCategoryAttribute eventTypeCategoryAttribute = this.getEventTypeCategoryAttribute(id, session);
        session.delete(eventTypeCategoryAttribute);
    }
    
}