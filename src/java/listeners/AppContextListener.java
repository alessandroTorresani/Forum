/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import db.DBManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author harwin
 */
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        ServletContext stx = sce.getServletContext();
        String dburl = stx.getInitParameter("DBURL"); //parametri connessione al database salvati n web.xml
        String user = stx.getInitParameter("DBUSER");
        String password = stx.getInitParameter("DBPWD");

        try {
            DBManager manager = new DBManager(dburl, user, password); 
            sce.getServletContext().setAttribute("dbmanager", manager);     // pubblico la connessione al database sul servlet context
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).severe(ex.toString());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        DBManager.shutdown();      // chiudo il database
    }

}

