package controller;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import dal.TripDBContext;

@WebListener
public class MyServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // This code runs when the application starts up
        TripDBContext tripDB = new TripDBContext();
        tripDB.updateTripStatus();
        System.out.println("Trip statuses updated on application startup."); // Optional logging
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // This code runs when the application shuts down (usually empty, unless you need cleanup)
    }
}