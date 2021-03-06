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
import java.util.List;
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
public class GroupFilter implements Filter {

    private FilterConfig filterConfig;
    private DBManager manager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession();
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute("user"); // ottengo l'attributo user
        this.manager = (DBManager) request.getServletContext().getAttribute("dbmanager"); //connessione al database
        int id_group = 0; // se non riesce il parsing rimane questo valore che viene considerato errore
        List<Integer> users = null; // lista che conterrà tutti gli id ammessi al gruppo

        try {
            id_group = Integer.parseInt(request.getParameter("id")); // ottengo l'id passato per l'url (devo controllare che venga passato un int e non qualcos'altro)
        } catch (Exception ex) {
            System.out.println(ex);
        }

        if (id_group > 0) { // se l'input è stato passato correttamente
            try {
                users = manager.getUsersIntoGroup(id_group); // funzione per calcolare gli id
            } catch (SQLException ex) {
                Logger.getLogger(GroupFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if ((id_group > 0) && (users.contains(user.getUserId()))) { // se l'utente è loggato e il suo id è tra quelli ammessi
            chain.doFilter(request, response); // può entrare nel gruppo
        } else {
            ((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath() + "/Home"); // altrimenti ridirigo alla home
        }

    }

    @Override
    public void destroy() {
    }

}
