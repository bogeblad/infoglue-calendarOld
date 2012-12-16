//$Id: HibernateInterceptor.java,v 1.6 2011/12/05 21:40:41 mattias Exp $
package org.infoglue.common.interceptor;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.infoglue.calendar.actions.CalendarAbstractAction;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * @author Gavin King
 */
public class HibernateInterceptor implements Interceptor {
	
	private static final Log LOG = LogFactory.getLog(HibernateInterceptor.class);
	
	public void destroy() {}

	public void init() {}

	public String intercept(ActionInvocation invocation) throws Exception 
	{
		Action action = invocation.getAction();
		
		if (!(action instanceof CalendarAbstractAction) ) 
		    return invocation.invoke();
		
		CalendarAbstractAction calendarAction = (CalendarAbstractAction)action;
		try 
		{
			return invocation.invoke();
		}
		catch (Exception e) 
		{	
		    calendarAction.setRollBackOnly(true);
			if (e instanceof HibernateException) 
			{
				LOG.error("Error in transaction: " + e.getMessage());
				LOG.warn("Error in transaction: " + e.getMessage(), e);
				return Action.ERROR;
			}
			else 
			{
			    LOG.error("Exception in execute:" + e.getMessage());
				LOG.warn("Exception in execute:" + e.getMessage(), e);
				throw e;
			}
		}
		
		finally 
		{
			try 
			{
			    calendarAction.disposeSession();
			}
			catch (HibernateException e) 
			{
				LOG.error("HibernateException in dispose()", e);
				return Action.ERROR;
			}
		}
	}

}
