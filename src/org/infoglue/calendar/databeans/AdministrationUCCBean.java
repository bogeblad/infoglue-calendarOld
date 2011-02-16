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
package org.infoglue.calendar.databeans;

import java.util.List;

/**
 * This bean holds all data needed in the Calendar Administration view.
 * @author mattias
 */
public class AdministrationUCCBean
{
    private List calendars;
    private List locations;
    private List categories;
    

    public List getCalendars()
    {
        return calendars;
    }
    
    public void setCalendars(List calendars)
    {
        this.calendars = calendars;
    }
    
    public List getCategories()
    {
        return categories;
    }
    
    public void setCategories(List categories)
    {
        this.categories = categories;
    }
    
    public List getLocations()
    {
        return locations;
    }
    
    public void setLocations(List locations)
    {
        this.locations = locations;
    }
}
