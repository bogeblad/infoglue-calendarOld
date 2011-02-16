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

package org.infoglue.common.contenttypeeditor.entities;

import org.infoglue.common.util.ConstraintExceptionBuffer;

public interface ContentTypeDefinition
{ 
	public static final Integer CONTENT = new Integer(0);
	public static final Integer EXTRANET_ROLE_PROPERTIES	= new Integer(1);
	public static final Integer EXTRANET_USER_PROPERTIES  	= new Integer(2);     
	public static final Integer EXTRANET_GROUP_PROPERTIES 	= new Integer(3);     
  
    public Long getId(); 
    
    public void setId(Long id);
	
    public java.lang.String getName();
                
    public void setName(java.lang.String name);
    
    public java.lang.String getSchemaValue();
                
    public void setSchemaValue(java.lang.String schemaValue);
    
	public java.lang.Integer getType();

	public void setType(java.lang.Integer type);
	
	//public ConstraintExceptionBuffer validate(); 
	
	/*
	public static final Integer CONTENT = new Integer(0);
	public static final Integer EXTRANET_ROLE_PROPERTIES	= new Integer(1);
	public static final Integer EXTRANET_USER_PROPERTIES  	= new Integer(2);     
	public static final Integer EXTRANET_GROUP_PROPERTIES 	= new Integer(3);     

    private Long id;
    private java.lang.String name;
    private java.lang.String schemaValue;
    private java.lang.Integer type = CONTENT;
  
    public Long getId() 
    {
        return this.id;
    }
    
    public void setId(Long id) 
    {
        this.id = id;
    }
	
    public java.lang.String getName()
    {
        return this.name;
    }
                
    public void setName(java.lang.String name)
    {
        this.name = name;
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
	
	public ConstraintExceptionBuffer validate() 
	{ 
		return null;
	}
	*/
}
        
