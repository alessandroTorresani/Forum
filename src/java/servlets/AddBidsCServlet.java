/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import db.DBManager;
import db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author harwin
 */
public class AddBidsCServlet extends HttpServlet {

    private DBManager manager;

    public void init() throws ServletException {
// inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        int group_id = Integer.parseInt(request.getParameter("group_id")); // ottengo l'id del gruppo
        String[] checkbox_params = request.getParameterValues("user"); // ottengo gli id degli utenti invitati
        List<String> ids = new ArrayList();

        if (checkbox_params != null) {
            for (int x = 0; x < checkbox_params.length; x++) { // metto i dati in una List<String> per la funzione sendbids
                ids.add(checkbox_params[x]);
            }


            if (checkbox_params.length > 0) {
                try {
                    manager.sendBids(ids, group_id, user.getUserId());
                } catch (SQLException ex) {
                    Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
                    throw new ServletException(ex);
                }
            }

        }

        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Add bids result</title>");
            out.println("</head>");
            out.println("<body>");

            if (checkbox_params != null) {
                out.println("<h1>Your Bids were send correctly</h1>");
                out.println("<form action = 'Home' method='post' >"); // tasto torna alla home
                out.println("<input type='submit' value = 'Torna alla Home'/>");
                out.println("</form>");
            } else {
                out.println("<h1>No user selected, retry!</h1>");
                out.println("<form action = 'AddBids' method='post' >"); // tasto riprova ad inserire i dati
                out.println("<input type='submit' value = 'Retry'/>");
                out.println("</form>");
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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
