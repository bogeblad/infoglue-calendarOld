package org.infoglue.common.util;

import com.opensymphony.util.TextUtils;

import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.validators.FieldValidatorSupport;


/**
 * EmailValidator checks that a given String field, if not empty, is a valid email address.
 */

public class EmailValidator extends FieldValidatorSupport 
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String value = (String) this.getFieldValue(fieldName, object);

        if (value == null) 
        {
            addFieldError(fieldName, object);
            return;
        } 
        else 
        {
            value = value.trim();

            if (value.length() == 0) {
                addFieldError(fieldName, object);
                return;
            }
        }

        if (!TextUtils.verifyEmail(value)) {
            addFieldError(fieldName, object);
        }
    }
}
