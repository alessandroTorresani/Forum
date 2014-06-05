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
public class AcceptBidServlet extends HttpServlet {

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

        /*HttpSession session = request.getSession();
         User user = (User) session.getAttribute("user");
         ArrayList<Integer> bidsIdsAcp = null; // inviti accettati
         ArrayList<Integer> bidsIdsRef = null;// inviti rifiutati

         String[] checkbox_params_acp = request.getParameterValues("acpBids"); // prendo gli id degli inviti accettati
         if (checkbox_params_acp != null) {
         bidsIdsAcp = new ArrayList<Integer>(checkbox_params_acp.length); //arraylist per controllo duplicati
         for (int x = 0; x < checkbox_params_acp.length; x++) {
         bidsIdsAcp.add(Integer.parseInt(checkbox_params_acp[x])); // aggiungo gli id all'arraylist
         }
         }

         String[] checkbox_params_ref = request.getParameterValues("refBids"); // prendo gli id degli inviti rifiutati
         if (checkbox_params_ref != null) {
         bidsIdsRef = new ArrayList<Integer>(checkbox_params_ref.length); // arraylist per controllo duplicati
         for (int x = 0; x < checkbox_params_ref.length; x++) {
         bidsIdsRef.add(Integer.parseInt(checkbox_params_ref[x])); //aggiungo gli id all'arraylist
         }
         }

         boolean error = false;
         if (bidsIdsAcp != null && bidsIdsRef != null) { // se sono stati sia rifiutati che accettati inviti devo controllare         
         for (int x = 0; x < bidsIdsRef.size(); x++) {
         if (bidsIdsAcp.contains(bidsIdsRef.get(x))) {
         error = true; // errore l'utente ha selezionato sia accetta che rifiuta 
         break;
         }
         }
         }

         System.out.println("error= " + error);
       
         boolean bidsCheckedAcp = false;
         boolean bidsCheckedRef = false;
         if (error == false) { // se non ci sono inviti sia rifiutati che accettati contemporanemente allora posso eseguire la query

         if ((bidsIdsAcp != null) && (manager.checkBids(bidsIdsAcp, user.getUserId()))) {
         bidsCheckedAcp = true;
         System.out.println("bidcheckedacp= " + bidsCheckedAcp);
         manager.acceptBids(checkbox_params_acp, user.getUserId()); //accetto gli invito selezionati
         }
         if ((bidsIdsRef != null) && (manager.checkBids(bidsIdsRef, user.getUserId()))) {
         bidsCheckedRef = true;
         System.out.println("bidcheckedref= " + bidsCheckedRef);
         manager.refuseBids(checkbox_params_ref); // rifiuto gli inviti selezionati
         }  
         } */
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String[] acceptedBids = request.getParameterValues("acpBids"); //checkbox params
        String[] refusedBids = request.getParameterValues("refBids");

        List<String> accBids = null; //lists for bids ids
        List<String> refBids = null;
        List<String> conflicts = null;

        if (acceptedBids != null) { //fill accepting list
            accBids = new ArrayList(acceptedBids.length);
            for (int x = 0; x < acceptedBids.length; x++) {
                accBids.add(acceptedBids[x]);
                System.out.println("accbid:: " + accBids.get(x));
            }
        }

        if (refusedBids != null) { // fill refused list
            refBids = new ArrayList(refusedBids.length);
            for (int x = 0; x < refusedBids.length; x++) {
                refBids.add(refusedBids[x]);
            }
        }

        boolean conflictError = false;
        if ((accBids != null) && (refBids != null)) { //check for conflicts
            conflicts = new ArrayList(acceptedBids.length + refusedBids.length);
            for (int x = 0; x < accBids.size(); x++) {
                for (int y = 0; y < refBids.size(); y++) {
                    if (accBids.get(x).equals(refBids.get(y))) {
                        conflicts.add(accBids.get(x));
                        conflictError = true;
                    }
                }
            }

            for (int x = 0; x < conflicts.size(); x++) { //remove conflicts
                accBids.remove(conflicts.get(x));
                refBids.remove(conflicts.get(x));
            }
        }

        boolean accError = false;
        boolean refError = false;
        if (accBids != null) { //check,accept and remove accepted bids

            for (int x = 0; x < accBids.size(); x++) {
                System.out.println("accbid:: " + accBids.get(x));
                if (manager.checkBids(user.getUserId(), Integer.parseInt(accBids.get(x))) == true) { //check if those bids are present and are referred to this user
                    System.out.println("removed: " + accBids.get(x));
                    accBids.remove(x);
                    accError = true;
                }

            }
            manager.AcceptBids(accBids, user.getUserId());
            manager.deleteBids(accBids);
        }
        if (refBids != null) {//check and remove refused bids

            for (int x = 0; x < refBids.size(); x++) {

                if (manager.checkBids(user.getUserId(), Integer.parseInt(refBids.get(x))) == true) {
                    refBids.remove(x);
                    refError = true;
                }
            }
            manager.deleteBids(refBids);
        }

        PrintWriter out = response.getWriter();

      // System.out.println(accBids.size() + " " + refBids.size());
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Bid Result</title>");
            out.println("</head>");
            out.println("<body>");
            System.out.println("prova1");
            if ((accBids == null) && (refBids == null)) {
                out.println("<h1> No bids selected, retry </h1>");
            } /*if (((accBids != null)&&(accBids.size() == 0))&&((refBids!=null)&&(refBids.size()==0)))  {
             System.out.println("prova");
             out.println("<h1> No bids selected, retry </h1>");
             }*/ else if (accError == true || refError == true) {
                response.sendRedirect(request.getContextPath() + "/Home");
            } else if (conflictError == true) {
                out.println("<h1>Invitation with the accept/refuse conflict not done</h1>");
            } else {
                out.println("<h1> Operation on your invitation done correctly :)</h1>");
            }
            out.println("<form action = 'Home' method='get' >"); // tasto torna alla home
            out.println("<input type='submit' value = 'Back to home'/>");
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
        try {
            processRequest(request, response);

        } catch (SQLException ex) {
            Logger.getLogger(AcceptBidServlet.class
                    .getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AcceptBidServlet.class
                    .getName()).log(Level.SEVERE, null, ex);
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
