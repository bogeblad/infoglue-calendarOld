/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.infoglue.common.util;

import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.validators.FieldValidatorSupport;


/**
 * RequiredStringValidator checks that a String field is non-null and has a length > 0
 * (i.e. it isn't "").  The "trim" parameter determines whether it will {@link String#trim() trim}
 * the String before performing the length check.  If unspecified, the String will be trimmed.
 */
public class RequiredStringValidator extends FieldValidatorSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean doTrim = true;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setTrim(boolean trim) {
        doTrim = trim;
    }

    public boolean getTrim() {
        return doTrim;
    }

    public void validate(Object object) throws ValidationException 
    {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        
        if (!(value instanceof String || value instanceof String[])) 
        {
            addFieldError(fieldName, object);
        } 
        else 
        {
            if(value instanceof String)
            {
                String s = (String) value;
                if (doTrim) {
                    s = s.trim();
                }

                if (s.length() == 0) {
                    addFieldError(fieldName, object);
                }
            }
            else if(value instanceof String[])
            {
                String[] s = (String[]) value;
                
                if (s.length == 0) {
                    addFieldError(fieldName, object);
                }                
            }
        }
    }
} 