// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HibernateSessionFactory.java

package org.infoglue.common.component;

import com.opensymphony.xwork.interceptor.component.Disposable;
import com.opensymphony.xwork.interceptor.component.Initializable;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class HibernateSessionFactory
    implements Initializable, Disposable, Serializable
{

    public HibernateSessionFactory()
    {
    }

    public SessionFactory getSessionFactory()
    {
        return factory;
    }

    public void init()
    {
        try
        {
            System.out.println("");
            System.out.println("HibernateSessionFactory.init........");
            System.out.println("");
            factory = (new Configuration()).configure().buildSessionFactory();
        }
        catch(Exception e)
        {
            LOG.error("error configuring", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void dispose()
    {
        try
        {
            factory.close();
        }
        catch(Exception e)
        {
            LOG.error("error closing", e);
        }
    }

    public String getDialect()
    {
        return Environment.getProperties().getProperty("hibernate.dialect");
    }

    private static final Log LOG;
    private SessionFactory factory;

    static 
    {
        LOG = LogFactory.getLog(org.infoglue.common.component.HibernateSessionFactory.class);
    }
}
