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

/**
 * This class represents a Language in which an event can be described.
 * 
 * @author mattias
 * 
 * @hibernate.class table="Language"
 */

public class Language implements BaseEntity
{
    private Long id;
    private String name;
    private String isoCode;

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
     * @hibernate.property name="getIsoCode" column="isoCode" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
	public String getIsoCode() 
	{
		return isoCode;
	}
	
	public void setIsoCode(String isoCode) 
	{
		this.isoCode = isoCode;
	}
	
	public boolean equals(Object object)
	{
		if(object instanceof Language) 
		{
			Language compLanguage = (Language)object;
			return this.getId().equals(compLanguage.getId());
		}
		else
			return false;
	}
	
	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}
