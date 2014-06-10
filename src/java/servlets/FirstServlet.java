/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author harwin
 */
public class FirstServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String loginResult = request.getParameter("login");// stringa usata in casa di errore nel login

        String off_style = "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" // stile offline -> boostrap
                + "<link href='Style/css/bootstrap.min.css' rel='stylesheet' media='screen'>"
                + "<script src='http://code.jquery.com/jquery.js'></script>"
                + "<script src='Style/js/bootstrap.min.js'></script>";

        String on_style = "<link rel=\"stylesheet\" href=\"http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css\" />\n" // stile online -> jquery mobile
                + "<script src=\"http://code.jquery.com/jquery-1.8.2.min.js\"></script>\n"
                + "<script src=\"http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js\"></script>";


        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Forum Login</title>");
            out.println(off_style);
            out.println("</head>");
            out.println("<body>");
            out.println("<div id=\"header\" style='margin:20px';>" // header
                    + "<h1>Forum - Login</h1>"
                    + "</div>");          
                    
            if ((loginResult != null) && (loginResult.equals("failure")))
                out.println("<p> Login errato </p>"); // dovrei fare un popup
            
            
             out.println("<div id='form_id'  style='margin: 20px';>"); // form login
             out.println("<FORM action='Login' method ='POST'>");
             out.println("Username: <input type='text' name='username'> <br />");
             out.println("Password:  <input type='password' name='password'> <br />");
             out.println("<INPUT type='submit' value='Login'>");
             out.println("</FORM>");
             out.println("</div>");

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
