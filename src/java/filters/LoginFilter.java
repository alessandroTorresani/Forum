/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import db.User;
import java.io.IOException;
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
 * @author Ale
 */
public class LoginFilter implements Filter {

    private FilterConfig filterConfig;
    // manca il logger qui

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        HttpSession session = ((HttpServletRequest) request).getSession();
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute("user");
        
        if (user != null) { // se esiste l'attributo user nella session
            chain.doFilter(request, response); // procedo verso la home
        } else {
            ((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath() + "/?login=failure"); // rimando alla pagina di login con un errore
        }

    }

    @Override
    public void destroy() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
