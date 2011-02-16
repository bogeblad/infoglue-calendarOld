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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class CacheController
{ 
    public final static Logger logger = Logger.getLogger(CacheController.class.getName());

    private static Map caches = Collections.synchronizedMap(new HashMap());
	
	public CacheController()
	{
		super();
	}

	public static void renameCache(String cacheName, String newCacheName)
	{
		synchronized(caches) 
		{
		    Object cacheInstance = caches.get(cacheName);
		    
		    if(cacheInstance != null)
		    {
		        synchronized(cacheInstance)
		        {
		            caches.put(newCacheName, cacheInstance);
		            caches.remove(cacheName);
		        }
		    }
		}
	}	

	public static void clearServerNodeProperty()
	{
		clearCache("serverNodePropertiesCache");
   	}

	public static void cacheObject(String cacheName, Object key, Object value)
	{
		synchronized(caches)
		{
			if(!caches.containsKey(cacheName))
			    caches.put(cacheName, Collections.synchronizedMap(new HashMap()));
		}
			
		synchronized(caches)
		{
			Map cacheInstance = (Map)caches.get(cacheName);
			if(cacheInstance != null && key != null && value != null)
		    {
			    synchronized(cacheInstance)
		        {
				    cacheInstance.put(key, value);
				    cacheInstance.put(key + "_insertTime", new Long(System.currentTimeMillis()));
		        }
		    }
		}
	}	
	
	public static Object getCachedObject(String cacheName, Object key)
	{
		synchronized(caches)
		{
			Map cacheInstance = (Map)caches.get(cacheName);
		    
		    if(cacheInstance != null)
		    {
		        synchronized(cacheInstance)
		        {
		            return cacheInstance.get(key);
		        }
		    }
		}
		
        return null;
    }

	public static Object getCachedObject(String cacheName, Object key, long timeout)
	{
		synchronized(caches)
		{
			Map cacheInstance = (Map)caches.get(cacheName);
		    
		    if(cacheInstance != null)
		    {
		        synchronized(cacheInstance)
		        {
		        	Object object = cacheInstance.get(key);
		            if(object != null)
		            {
		            	Long insertTime = (Long)cacheInstance.get(key + "_insertTime");
			         	if((System.currentTimeMillis() - insertTime.longValue()) > timeout)
			            	return null;
		            	else
		            		return object;
		            }
		        }
		    }
		}
		
        return null;
    }


	public static void clearCache(String cacheName)
	{
		logger.info("Clearing the cache called " + cacheName);
		synchronized(caches) 
		{
			if(caches.containsKey(cacheName))
			{
			    Object object = caches.get(cacheName);
			    if(object instanceof Map)
				{
					Map cacheInstance = (Map)object;
					synchronized(cacheInstance) 
					{
						cacheInstance.clear();
					}
				}

			    caches.remove(cacheName);
			 
			    logger.info("clearCache stop...");
			}
		}
	}
	
	public static void clearCaches()
	{
		synchronized(caches) 
		{
			Iterator cachesIterator = caches.values().iterator();
			while(cachesIterator.hasNext())
			{
			    Object object = cachesIterator.next();
			    if(object instanceof Map)
				{
					Map cacheInstance = (Map)object;
					synchronized(cacheInstance) 
					{
						cacheInstance.clear();
					}
				}
			 
			    logger.info("clearCache stop...");
			}
		    caches.clear();
		}
	}

    public static Map getCaches()
    {
        return caches;
    }


}

