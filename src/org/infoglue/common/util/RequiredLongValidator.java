/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.infoglue.common.util;

import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.validators.FieldValidatorSupport;


/**
 * RequiredStringValidator checks that a Long field is non-null
 * (i.e. it isn't "").  The "trim" parameter determines whether it will {@link String#trim() trim}
 * the String before performing the length check.  If unspecified, the String will be trimmed.
 */
public class RequiredLongValidator extends FieldValidatorSupport 
{
    public void validate(Object object) throws ValidationException 
    {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        
        if (!(value instanceof Long || value instanceof Long[])) 
        {
            addFieldError(fieldName, object);
        } 
        else 
        {
        	if(value instanceof Long)
            {
                Long f = (Long) value;
            	
                if (f == null || f.equals(0)) {
                	addFieldError(fieldName, object);
                }
            }
            else if(value instanceof Long[])
            {
            	Long[] f = (Long[]) value;
                
                if (f == null || f.length == 0) {
                    addFieldError(fieldName, object);
                }                
            }
        }
    }
} 