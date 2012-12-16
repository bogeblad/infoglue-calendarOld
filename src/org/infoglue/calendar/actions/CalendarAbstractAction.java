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

import java.io.File;
import java.security.Principal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.infoglue.calendar.entities.Resource;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.infoglue.calendar.controllers.AccessRightController;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.CalendarLabelsController;
import org.infoglue.calendar.controllers.CalendarSettingsController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.ICalendarController;
import org.infoglue.calendar.controllers.LanguageController;
import org.infoglue.calendar.controllers.ParticipantController;
import org.infoglue.calendar.controllers.ResourceController;
import org.infoglue.calendar.entities.Category;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventCategory;
import org.infoglue.calendar.entities.EventTiny;
import org.infoglue.calendar.entities.EventTypeCategoryAttribute;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.entities.EventVersionTiny;
import org.infoglue.calendar.entities.Language;
import org.infoglue.calendar.entities.Participant;
import org.infoglue.calendar.util.AttributeType;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.util.VisualFormatter;
import org.infoglue.common.exceptions.ConstraintException;
import org.infoglue.common.util.ActionValidatorManager;
import org.infoglue.common.util.ConstraintExceptionBuffer;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.ResourceBundleHelper;
import org.infoglue.common.util.Timer;
import org.infoglue.common.util.WebServiceHelper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.validator.ValidationException;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;


/**
* @author Mattias Bogeblad
*
* This is an abstract action used for all calendar actions.
*  Just to not have to put to much in the WebworkAbstractAction.
*/

public class CalendarAbstractAction extends ActionSupport
{
	private static Log log = LogFactory.getLog(CalendarAbstractAction.class);
	private String renderedString = null;
	
	/**
	 * This method lets the velocity template get hold of all actions inheriting.
	 * 
	 * @return The action object currently invoked 
	 */
	
	public CalendarAbstractAction getThis()
	{
		return this;
	}

	
    public Map getInternalEventMap()
    {
        Map yesOrNo = new HashMap();
        yesOrNo.put("true", "labels.internal.event.isInternal.true");
        yesOrNo.put("false", "labels.internal.event.isInternal.false");
        
        return yesOrNo;
    }

    public Map getIsOrganizedByGUMap()
    {
        Map yesOrNo = new HashMap();
        yesOrNo.put("true", "labels.internal.event.isOrganizedByGU");
        
        return yesOrNo;
    }

    public List getAttributeTypes()
    {    	
    	List list = new ArrayList();
        
        //list.add("label", "Label");
        list.add(new AttributeType("textfield", "TextField"));
        list.add(new AttributeType("textarea", "TextArea"));
        list.add(new AttributeType("checkbox", "Checkbox"));
        list.add(new AttributeType("radiobutton", "RadioButton"));
        list.add(new AttributeType("select", "SelectBox"));
        list.add(new AttributeType("hidden", "Hidden"));
        //list.add("password", "Label");
        //list.add("image", "Label");
        //list.add("submit", "Label");
        //list.add("clear", "Label");
        
        return list;
    }

    public List getItemsPerPageMap()
    {
    	List items = new ArrayList();
    	
        items.add(new String[]{"10", "10"});
        items.add(new String[]{"20", "20"});
        items.add(new String[]{"50", "50"});
        items.add(new String[]{"100", "100"});
        items.add(new String[]{"200", "200"});
        items.add(new String[]{"500", "500"});
        items.add(new String[]{"-1", "Alla"});
        
        return items;
    }

    public List getEventTypes()
    {    	
    	List list = new ArrayList();
        
        list.add(new AttributeType("0", "Evenemangsdefinition"));
        list.add(new AttributeType("1", "Anm�lningsformul�r"));
        
        return list;
    }

    public Map getAndSearch()
    {
        Map yesOrNo = new HashMap();
        yesOrNo.put("true", "labels.internal.soba.andSearchTrue");
        
        return yesOrNo;
    }

    public Map getYesNoMap()
    {
        Map yesOrNo = new HashMap();
        yesOrNo.put("true", "true");
        
        return yesOrNo;
    }

    public String concat(String start, String end)
    {
    	return start + end;
    }
    
    public Integer getSiteNodeId()
    {
    	Object temp = ServletActionContext.getRequest().getAttribute("siteNodeId");
    	if(temp != null){
    		if(temp instanceof String){
    			try{
    				Integer siteNodeId = new Integer((String)temp);
    				return siteNodeId;
    			}
    			catch(NumberFormatException e){
    				log.error("Error parsing siteNodeId " + temp);
    				return null;
    			}    			
    		}
    		else{
    	        return (Integer)ServletActionContext.getRequest().getAttribute("siteNodeId");
    		}
    	}
    	return null;
    }
    
    public List<Long> getEventIds()
    {
        return (List<Long>)ServletActionContext.getRequest().getAttribute("eventIds");
    }

    public Map<Long, String> getSupplementingImages()
    {
        return (Map<Long, String>)ServletActionContext.getRequest().getAttribute("supplementingImages");
    }

    public Integer getComponentId()
    {
        return (Integer)ServletActionContext.getRequest().getAttribute("componentId");
    }

    public String getLanguageCode()
    {
    	String languageCode = (String)ServletActionContext.getRequest().getAttribute("languageCode");
       	if(languageCode == null || languageCode.equals(""))
    		languageCode = (String)ServletActionContext.getRequest().getParameter("languageCode");

    	if(languageCode == null || languageCode.equals(""))
    		languageCode = "en";
    	
        return languageCode;
    }
  
    public Locale getLocale()
    {
    	String languageCode = getLanguageCode();
    	if(languageCode == null || languageCode.equals(""))
    		languageCode = "en";
    	
    	return new Locale(languageCode);
    }

    public Language getLanguage() throws Exception
    {
    	String languageCode = getLanguageCode();
    	if(languageCode == null || languageCode.equals(""))
    		languageCode = "en";
    	
    	return LanguageController.getController().getLanguageWithCode(languageCode, getSession());
    }

    public String getLogoutUrl()
    {
        return (String)ServletActionContext.getRequest().getAttribute("logoutUrl");
    }

    public String getCSSUrl()
    {
        return (String)ServletActionContext.getRequest().getAttribute("cssUrl");
    }

    public Integer getNumberOfItemsPerPage()
    {
        return (Integer)ServletActionContext.getRequest().getAttribute("numberOfItems");
    }

    public String getUseTinyEventsInFilteredList()
    {
        return (String)ServletActionContext.getRequest().getAttribute("useTinyEventsInFilteredList");
    }

    public String getUploadMaxSize()
    {
        return PropertyHelper.getProperty("uploadMaxSize");
    }
    
    public String getInfoGlueRemoteUser()
    {
    	return (String)ServletActionContext.getRequest().getAttribute("infoglueRemoteUser");
    }

    public List getInfoGlueRemoteUserRoles()
    {
        return (List)ServletActionContext.getRequest().getAttribute("infoglueRemoteUserRoles");
    }

    public List getInfoGlueRemoteUserGroups()
    {
        return (List)ServletActionContext.getRequest().getAttribute("infoglueRemoteUserGroups");
    }

    public String getStartDateTime()
    {
        return (String)ServletActionContext.getRequest().getAttribute("startDateTime");
    }

    public String getEndDateTime()
    {
        return (String)ServletActionContext.getRequest().getAttribute("endDateTime");
    }

    public String getCalendarMonth()
    {
    	return (String)ServletActionContext.getRequest().getAttribute("calendarMonth");
    }

    public String getFreeText()
    {
        return (String)ServletActionContext.getRequest().getAttribute("freeText");
    }

    public String getCategoryAttribute()
    {
        return (String)ServletActionContext.getRequest().getAttribute("categoryAttribute");
    }

    public String getCategoryNames()
    {
        return (String)ServletActionContext.getRequest().getAttribute("categoryNames");
    }

    public String getContainerDivHTMLStart()
    {
        return (String)ServletActionContext.getRequest().getAttribute("containerDivHTMLStart");
    }

    public String getContainerDivHTMLEnd()
    {
        return (String)ServletActionContext.getRequest().getAttribute("containerDivHTMLEnd");
    }

    public String getSkipRowDiv()
    {
        return (String)ServletActionContext.getRequest().getAttribute("skipRowDiv");
    }

    public String getRowDivHTMLStart()
    {
        return (String)ServletActionContext.getRequest().getAttribute("rowDivHTMLStart");
    }

    public String getRowDivHTMLEnd()
    {
        return (String)ServletActionContext.getRequest().getAttribute("rowDivHTMLEnd");
    }

    public String getPresentationTemplate()
    {
        return (String)ServletActionContext.getRequest().getAttribute("presentationTemplate");
    }

    public InfoGluePrincipalBean getInfoGluePrincipal() throws Exception
    {
    	try
    	{
            return AccessRightController.getController().getPrincipal(this.getInfoGlueRemoteUser());
    		//return UserControllerProxy.getController().getUser(this.getInfoGlueRemoteUser());
    	}
    	catch(Exception e)
    	{
    		log.error("Could not get infoglue user:" + e.getMessage());
    		log.warn("Could not get infoglue user:" + e.getMessage(), e);
    		throw e;
    	}
    }

    /*
    public String getRequestClass()
    {
    	HttpServletRequest request = ServletActionContext.getRequest();
    	
    	RenderRequest renderRequest = (RenderRequest)request.getAttribute("javax.portlet.request");
    	log.debug("renderRequest:" + renderRequest.getRemoteUser());
    	log.debug("Session ID in portlet: " + ServletActionContext.getRequest().getRequestedSessionId());

    	return (String)ServletActionContext.getRequest().getRemoteUser();
    }
    */

    public boolean getIsEventOwner(Long eventId) throws Exception
    {
        return getIsEventOwner(EventController.getController().getEvent(eventId, getSession()));
    }

    public List getAnonymousCalendars() throws Exception
    {
    	String anonymousCalendar = PropertyHelper.getProperty("anonymousCalendar");
    	log.info("anonymousCalendar:" + anonymousCalendar);
    	if(anonymousCalendar == null)
    		anonymousCalendar = "";
    	
    	return CalendarController.getController().getCalendar(anonymousCalendar, getSession());
    }
    
    public boolean getIsEventOwner(Event event)
    {
        boolean isEventOwner = false;
        
        try
        {
            org.infoglue.calendar.entities.Calendar owningCalendar = event.getOwningCalendar();
            if(owningCalendar != null)
            {
	            log.info("owningCalendar.getOwningRoles():" + owningCalendar.getOwningRoles());
	            log.info("this.getInfoGlueRemoteUserGroups():" + this.getInfoGlueRemoteUserGroups());
		        if(owningCalendar.getOwningRoles().size() > 0 && this.getInfoGlueRemoteUserGroups().size() == 0)
		        {
		            isEventOwner = false;
		        }
		        else
		        {
		            Set<org.infoglue.calendar.entities.Calendar> calendars = CalendarController.getController().getCalendarList(this.getInfoGlueRemoteUserRoles(), this.getInfoGlueRemoteUserGroups(), getSession());
			        log.info("calendars: " + calendars);			        
			        if(calendars.contains(owningCalendar))
			            isEventOwner = true;
		        }
            }
	    }
        catch(Exception e)
        {
            log.warn("Error occurred:" + e.getMessage(), e);
        }
        
        return isEventOwner;
    }

    
    public boolean getIsCalendarAdministrator(org.infoglue.calendar.entities.Calendar calendar)
    {
        boolean isCalendarOwner = false;
        
        try
        {
            log.info("calendar.getOwningRoles():" + calendar.getOwningRoles());
            log.info("this.getInfoGlueRemoteUserGroups():" + this.getInfoGlueRemoteUserGroups());
	        if(calendar.getOwningRoles().size() > 0 && this.getInfoGlueRemoteUserGroups().size() == 0)
	        {
	        	isCalendarOwner = false;
	        }
	        else
	        {
	            Set<org.infoglue.calendar.entities.Calendar> calendars = CalendarController.getController().getCalendarList(this.getInfoGlueRemoteUserRoles(), this.getInfoGlueRemoteUserGroups(), getSession());

		        if(calendars.contains(calendar))
		        	isCalendarOwner = true;
	        }
	    }
        catch(Exception e)
        {
            log.warn("Error occurred:" + e.getMessage(), e);
        }
        
        return isCalendarOwner;
    }

    public boolean getIsEventCreator(Event event)
    {
        boolean isEventCreator = false;
        
        try
        {
        	if(this.getInfoGlueRemoteUser() == null)
        		log.warn("InfoGlue remote user is null - should not happen..");
        	else
        	{
        		if(event.getCreator() != null && event.getCreator().equalsIgnoreCase(this.getInfoGlueRemoteUser()))
        			isEventCreator = true;           
        	}
	    }
        catch(Exception e)
        {
            log.warn("Error occurred:" + e.getMessage(), e);
        }
        
        return isEventCreator;
    }

    public List getEventCategories(String eventString, EventTypeCategoryAttribute categoryAttribute)
    {        
        Object object = findOnValueStack(eventString);
        if(object instanceof EventTiny)
        {
	        EventTiny event = (EventTiny)object;
	        
	        List categories = new ArrayList();
	        
	        Iterator i = event.getEventCategories().iterator();
	        while(i.hasNext())
	        {
	            EventCategory eventCategory = (EventCategory)i.next();
	            if(eventCategory.getEventTypeCategoryAttribute().getId().equals(categoryAttribute.getId()))
	                categories.add(eventCategory.getCategory());
	        }

	        return categories;
        }
        else
        {
        	Event event = (Event)object;
	        
	        List categories = new ArrayList();
	        
	        Iterator i = event.getEventCategories().iterator();
	        while(i.hasNext())
	        {
	            EventCategory eventCategory = (EventCategory)i.next();
	            if(eventCategory.getEventTypeCategoryAttribute().getId().equals(categoryAttribute.getId()))
	                categories.add(eventCategory.getCategory());
	        }

	        return categories;
        }
    }

    public List getEventCategories(Event event, EventTypeCategoryAttribute categoryAttribute)
    {        
        List categories = new ArrayList();
        
        Iterator i = event.getEventCategories().iterator();
        while(i.hasNext())
        {
            EventCategory eventCategory = (EventCategory)i.next();
            if(eventCategory.getEventTypeCategoryAttribute().getId().equals(categoryAttribute.getId()))
                categories.add(eventCategory.getCategory());
        }

        return categories;
    }

    public Collection getSortedChildren(Set<Category> categories)
    {
    	Locale locale = new Locale(this.getLanguageCode());
    	
    	SortedMap<String,Category> sortedChildren = new TreeMap<String,Category>();
    	for(Category category : categories)
    	{
    		sortedChildren.put(category.getLocalizedName(this.getLanguageCode(), "sv").toLowerCase(), category);
    	}
    	
    	return sortedChildren.values();
    }

    public String getState(Integer stateId)
    {
        if(stateId == null)
            return "None";
        
        if(stateId.intValue() == 0)
            return getLabel("labels.state.working");
        if(stateId.intValue() == 2)
            return getLabel("labels.state.publish");
        if(stateId.intValue() == 3)
            return getLabel("labels.state.published");
        
        return "";
    }
    

	/**
	 * This method fetches a value from the xml that is the contentVersions Value. If the 
	 * contentVersioVO is null the contentVersion has not been created yet and no values are present.
	 */
	 
	public String getAttributeValue(String xml, String key)
	{
		String value = "";
		
		log.info("xml:" + xml);

		if(xml != null)
		{
			try
	        {
		        //log.info("key:" + key);
				
				int startTagIndex = xml.indexOf("<" + key + ">");
				int endTagIndex   = xml.indexOf("]]></" + key + ">");

				if(startTagIndex > 0 && startTagIndex < xml.length() && endTagIndex > startTagIndex && endTagIndex <  xml.length())
				{
					value = xml.substring(startTagIndex + key.length() + 11, endTagIndex);
					value = new VisualFormatter().escapeHTML(value);
				}					
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
		}
		
		log.info("value:" + value);	
		
		return value;
	}
	
	
    public String getStringAttributeValue(String attributeName)
    {
    	try
    	{
    		return (String)ServletActionContext.getRequest().getAttribute(attributeName);
    	}
    	catch(Exception e)
    	{
    		return "";
    	}
    }

    public String getRequestParameterValue(String name)
    {
    	try
    	{
    		return (String)ServletActionContext.getRequest().getParameter(name);
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    }

    public String formatDate(Date date, String pattern, Locale locale)
    {	
    	if(date == null)
            return "";
     
        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
        String dateString = formatter.format(date);

        return dateString;
    }
    
    public Date parseDate(String dateString, String pattern, Locale locale)
    {	
        if(dateString == null)
            return new Date();
        
        Date date = new Date();    
        
        try
        {
	        // Format the current time.
	        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
	        date = formatter.parse(dateString);
        }
        catch(Exception e)
        {
            log.info("Could not parse date:" + e.getMessage() + " - defaulting to now...");
        }
        
        return date;
    }

    public String formatDate(Date date, String pattern)
    {	
    	if(date == null)
            return "";
     
        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, getLocale());
        String dateString = formatter.format(date);

        return dateString;
    }
    
    public Date parseDate(String dateString, String pattern)
    {	
        if(dateString == null)
            return new Date();
        
        Date date = new Date();    
        
        try
        {
	        // Format the current time.
	        SimpleDateFormat formatter = new SimpleDateFormat(pattern, new Locale(getLanguageCode()));
	        date = formatter.parse(dateString);
        }
        catch(Exception e)
        {
            log.info("Could not parse date:" + e.getMessage() + " - defaulting to now...");
        }
        
        return date;
    }

    /**
     * Gets a calendar object with date and hour
     * 
     * @param dateString
     * @param pattern
     * @param hour
     * @return
     */
    
    public Calendar getCalendar(String dateString, String pattern, boolean fallback)
    {	
        Calendar calendar = Calendar.getInstance();
        if(dateString == null)
        {
            //calendar.set(Calendar.HOUR_OF_DAY, hour.intValue());
            return calendar;
        }
        
        Date date = new Date();    
        
        try
        {
	        // Format the current time.
	        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	        date = formatter.parse(dateString);
	        calendar.setTime(date);
	        //calendar.set(Calendar.HOUR_OF_DAY, hour.intValue());
        }
        catch(Exception e)
        {
            log.info("Could not parse date:" + e.getMessage() + " - defaulting to now...");
            if(!fallback)
                return null;
        }
        
        return calendar;
    }

    /**
     * Gets a calendar object which is now
     * 
     * @return
     */
    
    public Calendar getNow()
    {	
        Calendar calendar = Calendar.getInstance();
        
        return calendar;
    }
    

    
    public String getVCalendar(Long eventId) throws Exception
    {
        return ICalendarController.getICalendarController().getICalendarUrl(eventId, getSession());
    }
    
    public String getResourceUrl(Long resourceId) throws Exception
    {
        return ResourceController.getController().getResourceUrl(resourceId, getSession());
    }

    public String getResourceUrl(Event event, String assetKey) throws Exception
    {
        return ResourceController.getController().getResourceUrl(event, assetKey, getSession());
    }

    public String getResourceThumbnailUrl(Long resourceId, int width, int height) throws Exception
    {
    	return ResourceController.getController().getResourceThumbnailUrl(resourceId, width, height, getSession());
    }
    public String getResourceThumbnailUrl(Event event, String assetKey, int width, int height) throws Exception
    {
    	return ResourceController.getController().getResourceThumbnailUrl(event, assetKey, width, height, getSession());
    }

    public Participant getParticipant(Long participantId) throws Exception
    {
        return ParticipantController.getController().getParticipant(participantId, getSession());
    }

    public void validateInput(CalendarAbstractAction action) throws ValidationException
    {
    	validateInput(action, null);
    }

    public void validateInput(CalendarAbstractAction action, ConstraintExceptionBuffer ceb) throws ValidationException
    {
    	validateInput(action, ceb, true);
    }
    
    public void validateInput(CalendarAbstractAction action, ConstraintExceptionBuffer ceb, boolean isCaptchaOk) throws ValidationException
    {
    	boolean throwError = false;
    	
    	Map fieldErrors = new HashMap();
    	if(!isCaptchaOk)
    	{
    		fieldErrors.put("captcha", new ArrayList<String>(Arrays.asList("errors.captcha")));
    		throwError = true;
    	}
        //log.debug("this.getFieldErrors() 0:" + this.getFieldErrors().size());

        String context = ActionContext.getContext().getName();
        ActionValidatorManager.validate(this, context);
        if(this.getFieldErrors() != null && this.getFieldErrors().size() > 0)
        {
        	fieldErrors.putAll(this.getFieldErrors());
        	log.debug("fieldErrors:" + fieldErrors.size());

            throwError = true;
        }
        
    	ActionContext.getContext().getValueStack().getContext().put("actionErrors", this.getActionErrors());
        ActionContext.getContext().getValueStack().getContext().put("fieldErrors", fieldErrors);
        //ActionContext.getContext().getValueStack().getContext().put("errorAction", this);
        
        log.debug("actionErrors:" + this.getActionErrors());
        log.debug("fieldErrors:" + this.getFieldErrors());
        log.debug("errorAction:" + this);

        log.debug("ceb:" + ceb);
        if(ceb != null)
        {
        	List errs = new ArrayList();
	        
        	ConstraintException ce = ceb.toConstraintException();
        	while(ce != null)
        	{
		        String fieldName 	= ce.getFieldName();
		        String errorCode 	= ce.getErrorCode();
		        String message 		= ce.getMessage();
		        
		        log.debug("fieldName:" + fieldName);
		        log.debug("errorCode:" + errorCode);
		        log.debug("message:" + message);
	
		        errs.add(errorCode);
		        
		        fieldErrors.put(fieldName, errs);
		    
	        	throwError = true;

		        ce = ce.getChainedException();
        	}        	
        }

        Iterator keyIterator = fieldErrors.keySet().iterator();
        while(keyIterator.hasNext())
        {
        	String key = (String)keyIterator.next();
        	log.info("FieldError: " + key + "=" + fieldErrors.get(key));
        }        	

        if(throwError)
        {
            ActionContext.getContext().getValueStack().getContext().put("errorAction", this);
            throw new ValidationException("An validation error occurred - more information is in the valuestack...");
        }
        
	        /*
	        if (fValidator != null) 
	        {
	            if (validatorContext.hasFieldErrors()) 
	            {
	                Collection fieldErrors = (Collection) validatorContext.getFieldErrors().get(fullFieldName);
	
	                if (fieldErrors != null) 
	                {
	                    errs = new ArrayList(fieldErrors);
	                }
	            }
	        } 
	        */
	        
	        /*
	        else if (validatorContext.hasActionErrors()) {
	            Collection actionErrors = validatorContext.getActionErrors();
	
	            if (actionErrors != null) {
	                errs = new ArrayList(actionErrors);
	            }
	        }
	        */
	/*
	        if (fValidator != null) {
	            if (validatorContext.hasFieldErrors()) {
	                Collection errCol = (Collection) validatorContext.getFieldErrors().get(fullFieldName);
	
	                if ((errCol != null) && !errCol.equals(errs)) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("Short-circuiting on field validation");
	                    }
	
	                    if (shortcircuitedFields == null) {
	                        shortcircuitedFields = new TreeSet();
	                    }
	
	                    shortcircuitedFields.add(fullFieldName);
	                }
	            }
	        } else if (validatorContext.hasActionErrors()) {
	            Collection errCol = validatorContext.getActionErrors();
	
	            if ((errCol != null) && !errCol.equals(errs)) {
	                if (LOG.isDebugEnabled()) {
	                    LOG.debug("Short-circuiting");
	                }
	
	                break;
	            }
	        }
        }
        */
    }
    
    public boolean validateCaptcha(String captcha, String correctCaptcha) throws ValidationException
    {
    	if(correctCaptcha == null || !correctCaptcha.equals(captcha))
    	{    		
    		return false;
    	}
    	return true;
    }

    public void setError(String message, Exception e)
    {
        String context = ActionContext.getContext().getName();
        ActionContext.getContext().getValueStack().getContext().put("message", message);
        ActionContext.getContext().getValueStack().getContext().put("error", e);
    }

    public ActionContext getActionContext() throws ValidationException
    {
        return ActionContext.getContext();
    }

    public boolean useEventPublishing()
    {
        String useEventPublishing = PropertyHelper.getProperty("useEventPublishing");
        
        return (useEventPublishing.equalsIgnoreCase("true") ? true : false);
    }

    public String getTempFilePath()
    {
        String digitalAssetPath = PropertyHelper.getProperty("digitalAssetPath");
        
        return digitalAssetPath;
    }

    public boolean useEntryLimitation()
    {
        String useEntryLimitation = PropertyHelper.getProperty("useEntryLimitation");
        
        return (useEntryLimitation.equalsIgnoreCase("true") ? true : false);
    }

    public String getParameterizedLabel(String key, String argument)
    {
        Locale locale = new Locale(this.getLanguageCode());
        
        String label = getLabel(key, locale, false, true, true);
        
        Object[] arguments = {argument};

        label = MessageFormat.format(label, arguments);
        
        //label"At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",
      
        return label;
    }

    
    public String getLabel(String key)
    {
        Locale locale = new Locale(this.getLanguageCode());
	    
	    return getLabel(key, locale, false, true, true);
    }
    

    public String getLabel(String key, String languageCode)
    {
    	Locale locale = Locale.ENGLISH;
    	if(languageCode != null && !languageCode.equals(""))
           	locale = new Locale(languageCode);
	    
	    return getLabel(key, locale, false, true, true);
    }

    public String getLabel(String key, String languageCode, boolean skipProperty)
    {
    	Locale locale = Locale.ENGLISH;
    	if(languageCode != null && !languageCode.equals(""))
           	locale = new Locale(languageCode);
	    
	    return getLabel(key, locale, skipProperty, true, true);
    }

    public String getLabel(String key, String languageCode, boolean skipProperty, boolean fallbackToDefault)
    {
    	Locale locale = Locale.ENGLISH;
    	if(languageCode != null && !languageCode.equals(""))
           	locale = new Locale(languageCode);
	    
	    return getLabel(key, locale, skipProperty, fallbackToDefault, true);
    }

    public String getLabel(String key, String languageCode, boolean skipProperty, boolean fallbackToDefault, boolean fallbackToKey)
    {
	    Locale locale = Locale.ENGLISH;
    	if(languageCode != null && !languageCode.equals(""))
        	locale = new Locale(languageCode);

	    String label = getLabel(key, locale, skipProperty, fallbackToDefault, fallbackToKey);
	    
	    return label;
    }
    
    public String getLabel(String key, Locale locale, boolean skipProperty, boolean fallbackToDefault, boolean fallbackToKey)
    {
    	if(locale == null)
    		locale = Locale.ENGLISH;
    		
    	String label = "";
    	if(fallbackToKey)
    		label = key;
	    
	    try
	    {
	    	Object derivedObject = findOnValueStack(key);
	        String derivedValue = null;
	        if(derivedObject != null)
	        	derivedValue = derivedObject.toString();
	        
	        if(!skipProperty)
	        {
		        if(derivedValue != null)
		    	    label = CalendarLabelsController.getCalendarLabelsController().getLabel("infoglueCalendar", derivedValue, locale, getSession());
		        else
		    	    label = CalendarLabelsController.getCalendarLabelsController().getLabel("infoglueCalendar", key, locale, getSession());
	        }
	        
	        if(skipProperty || ((label == null || label.equals("")) && fallbackToDefault))
	        {
		        ResourceBundle resourceBundle = ResourceBundleHelper.getResourceBundle("infoglueCalendar", locale);
		        
		    	if(derivedValue != null)
		    	    label = resourceBundle.getString(derivedValue);
		        else
		            label = resourceBundle.getString(key);
	        }
	        
	        if((label == null || label.equals("")) && fallbackToKey)
	            label = key;
	    }
	    catch(Exception e)
	    {
	        log.info("An label was not found for key: " + key + ": " + e.getMessage(), e);
	    }
	    
	    return label;
    }

    public EventVersion getMasterEventVersion(String eventString)
    {
        Object object = findOnValueStack(eventString);
        Event event = (Event)object;
        
        if(event == null)
    		return null;

       	EventVersion masterEventVersion = null;

    	try
    	{
	    	Language language = LanguageController.getController().getMasterLanguage(getSession());
	    	
	    	Iterator eventVersionsIterator = event.getVersions().iterator();
	        while(eventVersionsIterator.hasNext())
	        {
	        	EventVersion currentEventVersion = (EventVersion)eventVersionsIterator.next();
	        	if(currentEventVersion.getVersionLanguageId().equals(language.getId()))
	        	{
	        		masterEventVersion = currentEventVersion;
	        		break;
	        	}
	        }
	        
	        if(masterEventVersion == null && event.getVersions().size() > 0)
	        	masterEventVersion = (EventVersion)event.getVersions().toArray()[0];
    	}
    	catch(Exception e)
    	{
    		log.error("Error when getting event version for event: " + event + ":" + e.getMessage(), e); 
    	}
    	
        return masterEventVersion;
    }
        
    public Object getEventVersion(String eventString)
    {
		Timer t = new Timer();

        Object object = findOnValueStack(eventString);
        
        if(object instanceof EventTiny)
        {
            EventTiny event = (EventTiny)object;
            
            if(event == null)
        		return null;

            EventVersionTiny eventVersion = null;

        	try
        	{
        		Long languageId = LanguageController.getController().getLanguageIdForCode(this.getLanguageCode(), getSession());

    	    	Iterator eventVersionsIterator = event.getVersions().iterator();
    	        while(eventVersionsIterator.hasNext())
    	        {
    	        	EventVersionTiny currentEventVersion = (EventVersionTiny)eventVersionsIterator.next();
    	        	if(currentEventVersion.getLanguageId().equals(languageId))
    	        	{
    	        		eventVersion = currentEventVersion;
    	        		break;
    	        	}
    	        }
    	        
    	        if(eventVersion == null && event.getVersions().size() > 0)
    	        	eventVersion = (EventVersionTiny)event.getVersions().toArray()[0];
        	}
        	catch(Exception e)
        	{
        		log.error("Error when getting event version for event: " + event + ":" + e.getMessage(), e); 
        	}

            return eventVersion;
        }
        else
        {
	        Event event = (Event)object;
	        
	        if(event == null)
	    		return null;
	
	    	EventVersion eventVersion = null;
	
	    	try
	    	{
	    		Long languageId = LanguageController.getController().getLanguageIdForCode(this.getLanguageCode(), getSession());
	
		    	Iterator eventVersionsIterator = event.getVersions().iterator();
		        while(eventVersionsIterator.hasNext())
		        {
		        	EventVersion currentEventVersion = (EventVersion)eventVersionsIterator.next();
		        	if(currentEventVersion.getVersionLanguageId().equals(languageId))
		        	{
		        		eventVersion = currentEventVersion;
		        		break;
		        	}
		        }
		        
		        if(eventVersion == null && event.getVersions().size() > 0)
		        	eventVersion = (EventVersion)event.getVersions().toArray()[0];
	    	}
	    	catch(Exception e)
	    	{
	    		log.error("Error when getting event version for event: " + event + ":" + e.getMessage(), e); 
	    	}
	    	
	        return eventVersion;
        }
    }

    public EventVersion getEventVersion(Event event)
    {        
		Timer t = new Timer();
		
        if(event == null)
    		return null;

    	EventVersion eventVersion = null;

    	try
    	{
    		Long languageId = LanguageController.getController().getLanguageIdForCode(this.getLanguageCode(), getSession());
    		//Language language = LanguageController.getController().getLanguageWithCode(this.getLanguageCode(), getSession());
    		
	    	Iterator eventVersionsIterator = event.getVersions().iterator();
            while(eventVersionsIterator.hasNext())
	        {
	        	EventVersion currentEventVersion = (EventVersion)eventVersionsIterator.next();
	        	if(currentEventVersion.getVersionLanguageId().equals(languageId))
	        	{
	        		eventVersion = currentEventVersion;
	        		break;
	        	}
	        }
	        
	        if(eventVersion == null && event.getVersions().size() > 0)
	        	eventVersion = (EventVersion)event.getVersions().toArray()[0];
    	}
    	catch(Exception e)
    	{
    		log.error("Error when getting event version for event: " + event + ":" + e.getMessage(), e); 
    	}
    	
        return eventVersion;
    }
    
    public EventVersion getEventVersion(Event event, String languageCode, Session session)
    {        
		Timer t = new Timer();
		
        if(event == null)
    		return null;

    	EventVersion eventVersion = null;

    	try
    	{
    		Long languageId = LanguageController.getController().getLanguageIdForCode(languageCode, session);
    		//Language language = LanguageController.getController().getLanguageWithCode(this.getLanguageCode(), getSession());
    		
	    	Iterator eventVersionsIterator = event.getVersions().iterator();
            while(eventVersionsIterator.hasNext())
	        {
	        	EventVersion currentEventVersion = (EventVersion)eventVersionsIterator.next();
	        	if(currentEventVersion.getVersionLanguageId().equals(languageId))
	        	{
	        		eventVersion = currentEventVersion;
	        		break;
	        	}
	        }
	        
	        if(eventVersion == null && event.getVersions().size() > 0)
	        	eventVersion = (EventVersion)event.getVersions().toArray()[0];
    	}
    	catch(Exception e)
    	{
    		log.error("Error when getting event version for event: " + event + ":" + e.getMessage(), e); 
    	}
    	
        return eventVersion;
    }

    public String getCategoryValue(String feedEntryString, String categoryName)
    {
        Object object = findOnValueStack(feedEntryString);
        SyndEntry entry = (SyndEntry)object;
        
        if(entry == null)
    		return null;

       	String categoryValue = null;

    	try
    	{
    		Iterator categoriesIterator = entry.getCategories().iterator();
    		while(categoriesIterator.hasNext())
    		{
    			SyndCategory category = (SyndCategory)categoriesIterator.next();
    			if(category.getTaxonomyUri().indexOf(categoryName) > -1)
    			{
    				categoryValue = category.getName();
    				break;
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		log.error("Error when getting category for entry: " + feedEntryString + ":" + e.getMessage(), e); 
    	}
    	
        return categoryValue;
    }

    public VisualFormatter getVisualFormatter()
    {
    	return new VisualFormatter();
    }
    
    public boolean isActiveEventField(String fieldName)
    {
    	String hiddenEventFields = getSetting("hiddenEventFields");
    	log.info("hiddenEventFields:" + hiddenEventFields);

    	return !(hiddenEventFields.indexOf(fieldName) > -1);	 
    }

    public boolean isActiveEntryField(String fieldName)
    {
    	String hiddenEntryFields = getSetting("hiddenEntryFields");
    	log.info("hiddenEntryFields:" + hiddenEntryFields);

    	return !(hiddenEntryFields.indexOf(fieldName) > -1);	 
    }

    public String getSetting(String key)
    {
	    return getSetting(key, "default", false, true);
    }

    public String getSetting(String key, String variationId)
    {
	    return getSetting(key, variationId, false, true);
    }

    public String getSetting(String key, String variationId, boolean skipProperty)
    {
	    return getSetting(key, variationId, skipProperty, true);
    }

    public String getSetting(String key, boolean skipProperty, boolean fallbackToDefault)
    {
	    return getSetting(key, null, skipProperty, true);
    }

    public String getSetting(String key, String variationId, boolean skipProperty, boolean fallbackToDefault)
    {
    	//log.info("Getting setting for " + key + ":" + variationId + ":" + skipProperty + ":" + fallbackToDefault);
    	
    	if(variationId == null)
    		variationId = "default";
    		
    	String label = "";
    	
	    try
	    {
	    	Object derivedObject = findOnValueStack(key);
	        String derivedValue = null;
	        if(derivedObject != null)
	        	derivedValue = derivedObject.toString();
	        
	        //log.info("derivedValue:" + derivedValue);
	        
	        if(!skipProperty)
	        {
		        if(derivedValue != null)
		    	    label = CalendarSettingsController.getCalendarSettingsController().getSetting("infoglueCalendar", derivedValue, variationId, getSession());
		        else
		    	    label = CalendarSettingsController.getCalendarSettingsController().getSetting("infoglueCalendar", key, variationId, getSession());
	        }
	        
	        if(skipProperty || ((label == null || label.equals("")) && fallbackToDefault))
	        {
	        	Properties properties = PropertyHelper.getProperties();
		        
		    	if(derivedValue != null)
		    	    label = properties.getProperty(derivedValue);
		        else
		            label = properties.getProperty(key);
	        }
	        
	        if(label == null)
	            label = "";
	    }
	    catch(Exception e)
	    {
	        log.info("An label was not found for key: " + key + ": " + e.getMessage(), e);
	    }
	    
	    //log.info("label:" + label);
	    
	    return label;
    }

    public void setRenderedString(String renderedString)
	{
    	this.renderedString = renderedString;
	}

    public String getRenderedString()
	{
    	return this.renderedString;
	}

	public String getDatabaseConfigurationError() throws HibernateException {
	    return (String)ServletActionContext.getRequest().getAttribute("DATABASE_CONFIGURATION_ERROR");
	}

	public Session getSession() throws HibernateException {
	    return (Session)ServletActionContext.getRequest().getAttribute("HIBERNATE_SESSION");
	}
	

	public Session getSession(boolean readOnly) throws HibernateException {
		Session session = (Session)ServletActionContext.getRequest().getAttribute("HIBERNATE_SESSION");
		if(readOnly)
			session.setFlushMode(FlushMode.NEVER);
		return session;
	}

	public Transaction getTransaction() throws HibernateException {
	    return (Transaction)ServletActionContext.getRequest().getAttribute("HIBERNATE_TRANSACTION");
	}

	public void emptySession() throws HibernateException {
	    ServletActionContext.getRequest().removeAttribute("HIBERNATE_SESSION");
	}

	public void emptyTransaction() throws HibernateException {
	    ServletActionContext.getRequest().removeAttribute("HIBERNATE_TRANSACTION");
	}

	boolean rollBackOnly = false;
	
	public void disposeSession() throws HibernateException {
		
		log.debug("disposing");
		
		if (getSession()==null) return;

		if (rollBackOnly) {
			try {
				log.debug("Rolling back");
				if (getTransaction()!=null) getTransaction().rollback();
			}
			catch (TransactionException te) {
			    log.warn("Error getting transaction in rollback", te);
				throw te;
			}
			catch (HibernateException e) {
			    log.error("Error during rollback", e);
				throw e;
			}
			finally {
			    getSession().close();
				emptySession();
				emptyTransaction();
			}
		}
		else {
			try {
			    log.debug("committing");
				if (getTransaction()!=null) 
				{
				    getTransaction().commit();
				}
			}
			catch (HibernateException e) 
			{
			    log.error("error during commit:" + e.getMessage());
			    if(log.isDebugEnabled())
			    	log.debug("StackTrace:", e);
				getTransaction().rollback();
				throw e;
			}
			finally {
				getSession().close();
				emptySession();
				emptyTransaction();
			}
		}
	}

	/*
	protected void setRollbackOnly() {
		session.setRollBackOnly(true);
	}
	*/
	
	protected Object get(String name) {
		return ActionContext.getContext().getSession().get(name);
	}

	protected void set(String name, Object value) {
		ActionContext.getContext().getSession().put(name, value);
	} 
	
    public boolean isRollBackOnly()
    {
        return rollBackOnly;
    }
    public void setRollBackOnly(boolean rollBackOnly)
    {
        this.rollBackOnly = rollBackOnly;
    }
    
    public static Object findOnValueStack(String expr) 
    {
		ActionContext a = ActionContext.getContext();
		Object value = a.getValueStack().findValue(expr);
		return value;
	}

    public String getAjaxServiceUrl(){
    	 return (String)ServletActionContext.getRequest().getAttribute("ajaxServiceUrl");
    }
}

