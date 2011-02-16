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

package org.infoglue.common.util.mail;


import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.mail.*;
import javax.activation.*;
import javax.mail.internet.*;

import org.infoglue.common.exceptions.Bug;
import org.infoglue.common.exceptions.SystemException;

public class MailService 
{

    // The mail session.
    private Session session;


	/**
     * Creates a MailServices object and initializes it with the specified mail session.
     */

    public MailService(Session session) 
    {
        this.session = session;
    }


	/**
	 *
	 * @param from the sender of the email.
	 * @param to the recipient of the email.
	 * @param subject the subject of the email.
	 * @param content the body of the email.
	 * @throws SystemException if the email couldn't be sent due to some mail server exception.
	 */
	public void send(String from, String to, String subject, String content) throws SystemException 
	{
	    final Message message = createMessage(from, to, subject, content);
	 
	    try 
	    {
			Transport.send(message);
	    } 
	    catch(MessagingException e) 
	    {
	      	throw new SystemException("Unable to send message.", e);
	    }
	}

	/**
	 *
	 * @param from the sender of the email.
	 * @param to the recipient of the email.
	 * @param subject the subject of the email.
	 * @param content the body of the email.
	 * @throws SystemException if the email couldn't be sent due to some mail server exception.
	 */
	public void send(String from, String to, String bcc, String subject, String content, String contentType, String encoding, List attachments ) throws SystemException 
	{
		final Message message = createMessage(from, to, bcc, subject, content, contentType, encoding, attachments );
	 
		try 
		{
			Transport.send(message);
		} 
		catch(MessagingException e) 
		{
		    //e.printStackTrace();
			throw new SystemException("Unable to send message.", e);
		}
	}
	
		
	/**
	 *
	 */
	private Message createMessage(String from, String to, String subject, String content) throws SystemException
	{
		try 
		{
	    	final Message message = new MimeMessage(this.session);

	    	message.setContent(content, "text/html");
			message.setFrom(createInternetAddress(from));
	    	message.setRecipient(Message.RecipientType.TO, createInternetAddress(to));
	        message.setSubject(subject);
	        message.setText(content);
	        message.setDataHandler(new DataHandler(new StringDataSource(content, "text/html"))); 
	
	        return message;
	    } 
	    catch(MessagingException e) 
	    {
	        throw new SystemException("Unable to create the message.", e);
	    }
	}

	/**
	 * @param attachments 
	 *
	 */
	private Message createMessage(String from, String to, String bcc, String subject, String content, String contentType, String encoding, List attachments) throws SystemException
	{
		try 
		{
			final Message message = new MimeMessage(this.session);
			String contentTypeWithEncoding = contentType + ";charset="
					+ encoding;

			// message.setContent(content, contentType);
			message.setFrom(createInternetAddress(from));
			//message.setRecipient(Message.RecipientType.TO,
			//		createInternetAddress(to));
			message.setRecipients(Message.RecipientType.TO,
					createInternetAddresses(to));
			if (bcc != null)
				message.setRecipients(Message.RecipientType.BCC,
						createInternetAddresses(bcc));
			// message.setSubject(subject);

			((MimeMessage) message).setSubject(subject, encoding);
			MimeMultipart mp = new MimeMultipart();
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setDataHandler(new DataHandler(new StringDataSource(content,
					contentTypeWithEncoding, encoding)));
			mp.addBodyPart(mbp1);
			if ( attachments != null ) 
			{
				for( Iterator it = attachments.iterator(); it.hasNext(); ) 
				{
					File attachmentFile = ( File ) it.next();
					if(attachmentFile.exists())
					{
						MimeBodyPart attachment = new MimeBodyPart();
						attachment.setFileName( attachmentFile.getName() );
						attachment.setDataHandler(new DataHandler(new FileDataSource(attachmentFile)));
						mp.addBodyPart(attachment);
					}
				}
			}
			message.setContent(mp);
			// message.setText(content);
			// message.setDataHandler(new DataHandler(new
			// StringDataSource(content, contentTypeWithEncoding, encoding)));
			// message.setText(content);
			// message.setDataHandler(new DataHandler(new
			// StringDataSource(content, "text/html")));

			return message;
		} 
		catch(MessagingException e) 
		{
			throw new SystemException("Unable to create the message.", e);
		}
	}
	
	/**
	 *
	 */
	private Address createInternetAddress(String address) throws SystemException
	{
		try 
		{
	        return new InternetAddress(address);
	    } 
	    catch(AddressException e) 
	    {
	        throw new SystemException("Badly formatted email address [" + address + "].", e);
	    }
	}

	/**
	 *
	 */
	private Address[] createInternetAddresses(String emailAddressString) throws SystemException
	{
	    String[] emailAddresses = emailAddressString.split(";");
	    
	    Address[] addresses = new Address[emailAddresses.length];
	    for(int i=0; i<emailAddresses.length; i++)
	    {
	        String email = emailAddresses[i];
	        try 
			{
	            addresses[i] = new InternetAddress(email);
	        } 
		    catch(AddressException e) 
		    {
		        throw new SystemException("Badly formatted email address [" + email + "].", e);
		    }
	    }
	    
	    return addresses;
	}

}