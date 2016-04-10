package controleur;

import dao.AventureDAO;
import dao.DAOException;
import dao.EpisodeDAO;
import dao.ParagrapheDAO;
import java.io.*;
import java.rmi.ServerException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import modele.*;

/**
 * Contrôleur d'episodes.
 */
@WebServlet(name = "EpisodeCtrl", urlPatterns = {"/episode"})
public class EpisodeCtrl extends HttpServlet {

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

        String action = request.getParameter("action");

        // Force le login et gère les erreurs
        if (Main.notLogged(request, response)
                || action == null) {
            return;
        }

        switch (action) {
            case "edit": {
                int epID = Integer.parseInt(request.getParameter("id"));
                int persoID = Integer.parseInt(request.getParameter("persoID"));
                Main.ownerOrMj(persoID, Main.GetJoueurSession(request));
                EpisodeDAO ed = EpisodeDAO.Get();
                //requete DAO
                EpisodeDAO edd = EpisodeDAO.Get();
                ParagrapheDAO pad = ParagrapheDAO.Get();
                try {
                    Episode e = edd.getEpisode(epID);
                    e.paragraphes = pad.getParagraphes(e);
                    request.setAttribute("episode", e);
                    request.setAttribute("persoID", persoID);
                    request.getRequestDispatcher("/WEB-INF/episode/EditionEpisode.jsp").forward(request, response);
                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }
                break;
            }
            case "suppr": {
                int epID = Integer.parseInt(request.getParameter("id"));
                EpisodeDAO ed = EpisodeDAO.Get();
                int persoID = Integer.parseInt(request.getParameter("persoID"));
                Main.ownerOrMj(persoID, Main.GetJoueurSession(request));
                //requete DAO
                EpisodeDAO edd = EpisodeDAO.Get();
                ParagrapheDAO pad = ParagrapheDAO.Get();
                try {
                    Episode e = edd.getEpisode(epID);
                    e.paragraphes = pad.getParagraphes(e);
                    request.setAttribute("episode", e);
                    request.setAttribute("persoID", persoID);
                    request.getRequestDispatcher("/WEB-INF/episode/Supprimer.jsp").forward(request, response);
                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }
                break;
            }
            case "valider": {
                int epID = Integer.parseInt(request.getParameter("id"));
                int persoID = Integer.parseInt(request.getParameter("persoID"));
                Main.ownerOrMj(persoID, Main.GetJoueurSession(request));
                //requete DAO
                EpisodeDAO ed = EpisodeDAO.Get();
                ParagrapheDAO pad = ParagrapheDAO.Get();
                try {
                    Episode e = ed.getEpisode(epID);
                    e.paragraphes = pad.getParagraphes(e);
                    request.setAttribute("episode", e);
                    request.setAttribute("persoID", persoID);
                    request.getRequestDispatcher("/WEB-INF/episode/Valider.jsp").forward(request, response);
                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }
                break;
            }

            case "validationList": {
                EpisodeDAO ed = EpisodeDAO.Get();
                ParagrapheDAO pad = ParagrapheDAO.Get();

                try {
                    List<Episode> epi = ed.getEpisodesAValider(Main.GetJoueurSession(request));

                    for (Episode e : epi) {
                        e.paragraphes = pad.getParagraphes(e);
                    }

                    request.setAttribute("episodes", epi);
                    request.getRequestDispatcher("/WEB-INF/episode/AValider.jsp").forward(request, response);

                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }

                break;
            }

            case "new":
                int bioID = Integer.parseInt(request.getParameter("bioID"));
                int pid = Integer.parseInt(request.getParameter("pid"));
                Main.ownerOrMj(pid, Main.GetJoueurSession(request));
                //DAO : liste des aventures
                AventureDAO ad = AventureDAO.Get();
                try {
                    List<Aventure> l = ad.getAventureAssociee(pid);
                    request.setAttribute("aventures", l);
                    request.setAttribute("bioID", bioID);
                    request.setAttribute("persoID", pid);
                    request.getRequestDispatcher("/WEB-INF/episode/NouvelEpisode.jsp").forward(request, response);
                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }
                break;
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

        String action = request.getParameter("action");

        // Force le login et gère les erreurs
        if (Main.notLogged(request, response)
                || action == null) {
            return;
        }

        switch (action) {
        case "validesuppr": {
            try {
                int persoID = Integer.parseInt(request.getParameter("persoID"));

                if (request.getParameter("res").equals("oui")) {
                    int pid = Integer.parseInt(request.getParameter("pID"));
                    Main.ownerOrMj(persoID, Main.GetJoueurSession(request));
                    EpisodeDAO ed = EpisodeDAO.Get();
                        ed.suppressEpisode(pid);
                        response.sendRedirect("biographie?action=afficher&id=" + persoID);

                } else {
                    response.sendRedirect("biographie?action=afficher&id=" + persoID);
                }

            } catch (NumberFormatException | DAOException | IOException e) {
                Main.dbError(request, response, e);
            }
            break;
        }

        case "validevalid": {
            int persoID = Integer.parseInt(request.getParameter("persoID"));
            Main.ownerOrMj(persoID, Main.GetJoueurSession(request));
            if (request.getParameter("res").equals("oui")) {
                int eid = Integer.parseInt(request.getParameter("pID"));

                int joueurID = Main.GetJoueurSession(request).getId();
                EpisodeDAO ed = EpisodeDAO.Get();

                try {
                    ed.valideEpisode(eid, persoID, joueurID);
                    response.sendRedirect("biographie?action=afficher&id=" + persoID);

                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }

            } else {
                response.sendRedirect("biographie?action=afficher&id=" + persoID);
            }
            break;
        }
        
        case "new": {
            int persoID = Integer.parseInt(request.getParameter("persoID"));
            Main.ownerOrMj(persoID, Main.GetJoueurSession(request));
            String avt = request.getParameter("aventure");
            if (avt.equals("__NONE__")) {
                EpisodeDAO ed = EpisodeDAO.Get();
                try {
                    int idbio = Integer.parseInt(request.getParameter("IDbio"));
                    int date = Integer.parseInt(request.getParameter("date"));
                    ed.nouvelEpisode(false, 0, idbio, date);
                    response.sendRedirect("biographie?action=afficher&id=" + persoID);
                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }

            } else {
                int avent = Integer.parseInt(avt);
                int idbio = Integer.parseInt(request.getParameter("IDbio"));
                int date = Integer.parseInt(request.getParameter("date"));
                EpisodeDAO ed = EpisodeDAO.Get();
                try {
                    ed.nouvelEpisode(true, avent, idbio, date);
                    response.sendRedirect("biographie?action=afficher&id=" + persoID);
                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }
            }
            break;
        }
        
        case "validerParMJ":
            int epID = Integer.parseInt(request.getParameter("eID"));
            //requete DAO
            EpisodeDAO ed = EpisodeDAO.Get();
            if (request.getParameter("res").equals("oui")) {

                try {
                    ed.valideEpisodeParMj(epID);

                } catch (DAOException e) {
                    Main.dbError(request, response, e);
                }
            }
            response.sendRedirect("episode?action=validationList");
            break;
        }
    }
}
