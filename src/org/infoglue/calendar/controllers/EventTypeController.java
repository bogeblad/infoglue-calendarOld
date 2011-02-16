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
import org.infoglue.calendar.entities.EventType;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class EventTypeController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(EventTypeController.class);
        
    
    /**
     * Factory method to get EventTypeController
     * 
     * @return EventTypeController
     */
    
    public static EventTypeController getController()
    {
        return new EventTypeController();
    }
        
        
    /**
     * This method is used to create a new EventType object in the database inside a transaction.
     */
    
    public EventType createEventType(String name, String description, Integer type, Session session) throws HibernateException, Exception 
    {
        EventType eventType = new EventType();
        eventType.setName(name);
        eventType.setDescription(description);
        eventType.setType(type);
        
        session.save(eventType);
        
        return eventType;
    }
    
    
    /**
     * Updates an eventType.
     * 
     * @throws Exception
     */
    
    public void updateEventType(Long id, String name, String description, String schemaValue, Integer type, Session session) throws Exception 
    {
		EventType eventType = getEventType(id, session);
		updateEventType(eventType, name, description, schemaValue, type, session);
    }
    
    /**
     * Updates an eventType inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateEventType(EventType eventType, String name, String description, String schemaValue, Integer type, Session session) throws Exception 
    {
        eventType.setName(name);
        eventType.setDescription(description);
        if(schemaValue != null)
        	eventType.setSchemaValue(schemaValue);
        
        log.debug("type:" + type);
        eventType.setType(type);
        
		session.update(eventType);
	}
    
 
    
    /**
     * This method returns a EventType based on it's primary key inside a transaction
     * @return EventType
     * @throws Exception
     */
    
    public EventType getEventType(Long id, Session session) throws Exception
    {
        EventType eventType = (EventType)session.load(EventType.class, id);
		if(eventType.getSchemaValue() == null || eventType.getSchemaValue().equals(""))
		{
			eventType.setSchemaValue("<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" version=\"2.2\" xmlns:xi=\"http://www.w3.org/2001/XInclude\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:simpleType name=\"textarea\"> <xs:restriction base=\"xs:string\"><xs:maxLength value=\"100\"></xs:maxLength></xs:restriction> </xs:simpleType><xs:simpleType name=\"radiobutton\"> <xs:restriction base=\"xs:string\"> <xs:maxLength value=\"100\"></xs:maxLength> </xs:restriction> </xs:simpleType> <xs:simpleType name=\"checkbox\"> <xs:restriction base=\"xs:string\"> <xs:maxLength value=\"100\"></xs:maxLength> </xs:restriction> </xs:simpleType> <xs:simpleType name=\"select\"> <xs:restriction base=\"xs:string\"> <xs:maxLength value=\"100\"></xs:maxLength>	</xs:restriction> </xs:simpleType> <xs:simpleType name=\"textfield\"> <xs:restriction base=\"xs:string\"> <xs:maxLength value=\"100\"></xs:maxLength> </xs:restriction> </xs:simpleType> <xs:complexType name=\"Content\"> 	<xs:all> <xs:element name=\"Attributes\"> <xs:complexType> <xs:all></xs:all> </xs:complexType> </xs:element> </xs:all> </xs:complexType> <xs:complexType name=\"Validation\" xmlns:xi=\"http://www.w3.org/2001/XInclude\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:annotation><xs:appinfo><form-validation><global><validator classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRequired\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field\" msg=\"300\" name=\"required\"></validator><validator classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRequiredIf\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field,org.apache.commons.validator.Validator\" msg=\"315\" name=\"requiredif\"></validator><validator classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRegexp\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field\" msg=\"300\" name=\"matchRegexp\"></validator></global><formset><form name=\"requiredForm\"></form></formset></form-validation></xs:appinfo></xs:annotation></xs:complexType></xs:schema>");
			session.update(eventType);
		}
        
		return eventType;
    }
    
    
    
    /**
     * Gets a list of all eventTypes available sorted by primary key.
     * @return List of EventType
     * @throws Exception
     */
    
    public List getEventTypeList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from EventType eventType order by eventType.name");
   
        result = q.list();
        
        return result;
    }

    /**
     * Gets a list of all eventTypes of type entryform available sorted by primary key.
     * @return List of EventType
     * @throws Exception
     */
    
    public List getEventTypeList(Integer type, Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from EventType eventType where type = ? order by eventType.id").setInteger(0, type.intValue());
   
        result = q.list();
        
        return result;
    }

    /**
     * Gets a list of eventTypes fetched by name.
     * @return List of EventType
     * @throws Exception
     */
    
    public List getEventType(String name, Session session) throws Exception 
    {
        List eventTypes = null;
        
        eventTypes = session.createQuery("from EventType as eventType where eventType.name = ?").setString(0, name).list();
        
        return eventTypes;
    }
    
    
    /**
     * Deletes a eventType object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteEventType(Long id, Session session) throws Exception 
    {
        EventType eventType = this.getEventType(id, session);
        session.delete(eventType);
    }
    
}