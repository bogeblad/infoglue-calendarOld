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

/**
 * This class represents an access right in the system.
 * 
 * @author mattias
 * 
 * @hibernate.class table="AccessRight"
 */

public class AccessRight implements BaseEntity
{
	private Long id;
	private String name;
	private String parameters;
	
	private InterceptionPoint interceptionPoint;

	private Set roles = new HashSet();
	private Set groups = new HashSet();
	private Set users = new HashSet();

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public InterceptionPoint getInterceptionPoint()
	{
		return interceptionPoint;
	}

	public void setInterceptionPoint(InterceptionPoint interceptionPoint)
	{
		this.interceptionPoint = interceptionPoint;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getParameters()
	{
		return parameters;
	}

	public void setParameters(String parameters)
	{
		this.parameters = parameters;
	}

	public Set getRoles()
	{
		return roles;
	}

	public void setRoles(Set roles)
	{
		this.roles = roles;
	}

	public Set getGroups()
	{
		return groups;
	}

	public void setGroups(Set groups)
	{
		this.groups = groups;
	}

	public Set getUsers()
	{
		return users;
	}

	public void setUsers(Set users)
	{
		this.users = users;
	}

	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}
