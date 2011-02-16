package org.infoglue.calendar.util;

import java.util.Comparator;

import org.infoglue.calendar.entities.Event;


/**
 * @author Mattias Bogeblad
 *
 */

public class EventComparator implements Comparator
{

	public int compare(Object o1, Object o2) 
	{
		int result = 0;
		Event event2 = (Event)o2;
		Event event1 = (Event)o1;
	
		int orderColumnResult = event1.getStartDateTime().getTime().compareTo(event2.getStartDateTime().getTime());
		
		if(orderColumnResult != 0)
			result = orderColumnResult;
		
		return result;
	}

}
