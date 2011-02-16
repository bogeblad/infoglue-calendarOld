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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.infoglue.calendar.controllers.AccessRightController;
import org.infoglue.calendar.controllers.ContentTypeDefinitionController;
import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.util.ConstraintExceptionBuffer;
import org.infoglue.common.util.VelocityTemplateProcessor;
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

public class CreateEntryAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(CreateEntryAction.class);

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
    
    private String captcha;
    private String captchaTextVariableName;

    private Long eventId;
    private Long entryId;
    private String returnAddress;
    
    private Event event;
    private Entry newEntry;
    private Entry entry;
    
    private List attributes;
    
    private boolean isReserve = false;

    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        /*
        if(useEntryLimitation())
        {
		    Event event = EventController.getController().getEvent(eventId, getSession());
	        List entries = EntryController.getController().getEntryList(null, null, null, eventId, null, null, getSession());
	        
	        if(event.getMaximumParticipants() != null && event.getMaximumParticipants().intValue() <= entries.size())
	            return "maximumReachedPublic";
        }
        
        */
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
            log.info("idKey:" + idKey);
            while(idKey != null && idKey.length() > 0)
            {
            	log.debug("idKey in loop: " + idKey);
            	
                String[] value = ServletActionContext.getRequest().getParameterValues(idKey);
                if(value == null || value.length == 0)
                	value = new String[0];
                    //this.addFieldError(idKey, "errors.atLeastOneItem");

                log.debug(idKey + "=" + value);
                log.info("value:" + value);
                
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
                log.info("idKey:" + idKey);
            }
            
            String xml = domBuilder.getFormattedDocument(document, "UTF-8");
            log.debug("xml:" + xml);
            
            ServletActionContext.getRequest().getSession().setAttribute("attributes", attributes);
        	
            Event event = EventController.getController().getEvent(eventId, getSession());
            int numberOfExistingEntries = event.getEntries().size();
            if(event != null && event.getMaximumParticipants() != null && numberOfExistingEntries >= event.getMaximumParticipants())
            	isReserve = true;
            
            EventType eventType = EventTypeController.getController().getEventType(event.getEntryFormId(), getSession());
            
            Entry entry = new Entry();
            entry.setAttributes(xml);
            ConstraintExceptionBuffer ceb = entry.validate(eventType);
            ActionContext.getContext().getValueStack().getContext().put("errorEntry", entry);
            log.debug("Added error entry to stack:" + entry.getAttributes());
            
            //System.out.println("captchaTextVariableName:" + captchaTextVariableName);
            boolean isCaptchaOk = true;
            //if(captchaTextVariableName != null && !captchaTextVariableName.equals(""))
            //	isCaptchaOk = validateCaptcha(captcha, (String)ServletActionContext.getRequest().getSession().getAttribute(captchaTextVariableName));
            
            validateInput(this, ceb, isCaptchaOk);

	        newEntry = EntryController.getController().createEntry(firstName, 
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
	                									eventId,
	                									getSession());
	        
	        EntryController.getController().mailVerification(newEntry, this.getLocale(), isReserve, getSession());
	        
	        InfoGluePrincipalBean principalBean = null;
	        try
	        {
	        	principalBean = this.getInfoGluePrincipal();
	        }
	        catch(Exception e)
	        {
	        	System.out.println("Problem getting current principal - falling back:" + e.getMessage());
		        try
		        {
		        	principalBean = new InfoGluePrincipalBean();
		        	principalBean.setDisplayName("Anonymous");
		        	principalBean.setEmail(email);
		        	principalBean.setName("anonymous");
		        	principalBean.setFirstName("Anomymous");
		        	principalBean.setLastName("User");
		        }
		        catch (Exception e2) 
		        {
		        	System.out.println("Problem getting fallback principal - falling back:" + e2.getMessage());
				}
	        }
	        EntryController.getController().notifyEventOwner(newEntry, this.getLocale(), principalBean, getSession());

	        String presentationTemplate = getPresentationTemplate();
	        log.info("presentationTemplate:" + presentationTemplate);
	        if(presentationTemplate != null && !presentationTemplate.equals(""))
	        {
			    Map parameters = new HashMap();
			    parameters.put("event", event);
			    parameters.put("attributes", this.attributes);
			    parameters.put("this", this);
			    
				StringWriter tempString = new StringWriter();
				PrintWriter pw = new PrintWriter(tempString);
				new VelocityTemplateProcessor().renderTemplate(parameters, pw, presentationTemplate);
				String renderedString = tempString.toString();
				setRenderedString(renderedString);
	        }
        }
        catch(ValidationException e)
        {
            return Action.ERROR;            
        }
        catch(Exception e)
        {
            throw e;
        }

        return Action.SUCCESS;
    } 
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String doPublic() throws Exception 
    {
        /*
        if(useEntryLimitation())
        {
		    Event event = EventController.getController().getEvent(eventId, getSession());
	        List entries = EntryController.getController().getEntryList(null, null, null, eventId, null, null, getSession());
	        
	        if(event.getMaximumParticipants() != null && event.getMaximumParticipants().intValue() <= entries.size())
	            return "maximumReachedPublic";
        }
        */
        
        try
        {
            validateInput(this);

            this.execute();
        }
        catch(ValidationException e)
        {
            return Action.ERROR + "Public";            
        }
        
        if(getRenderedString() != null && !getRenderedString().equals(""))
			return Action.SUCCESS + "RenderedTemplate";
        
        return Action.SUCCESS + "Public";
    } 

    public String doPublicGU() throws Exception 
    {
        /*
        if(useEntryLimitation())
        {
		    Event event = EventController.getController().getEvent(eventId, getSession());
	        List entries = EntryController.getController().getEntryList(null, null, null, eventId, null, null, getSession());
	        
	        if(event.getMaximumParticipants() != null && event.getMaximumParticipants().intValue() <= entries.size())
	            return "maximumReachedPublic";
        }
        */
        
        try
        {
            validateInput(this);

            this.execute();
        }
        catch(ValidationException e)
        {
            return Action.ERROR + "PublicGU";            
        }

        if(getRenderedString() != null && !getRenderedString().equals(""))
			return Action.SUCCESS + "RenderedTemplate";
        
        return Action.SUCCESS + "PublicGU";
    } 

    public String doPublicCustom() throws Exception 
    {
        /*
        if(useEntryLimitation())
        {
		    Event event = EventController.getController().getEvent(eventId, getSession());
	        List entries = EntryController.getController().getEntryList(null, null, null, eventId, null, null, getSession());
	        
	        if(event.getMaximumParticipants() != null && event.getMaximumParticipants().intValue() <= entries.size())
	            return "maximumReachedPublic";
        }
        */
        
        try
        {
            validateInput(this);

            this.execute();
        }
        catch(ValidationException e)
        {
            return Action.ERROR + "PublicCustom";            
        }
        
        if(getRenderedString() != null && !getRenderedString().equals(""))
			return Action.SUCCESS + "RenderedTemplate";

        return Action.SUCCESS + "PublicCustom";
    } 

    
    /**
     * This is the entry point creating a new calendar.
     */
    
    public String input() throws Exception 
    {
        event = EventController.getController().getEvent(eventId, getSession());

        EventType eventType = EventTypeController.getController().getEventType(event.getEntryFormId(), getSession());
        
		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());
        /*
        if(useEntryLimitation())
        {
	        List entries = EntryController.getController().getEntryList(null, null, null, eventId, null, null, getSession());
	        
	        if(event.getMaximumParticipants() != null && event.getMaximumParticipants().intValue() <= entries.size())
	            return "maximumReached";
        }
        */
        return Action.INPUT;
    } 
    
    /**
     * This is the entry point creating a new calendar.
     */
    
    public String inputPublic() throws Exception 
    {
	    event = EventController.getController().getEvent(eventId, getSession());

        EventType eventType = EventTypeController.getController().getEventType(event.getEntryFormId(), getSession());
        
		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());
		/*
	    if(useEntryLimitation())
        {
	        List entries = EntryController.getController().getEntryList(null, null, null, eventId, null, null, getSession());
	        
	        if(event.getMaximumParticipants() != null && event.getMaximumParticipants().intValue() <= entries.size())
	            return "maximumReachedPublic";
        }
        */
		
        String presentationTemplate = getPresentationTemplate();
        log.info("presentationTemplate:" + presentationTemplate);
        if(presentationTemplate != null && !presentationTemplate.equals(""))
        {
		    Map parameters = new HashMap();
		    parameters.put("event", event);
		    parameters.put("attributes", this.attributes);
		    parameters.put("this", this);
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, presentationTemplate);
			String renderedString = tempString.toString();
			setRenderedString(renderedString);
			return Action.SUCCESS + "RenderedTemplate";
        }

        return Action.INPUT + "Public";
    } 

    /**
     * This is the entry point creating a new calendar.
     */
    
    public String inputPublicGU() throws Exception 
    {
        event = EventController.getController().getEvent(eventId, getSession());

        EventType eventType = EventTypeController.getController().getEventType(event.getEntryFormId(), getSession());
        
		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());
	    /*
        if(useEntryLimitation())
        {
		    List entries = EntryController.getController().getEntryList(null, null, null, eventId, null, null, getSession());
	        
	        if(event.getMaximumParticipants() != null && event.getMaximumParticipants().intValue() <= entries.size())
	            return "maximumReachedPublic";
        }
        */
        
        String presentationTemplate = getPresentationTemplate();
        log.info("presentationTemplate:" + presentationTemplate);
        if(presentationTemplate != null && !presentationTemplate.equals(""))
        {
		    Map parameters = new HashMap();
		    parameters.put("event", event);
		    parameters.put("attributes", this.attributes);
		    parameters.put("this", this);
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, presentationTemplate);
			String renderedString = tempString.toString();
			setRenderedString(renderedString);
			return Action.SUCCESS + "RenderedTemplate";
        }

        return Action.INPUT + "PublicGU";
    } 
    
    /**
     * This is the entry point creating a new calendar.
     */
    
    public String inputPublicCustom() throws Exception 
    {
    	event = EventController.getController().getEvent(eventId, getSession());

        EventType eventType = EventTypeController.getController().getEventType(event.getEntryFormId(), getSession());
        
		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());
	    /*
        if(useEntryLimitation())
        {
		    List entries = EntryController.getController().getEntryList(null, null, null, eventId, null, null, getSession());
	        
	        if(event.getMaximumParticipants() != null && event.getMaximumParticipants().intValue() <= entries.size())
	            return "maximumReachedPublic";
        }
        */
        
        String presentationTemplate = getPresentationTemplate();
        log.info("presentationTemplate:" + presentationTemplate);
        if(presentationTemplate != null && !presentationTemplate.equals(""))
        {
		    Map parameters = new HashMap();
		    parameters.put("event", event);
		    parameters.put("attributes", this.attributes);
		    parameters.put("this", this);
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, presentationTemplate);
			String renderedString = tempString.toString();
			setRenderedString(renderedString);
			return Action.SUCCESS + "RenderedTemplate";
        }
        
        return Action.INPUT + "PublicCustom";
    } 

    public String receipt() throws Exception 
    {
        String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);
        
        event = EventController.getController().getEvent(eventId, getSession());
        entry = EntryController.getController().getEntry(entryId, this.getSession());
        
        int numberOfExistingEntries = event.getEntries().size();
        //if(numberOfExistingEntries > event.getMaximumParticipants())
       	if(event != null && event.getMaximumParticipants() != null && numberOfExistingEntries > event.getMaximumParticipants())
           	isReserve = true;

        return "receipt";
    } 

    public String receiptPublic() throws Exception 
    {
        String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);
        
        event = EventController.getController().getEvent(eventId, getSession());
        entry = EntryController.getController().getEntry(entryId, this.getSession());

        int numberOfExistingEntries = event.getEntries().size();
        //if(numberOfExistingEntries > event.getMaximumParticipants())
       	if(event != null && event.getMaximumParticipants() != null && numberOfExistingEntries > event.getMaximumParticipants())
          	isReserve = true;

       	String presentationTemplate = getPresentationTemplate();
        log.info("presentationTemplate:" + presentationTemplate);
        if(presentationTemplate != null && !presentationTemplate.equals(""))
        {
		    Map parameters = new HashMap();
		    parameters.put("event", event);
		    parameters.put("entry", entry);
		    parameters.put("isReserve", isReserve);
		    parameters.put("this", this);
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, presentationTemplate);
			String renderedString = tempString.toString();
			setRenderedString(renderedString);
			return Action.SUCCESS + "RenderedTemplate";
        }

        return "receiptPublic";
    } 

    public String receiptPublicGU() throws Exception 
    {
    	log.info("Receipt public GU start");
        String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        log.info("requestEntryId:" + requestEntryId);
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);

        event = EventController.getController().getEvent(eventId, getSession());
        entry = EntryController.getController().getEntry(entryId, this.getSession());

        int numberOfExistingEntries = event.getEntries().size();
        //if(numberOfExistingEntries > event.getMaximumParticipants())
       	if(event != null && event.getMaximumParticipants() != null && numberOfExistingEntries > event.getMaximumParticipants())
           	isReserve = true;

       	String presentationTemplate = getPresentationTemplate();
        log.info("presentationTemplate:" + presentationTemplate);
        if(presentationTemplate != null && !presentationTemplate.equals(""))
        {
		    Map parameters = new HashMap();
		    parameters.put("event", event);
		    parameters.put("entry", entry);
		    parameters.put("isReserve", isReserve);
		    parameters.put("this", this);
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, presentationTemplate);
			String renderedString = tempString.toString();
			setRenderedString(renderedString);
			return Action.SUCCESS + "RenderedTemplate";
        }

        return "receiptPublicGU";
    } 
    
    public String receiptPublicCustom() throws Exception 
    {
    	log.info("Receipt public GU start");
        String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        log.info("requestEntryId:" + requestEntryId);
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);

        event = EventController.getController().getEvent(eventId, getSession());
        entry = EntryController.getController().getEntry(entryId, this.getSession());

        int numberOfExistingEntries = event.getEntries().size();
        //if(numberOfExistingEntries > event.getMaximumParticipants())
       	if(event != null && event.getMaximumParticipants() != null && numberOfExistingEntries > event.getMaximumParticipants())
           	isReserve = true;

       	String presentationTemplate = getPresentationTemplate();
        log.info("presentationTemplate:" + presentationTemplate);
        if(presentationTemplate != null && !presentationTemplate.equals(""))
        {
		    Map parameters = new HashMap();
		    parameters.put("event", event);
		    parameters.put("entry", entry);
		    parameters.put("isReserve", isReserve);
		    parameters.put("this", this);
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, presentationTemplate);
			String renderedString = tempString.toString();
			setRenderedString(renderedString);
			return Action.SUCCESS + "RenderedTemplate";
        }

        return "receiptPublicCustom";
    } 
    
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getFirstName()
    {
    	log.debug("firstName:" + firstName);
        return firstName;
    }
    
    public void setFirstName(String firstName)
    {
    	log.debug("set firstName:" + firstName);
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
    
    public Long getEventId()
    {
        return eventId;
    }
    
    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
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
    public String getReturnAddress()
    {
        return returnAddress;
    }
    public void setReturnAddress(String returnAddress)
    {
        this.returnAddress = returnAddress;
    }
    public Event getEvent()
    {
        return event;
    }
    public Entry getNewEntry()
    {
        return newEntry;
    }
    public Long getEntryId()
    {
        return entryId;
    }
    public void setEntryId(Long entryId)
    {
        this.entryId = entryId;
    }
    public Entry getEntry()
    {
        return entry;
    }

	public List getAttributes()
	{
		return attributes;
	}

	public boolean getIsReserve()
	{
		return isReserve;
	}
	
	public void setCaptcha(String captcha)
	{
		this.captcha = captcha;
	}
	
	public String getCaptcha()
	{
		return captcha;
	}
	
	public void setCaptchaTextVariableName(String captchaTextVariableName)
	{
		this.captchaTextVariableName = captchaTextVariableName;
	}
	
	public String getCaptchaTextVariableName()
	{
		return captchaTextVariableName;
	}
}
