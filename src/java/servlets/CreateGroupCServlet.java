/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import db.DBManager;
import db.Group;
import db.User;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class CreateGroupCServlet extends HttpServlet {

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
        List<String> checkbox_bids = new ArrayList();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // data per la creazione del gruppo
        Date date = new Date();

        String[] checkbox_params = request.getParameterValues("user"); // parametri del checkbox
        String group_name = request.getParameter("group_name"); // nome del gruppo
        Group created_group = null;

        if ((group_name != null) && (MyUtility.MyUtility.checkHtml(group_name)) && (group_name.length() > 0)) { // controllo se è stato compilato il campo nome, e se contiene solo caratteri ammessi
            created_group = manager.createGroup(user.getUserId(), group_name, dateFormat.format(date));// creo il gruppo
        }

        int group_id = 0;
        if (created_group != null) { // se la creazione è andata a buon fine mi salvo l'id del gruppo appena creato
            group_id = created_group.getGroupId();
            String folderPath = request.getServletContext().getRealPath("/");
            File dir = new File(folderPath + File.separator + "usersFiles"+ File.separator + group_id); // creo la cartella dove saranno messi i file in upload
            dir.mkdir();
        }

        if (checkbox_params != null) {
            for (int x = 0; x < checkbox_params.length; x++) { // trasferisco i dati in una lista per passarla alla funzione sendBids del dbmanager
                checkbox_bids.add(checkbox_params[x]);
            }
        }

        if ((checkbox_params != null) && (group_id != 0)) { // mando gli inviti se l'utente ha selezionato degli utenti e la creazione del gruppo è andata a buon fine
            manager.sendBids(checkbox_bids, group_id, user.getUserId());
        }
        
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Group Confirmed</title>");
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
            if (group_id != 0) { // andato tutto ok, gruppo creato
                out.println("<h1> Group created succesfully; invitations sended </h1>");
                out.println("<form action = 'Home' method='post' >"); // tasto torna alla home
                out.println("<input type='submit' value = 'Torna alla Home'/>");
                out.println("</form>");
            } else if ((group_name == null) || (MyUtility.MyUtility.checkHtml(group_name) == false)) { // se servlet chiamata illegalmente, o con input errato da errore
                out.println("<h1> Error: one name required; use only alphanumeric characters, retry </h1>");
                out.println("<form action = 'CreateGroup' method='post' >"); // tasto riprova ad inserire i dati
                out.println("<input type='submit' value = 'Riprova'/>");
                out.println("</form>");
            } else {
                out.println("<h1>Error: name already used, please try another </h1>"); // unico caso rimanente: nome già presente
                out.println("<form action = 'CreateGroup' method='post' >"); // tasto riprova ad inserire i dati
                out.println("<input type='submit' value = 'Riprova'/>");
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
            Logger.getLogger(CreateGroupCServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(CreateGroupCServlet.class.getName()).log(Level.SEVERE, null, ex);
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
