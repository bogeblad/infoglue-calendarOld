package org.infoglue.calendar.controllers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Blob;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.FoldingWriter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.UidGenerator;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.entities.Language;
import org.infoglue.calendar.entities.Location;
import org.infoglue.calendar.entities.Resource;
import org.infoglue.calendar.util.ICalendar;
import org.infoglue.calendar.util.ICalendarVEvent;
import org.infoglue.common.util.PropertyHelper;

public class ICalendarController extends BasicController
{
    private ICalendarController(){}
    
    public static final ICalendarController getICalendarController()
    {
        return new ICalendarController();
    }
    
    /**
     * This method returns a ICalendar based on it's primary key
     */
    
    public String getICalendarUrl(Long id, Session session) throws Exception
    {
        String url = "";

        Event event = EventController.getController().getEvent(id, session);
        Language masterLanguage = LanguageController.getController().getMasterLanguage(session);
        
		String calendarPath = PropertyHelper.getProperty("calendarsPath");
		String fileName = "event_" + event.getId() + ".vcs";
		
		getVCalendar(event, masterLanguage, calendarPath + fileName);
		
		String urlBase = PropertyHelper.getProperty("urlBase");
		
		url = urlBase + "calendars/" + fileName;
		
		return url;
    }

    
    public void getVCalendar(Event event, Language language, String file) throws Exception
    {
    	Set versions = event.getVersions();
    	Iterator versionsIterator = versions.iterator();
		EventVersion eventVersion = null;
		EventVersion eventVersionCandidate = null;
    	while(versionsIterator.hasNext())
    	{
    		eventVersionCandidate = (EventVersion)versionsIterator.next();
    		if(eventVersionCandidate.getLanguage().getId().equals(language.getId()))
    		{
    			eventVersion = eventVersionCandidate;
        		break;
    		}
    	}
    	if(eventVersion == null && eventVersionCandidate != null)
    		eventVersion = eventVersionCandidate;
    		    	
    	ICalendar iCal = new ICalendar();
		iCal.icalEventCollection = new LinkedList();
		iCal.setProdId("InfoGlueCalendar");
		iCal.setVersion("1.0");
		// Event Test
		ICalendarVEvent vevent = new ICalendarVEvent();
		Date workDate = new Date();
		vevent.setDateStart(event.getStartDateTime().getTime());
		vevent.setDateEnd(event.getEndDateTime().getTime());
		vevent.setSummary(eventVersion.getName());
		
		if(eventVersion != null)
			vevent.setDescription(eventVersion.getDescription());
		else
			vevent.setDescription("No description set");
		
		vevent.setSequence(0);
		vevent.setEventClass("PUBLIC");
		vevent.setTransparency("OPAQUE");
		vevent.setDateStamp(workDate);
		vevent.setCreated(workDate);
		vevent.setLastModified(workDate);
		if(eventVersion != null)
			vevent.setOrganizer(eventVersion.getOrganizerName());
		else
			vevent.setOrganizer("MAILTO:sfg@eurekait.com");
		vevent.setUid("igcal-"+workDate);
		vevent.setPriority(3);
		
		String locationString = null;
		if(eventVersion.getAlternativeLocation() != null && !eventVersion.getAlternativeLocation().equals(""))
			locationString = eventVersion.getAlternativeLocation() + ", ";
		else 
		{
			Iterator locationsIterator = event.getLocations().iterator();
			while(locationsIterator.hasNext())
	    	{
				Location location = (Location)locationsIterator.next();
	    		String localizedName = location.getLocalizedName("en","sv");
	    		locationString += localizedName + ", ";
	    	}	
			if(eventVersion.getCustomLocation() != null && !eventVersion.getCustomLocation().equals(""))
				locationString += eventVersion.getCustomLocation();
		}
		
		vevent.setLocation(locationString);
		
		iCal.icalEventCollection.add(vevent);
    	
		// Now write to string and view as file.
		
		//writeUTF8ToFile(new File(file), iCal.getVCalendar(), false);
		writeISO88591ToFile(new File(file), iCal.getVCalendar(), false);
		//writeUTF8ToFile(new File("c:/calendar.vcs"), iCal.getVCalendar(), false);
    }
    
    public void getICalendar(Event event, Language language, String file) throws Exception
    {
    	Set versions = event.getVersions();
    	Iterator versionsIterator = versions.iterator();
		EventVersion eventVersion = null;
		EventVersion eventVersionCandidate = null;
    	while(versionsIterator.hasNext())
    	{
    		eventVersionCandidate = (EventVersion)versionsIterator.next();
    		if(eventVersionCandidate.getLanguage().getId().equals(language.getId()))
    		{
    			eventVersion = eventVersionCandidate;
        		break;
    		}
    	}
    	if(eventVersion == null && eventVersionCandidate != null)
    		eventVersion = eventVersionCandidate;
    
		String locationString = "";
		if(eventVersion.getAlternativeLocation() != null && !eventVersion.getAlternativeLocation().equals(""))
			locationString = eventVersion.getAlternativeLocation();
		else 
		{
			Iterator locationsIterator = event.getLocations().iterator();
			while(locationsIterator.hasNext())
	    	{
				Location location = (Location)locationsIterator.next();
	    		String localizedName = location.getLocalizedName("en","sv");
	    		if(!locationString.equals("") && !locationString.endsWith(", "))
	    			locationString += ", ";
	    		
	    		locationString += localizedName;
	    	}	
			if(eventVersion.getCustomLocation() != null && !eventVersion.getCustomLocation().equals(""))
				if(!locationString.equals("") && !locationString.endsWith(", "))
	    			locationString += ", ";
	  			locationString += eventVersion.getCustomLocation();
		}

    	try 
    	{
            TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
            TimeZone timezone = registry.getTimeZone("Europe/Stockholm");
            VTimeZone tz = timezone.getVTimeZone();

            //Create the event
            DateTime start = new DateTime(event.getStartDateTime().getTime());
            DateTime end = new DateTime(event.getEndDateTime().getTime());
            VEvent meeting = new VEvent(start, end, eventVersion.getName());
            meeting.getProperties().add(tz.getTimeZoneId());
            net.fortuna.ical4j.model.property.Location iCalLocation = new net.fortuna.ical4j.model.property.Location(locationString);
            meeting.getProperties().add(iCalLocation);
            Description iCalDescription = new Description("" + eventVersion.getLongDescription());
            meeting.getProperties().add(iCalDescription);
            
            //Create a calendar
            Calendar icsCalendar = new Calendar();
            icsCalendar.getProperties().add(new ProdId("-//InfoGlue//InfoGlue Calendar 1.0//EN"));
            icsCalendar.getProperties().add(CalScale.GREGORIAN);

            UidGenerator ug = new UidGenerator("uidGen");
            Uid uid = ug.generateUid();
            icsCalendar.getProperties().add(uid);

            //Add the event and print
            icsCalendar.getComponents().add(meeting);
            
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"));
            
            CalendarBuilder builder = new CalendarBuilder();
            CalendarOutputter outputter = new CalendarOutputter(false, FoldingWriter.REDUCED_FOLD_LENGTH);
            outputter.setValidating(false);
           
            outputter.output(icsCalendar, out);
            
            //out.flush();
            //out.close();

        } 
    	catch (Exception e) 
    	{
            System.out.println("Error: " + e.getMessage());
        }
    	
    	/*
		if(eventVersion != null)
			vevent.setDescription(eventVersion.getDescription());
		else
			vevent.setDescription("No description set");
		
		vevent.setSequence(0);
		vevent.setEventClass("PUBLIC");
		vevent.setTransparency("OPAQUE");
		vevent.setDateStamp(workDate);
		vevent.setCreated(workDate);
		vevent.setLastModified(workDate);
		if(eventVersion != null)
			vevent.setOrganizer(eventVersion.getOrganizerName());
		else
			vevent.setOrganizer("MAILTO:sfg@eurekait.com");
		vevent.setUid("igcal-"+workDate);
		vevent.setPriority(3);
		
		
		vevent.setLocation(locationString);
		
		iCal.icalEventCollection.add(vevent);
    	
		writeISO88591ToFile(new File(file), iCal.getVCalendar(), false);
		*/
    }

    public synchronized void writeUTF8ToFile(File file, String text, boolean isAppend) throws Exception
	{
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
        out.write(text);
        out.flush();
        out.close();
	}

    public synchronized void writeISO88591ToFile(File file, String text, boolean isAppend) throws Exception
	{
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"));
        out.write(text);
        out.flush();
        out.close();
	}

}