package org.infoglue.common.settings.controllers;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.infoglue.calendar.entities.CalendarProperty;
import org.infoglue.calendar.entities.Event;
import org.infoglue.common.settings.entities.Property;

public interface SettingsPersister
{
    /**
     * This method returns a Property based on it's primary key inside a transaction
     * @return Property
     * @throws Exception
     */

	public Property getProperty(Long id, Session session) throws Exception;
    
    
    /**
     * Gets a list of all events available for a particular day.
     * @return List of Event
     * @throws Exception
     */
    
    public Property getProperty(String nameSpace, String name, Session session) throws Exception;

    
    /**
     * This method is used to create a new Property object in the database inside a transaction.
     */
    
    public Property createProperty(String nameSpace, String name, String value, Session session) throws Exception;
    

    /**
     * Updates an property.
     * 
     * @throws Exception
     */
    
    public void updateProperty(String nameSpace, String name, String value, Session session) throws Exception;
    
    
    /**
     * Updates an property inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateProperty(Property property, String value, Session session) throws Exception;

}
