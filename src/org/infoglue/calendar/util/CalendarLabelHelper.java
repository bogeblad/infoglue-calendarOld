package org.infoglue.calendar.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.controllers.CalendarLabelsController;
import org.infoglue.common.util.ResourceBundleHelper;

import com.opensymphony.xwork.ActionContext;

public class CalendarLabelHelper
{
	private static Log log = LogFactory.getLog(CalendarAbstractAction.class);

	public static CalendarLabelHelper getHelper()
	{
		return new CalendarLabelHelper();
	}
	
    public String getLabel(String key, String languageCode, Session session)
    {
    	Locale locale = Locale.ENGLISH;
    	if(languageCode != null && !languageCode.equals(""))
           	locale = new Locale(languageCode);
	    
	    return getLabel(key, locale, false, true, true, session);
    }

    public String getLabel(String key, String languageCode, boolean skipProperty, Session session)
    {
    	Locale locale = Locale.ENGLISH;
    	if(languageCode != null && !languageCode.equals(""))
           	locale = new Locale(languageCode);
	    
	    return getLabel(key, locale, skipProperty, true, true, session);
    }

    public String getLabel(String key, String languageCode, boolean skipProperty, boolean fallbackToDefault, Session session)
    {
    	Locale locale = Locale.ENGLISH;
    	if(languageCode != null && !languageCode.equals(""))
           	locale = new Locale(languageCode);
	    
	    return getLabel(key, locale, skipProperty, fallbackToDefault, true, session);
    }

    public String getLabel(String key, String languageCode, boolean skipProperty, boolean fallbackToDefault, boolean fallbackToKey, Session session)
    {
	    Locale locale = Locale.ENGLISH;
    	if(languageCode != null && !languageCode.equals(""))
        	locale = new Locale(languageCode);

	    String label = getLabel(key, locale, skipProperty, fallbackToDefault, fallbackToKey, session);
	    
	    return label;
    }
    
    public String getLabel(String key, Locale locale, boolean skipProperty, boolean fallbackToDefault, boolean fallbackToKey, Session session)
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
		    	    label = CalendarLabelsController.getCalendarLabelsController().getLabel("infoglueCalendar", derivedValue, locale, session);
		        else
		    	    label = CalendarLabelsController.getCalendarLabelsController().getLabel("infoglueCalendar", key, locale, session);
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

    public static Object findOnValueStack(String expr) 
    {
		ActionContext a = ActionContext.getContext();
		Object value = a.getValueStack().findValue(expr);
		return value;
	}

}
