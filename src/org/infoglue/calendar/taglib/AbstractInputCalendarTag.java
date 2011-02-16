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
package org.infoglue.calendar.taglib;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Base class for all Calendar Input tags.
 */

public class AbstractInputCalendarTag extends AbstractCalendarTag 
{
	private static Log log = LogFactory.getLog(AbstractInputCalendarTag.class);

	public boolean required = false;
	
	protected AbstractInputCalendarTag()
	{
		super();
	}

    public void setRequired(String required) throws JspException
    {
        String evaluatedString = evaluateString("AbstractInputCalendarTag", "required", required);
        if(evaluatedString != null && !evaluatedString.equals(required))
        	required = evaluatedString;
        
    	if(required.equalsIgnoreCase("true"))
            this.required = true;
        else
            this.required = false;   
    }

    public boolean getRequired()
    {
    	return this.required;
    }

}
