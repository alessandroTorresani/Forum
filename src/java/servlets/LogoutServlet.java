/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import db.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Ale
 */
public class LogoutServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        Cookie cookies[] = request.getCookies(); //ottengo i cookies
        String cookie_name = null;
        String cookie_value = null;

        for (int i = 0; i < cookies.length; i++) {
            cookie_name = cookies[i].getName();
            if (cookie_name.equals("hour_in/" + user.getUserId())) {
                cookie_value = cookies[i].getValue();
            }
        }

        Cookie cookie_out = new Cookie("hour_out/" + user.getUserId(), cookie_value); // creo il cookie per salvare l'ora di entrata dell'utente
        cookie_out.setMaxAge(604800); // durata una settimana

        Cookie cookie_in = new Cookie("hour_in/" + user.getUserId(), null); // annullo il cookie di hour_in che non mi serve più
        cookie_in.setMaxAge(0);

        response.addCookie(cookie_in); // annullo il cookie di ingresso
        response.addCookie(cookie_out); // setto quello di uscita per memorizzare l'ultima data di accesso

        session.removeAttribute("user"); // elimino l'attributo user
        session.invalidate(); // invalido la sessione

        response.sendRedirect(request.getContextPath() + "/"); // redirico alla pagina di login

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
