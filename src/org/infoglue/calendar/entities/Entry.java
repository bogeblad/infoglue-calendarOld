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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.infoglue.calendar.util.validators.BaseValidator;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeDefinition;
import org.infoglue.common.util.ConstraintExceptionBuffer;

/**
 * This class represents a persons entry to be present at the event.
 * It differs from participants in that it is volountary and that is is open for anyone to register while
 * participants are selected by the administrator of an event and only concerns internal users.
 * 
 * @author Mattias Bogeblad
 * 
 * @hibernate.class table="Entry"
 */

public class Entry implements BaseEntity
{
    private Long id;
    private String firstName;
    private String lastName;
    private String email;    
    private String organisation;
    private String address;
    private String zipcode;
    private String city;
    private String phone;
    private String fax;
    private String message;
    private String attributes;
    
    private Event event;

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
     * @hibernate.property name="getFirstName" column="firstName" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    /**
     * @hibernate.property name="getLastName" column="lastName" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getLastName()
    {
        return lastName;
    }
    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
	
    /**
     * @hibernate.property name="getEmail" column="email" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
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
        return this.getFirstName() + " " + this.getLastName();
    }

    public String getAddress()
    {
        return address;
    }
    
    public void setAddress(String address)
    {
        this.address = address;
    }
    
    public String getCity()
    {
        return city;
    }
    
    public void setCity(String city)
    {
        this.city = city;
    }
    public String getFax()
    {
        return fax;
    }
    
    public void setFax(String fax)
    {
        this.fax = fax;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public String getOrganisation()
    {
        return organisation;
    }
    
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }
    
    public String getPhone()
    {
        return phone;
    }
    
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
    
    public String getZipcode()
    {
        return zipcode;
    }
    
    public void setZipcode(String zipcode)
    {
        this.zipcode = zipcode;
    }

	public String getAttributes()
	{
		return attributes;
	}

	public void setAttributes(String attributes)
	{
		this.attributes = attributes;
	}

	public ConstraintExceptionBuffer validate(ContentTypeDefinition contentTypeDefinition)
	{
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		ceb.add(new BaseValidator().validate(contentTypeDefinition, this.getAttributes()));
		
		return ceb;
	}
	
	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}