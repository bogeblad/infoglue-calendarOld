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

/**
 * This class represents a EventCategory which is a mapping between a event and whatever categories it has.
 * 
 * @author mattias
 * 
 * @hibernate.class table="EventCategory"
 */

public class EventCategory implements BaseEntity
{
    private Long id;

    private EventTypeCategoryAttribute eventTypeCategoryAttribute;
    private Event event;
    private Category category;
    
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
    
    public String getName()
    {
        return "";
    }
    
    public Event getEvent()
    {
        return event;
    }
    public void setEvent(Event event)
    {
        this.event = event;
    }

    public Category getCategory()
    {
        return category;
    }
    public void setCategory(Category category)
    {
        this.category = category;
    }

    public EventTypeCategoryAttribute getEventTypeCategoryAttribute()
    {
        return eventTypeCategoryAttribute;
    }
    public void setEventTypeCategoryAttribute(EventTypeCategoryAttribute eventTypeCategoryAttribute)
    {
        this.eventTypeCategoryAttribute = eventTypeCategoryAttribute;
    }
    
	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}
