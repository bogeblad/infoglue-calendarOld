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
package org.infoglue.calendar.entities;

import java.sql.Blob;

/**
 * This class represents a simple resource. That is assets you wish to associate with an event. 
 * Can be binary documents etc.
 * 
 * @author mattias
 * 
 * @hibernate.class table="Resource"
 */

public class Resource implements BaseEntity
{
    private Long id;
    private String assetKey;
    private String fileName;
    private Blob resource;
    private Event event;
    
    /**
     * @hibernate.id generator-class="native" type="long" column="id" unsaved-value="null"
     * 
     * @return long
     */    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    /**
     * @hibernate.property name="getAssetKey" column="assetKey" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getAssetKey()
    {
        return assetKey;
    }
    
    public void setAssetKey(String assetKey)
    {
        this.assetKey = assetKey;
    }

    /**
     * @hibernate.property name="getFileName" column="fileName" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getFileName()
    {
        return fileName;
    }

    public String getShortendFileName()
    {
        String shortFileName = this.fileName;
        if(shortFileName != null)
        {
			if(shortFileName.lastIndexOf("/") > -1)
			    shortFileName = shortFileName.substring(shortFileName.lastIndexOf("/") + 1);
			if(shortFileName.lastIndexOf("\\") > -1)
			    shortFileName = shortFileName.substring(shortFileName.lastIndexOf("\\") + 1);
        }
		return shortFileName;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
    * @hibernate.property column = "file" type = "blob" not-null = "true"
    */
    public Blob getResource()
    {
        return resource;
    }
    
    public void setResource(Blob resource)
    {
        this.resource = resource;
    }
    
    /** 
     * @hibernate.many-to-one class="org.infoglue.calendar.entities.Event" column="event_id"
     */  
    public Event getEvent()
    {
        return event;
    }
    
    public void setEvent(Event event)
    {
        this.event = event;
    }

    public String getName()
    {
        return this.getAssetKey();
    }

	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}
