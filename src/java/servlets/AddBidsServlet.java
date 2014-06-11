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
public class AddBidsServlet extends HttpServlet {

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
        
        int group_id = Integer.parseInt(request.getParameter("group_id"));
        List<Integer> first_filter = new ArrayList(); // primo filtro di id, ottengo gli id degli utenti che non fanno gia parte del gruppo
        List<Integer> second_filter = new ArrayList(); // secondo filtro per ottenere gli utenti che non hanno gia un invito attivo per tale gruppo
        List<String> users_ids = new ArrayList(); // utenti definitivi che dovranno essere invitabili dal form

        try {
            first_filter = manager.getUsersNotIntoGroup(group_id); // ottengo gli id degli utenti che non fanno gia parte di questo gruppo (bug un utente che non ha già un gruppo non compare)

        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }

        try {
            second_filter = manager.getUserNotInvited(group_id); // ottengo gli id degli utenti che non fanno gia parte di questo gruppo

        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }

        if ((second_filter.size() == 0) && (first_filter.size() > 0)) { // se il secondo filtro non ha dato risultati e il primo non è vuoto allora il primo sarà il definiivo
            for (int x = 0; x < first_filter.size(); x++) {
                users_ids.add("" + first_filter.get(x));
            }

        } else if ((second_filter.size() > 0) && (first_filter.size() > 0)) { // devo togliere gli utenti che hanno già un invito in attesa
            for (int x = 0; x < second_filter.size(); x++) {
                if (first_filter.contains(second_filter.get(x))) {
                    first_filter.remove(second_filter.get(x));
                }
            }
            for (int x = 0; x < first_filter.size(); x++) {
               users_ids.add("" + first_filter.get(x));
            }
        }

        // adesso che ho gli id delle persone che posso invitare devo recuperare tali utenti per poterli stampare
        List<User> users = null;
        try {
            users = manager.getUsersById(users_ids);

        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }

        // devo mandare gli inviti alla persone che non fanno già parte del gruppo o che non hanno già un'invito relativo al gruppo
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Add Bids</title>");
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
            
            if (users.size() > 0) {
                out.println("<h1>Who do you want to invite ? </h1>");
                 
                out.println("<FORM action='AddBidsC' method ='POST'>");
                User u;
                for (int x = 0; x < users.size(); x++) {
                    u = users.get(x);
                    out.println("<input type='checkbox' name='user' value= " + "'" + u.getUserId() + "'>" + u.getUsername() + "<br>");
                }
                out.println("<br>");
                out.println("<input type='hidden' value = '" + group_id + "' name='group_id'>");
                out.println("<INPUT type='submit' value='Invite'>");
                out.println("</FORM>");
            } else {
                out.println("<h1>No invitable users</h1>");
                out.println("<br>");
                out.println("<form action = 'Home' method='get' >"); // tasto torna alla home
                out.println("<input type='submit' value = 'Back to home'/>");
                out.println("</form>");
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
            Logger.getLogger(AddBidsServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AddBidsServlet.class.getName()).log(Level.SEVERE, null, ex);
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
