/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import MyUtility.MyUtility;
import db.DBManager;
import db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import java.io.File;
import java.nio.file.Files;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;

/**
 *
 * @author Alessandro
 */
public class AddPostCServlet extends HttpServlet {

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
        PrintWriter out = response.getWriter();

        int group_id = Integer.parseInt(request.getParameter("id")); // prendo l'id
        String uploadDir = request.getServletContext().getRealPath("/") + File.separator + "usersFiles" + File.separator + group_id;  // cartella dove carico i file! + id del gruppo! DEVO CREARE LA CARTELLA DEL GRUPPO AL MOMENTO DELLA CREAZIONE

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // data per la creazione del gruppo
        Date date = new Date();
        int post_id = -1;
        boolean resPost = false;
        boolean resFile = false;
        MultipartRequest multi = null;
        boolean multiError = false;
        String post_content = null;
        File f = null;

        try {
            multi = new MultipartRequest(request, uploadDir, 10 * 1024 * 1024, "ISO-8859-1", new DefaultFileRenamePolicy());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        System.out.println(multi);
        if (multi != null) {
            post_content = multi.getParameter("post_content"); // ottengo il contenuto del post da multi
            if ((post_content != null) && (post_content.length() > 0)) {
                post_content = MyUtility.cleanHTMLTags(post_content); // pulisco il codice da i caratteri html
                post_content = MyUtility.checkMultiLink(post_content);// controllo se l'utente ha inserito link e li rendo cliccabili
                post_content = post_content.replaceAll("[\n\r]+", "<br>");

                post_id = manager.createPost(group_id, user.getUserId(), dateFormat.format(date), post_content);
                resPost = true;

                if (post_id > -1) {
                    Enumeration files = multi.getFileNames();
                    // ottengo il file (solo un file in upload) 
                    while (files.hasMoreElements()) {
                        String name = ((String) files.nextElement());

                        f = multi.getFile(name);
                        System.out.println("file name: " + f);
                        if (f != null) { // se il file esiste allora adesso devo aggiornare la tabella POST-FILE
                            resFile = manager.addFileToPost(f.getName(), post_id, group_id, user.getUserId());
                        }
                    }
                } else {
                    Enumeration files = multi.getFileNames(); //if something in the post creation went wrong, and was uploaded a file -> delete the file
                    while (files.hasMoreElements()) {
                        String name = ((String) files.nextElement());
                        System.out.println("name:" + name);
                        f = multi.getFile(name);
                        if (f != null) {
                            System.out.println("entrato nell'if del delete");
                            f.delete();
                        }
                    }
                }
            } else {
                Enumeration files = multi.getFileNames(); //if the post content was empty, delete the uploaded file if there is
                while (files.hasMoreElements()) {
                    String name = ((String) files.nextElement());
                    System.out.println("name:" + name);
                    f = multi.getFile(name);
                    if (f != null) {
                        System.out.println("entrato nell'if del delete");
                        f.delete();
                    }
                }
            }
        } else {
            multiError = true; //error no multipart request
        }
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Add Post Confirmed</title>");
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
            
            if (multiError == true) {
                out.println("<h1>File size error or servlet not called property</h1>");
                out.println("<form action = 'SeeGroup' method='get' >");
                out.println("<input type='hidden' value ='" + group_id + "' name ='id'>");
                out.println("<input type='submit' value = 'Retry'/>");
                out.println("</form>");
                
            } else if ((resPost == true) || (resFile == true)) {
                response.sendRedirect(request.getContextPath() + "/SeeGroup?id=" + group_id);
                
            } else if ((post_content != null) && (post_content.length() == 0)) {
                out.println("<h1>Post message can't be void, please retry</h1>");
                out.println("<form action = 'SeeGroup' method='get' >");
                out.println("<input type='hidden' value ='" + group_id + "' name='id' >");
                out.println("<input type='submit' value = 'Retry'/>");
                out.println("</form>");
                
            } else if ((resPost == true) && (resFile == false)) {
                out.println("<h1>Error uploading your file, please retry</h1>");
                out.println("<form action = 'SeeGroup' method='get' >");
                out.println("<input type='hidden' value ='" + group_id + "' name ='id'>");
                out.println("<input type='submit' value = 'Retry'/>");
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
            Logger.getLogger(AddPostCServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AddPostCServlet.class.getName()).log(Level.SEVERE, null, ex);
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
