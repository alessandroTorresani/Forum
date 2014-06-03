/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import MyUtils.MyUtility;
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
        String uploadDir = request.getServletContext().getRealPath("/") + File.separator+"usersFiles"+ File.separator +  group_id;  // cartella dove carico i file! + id del gruppo! DEVO CREARE LA CARTELLA DEL GRUPPO AL MOMENTO DELLA CREAZIONE
        MultipartRequest multi = new MultipartRequest(request, uploadDir, 10 * 1024 * 1024, "ISO-8859-1", new DefaultFileRenamePolicy()); // devo usare un multipartrequest per passare i parametri (10 Mb limite file)

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String post_content = multi.getParameter("post_content"); // ottengo il contenuto del post da multi
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // data per la creazione del gruppo
        Date date = new Date();
        int post_id = -1;
        boolean resPost = false;
        boolean resFile = false;
        String cleaned = null;
        String postChecked = null;

        // devo fare il parsing qui per controllare i link!
        // primo passo controllo che non ci sia codice html nel testo del post (sostituisco i carattere "tipici" html con la loro rappresentazione non interpretabile in html)
        if ((post_content != null) && (post_content.length() > 0)) {
            cleaned = MyUtility.cleanHTMLTags(post_content); // pulisco il codice da i caratteri html
            postChecked = MyUtility.checkMultiLink(cleaned); // controllo se l'utente ha inserito link e li rendo cliccabili
            
        }
        //System.out.println(postChecked);

        if ((postChecked != null) && (postChecked.length() > 0)) {
            post_id = manager.createPost(group_id, user.getUserId(), dateFormat.format(date), postChecked);
            System.out.println("post_id = " + post_id);
            resPost = true;
            if (post_id > -1) {
                Enumeration files = multi.getFileNames();
                File f;// ottengo il file (solo un file in upload) 
                while (files.hasMoreElements()) {
                    String name = ((String) files.nextElement());
                    /*System.out.println("name: " + name);
                    System.out.println("getFilesystemName. " + multi.getFilesystemName(name));
                    System.out.println("getOriginalFileName: " + multi.getOriginalFileName(name));
                    System.out.println("getContentType " + multi.getContentType(name));*/

                    f = multi.getFile(name);
                    System.out.println("file name: " + f);
                    if (f != null) { // se il file esiste allora adesso devo aggiornare la tabella POST-FILE
                        /*out.println("f.toString()" + f.toString() + "\n");
                        out.println("f.getName()" + f.getName() + "\n");
                        out.println("f.exists()" + f.exists() + "\n");
                        out.println("f.lenght()" + f.length() + "\n");*/
                        resFile = manager.addFileToPost(f.getName(), post_id, group_id, user.getUserId());
                    }
                }
            }
        }
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>Add Post Result</title>");
            out.println("</head>");
            out.println("<body>");

            if ((resPost == true) || (resFile == true)) {
            // 
                /*RequestDispatcher rd = request.getRequestDispatcher("/SeeGroup"); 
                rd.forward(request, response);*/
                response.sendRedirect(request.getContextPath()+ "/SeeGroup?id=" + group_id); //ridirigo alla pagina dove visualizzo i gruppi
                return;
            } else if ((post_content != null) && (post_content.length() == 0)) {
                out.println("<h1>Post message can't be void, please retry</h1>");
                out.println("<form action = 'SeeGroup' method='get' >"); // tasto riprova ad inserire i dati
                out.println("<input type='hidden' value ='"+ group_id + "' name='id' >"); // devo ripubblicare l'id per via del filtro
                out.println("<input type='submit' value = 'Retry'/>");
                out.println("</form>");
            } else if ((resPost == true) && (resFile == false)){
                out.println("<h1>Error uploading your file, please retry</h1>");
                out.println("<form action = 'SeeGroup' method='get' >");// tasto riprova ad inserire i dati
                out.println("<input type='hidden' value ='"+ group_id + "' name ='id'>"); // devo ripubblicare l'id per via del filtro
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
