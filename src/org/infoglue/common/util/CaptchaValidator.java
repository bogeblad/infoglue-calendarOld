/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.infoglue.common.util;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.validators.FieldValidatorSupport;


/**
 * CaptchaValidator checks that the captcha text is the same as the stored session captcha text.
 */
public class CaptchaValidator extends FieldValidatorSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    //~ Methods ////////////////////////////////////////////////////////////////

    public void validate(Object object) throws ValidationException 
    {
    	String useCaptchaForEntry = (String)ServletActionContext.getRequest().getSession().getAttribute("useCaptchaForEntry");
    	//System.out.println("useCaptchaForEntry:" + useCaptchaForEntry);
    	if(useCaptchaForEntry != null && useCaptchaForEntry.equals("true"))
    	{
	        String fieldName = getFieldName();
	        Object value = this.getFieldValue(fieldName, object);
	        //System.out.println("fieldName:" + fieldName);
	        //System.out.println("value:" + value);
	    	String captchaTextVariableName = ServletActionContext.getRequest().getParameter("captchaTextVariableName");
	    	//System.out.println("captchaTextVariableName:" + captchaTextVariableName);
    		
	    	String correctCaptcha = (String)ServletActionContext.getRequest().getSession().getAttribute(captchaTextVariableName);
	    	//System.out.println("correctCaptcha:" + correctCaptcha);
    		if(captchaTextVariableName == null || !correctCaptcha.equals(value))
    		{
                addFieldError(fieldName, object);
    		}
    	}
    }
} 