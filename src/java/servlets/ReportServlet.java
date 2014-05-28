/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import db.DBManager;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.darwinsys.spdf.PDF;
import com.darwinsys.spdf.Page;
import com.darwinsys.spdf.Text;
import com.darwinsys.spdf.MoveTo;
import db.User;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alessandro
 */
public class ReportServlet extends HttpServlet {

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
            throws ServletException, IOException {

        List<Integer> users_ids;
        List<String> users_names;
        List<User> users;
        String lastPostMade;
        int numberOfPosts;
        int group_id = Integer.parseInt(request.getParameter("group_id"));
        String group_name;

        PrintWriter out = response.getWriter();
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "inline; filename ='Report.pdf'");

        try {
            users_ids = manager.getUsersIntoGroup(group_id); // ottengo gli id degli utenti partecipanti
        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }

        try {
            users_names = manager.getUsersNameByIds(users_ids); // ottengo i nomi degli utenti partecipanti
        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }
        
        try {
            lastPostMade = manager.getLastPost(group_id); // ottengo il timestamp dell'ultimo post fatto
        } catch (SQLException ex) {
            Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }
        
        try {
            numberOfPosts = manager.getNumberOfPosts(group_id); // ottengo il numero di post
        } catch (SQLException ex){
             Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }
        
        try {
            group_name = manager.getGroupName(group_id);
        } catch (SQLException ex){
             Logger.getLogger(HomeServlet.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            throw new ServletException(ex);
        }

        PDF p = new PDF(out); // creo il pdf
        Page p1 = new Page(p); // creo la pagina all'interno del pdf
        p1.add(new MoveTo(p, 200, 700));
        
         p1.add(new Text(p, "" + group_name));
         p1.add(new Text(p,""));

        p1.add(new Text(p, "- Users in your group:"));
        for (int x = 0; x < users_names.size(); x++) {
            p1.add(new Text(p, "*   " + users_names.get(x))); // 
        }
        p1.add(new Text(p,""));

        p1.add(new Text(p, "- Data of last post: "));
        if (lastPostMade != null) {
            p1.add(new Text(p, "*   " + lastPostMade));
        } else {
            p1.add(new Text(p, "*   No posts in your group"));
        }
        p1.add(new Text(p,""));
        
        p1.add(new Text(p, "- Number of post: "));
        p1.add(new Text(p, "*   "+numberOfPosts)); // query da fare
        p.add(p1);
        p.setAuthor("Alessandro");
        p.writePDF();
        
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
