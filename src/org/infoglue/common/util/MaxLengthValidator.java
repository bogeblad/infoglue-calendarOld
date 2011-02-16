/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.infoglue.common.util;

import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.validators.FieldValidatorSupport;


/**
 * MaxLengthValidator checks that a String field is non-null and has a length > 0
 * (i.e. it isn't "").  The "trim" parameter determines whether it will {@link String#trim() trim}
 * the String before performing the length check.  If unspecified, the String will be trimmed.
 */
public class MaxLengthValidator extends FieldValidatorSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    private int maxLength = 255;
    
    //~ Methods ////////////////////////////////////////////////////////////////

    public void validate(Object object) throws ValidationException 
    {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        if(value != null)
        {
        	String stringValue = value.toString();
        	if(stringValue.length() > maxLength)
        	{
        		addFieldError(fieldName, object);
        	}
        }
    }

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
} 