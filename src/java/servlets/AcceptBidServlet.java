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
public class AcceptBidServlet extends HttpServlet {

    private DBManager manager;

    public void init() throws ServletException {// inizializza il DBManager dagli attributi di Application
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

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String[] acceptedBids = request.getParameterValues("acpBids"); //inviti accettati
        String[] refusedBids = request.getParameterValues("refBids"); //inviti rigiutati

        List<String> accBids = null; //lista degli inviti accettati
        List<String> refBids = null; //lista degli inviti rifiutati
        List<String> conflicts = null; //lista dei conflitti

        if (acceptedBids != null) { //riempimento lista inviti accettati
            accBids = new ArrayList(acceptedBids.length);
            for (int x = 0; x < acceptedBids.length; x++) {
                accBids.add(acceptedBids[x]);
            }
        }

        if (refusedBids != null) { //riempimento lista inviti rifiutati
            refBids = new ArrayList(refusedBids.length);
            for (int x = 0; x < refusedBids.length; x++) {
                refBids.add(refusedBids[x]);
            }
        }

        boolean conflictError = false;
        if ((accBids != null) && (refBids != null)) { //controllo dei conflitti
            conflicts = new ArrayList(acceptedBids.length + refusedBids.length);
            for (int x = 0; x < accBids.size(); x++) {
                for (int y = 0; y < refBids.size(); y++) {
                    if (accBids.get(x).equals(refBids.get(y))) {
                        conflicts.add(accBids.get(x));
                        conflictError = true;
                    }
                }
            }

            for (int x = 0; x < conflicts.size(); x++) { //rimuovo gli inviti con conflitti
                accBids.remove(conflicts.get(x));
                refBids.remove(conflicts.get(x));
            }
        }

        boolean accError = false;
        boolean refError = false;
        if (accBids != null) {

            for (int x = 0; x < accBids.size(); x++) {
                if (manager.checkBids(user.getUserId(), Integer.parseInt(accBids.get(x))) == true) { //controllo dell'invito accettato e invio
                    accBids.remove(x);
                    accError = true;
                }

            }
            manager.AcceptBids(accBids, user.getUserId());
            manager.deleteBids(accBids);
        }
        if (refBids != null) {//check and remove refused bids

            for (int x = 0; x < refBids.size(); x++) {

                if (manager.checkBids(user.getUserId(), Integer.parseInt(refBids.get(x))) == true) { //controllo dell'invito rifiutato
                    refBids.remove(x);
                    refError = true;
                }
            }
            manager.deleteBids(refBids);
        }

        PrintWriter out = response.getWriter();

        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Accept Bids Confirmed</title>");
            out.println("<link href='Style/css/bootstrap.css' rel='stylesheet'>");
            out.println("<meta charset='utf-8'>");
            out.println("<meta http-equiv='X-UA-Compatible' content='IE=edge'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("</head>");

            out.println("<body>");
            out.println("<script src='Style/js/bootstrap.min.js'></script>");
            out.println("<script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js'></script>");
            out.println("<nav class='navbar navbar-default' role='navigation'>");
            out.println("<div class='container-fluid'>");
            out.println("<div class='navbar-header'>");
            out.println("<button type='button' class='navbar-toggle' data-toggle='collapse' data-target='#bs-example-navbar-collapse-1'>");
            out.println("<span class='sr-only'>Toggle navigation</span>");
            out.println("<span class='icon-bar'></span>");
            out.println("<span class='icon-bar'></span>");
            out.println("<span class='icon-bar'></span>");
            out.println("</button>");
            out.println("<a class='navbar-brand' href='Home'><span class='glyphicon glyphicon-home'></span><b> Forum</b></a>");
            out.println("</div>");

            out.println("<div class='collapse navbar-collapse' id='bs-example-navbar-collapse-1'>");
            out.println("<ul class='nav navbar-nav navbar-right'><li><a><span class='glyphicon glyphicon-user'></span> " + user.getUsername() + "</a></li></ul>");
            out.println("</div>");
            out.println("</div></nav>");

            out.println("<div style='width:80%; margin:0 auto;'>");
            if ((accBids == null) && (refBids == null)) {
                out.println("<h1> No bids selected, retry </h1>");
            } else if (accError == true || refError == true) {
                response.sendRedirect(request.getContextPath() + "/Home");
            } else if (conflictError == true) {
                out.println("<h1>Invitation with the accept/refuse conflict not done</h1>");
            } else {
                out.println("<h1> Operation on your invitation done correctly</h1>");
            }
            out.println("<form action = 'Home' method='get' >"); 
            out.println("<input type='submit' value = 'Back to home'/>");
            out.println("</form>");
            out.println("</div>");
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
        try {
            processRequest(request, response);

        } catch (SQLException ex) {
            Logger.getLogger(AcceptBidServlet.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
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
            Logger.getLogger(AcceptBidServlet.class
                    .getName()).log(Level.SEVERE, null, ex);
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
