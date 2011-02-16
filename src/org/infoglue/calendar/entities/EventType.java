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

import java.util.Set;

import org.infoglue.common.contenttypeeditor.entities.ContentTypeDefinition;

/**
 * This class represents a eventType where events can take place.
 * 
 * @author mattias
 * 
 * @hibernate.class table="EventType"
 */

public class EventType implements BaseEntity, ContentTypeDefinition
{
	public static final Integer EVENT_DEFINITION = new Integer(0);
	public static final Integer ENTRY_DEFINITION = new Integer(1);

    private Long id;
    private String name;
    private String description;
    private String schemaValue = null;
    private Integer type = EVENT_DEFINITION;

    private Set categoryAttributes;
    
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
     * @hibernate.property name="getName" column="name" type="string" not-null="false" unique="true"
     * 
     * @return String
     */
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * @hibernate.property name="getDescription" column="description" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
	public String getDescription() 
	{
		return description;
	}
	
	public void setDescription(String description) 
	{
		this.description = description;
	}
	
    public Set getCategoryAttributes()
    {
        return categoryAttributes;
    }
    
    public void setCategoryAttributes(Set categoryAttributes)
    {
        this.categoryAttributes = categoryAttributes;
    }
    
    public java.lang.String getSchemaValue()
    {
        return this.schemaValue;
    }
                
    public void setSchemaValue(java.lang.String schemaValue)
    {
        this.schemaValue = schemaValue;
    }
    
	public java.lang.Integer getType()
	{
		return type;
	}

	public void setType(java.lang.Integer type)
	{
		this.type = type;
	}

	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}
