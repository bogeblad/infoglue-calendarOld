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
 * This class represents a single interception point object. An interception point is called upon with a key/name.
 * 
 * @author mattias
 * 
 * @hibernate.class table="InterceptionPoint"
 */

public class InterceptionPoint implements BaseEntity
{
    private Long id;
    private String name;
    private String description;
    private String category;
    private Boolean usesExtraDataForAccessControl;
    	
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

    /**
     * @hibernate.property name="getCategory" column="category" type="string" not-null="true" unique="false"
     * 
     * @return String
     */

	public String getCategory() 
	{
		return category;
	}
	
	public void setCategory(String category) 
	{
		this.category = category;
	}

    /**
     * @hibernate.property name="getUsesExtraDataForAccessControl" column="usesExtraDataForAccessControl" type="boolean" not-null="true" unique="false"
     * 
     * @return String
     */

	public Boolean getUsesExtraDataForAccessControl() 
	{
		return usesExtraDataForAccessControl;
	}
	
	public void setUsesExtraDataForAccessControl(Boolean usesExtraDataForAccessControl) 
	{
		this.usesExtraDataForAccessControl = usesExtraDataForAccessControl;
	}
       
	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}
