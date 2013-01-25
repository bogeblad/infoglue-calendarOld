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

package org.infoglue.calendar.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.entities.Event;
import org.infoglue.common.util.HibernateUtil;
import org.infoglue.common.util.RemoteCacheUpdater;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * <p>
 * This job looks for events that has expired since last job and evicts them by invoking the clean
 * cache method of the remote systems.
 * </p>
 * 
 * @author Mattias Bogeblad
 */

public class RemoteCacheUpdateJob implements Job 
{
	private static Log log = LogFactory.getLog(RemoteCacheUpdateJob.class);
	
	/**
	 * <p>
	 * Called by the <code>{@link org.quartz.Scheduler}</code> when a
	 * <code>{@link org.quartz.Trigger}</code> fires that is associated with
	 * the <code>Job</code>.
	 * </p>
	 * 
	 * @throws JobExecutionException if there is an exception while executing the job.
	 */
	
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
        log.info("---" + context.getJobDetail().getFullName() + " executing.[" + new Date() + "]");
        
        try
        {
            Date lastFireTime = context.getPreviousFireTime();
            Date currentFireTime = context.getFireTime();

            if(lastFireTime != null)
            {
	            Session session = HibernateUtil.currentSession();
	            Transaction tx = session.beginTransaction();
	            
	            Calendar currentCalendar = Calendar.getInstance();
	            currentCalendar.setTime(currentFireTime);
	
	            Calendar calendar = Calendar.getInstance();
	            calendar.setTime(lastFireTime);
	
	            List expiredEvents = EventController.getController().getExpiredEventList(currentCalendar, session);
	            Iterator expiredEventsIterator = expiredEvents.iterator();
	            while(expiredEventsIterator.hasNext())
	            {
	                Event event = (Event)expiredEventsIterator.next();
	                log.info("Event to expire:" + event.getName() + ":" + calendar.getTime() + "=" + event.getEndDateTime().getTime() + "?" + (calendar.getTimeInMillis() <= event.getEndDateTime().getTimeInMillis()));
	                if(calendar.getTimeInMillis() <= event.getEndDateTime().getTimeInMillis())
	                {
	                    log.info("It was not only recent - it was not expired before...");
	            		new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
	                    //new RemoteCacheUpdater().updateRemoteCaches(event.getOwningCalendar().getId());
	                }
	            }
	            
	            tx.commit();
	            HibernateUtil.closeSession();
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	    
	    log.info("Done in RemoteCacheUpdateJob...");
	}

}