/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import MyUtils.MyUtility;
import db.DBManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author harwin
 */
public class ChangeNameCServlet extends HttpServlet {

    private DBManager manager;

    public void init() throws ServletException {
// inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

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
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");

        boolean res = false; // variabile per sapere se l'operazione è andata a buon fine (funzione non ancora implementata)
        int group_id = 0;
        String group_name = null;

        if ((request.getParameter("group_id")) == null) { // controllo se la servlet viene chiamata in modo appropriato (tramite il form) controllo in teoria inutile per l'esistenza del adminfilter
            response.sendRedirect(request.getContextPath() + "/Home"); // se viene chiamata in modo sbagliato ridirigo alla home
        } else {
            group_id = Integer.parseInt(request.getParameter("group_id"));
            group_name = request.getParameter("group_name");
        }

        if (MyUtility.checkHtml(group_name) == true) { // controllo che il nome contenga solo caratteri ammessi
            res = manager.changeGroupName(group_name, group_id);
        }
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Change Name result</title>");
            out.println("</head>");
            out.println("<body>");
            if (res == true) {
                out.println("<h1>Group' name changed succesfully </h1>");
                out.println("<form action = 'Home' method='post' >"); // tasto torna alla home
                out.println("<input type='submit' value = 'Torna alla Home'/>");
                out.println("</form>");
            } else if ((MyUtility.checkHtml(group_name)) == false) {
                out.println("<h1>Error: use only alphanumeric characters, retry</h1>");
                out.println("<form action = 'ChangeName' method='post' >"); // tasto riprova ad inserire i dati
                out.println("<input type='submit' value = 'Riprova'/>");
                out.println("</form>");
            }
            else {
                out.println("<h1>Error: name already used, please try another</h1>");
                out.println("<form action = 'ChangeName' method='post' >"); // tasto riprova ad inserire i dati
                out.println("<input type='submit' value = 'Riprova'/>");
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
        /* try {
         processRequest(request, response);
         } catch (SQLException ex) {
         Logger.getLogger(ChangeNameCServlet.class.getName()).log(Level.SEVERE, null, ex);
         }*/
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(ChangeNameCServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
