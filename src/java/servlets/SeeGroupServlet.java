/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import db.DBManager;
import db.Post;
import db.PostFile;
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
public class SeeGroupServlet extends HttpServlet {

    private DBManager manager;

    public void init() throws ServletException {    // inizializza il DBManager dagli attributi di Application
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
        int group_id = Integer.parseInt(request.getParameter("id")); // id del gruppo
        List<Post> posts = new ArrayList(); // lista per i post
        PostFile pf = null; // oggetto di tipo pf usato per l'eventuale link al file caricato in un post
        String fileLink = null; // link al file caricato in un post

        posts = manager.getPosts(group_id);
        String group_name = manager.getGroupName(group_id);
        PrintWriter out = response.getWriter();

        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Group Page</title>");
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
            out.println("<ul class='nav navbar-nav navbar-left'>\n<li><a href='AddPost?id=" + group_id + "'><span class='glyphicon glyphicon-comment'></span><b> Add post</b></a></li></ul>");
            out.println("<ul class='nav navbar-nav navbar-right'><li><a><span class='glyphicon glyphicon-user'></span> " + user.getUsername() + "</a></li></ul>");
            out.println("</div>");
            out.println("</div></nav>");

            out.println("<div style='width:80%; margin:0 auto;'>");

            out.println("<br>");

            if (posts.size() == 0) { // nessun post relativo al gruppo
                out.println("<p> No posts in this group </p>");
            } else { // crea la tabella per visualizzare i post

                Post p;
                for (int x = 0; x < posts.size(); x++) { // ottengo tutti i posti relativi a quel gruppo
                    p = posts.get(x);
                    pf = manager.getFileByPostId(p.getPostId());

                    out.println("<div class='panel panel-default'>");
                    out.println("<table class='table table-condensed table-striped'>");
                    out.println("<th class='col-sm-2' >" + posts.get(x).getWriterName() + " </th>");

                    out.println("<tr>");
                    out.println("<td>" + posts.get(x).getContent());
                    if (pf.getFileName() != null) { // se il post ha un link relativo ad un file lo visualizzo
                        fileLink = request.getContextPath() + "/usersFiles/" + group_id + "/" + pf.getFileName() + "?id=" + group_id;
                        System.out.println("link:" + fileLink);
                        out.println("<br><a href = '" + fileLink + " ' target='_blank'><span class='glyphicon glyphicon-paperclip'></span></a>" + pf.getFileName());
                    }
                    out.println("<br><br><p style='font-size: 10px'>Message posted at " + posts.get(x).getDate() + "</p>");
                    out.println("</td>");
                    out.println("</tr>");
                    out.println("</table>");
                    out.println("</div>");
                    }
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
            Logger.getLogger(SeeGroupServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SeeGroupServlet.class.getName()).log(Level.SEVERE, null, ex);
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
