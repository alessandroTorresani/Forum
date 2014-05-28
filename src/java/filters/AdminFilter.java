/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import db.DBManager;
import db.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author harwin
 */
public class AdminFilter implements Filter {

    private FilterConfig filterConfig;
    private DBManager manager; // devo usare la connessione al db

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.filterConfig = filterConfig;

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        HttpSession session = ((HttpServletRequest) request).getSession(); 
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute("user"); // ottengo l'user loggato
        this.manager = (DBManager) request.getServletContext().getAttribute("dbmanager"); // ottengo la connessione al db

        int owner_id = 0;
        String group_id = request.getParameter("group_id"); // passoto nell'url tramite get l'id del gruppo 

        if ((user != null) && (group_id != null)) { 
            try {
                owner_id = manager.getGroupOwner(Integer.parseInt(group_id)); // ottengo l'id del proprietario del gruppo
            } catch (SQLException ex) {
                Logger.getLogger(AdminFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
   
        if ((user != null) && (owner_id == user.getUserId())) { // se il proprietario del gruppo e l'user attualmente nella sessione coincidono -> ok
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath() + "/Home"); // altrimenti ridirigo alla home

        }
    }

    @Override
    public void destroy() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
