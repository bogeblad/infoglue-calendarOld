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

/**
 * This class is a wrapper class for having a connection/transaction open for a while.
 * 
 * @author mattias
 */

public abstract class DBSessionWrapper
{
    /**
     * This method returns an instance of the DBSessionWrapper of the subclass given.
     * @param provider
     * @return
     * @throws Exception
     */
    public static DBSessionWrapper getInstance(String provider) throws Exception
    {
        if(provider.equalsIgnoreCase("Hibernate"))
            return new HibernateSessionWrapper();
        else
            return null;
    }
}
