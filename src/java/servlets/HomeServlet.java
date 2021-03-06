/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import db.Bid;
import db.DBManager;
import db.Group;
import db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class HomeServlet extends HttpServlet {

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
        // devo accedere al database e caricare gruppi e inviti.

        HttpSession session = request.getSession();
        List<Group> groups; // gruppi a cui un utente è inscritto
        List<Bid> bids; // inviti che ha ricevuto
        User user = (User) session.getAttribute("user");

        String cookie_name = null;
        String cookie_value = null;
        String ultimo_accesso = null;

        Cookie cookie[] = request.getCookies(); // prendo tutti i cookie 

        for (int i = 0; i < cookie.length; i++) { // cerco il cookie chiamato hour_out/id_user
            cookie_name = cookie[i].getName();
            cookie_value = cookie[i].getValue();
            String[] parts = cookie_name.split("/"); // divido il nome del cookie per ricavarmi l'id che specifica a quale utente mi riferisco
            if (("hour_out".equals(parts[0])) && (("" + user.getUserId()).equals(parts[1])) && (cookie_value.length() > 0)) { // appena lo trovo mi salvo il valore del cookie
                ultimo_accesso = cookie_value;
            }
        }

        // recupero i gruppi a cui partecipa l'utente usando il suo id
        try {
            groups = manager.getGroups(user.getUserId());

        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);

        }
        try {
            bids = manager.getBids(user.getUserId()); // recupero gli inviti ricevuti dall'utente
        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }

        PrintWriter out = response.getWriter();
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Forum Home</title>");
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
            out.println("<ul class='nav navbar-nav navbar-left'>\n<li><a href='CreateGroup'><span class='glyphicon glyphicon-plus-sign'></span><b> Create group</b></a></li></ul>");
            out.println("<ul class='nav navbar-nav navbar-right'><li><a><span class='glyphicon glyphicon-user'></span> " + user.getUsername() + "</a></li></ul>");
            out.println("</div>");
            out.println("</div></nav>");

            out.println("<div style='width:80%; margin:0 auto;'>");

            out.println("<h1> Home </h1>");

            out.println("<h3>Groups</h3>");
            if (groups.size() == 0) { // se l'utente non fa parte di nessun gruppo
                out.println("<p> You don't have groups </p>");
            } else {

                out.println("<div class='panel panel-default'>");
                out.println("<table class='table'>"); // altimenti creo una tabella per visualizzare i gruppi
                out.println("<tr>");

                out.println("<th> Group Name </th>");
                out.println("<th> Owner  </th>");
                out.println("<th> Creation date </th>");
                out.println("<th></th>");
                out.println("<th></th>");
                out.println("<th></th>");

                out.println("</tr>");

                for (int x = 0; x < groups.size(); x++) { // stampo i gruppi
                    out.println("<tr>");

                    out.println("<td>" + "<a href='SeeGroup?id=" + groups.get(x).getGroupId() + "'>" + groups.get(x).getGroupName() + "</a> </td>");
                    out.println("<td>" + groups.get(x).getOwnerName() + "</td>");
                    out.println("<td>" + groups.get(x).getCreationDate() + "</td>");
                    if (groups.get(x).getOwner() == user.getUserId()) { // tasto solo per l'admin del gruppo per poterlo modificare
                        out.println("<td> <form action ='ChangeName' method ='get'>"
                                + "<input type='hidden' value = '" + groups.get(x).getGroupId() + "' name='group_id' >"
                                + "<input type='submit' value='Change name' style='width:100%'>"
                                + "</form></td>");
                        out.println("<td> <form action='AddBids' method='get'>"
                                + "<input type='hidden' value = '" + groups.get(x).getGroupId() + "' name='group_id' >"
                                + "<input type='submit' value='Invite' style='width:100%'>"
                                + "</form></td>");

                        out.println("<td> <form action ='Report' method ='get' target='_blank'>"
                                + "<input type='hidden' value = '" + groups.get(x).getGroupId() + "' name='group_id' >"
                                + "<input type='submit' value='Report' style='width:100%'>"
                                + "</form></td>");
                    } else {
                        out.println("<td> </td>");
                        out.println("<td> </td>");
                        out.println("<td> </td>");
                    }

                    out.println("</tr>");
                }
                out.println("</table>");
                out.println("</div>");
            }

            out.println("<br>");
            out.println("<h3>Invitations</h3>");

            if (bids.size() == 0) {
                out.println("<p> You have no bids ");
            } else {
                out.println("<div class='panel panel-default'>");
                out.println("<table class='table'>");
                out.println("<tr>");

                out.println("<th> Group Name </th>");
                out.println("<th> Sender </th>");
                out.println("<th> Accept </th>");
                out.println("<th> Refuse </th>");

                out.println("</tr>");

                out.println("<form action ='AcceptBid' method ='get'>");
                for (int x = 0; x < bids.size(); x++) { // stampo gli inviti
                    out.println("<tr>");

                    out.println("<td>" + bids.get(x).getGroupName() + "</td>");
                    out.println("<td>" + bids.get(x).getSenderName() + "</td>");
                    out.println("<td> <input type='checkbox' name='acpBids' value='" + bids.get(x).getBidId() + "' checked='checked'>");// checkbox per accetare gli invii
                    out.println("<td> <input type='checkbox' name='refBids' value='" + bids.get(x).getBidId() + "' >");//checkbok per rifutare gli inviti

                    out.println("</tr>");
                }

                out.println("</table>");
                out.println("</div>");
                out.println("<input type='submit' value = 'Submit'/>");
                out.println("</form>");
                out.println("<br>");

            }

            out.println("<br>");

            out.println("<form action = 'Logout' method='POST' >"); // tasto logout
            out.println("<input type='submit' value = 'Logout'/>");
            out.println("</form>");
            out.println("<br>");

            if (ultimo_accesso != null) { // data ultimo accesso
                out.println("<p> Last access: " + ultimo_accesso + "</p>");
            } else {
                out.println("<p> Last access: First access or invalid/removed cookie </p>");
            }
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
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, null, ex);
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
