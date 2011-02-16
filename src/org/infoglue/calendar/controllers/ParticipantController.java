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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.Participant;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ParticipantController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(ParticipantController.class);
        
    
    /**
     * Factory method to get ParticipantController
     * 
     * @return ParticipantController
     */
    
    public static ParticipantController getController()
    {
        return new ParticipantController();
    }
        
    
    /**
     * This method is used to create a new Participant object in the database inside a transaction.
     */
    
    public Participant createParticipant(String userName, Event event, Session session) throws HibernateException, Exception 
    {
        Participant participant = new Participant();
        participant.setUserName(userName);
        participant.setEvent(event);
        
        session.save(participant);
        
        return participant;
    }
    
    
    /**
     * Updates an participant.
     * 
     * @throws Exception
     */
    
    public void updateParticipant(Long id, String userName, Session session) throws Exception 
    {
		Participant participant = getParticipant(id, session);
		updateParticipant(participant, userName, session);
    }
    
    /**
     * Updates an participant inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateParticipant(Participant participant, String userName, Session session) throws Exception 
    {
        participant.setUserName(userName);
	
		session.update(participant);
	}
    
 
    /**
     * This method returns a Participant based on it's primary key inside a transaction
     * @return Participant
     * @throws Exception
     */
    
    public Participant getParticipant(Long id, Session session) throws Exception
    {
        Participant participant = (Participant)session.load(Participant.class, id);
		
		return participant;
    }
    
    
    
    /**
     * Gets a list of all participants available sorted by primary key.
     * @return List of Participant
     * @throws Exception
     */
    
    public List getParticipantList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Participant participant order by participant.id");
   
        result = q.list();
        
        return result;
    }
    
    /**
     * Gets a list of participants fetched by name.
     * @return List of Participant
     * @throws Exception
     */
    
    public List getParticipant(String userName, Session session) throws Exception 
    {
        List participants = null;
        
        participants = session.createQuery("from Participant as participant where participant.userName = ?").setString(0, userName).list();
        
        return participants;
    }
    
    
    /**
     * Deletes a participant object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteParticipant(Long id, Session session) throws Exception 
    {
        Participant participant = this.getParticipant(id, session);
        session.delete(participant);
    }
    
}