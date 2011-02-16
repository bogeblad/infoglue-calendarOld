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

package org.infoglue.calendar.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.common.util.ConstraintExceptionBuffer;
import org.infoglue.common.util.dom.DOMBuilder;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class UpdateEntryAction extends CalendarUploadAbstractAction
{
	private static Log log = LogFactory.getLog(UpdateEntryAction.class);

    private Long entryId;
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

    private Long[] searchEventId;
    private String searchFirstName;
    private String searchLastName;
    private String searchEmail;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        try
        {
	    	Map attributeValues = new HashMap();
	    	
	    	DOMBuilder domBuilder = new DOMBuilder();
	    	Document document = domBuilder.createDocument();
	    	Element articleElement = domBuilder.addElement(document, "entry");
	    	//domBuilder.addAttribute(articleElement, "xmlns", "x-schema:ArticleSchema.xml");
	    	Element attributesElement = domBuilder.addElement(articleElement, "attributes");
	    	        	
	        int i = 0;
	        String idKey = ServletActionContext.getRequest().getParameter("attributeName_" + i);
	        log.debug("idKey:" + idKey);
	        while(idKey != null && idKey.length() > 0)
	        {
	            String[] value = ServletActionContext.getRequest().getParameterValues(idKey);
	            if(value == null || value.length == 0)
	                value = new String[0];
	            	//this.addFieldError(idKey, "errors.atLeastOneItem");
            	
	            log.debug(idKey + "=" + value);
	            
	            String valueString = "";
	            if(value != null)
	            {
		            for(int j=0; j<value.length; j++)
		            {
		            	if(j>0)
		            		valueString += ",";
		            	
		            	valueString += value[j];
		            }
	            }
	            
	            int index = idKey.indexOf("attribute_");
	            if(index == -1)
	            	index = 0;
	            else
	            	index = index + 10;
	            
	            Element element = domBuilder.addElement(attributesElement, idKey.substring(index));
	            domBuilder.addCDATAElement(element, valueString);
	        	
	            attributeValues.put(idKey, value);
	            
	            i++;
	            idKey = ServletActionContext.getRequest().getParameter("attributeName_" + i);
	        }
	
	        String xml = domBuilder.getFormattedDocument(document, "UTF-8");
	        log.debug("xml:" + xml);
	
	        Entry entry = EntryController.getController().getEntry(entryId, getSession());
	        EventType eventType = EventTypeController.getController().getEventType(entry.getEvent().getEntryFormId(), getSession());
	
	        Entry errorEntry = new Entry();
	        errorEntry.setAttributes(xml);
	        ConstraintExceptionBuffer ceb = errorEntry.validate(eventType);
	        ActionContext.getContext().getValueStack().getContext().put("errorEntry", errorEntry);
	        
	        validateInput(this, ceb);
	
	        EntryController.getController().updateEntry(entryId, 
	                									firstName, 
	                									lastName, 
	                									email,
	                									organisation,
	                									address,
	                									zipcode,
	                									city,
	                									phone,
	                									fax,
	                									message,
	                									xml,
	                									getSession());
	        

	    }
	    catch(ValidationException e)
	    {
	        return Action.ERROR;            
	    }

        return Action.SUCCESS;
    } 
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public Long getEntryId()
    {
        return entryId;
    }
    
    public void setEntryId(Long entryId)
    {
        this.entryId = entryId;
    }
    
    public String getFirstName()
    {
        return firstName;
    }
    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    public String getLastName()
    {
        return lastName;
    }
    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
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
    
    public String getSearchEmail()
    {
        return searchEmail;
    }
    public void setSearchEmail(String searchEmail)
    {
        this.searchEmail = searchEmail;
    }
    public Long[] getSearchEventId()
    {
        return searchEventId;
    }
    public void setSearchEventId(Long[] searchEventId)
    {
        this.searchEventId = searchEventId;
    }
    public String getSearchFirstName()
    {
        return searchFirstName;
    }
    public void setSearchFirstName(String searchFirstName)
    {
        this.searchFirstName = searchFirstName;
    }
    public String getSearchLastName()
    {
        return searchLastName;
    }
    public void setSearchLastName(String searchLastName)
    {
        this.searchLastName = searchLastName;
    }
}
