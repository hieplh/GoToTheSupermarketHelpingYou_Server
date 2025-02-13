package com.smhu.listener;

import com.smhu.GototheSupermarketHelpingYouApplication;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener()
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new GototheSupermarketHelpingYouApplication().init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
