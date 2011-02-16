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

package org.infoglue.common.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
* ResourceBundleHelper.java
* 
* This class is used to get labels etc for the system in a language independent way.
* 
* @author Mattias Bogeblad 
*/

public class ResourceBundleHelper
{
    private static Map resourceBundles = new HashMap();
    
    private ResourceBundleHelper()
    {}
    
    public static final ResourceBundle getResourceBundle(String name, Locale locale)
    {
        ResourceBundle resourceBundle = (ResourceBundle)resourceBundles.get(name + "_" + locale.getLanguage());
        
        if(resourceBundle == null)
        {
            resourceBundle = ResourceBundle.getBundle(name, locale);
            if(resourceBundle != null)
                resourceBundles.put(name, resourceBundle);
        }
        
        return resourceBundle;
    }
    
}
