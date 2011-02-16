package org.infoglue.common.component;

import java.io.PrintStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;

public class HibernateSession implements HibernateSessionFactoryAware
{

	public HibernateSession()
	{
	}

	public Session getSession() throws HibernateException
	{
		System.out.println("");
		System.out.println("HibernateSession.getSession()........");
		System.out.println("");
		if (session == null)
		{
			System.out.println("Initializing session:" + factory);
			System.out.println("Initializing session:" + factory.getSessionFactory());
			session = factory.getSessionFactory().openSession();
			System.out.println("Initializing session:" + session);
			transaction = session.beginTransaction();
		}
		return session;
	}

	public void setHibernateSessionFactory(HibernateSessionFactory factory)
	{
		this.factory = factory;
	}

	public void disposeSession() throws HibernateException
    {
        LOG.debug("disposing");
        if(session == null)
            return;
        if(rollBackOnly)
        {
            try
            {
                LOG.debug("rolling back");
                if(transaction != null)
                    transaction.rollback();
            }
            catch(HibernateException e)
            {
                LOG.error("error during rollback", e);
                throw e;
            }
            try
            {
            }
            finally
            {
                session.close();
                session = null;
                transaction = null;
            }
        }
        try
        {
            LOG.debug("committing");
            if(transaction != null)
                transaction.commit();
        }
        catch(HibernateException e)
        {
            LOG.error("error during commit", e);
            transaction.rollback();
            throw e;
        }

        session.close();
        session = null;
        transaction = null;
    }

	public boolean isRollBackOnly()
	{
		return rollBackOnly;
	}

	public void setRollBackOnly(boolean rollBackOnly)
	{
		this.rollBackOnly = rollBackOnly;
	}

	private static final Log LOG;

	private Session session;

	private Transaction transaction;

	private HibernateSessionFactory factory;

	private boolean rollBackOnly;

	static
	{
		LOG = LogFactory.getLog(org.infoglue.common.component.HibernateSession.class);
	}
}
