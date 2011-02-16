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

package org.infoglue.common.settings.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.infoglue.calendar.entities.BaseEntity;
import org.infoglue.calendar.util.validators.BaseValidator;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeDefinition;
import org.infoglue.common.util.ConstraintExceptionBuffer;

/**
 * This class represents a Property entry which has a name and a value.
 * All applications that wants to have properties in their systems can inherit this class
 * and solve the persistence handling there.
 * 
 * @author Mattias Bogeblad
 */

public abstract class Property implements BaseEntity
{
    private Long id;
    private String nameSpace;
    private String name;
    private String value;
    
    /**
     * @hibernate.id generator-class="native" type="long" column="id" unsaved-value="null"
     * 
     * @return long
     */    
    public Long getId() 
    {
        return this.id;
    }
    
    public void setId(Long id) 
    {
        this.id = id;
    }
    
    /**
     * @hibernate.property name="getNameSpace" column="nameSpace" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getNameSpace()
    {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace)
    {
        this.nameSpace = nameSpace;
    }

    /**
     * @hibernate.property name="getName" column="name" type="string" not-null="false" unique="false"
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

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public ConstraintExceptionBuffer validate()
	{
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		//ceb.add(new BaseValidator().validate(contentTypeDefinition, this.value()));
		
		return ceb;
	}
	
	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}