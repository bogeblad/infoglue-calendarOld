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
import org.infoglue.common.exceptions.SystemException;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.util.ConstraintExceptionBuffer;
import org.infoglue.common.util.HttpUtilities;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.VelocityTemplateProcessor;
import org.infoglue.common.util.dom.DOMBuilder;
import org.infoglue.common.util.mail.MailServiceFactory;

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
    private String attributesString;
    
    private String captcha;
    private String captchaTextVariableName;

    private Long eventId;
    private Long entryId;
    private String returnAddress;
    
    private Event event;
    private Entry newEntry;
    private Long newEntryId;
    private Entry entry;
    
    private List attributes;
    
    private boolean isReserve = false;
    private String newEntryDataAsQueryString = null;
    
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

            Map<String,String> arguments = new HashMap<String,String>();
            arguments.put("firstName", "" + firstName);
            arguments.put("lastName", "" + lastName);
            arguments.put("email", "" + email);
            arguments.put("organisation", "" + organisation);
            arguments.put("address", "" + address);
            arguments.put("zipcode", "" + zipcode);
            arguments.put("city", "" + city);
            arguments.put("phone", "" + phone);
            arguments.put("fax", "" + fax);
            arguments.put("message", "" + message);
            arguments.put("xml", "" + xml);
            arguments.put("eventId", "" + eventId);
            arguments.put("languageCode", "" + this.getLocale());
            
            String eventEntryRESTService = PropertyHelper.getProperty("notificationUrl.eventEntryRESTService");
            System.out.println("eventEntryRESTService:" + eventEntryRESTService);
            
            if(eventEntryRESTService != null && eventEntryRESTService.equalsIgnoreCase("") && eventEntryRESTService.indexOf("@notificationUrl.eventEntryRESTService@") == -1)
            {
	            String response = null;
	            try
	            {
	            	response = HttpUtilities.postToUrl(eventEntryRESTService, arguments, 3000);
	            }
	            catch (Exception e) 
	            {
	            	
	    			try
	    			{
	    		        String contentType = PropertyHelper.getProperty("mail.contentType");
	    		        if(contentType == null || contentType.length() == 0)
	    		            contentType = "text/html";
	
	    				String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
	    				if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
	    					systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");
	
	    				String warningEmailReceiver = PropertyHelper.getProperty("warningEmailReceiver");
	    				if(warningEmailReceiver != null && !warningEmailReceiver.equalsIgnoreCase(""))
	    				{
	    					MailServiceFactory.getService().send(systemEmailSender, warningEmailReceiver, null, "Could not reach event registration service", "<div>Error reported trying to reach:<br/>" + eventEntryRESTService + ": " + e.getMessage() + "</div>", contentType, "UTF-8", null);
	    				}
	    			}
	    			catch(Exception e2)
	    			{
	    				log.error("The warning was not sent. Reason:" + e2.getMessage());
	    			}
	
	            	log.error("Problem reaching [" + eventEntryRESTService + "]: " + e.getMessage());
				}
	            
	            //System.out.println("response:" + response);
	            if(response == null || (response.indexOf("OK") == -1 || response.indexOf("entryId") == -1))
	            { 
	            	log.error("Failed to create entry in server:" + response.trim());
	            	throw new SystemException("Failed to create entry in server");
	            }
	            
	            System.out.println("response:" + response);
	            String entryIdString = response.substring(response.indexOf("entryId=") + 8);
	            entryIdString = entryIdString.substring(0,response.indexOf(" "));
	            System.out.println("entryIdString:" + entryIdString);
	            
	            this.newEntryId = Long.parseLong(entryIdString);
	            arguments.put("id", "" + this.newEntryId);
	            
	            ServletActionContext.getRequest().getSession().setAttribute(""+this.newEntryId, arguments);
            }
            else
            {
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
		      
	            this.newEntryId = newEntry.getId();
	            arguments.put("id", "" + newEntry.getId());
	            
	            ServletActionContext.getRequest().getSession().setAttribute(""+this.newEntryId, arguments);

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
			}
                        
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
        	//e.printStackTrace();
            return Action.ERROR;            
        }
        catch(SystemException se)
        {
        	//se.printStackTrace();
            throw se;
        }
        catch(Exception e)
        {
        	//e.printStackTrace();
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
        catch(SystemException se)
        {
        	return "errorRegistrationFailed";
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
        catch(SystemException se)
        {
        	return "errorRegistrationFailed";
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
        catch(SystemException se)
        {
        	return "errorRegistrationFailed";
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
    	System.out.println("receipt");

    	String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);
        
        event = EventController.getController().getEvent(eventId, getSession());
        //entry = EntryController.getController().getEntry(entryId, this.getSession());

        Map attributes = (Map)ServletActionContext.getRequest().getSession().getAttribute(""+entryId);
        System.out.println("attributes:" + attributes);

        entry = new Entry();
        entry.setId(entryId);
        entry.setAddress(""+attributes.get("address"));
        entry.setCity(""+attributes.get("city"));
        entry.setEmail(""+attributes.get("email"));
        entry.setEvent(event);
        entry.setFax(""+attributes.get("fax"));
        entry.setFirstName(""+attributes.get("firstName"));
        entry.setLastName(""+attributes.get("lastName"));
        entry.setMessage(""+attributes.get("message"));
        entry.setOrganisation(""+attributes.get("organisation"));
        entry.setPhone(""+attributes.get("phone"));
        entry.setZipcode(""+attributes.get("zipcode"));
        entry.setAttributes(""+attributes.get("attributesString"));

        
        int numberOfExistingEntries = event.getEntries().size();
        //if(numberOfExistingEntries > event.getMaximumParticipants())
       	if(event != null && event.getMaximumParticipants() != null && numberOfExistingEntries > event.getMaximumParticipants())
           	isReserve = true;

        return "receipt";
    } 

    public String receiptPublic() throws Exception 
    {
    	System.out.println("receiptPublic");

        String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);
        
        event = EventController.getController().getEvent(eventId, getSession());
        //entry = EntryController.getController().getEntry(entryId, this.getSession());

        Map attributes = (Map)ServletActionContext.getRequest().getSession().getAttribute(""+entryId);
        System.out.println("attributes:" + attributes);

        entry = new Entry();
        entry.setId(entryId);
        entry.setAddress(""+attributes.get("address"));
        entry.setCity(""+attributes.get("city"));
        entry.setEmail(""+attributes.get("email"));
        entry.setEvent(event);
        entry.setFax(""+attributes.get("fax"));
        entry.setFirstName(""+attributes.get("firstName"));
        entry.setLastName(""+attributes.get("lastName"));
        entry.setMessage(""+attributes.get("message"));
        entry.setOrganisation(""+attributes.get("organisation"));
        entry.setPhone(""+attributes.get("phone"));
        entry.setZipcode(""+attributes.get("zipcode"));
        entry.setAttributes(""+attributes.get("attributesString"));

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
    	System.out.println("receiptPublicGU");

    	log.info("Receipt public GU start");
        String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        log.info("requestEntryId:" + requestEntryId);
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);

        event = EventController.getController().getEvent(eventId, getSession());
        //entry = EntryController.getController().getEntry(entryId, this.getSession());

        Map attributes = (Map)ServletActionContext.getRequest().getSession().getAttribute(""+entryId);
        System.out.println("attributes:" + attributes);

        entry = new Entry();
        entry.setId(entryId);
        entry.setAddress(""+attributes.get("address"));
        entry.setCity(""+attributes.get("city"));
        entry.setEmail(""+attributes.get("email"));
        entry.setEvent(event);
        entry.setFax(""+attributes.get("fax"));
        entry.setFirstName(""+attributes.get("firstName"));
        entry.setLastName(""+attributes.get("lastName"));
        entry.setMessage(""+attributes.get("message"));
        entry.setOrganisation(""+attributes.get("organisation"));
        entry.setPhone(""+attributes.get("phone"));
        entry.setZipcode(""+attributes.get("zipcode"));
        entry.setAttributes(""+attributes.get("attributesString"));

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
    
    public String receiptFailedRegistration() throws Exception 
    {
    	log.info("receiptFailedRegistration start");
        String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        log.info("requestEntryId:" + requestEntryId);
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);

        return "receiptFailedRegistration";
    } 
    
    public String receiptPublicCustom() throws Exception 
    {
    	log.info("Receipt public GU start");
        String requestEntryId = ServletActionContext.getRequest().getParameter("entryId");
        log.info("requestEntryId:" + requestEntryId);
        if(this.entryId == null && requestEntryId != null && !requestEntryId.equalsIgnoreCase(""))
            entryId = new Long(requestEntryId);

        event = EventController.getController().getEvent(eventId, getSession());
        //entry = EntryController.getController().getEntry(entryId, this.getSession());

        Map attributes = (Map)ServletActionContext.getRequest().getSession().getAttribute(""+entryId);
        System.out.println("attributes:" + attributes);

        entry = new Entry();
        entry.setId(entryId);
        entry.setAddress(""+attributes.get("address"));
        entry.setCity(""+attributes.get("city"));
        entry.setEmail(""+attributes.get("email"));
        entry.setEvent(event);
        entry.setFax(""+attributes.get("fax"));
        entry.setFirstName(""+attributes.get("firstName"));
        entry.setLastName(""+attributes.get("lastName"));
        entry.setMessage(""+attributes.get("message"));
        entry.setOrganisation(""+attributes.get("organisation"));
        entry.setPhone(""+attributes.get("phone"));
        entry.setZipcode(""+attributes.get("zipcode"));
        entry.setAttributes(""+attributes.get("attributesString"));

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

    public String getAttributesString()
    {
        return this.attributesString;
    }
    
    public void setAttributesString(String attributesString)
    {
        this.attributesString = attributesString;
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
    public Long getNewEntryId()
    {
        return newEntryId;
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

	public String getNewEntryDataAsQueryString()
	{
		return newEntryDataAsQueryString;
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
