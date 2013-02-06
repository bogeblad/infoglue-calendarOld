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

package org.infoglue.calendar.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.entities.AccessRight;
import org.infoglue.calendar.entities.Group;
import org.infoglue.calendar.entities.Participant;
import org.infoglue.calendar.entities.Role;
import org.infoglue.common.exceptions.Bug;
import org.infoglue.common.exceptions.SystemException;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.util.CacheController;
import org.infoglue.common.util.WebServiceHelper;

public class AccessRightController extends BasicController
{    
    private static Log log = LogFactory.getLog(AccessRightController.class);
        
    /**
     * Factory method to get InterceptionPointController
     * 
     * @return InterceptionPointController
     */
    private static final long timeoutLength = 3600000;
    
    public static AccessRightController getController()
    {
        return new AccessRightController();
    }
        
	
	public List getAccessRightList(Long interceptionPointId, String parameters, String roleName, Session session) throws SystemException, Bug
	{
		List result = null;
        
		if(parameters == null || parameters.length() == 0)
		{
			Criteria criteria = session.createCriteria(AccessRight.class);
			criteria.createCriteria("interceptionPointId").add(Expression.eq("interceptionPointId", interceptionPointId));
	        criteria.createCriteria("parameters").add(Expression.eq("parameters", parameters));

	        criteria.createCriteria("roles").add(Expression.eq("name", roleName));

	        criteria.addOrder(Order.asc("id"));
	   
	        result = criteria.list();
		}
		else
		{
			Criteria criteria = session.createCriteria(AccessRight.class);
			criteria.createCriteria("interceptionPointId").add(Expression.eq("interceptionPointId", interceptionPointId));
	        criteria.createCriteria("parameters").add(Expression.eq("parameters", parameters));

	        criteria.createCriteria("roles").add(Expression.eq("name", roleName));

	        criteria.addOrder(Order.asc("id"));
	   
	        result = criteria.list();
		}
				
		return result;		
	}
    
	/**
     * Gets a list of all roles existing in the calendar_role table.
     * @return List of Role
     * @throws Exception
     */
    
    public Set getCalendarRole(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Role role order by role.name");
   
        result = q.list();
        
        Set set = new LinkedHashSet();
        set.addAll(result);
        
        return set;
    }

	/**
     * Gets a list of all groups existing in the calendar_group table.
     * @return List of Role
     * @throws Exception
     */
    
    public Set getCalendarGroup(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Group g order by g.name");
   
        result = q.list();
        
        Set set = new LinkedHashSet();
        set.addAll(result);
        
        return set;
    }

	/**
     * Gets a list of all users existing in the calendar_participant table.
     * @return List of Participant
     * @throws Exception
     */
    
    public Set getCalendarParticipant(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Participant participant order by participant.userName");
   
        result = q.list();
        
        Set set = new LinkedHashSet();
        set.addAll(result);
        
        return set;
    }

	/**
     * Replace all occurrancies in the calendar_participant table.
     * @throws Exception
     */
    
    public void complementEventParticipant(Session session, String userName, String newUserName) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Participant participant where participant.userName = ?");
        q.setString(0, userName);
        
        result = q.list();

        Iterator resultIterator = result.iterator();
        while(resultIterator.hasNext())
        {
        	Participant participant = (Participant)resultIterator.next();
        	if(participant.getEvent() != null)
        	{
        		ParticipantController.getController().createParticipant(newUserName, participant.getEvent(), session);
        	}
        }
    }

	/**
     * Replace all occurrancies in the calendar_role table.
     * @throws Exception
     */
    
    public void complementCalendarRole(Session session, String roleName, String newRoleName) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Role role where role.name = ?");
        q.setString(0, roleName);
        
        result = q.list();

        Iterator resultIterator = result.iterator();
        while(resultIterator.hasNext())
        {
        	Role role = (Role)resultIterator.next();
        	if(role != null && role.getCalendar() != null)
        	{
	            Role newRole = new Role();
	            newRole.setCalendar(role.getCalendar());
	            newRole.setName(newRoleName);
	            session.save(newRole);
	            role.getCalendar().getOwningRoles().add(newRole);
        	}
        }
    }

	/**
     * Replace all occurrancies in the calendar_role table.
     * @throws Exception
     */
    
    public void complementCalendarGroup(Session session, String groupName, String newGroupName) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Group g where g.name = ?");
        q.setString(0, groupName);
        
        result = q.list();

        Iterator resultIterator = result.iterator();
        while(resultIterator.hasNext())
        {
        	Group group = (Group)resultIterator.next();
        	if(group != null && group.getCalendar() != null)
        	{
	            Group newGroup = new Group();
	            newGroup.setCalendar(group.getCalendar());
	            newGroup.setName(newGroupName);
	            session.save(newGroup);
	            group.getCalendar().getOwningGroups().add(newGroup);
        	}
        }
    }

	public List getRoles()
	{
		List list = (List)CacheController.getCachedObject("rolesCache", "allRoles", timeoutLength);
		if(list != null)
		{
			log.info("cached roles...");
			return list;			
		}
		
		try
		{
			log.info("looking for roles...");
		    WebServiceHelper wsh = new WebServiceHelper();
	        wsh.setServiceUrl(getServiceURL());
	        
	        list = new ArrayList(Arrays.asList((Object[])wsh.getArray("getRoles")));
	        if(list != null)
	        	CacheController.cacheObject("rolesCache", "allRoles", list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			list = (List)CacheController.getCachedObject("rolesCache", "allRoles");
		}
		
		return list;
	}
	
	public List getGroups()
	{
		List list = (List)CacheController.getCachedObject("groupsCache", "allGroups", timeoutLength);
		if(list != null)
		{
			log.info("cached groups...");
			return list;			
		}
			
		try
		{
		    WebServiceHelper wsh = new WebServiceHelper();
	        wsh.setServiceUrl(getServiceURL());
	        
	        list = new ArrayList(Arrays.asList((Object[])wsh.getArray("getGroups")));
	        if(list != null)
	        	CacheController.cacheObject("groupsCache", "allGroups", list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			list = (List)CacheController.getCachedObject("groupsCache", "allGroups");
		}
		
		return list;
	}
	
	public List getPrincipals()
	{
		List list = (List)CacheController.getCachedObject("principalsCache", "allPrincipals", timeoutLength);
		if(list != null)
		{
			log.info("cached principals...");
			return list;			
		}
			
		try
		{
		    WebServiceHelper wsh = new WebServiceHelper();
	        wsh.setServiceUrl(getServiceURL());
	        
	        list = new ArrayList(Arrays.asList((Object[])wsh.getArray("getPrincipals")));
	        if(list != null)
	        	CacheController.cacheObject("principalsCache", "allPrincipals", list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			list = (List)CacheController.getCachedObject("principalsCache", "allPrincipals");
		}
		
		return list;
	}

	public List getPrincipalsWithRole(String roleName)
	{
		List list = (List)CacheController.getCachedObject("principalsCache", "role_principals_" + roleName, timeoutLength);
		if(list != null)
		{
			log.info("cached role principals...");
			return list;			
		}
			
		try
		{
		    WebServiceHelper wsh = new WebServiceHelper();
	        wsh.setServiceUrl(getServiceURL());
	        
	        Object[] objects = (Object[])wsh.getArray("getPrincipalsWithRole", roleName);
	        if(objects != null)
	        	list = new ArrayList(Arrays.asList(objects));
	        
	        if(list != null)
	        	CacheController.cacheObject("principalsCache", "role_principals_" + roleName, list);
		}
		catch (Exception e) 
		{
			log.error("Error getting principal with role:" + e.getMessage());
			log.info("Error getting principal with role:" + e.getMessage(), e);
			list = (List)CacheController.getCachedObject("principalsCache", "role_principals_" + roleName);
		}
		
		return list;
	}

	public List getPrincipalsWithGroup(String groupName)
	{
		List list = (List)CacheController.getCachedObject("principalsCache", "group_principals_" + groupName, timeoutLength);
		if(list != null)
		{
			log.info("cached group principals...");
			return list;			
		}
			
		try
		{
		    WebServiceHelper wsh = new WebServiceHelper();
	        wsh.setServiceUrl(getServiceURL());
	        
	        list = new ArrayList(Arrays.asList((Object[])wsh.getArray("getPrincipalsWithGroup", groupName)));
	        if(list != null)
	        	CacheController.cacheObject("principalsCache", "group_principals_" + groupName, list);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			list = (List)CacheController.getCachedObject("principalsCache", "group_principals_" + groupName);
		}
		
		return list;
	}

	public InfoGluePrincipalBean getPrincipal(String userName)
	{
		InfoGluePrincipalBean infoGluePrincipalBean = (InfoGluePrincipalBean)CacheController.getCachedObject("principalsCache", "principal_" + userName, timeoutLength);
		if(infoGluePrincipalBean != null)
		{
			log.info("cached principal...");
			return infoGluePrincipalBean;			
		}
			
		try
		{
		    WebServiceHelper wsh = new WebServiceHelper();
	        wsh.setServiceUrl(getServiceURL());
	        
	        infoGluePrincipalBean = (InfoGluePrincipalBean)wsh.getObject("getPrincipal", userName);
	        if(infoGluePrincipalBean != null)
	        	CacheController.cacheObject("principalsCache", "principal_" + userName, infoGluePrincipalBean);	        	
		}
		catch (Exception e) 
		{
			log.error("Error getting principal:" + e.getMessage(), e);
			infoGluePrincipalBean = (InfoGluePrincipalBean)CacheController.getCachedObject("principalsCache", "principal_" + userName);
		}
		
		return infoGluePrincipalBean;
	}

	private String getServiceURL()
	{
		CalendarAbstractAction action = new CalendarAbstractAction();
		
		return action.getSetting("remoteUserServiceURL");
	}
}