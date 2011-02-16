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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;


/**
 * 
 */
public class SizeTag extends AbstractTag 
{

	private static final long serialVersionUID = 3617579309963752240L;

	private List list;
	
	/**
	 * 
	 */
	public SizeTag() 
	{
		super();
	}
	
	public int doEndTag() throws JspException
    {
	    setResultAttribute(new Integer(list.size()));
        return EVAL_PAGE;
    }

    public void setList(final String list) throws JspException
    {
        this.list = evaluateList("sublistTag", "list", list);
    }
}
