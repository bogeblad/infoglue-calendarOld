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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;


/**
 * This action shows the error screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewErrorAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(ViewErrorAction.class);
    
	private String returnUrl;
	private String message;
        
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	ActionContext.getContext().getValueStack().getContext().put("errorMessage", ServletActionContext.getRequest().getSession().getAttribute("errorMessage"));
    	
        return Action.SUCCESS;
    } 

    public String getReturnUrl()
    {
        return returnUrl;
    }
    
    public void setReturnUrl(String returnUrl)
    {
        this.returnUrl = returnUrl;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
}
