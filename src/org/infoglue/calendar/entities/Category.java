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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.opensymphony.xwork.ActionContext;

/**
 * This just represents a simple category for events. Such as meeting, horse show or anything else that are common.
 * 
 * @author mattias
 * 
 * @hibernate.class table="Category"
 */

public class Category implements BaseEntity
{
    private Long id;
    private String internalName;
    private String name;
    private String description;
    private Boolean active = new Boolean(true); //Default if not otherwise set
    
    private Category parent;
    private Set children;
    private Set activeChildren;

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
     * @hibernate.property name="getInternalName" column="internalName" type="string" not-null="false" unique="true"
     * 
     * @return String
     */
    public String getInternalName()
    {
        return internalName;
    }

    public void setInternalName(String internalName)
    {
        this.internalName = internalName;
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
     * This method returns a localized / parsed version of the name
     */
    public String getLocalizedName(String isoCode, String fallbackIsoCode)
    {
        Object object = findOnValueStack(isoCode);
        if(object != null)
        	isoCode = (String)object;
        
    	String localizedName = name;
    	
    	int startIndex = name.indexOf(isoCode + "=");
    	if(startIndex > -1)
    	{
    		int stopIndex = name.indexOf(",", startIndex + isoCode.length() + 1);
    		if(stopIndex > -1)
    			localizedName = name.substring(startIndex + isoCode.length() + 1, stopIndex);
    		else
    			localizedName = name.substring(startIndex + isoCode.length() + 1);    			
    	}
    	else
    	{
    		startIndex = name.indexOf(fallbackIsoCode + "=");
        	if(startIndex > -1)
        	{
        		int stopIndex = name.indexOf(",", startIndex + fallbackIsoCode.length() + 1);
        		if(stopIndex > -1)
        			localizedName = name.substring(startIndex + fallbackIsoCode.length() + 1, stopIndex);
        		else
        			localizedName = name.substring(startIndex + fallbackIsoCode.length() + 1);    			
        	}
    	}
    	
    	if(localizedName.indexOf(",") > -1)
    	{
    		localizedName = localizedName.substring(0, localizedName.indexOf(","));
    	}
		if(localizedName.indexOf("=") > -1)
		{
			localizedName = localizedName.substring(localizedName.indexOf("=") + 1);
		}
    	
    	return localizedName;
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
	
    public Boolean getActive()
    {
        return active;
    }
    
    public void setActive(Boolean active)
    {
        this.active = active;
    }
    
    public Set getChildren()
    {
        return children;
    }
    
    public void setChildren(Set children)
    {
        this.children = children;
    }
    
    public Set getActiveChildren()
    {
        return activeChildren;
    }

    public void setActiveChildren(Set activeChildren)
    {
        this.activeChildren = activeChildren;
    }

    public Category getParent()
    {
        return parent;
    }
    
    public void setParent(Category parent)
    {
        this.parent = parent;
    }
    
    public static Object findOnValueStack(String expr) 
    {
		ActionContext a = ActionContext.getContext();
		Object value = a.getValueStack().findValue(expr);
		return value;
	}

}
