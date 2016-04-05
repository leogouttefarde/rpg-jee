package controleur;

import dao.EpisodeDAO;
import dao.ParagrapheDAO;
import java.io.*;
import java.rmi.ServerException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import modele.*;

/**
 * Contrôleur de paragraphes.
 */
@WebServlet(name = "ParagrapheCtrl", urlPatterns = {"/paragraphe"})
public class ParagrapheCtrl extends HttpServlet {

    /**
     * Requetes GET
     *
     * @param request
     * @param response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        String action = (request.getParameter("action") != null ? request.getParameter("action") : "");
        if (action.equals("edit")) {
            int paragid = Integer.parseInt(request.getParameter("ID"));

            //requete DAO
            try {
                ParagrapheDAO pad = ParagrapheDAO.Get();
                Paragraphe p = pad.getParagraphe(paragid);

                request.setAttribute("parag", p);
                request.getRequestDispatcher("/WEB-INF/Paragraphe/Editparagraphes.jsp").forward(request, response);
            } catch (Exception e) {
                throw new ServerException(null, e);
            }
        } else if (action.equals("new")) {
            int eid = Integer.parseInt(request.getParameter("eID"));
            request.setAttribute("eID", eid);
            request.getRequestDispatcher("/WEB-INF/Paragraphe/NewParagraphe.jsp").forward(request, response);
        }
    }

    /**
     * Requetes POST
     *
     * @param request
     * @param response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        String action = (request.getParameter("action") != null ? request.getParameter("action") : "");
        if (action.equals("reveler")) {
            if (request.getParameter("res").equals("oui")) {
                int pid = Integer.parseInt(request.getParameter("pID"));
                ParagrapheDAO pad = ParagrapheDAO.Get();
                try {
                    pad.reveleParagraphe(pid);
                    response.sendRedirect("character?action=ownedList");
                } catch (Exception e) {

                    throw new ServerException(null, e);
                }
            }
        } else if (action.equals("new")) {
            boolean secret = request.getParameter("secret") !=null ;
            String texte = request.getParameter("texte");
            int episode = Integer.parseInt(request.getParameter("episodeID"));
            ParagrapheDAO pad = ParagrapheDAO.Get();
            try {
                pad.ajouteParagraphe(secret, texte, episode);
                response.sendRedirect("character?action=ownedList");
            } catch (Exception e) {

                throw new ServerException( null, e);
            }
        } else if(action.equals("edit")){
            String texte = request.getParameter("texte");
            int pid = Integer.parseInt(request.getParameter("id"));
            ParagrapheDAO pad = ParagrapheDAO.Get();
            try {
                pad.updateParagraphe(pid, texte);
                response.sendRedirect("character?action=ownedList");
            } catch (Exception e) {

                throw new ServerException( null, e);
            }
        }
    }
}
