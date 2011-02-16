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
import org.infoglue.calendar.entities.Location;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class LocationController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(LocationController.class);
        
    
    /**
     * Factory method to get LocationController
     * 
     * @return LocationController
     */
    
    public static LocationController getController()
    {
        return new LocationController();
    }
        
        
    /**
     * This method is used to create a new Location object in the database inside a transaction.
     */
    
    public Location createLocation(String name, String description, Session session) throws HibernateException, Exception 
    {
        Location location = new Location();
        location.setName(name);
        location.setDescription(description);
        
        session.save(location);
        
        return location;
    }
    
    
    /**
     * Updates an location.
     * 
     * @throws Exception
     */
    
    public void updateLocation(Long id, String name, String description, Session session) throws Exception 
    {
		Location location = getLocation(id, session);
		updateLocation(location, name, description, session);
    }
    
    /**
     * Updates an location inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateLocation(Location location, String name, String description, Session session) throws Exception 
    {
        location.setName(name);
        location.setDescription(description);
	
		session.update(location);
	}
    
 
    
    /**
     * This method returns a Location based on it's primary key inside a transaction
     * @return Location
     * @throws Exception
     */
    
    public Location getLocation(Long id, Session session) throws Exception
    {
        Location location = (Location)session.load(Location.class, id);
		
		return location;
    }
    
    
    
    /**
     * Gets a list of all locations available sorted by primary key.
     * @return List of Location
     * @throws Exception
     */
    
    public List getLocationList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Location location order by location.description asc");
   
        result = q.list();
        
        return result;
    }
    
    /**
     * Gets a list of locations fetched by name.
     * @return List of Location
     * @throws Exception
     */
    
    public List getLocation(String name, Session session) throws Exception 
    {
        List locations = null;
        
        locations = session.createQuery("from Location as location where location.name = ?").setString(0, name).list();
        
        return locations;
    }
    
    
    /**
     * Deletes a location object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteLocation(Long id, Session session) throws Exception 
    {
        Location location = this.getLocation(id, session);
        
        session.delete(location);
    }
    
}