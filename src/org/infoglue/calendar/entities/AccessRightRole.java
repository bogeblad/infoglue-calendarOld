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
 * This class represents an access right role in the system.
 * 
 * @author mattias
 * 
 * @hibernate.class table="AccessRightRole"
 */


public class AccessRightRole implements BaseEntity
{
	private Long id;
	private String name;
	
	private AccessRight accessRight;

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
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public AccessRight getAccessRight()
	{
		return accessRight;
	}

	public void setAccessRight(AccessRight accessRight)
	{
		this.accessRight = accessRight;
	}
    
	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}
