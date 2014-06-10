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
public class ChangeNameServlet extends HttpServlet {

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

        int group_id = 0;
        
        try {
            group_id = Integer.parseInt(request.getParameter("group_id")); // altrimento salvo l'id del gruppo 
        } catch (NumberFormatException ex){
            System.out.println(ex);
        }
        
        PrintWriter out = response.getWriter();
        // System.out.println("User id = " + user.getUserId() + "Group_id = " + group_id);

        /*if ((group_id > 0) && (user.getUserId() >0)){
         manager.changeGroupName(user.getUserId(), group_id);
         }
         */
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Change Name Form</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1> Insert new Name (Use only numbers and alpahumeric characters)</h1>");
            out.println("<form action = 'ChangeNameC' method='post' >"); // Form per cambiare il nome
            out.println("Group name:  <input type='text' name='group_name'>");
            out.println("<input type='hidden' value = '" + group_id + "' name='group_id'>");
            out.println("<input type='submit' value = 'Submit'/>");
            out.println("</form>");
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
