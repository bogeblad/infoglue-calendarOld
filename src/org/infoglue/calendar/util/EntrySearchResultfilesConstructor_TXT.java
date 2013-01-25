package org.infoglue.calendar.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.entities.Entry;

public class EntrySearchResultfilesConstructor_TXT {

	private static Log log = LogFactory.getLog( EntrySearchResultfilesConstructor_TXT.class );
	
	private Set entries;
	private String fileFolderLocation;
	private String httpFolderLocation;
	private String httpFileLocation;
	private List resultValues;

	private CalendarAbstractAction action;

	public EntrySearchResultfilesConstructor_TXT( Set entries, String fileFolderLocation, String httpFolderLocation, List resultValues, CalendarAbstractAction action ) {
		this.entries = entries;
		this.fileFolderLocation = fileFolderLocation;
		this.httpFolderLocation = httpFolderLocation;
		this.resultValues = resultValues;
		this.action = action;
	}

	public boolean createFile() {
		log.debug( "Entries size: " + entries.size() + ", fileFolderLocation: " + fileFolderLocation );
		String fileName = "SearchResults_" + System.currentTimeMillis()
				+ ".txt";
		String fileFileLocation = fileFolderLocation + fileName;
		httpFileLocation = httpFolderLocation + fileName;
		log.debug( "Creating file: " + fileFileLocation );
		log.debug( "HTTP file: " + httpFileLocation );
		String lineSeparator = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		try {
			printHeadline( sb, lineSeparator );
			printEntries( sb, lineSeparator );
			FileOutputStream fos;
			fos = new FileOutputStream(fileFileLocation);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					fos));
			bw.write(sb.toString());
			bw.close();
		} catch( Exception e) {
			// TODO Auto-generated catch block
			log.error( "Failed to create TXT file." , e );
			return false;
		} 	
		return true;
	}
	
	private void printEntries(StringBuffer sb, String lineSeparator) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for (Iterator it = entries.iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			for( Iterator resultIterator = resultValues.iterator(); resultIterator.hasNext(); ) {
				String resultValue = ( String ) resultIterator.next();
				Method m = entry.getClass().getMethod( "get" + resultValue, null );
				Object o = m.invoke( entry, null );
				if( resultValue.equals("Id") ) {
					Long id = ( Long ) o;
					sb.append( id.longValue() );
				} else {
					String val = ( String ) o;
					sb.append( val );
				}
				if( resultIterator.hasNext() ) {
					sb.append( ", " );
				}
			}
			sb.append(lineSeparator);			
		}
		
	}

	private void printHeadline( StringBuffer sb, String lineSeparator ) {
		//System.out.println("resultValues:" + resultValues);
		for( Iterator it = resultValues.iterator(); it.hasNext(); ) {
			String resultValue = ( String ) it.next();
			if( resultValue.equalsIgnoreCase("id") ) {
				sb.append( action.getLabel("labels.internal.soba.idColumnHeader") );
			} else if( resultValue.equalsIgnoreCase("name") ) {
				sb.append( action.getLabel("labels.internal.soba.nameColumnHeader") );
			} else if( resultValue.equalsIgnoreCase("email") ) {
				sb.append( action.getLabel("labels.internal.soba.emailColumnHeader") );
			} else if( resultValue.equalsIgnoreCase("organisation") ) {
				sb.append( action.getLabel("labels.internal.soba.organisationColumnHeader") );
			} else if( resultValue.equalsIgnoreCase("address") ) {
				sb.append( action.getLabel("labels.internal.soba.addressColumnHeader") );
			} else if( resultValue.equalsIgnoreCase("city") ) {
				sb.append( action.getLabel("labels.internal.soba.cityColumnHeader") );
			} else if( resultValue.equalsIgnoreCase("zipcode") ) {
				sb.append( action.getLabel("labels.internal.soba.zipcodeColumnHeader") );
			}
			if( it.hasNext() ) {
				sb.append( ", " );
			}
			
		}
		sb.append(lineSeparator + lineSeparator);		
	}

	public String getFileLocation() {
		return httpFileLocation;
	}
}
