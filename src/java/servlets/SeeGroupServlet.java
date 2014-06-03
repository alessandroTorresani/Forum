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
            out.println("<title>" + group_name + "</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>" + group_name + "</h1>");

            out.println("<form action = 'AddPost' method='get' >");// tasto aggiungi post
            out.println("<input type='hidden' value = '" + group_id + "' name='id' >"); // sarà filtrato dal groupFilter
            out.println("<input type='submit' value = 'Add Post'/>");
            out.println("</form>");
            out.println("<br>");

            if (posts.size() == 0) { // nessun post relativo al gruppo
                out.println("<p> No posts in this group </p>");
            } else { // crea la tabella per visualizzare i post

                Post p;
                out.println("<table border='1'>"); // creo la tabella per visualizzare il post
                out.println("<tr>");
                out.println("<th> From </th>");
                out.println("<th> Data </th>");
                out.println("<th> Post  </th>");
                out.println("<th> Allegati  </th>");
                out.println("</tr>");

                for (int x = 0; x < posts.size(); x++) { // ottengo tutti i posti relativi a quel gruppo
                    p = posts.get(x);
                    pf = manager.getFileByPostId(p.getPostId());

                    out.println("<tr>");
                    out.println("<td>" + posts.get(x).getWriterName() + "</td>");
                    out.println("<td>" + posts.get(x).getDate() + "</td>");
                    out.println("<td>" + posts.get(x).getContent() + "</td>");

                    if (pf.getFileName() != null) { // se il post ha un link relativo ad un file lo visualizzo
                        fileLink = request.getContextPath() + "/usersFiles/" + group_id + "/" + pf.getFileName()+"?id="+group_id;
                        System.out.println("link:" + fileLink);
                        out.println("<td> <a href = '"+fileLink+" ' target='_blank'>" + pf.getFileName() + "</a> </td>");
                    } else {
                        out.println("<td></td>");
                    }
                    out.println("</tr>");
                }
            }
            out.println("</table>");
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
