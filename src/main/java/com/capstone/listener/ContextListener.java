package com.capstone.listener;

import com.capstone.main.Main;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener()
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new Main().init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
