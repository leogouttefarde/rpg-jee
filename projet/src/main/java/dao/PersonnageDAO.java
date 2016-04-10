/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import modele.Aventure;
import modele.Biographie;
import modele.Joueur;
import modele.Personnage;
import modele.Univers;

/**
 *
 * @author Jules-Eugène Demets, Léo Gouttefarde, Salim Aboubacar, Simon Rey
 */
public final class PersonnageDAO extends AbstractPersonnageDAO {
    
    private static PersonnageDAO instance;
    
    private PersonnageDAO(DataSource ds) {
        super(ds);
    }
    
    public static PersonnageDAO Create(DataSource ds) {
        if (instance == null) {
            instance = new PersonnageDAO(ds);
        }

        return instance;
    }
    
    public static PersonnageDAO Get() {
        return instance;
    }


    public Collection<Personnage> getAllPersonnages() throws DAOException {
        List<Personnage> result = new ArrayList<>();
        Connection link = null;
        PreparedStatement ps = null;
        
        try {
            link = getConnection();
            ps = link.prepareStatement("SELECT * FROM Personnage ORDER BY nom");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Personnage personnage = new Personnage(rs.getString("nom"),
                        rs.getString("naissance"), rs.getString("profession"),
                        rs.getString("portrait"),
                        new Univers(rs.getInt("univers_id")));
                
                result.add(personnage);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur d'accès aux personnages " + e.getMessage(), e);
            
        } finally {
            CloseStatement(ps);
            closeConnection(link);
        }
        
        return result;
    }

    @Override
    public ArrayList<Personnage> getPersonnagesJoueur(Joueur j) throws DAOException {
        ArrayList<Personnage> persos = new ArrayList<>();
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = getConnection();
            statement = link.prepareStatement("SELECT id, nom "
                    + "FROM Personnage where joueur_id = ? ORDER BY nom");
            
            statement.setInt(1, j.getId());
            ResultSet res = statement.executeQuery();
            Personnage perso;

            while (res.next()) {
                perso = new Personnage();
                perso.setId(res.getInt("id"));
                perso.setNom(res.getString("nom"));
                
                persos.add(perso);
            }

        } catch (Exception e) {
            throw new DAOException("Erreur d'accès à la liste des personnages "
                    + "possédés : " + e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }

        return persos;
    }

    @Override
    public Collection<Personnage> getPersonnagesAValider(Joueur j) throws DAOException {
        ArrayList<Personnage> persos = new ArrayList<>();
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = getConnection();
            statement = link.prepareStatement("SELECT id, nom FROM Personnage "
                    + "where valide = 0 and validateur_id = ? ORDER BY nom");
            
            statement.setInt(1, j.getId());
            ResultSet res = statement.executeQuery();
            Personnage perso;

            while (res.next()) {
                perso = new Personnage();
                perso.setId(res.getInt("id"));
                perso.setNom(res.getString("nom"));
                
                persos.add(perso);
            }

        } catch (Exception e) {
            throw new DAOException("Erreur d'accès à la liste des personnages "
                    + "à valider : " + e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }

        return persos;
    }

    @Override
    public Collection<Personnage> getTransfertsAValider(Joueur j) throws DAOException {
        ArrayList<Personnage> persos = new ArrayList<>();
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = getConnection();
            statement = link.prepareStatement("SELECT id, nom FROM Personnage "
                    + "where transfert_id = ? and mj_id != ? and valide = 1 "
                    + "ORDER BY nom");
            
            statement.setInt(1, j.getId());
            statement.setInt(2, j.getId());
            ResultSet res = statement.executeQuery();
            Personnage perso;

            while (res.next()) {
                perso = new Personnage();
                perso.setId(res.getInt("id"));
                perso.setNom(res.getString("nom"));
                
                persos.add(perso);
            }

        } catch (Exception e) {
            throw new DAOException("Erreur d'accès à la liste des transferts "
                    + "à valider : " + e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }

        return persos;
    }

    @Override
    public Personnage getPersonnageAssocie(Joueur j, Aventure a) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Personnage> getPersonnagesMenes(Joueur mj) throws DAOException {
        ArrayList<Personnage> persos = new ArrayList<>();
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = getConnection();
            statement = link.prepareStatement("SELECT id, nom FROM Personnage "
                    + "where mj_id = ? and valide = 1 ORDER BY nom");
            
            statement.setInt(1, mj.getId());
            ResultSet res = statement.executeQuery();
            Personnage perso;

            while (res.next()) {
                perso = new Personnage();
                perso.setId(res.getInt("id"));
                perso.setNom(res.getString("nom"));
                
                persos.add(perso);
            }

        } catch (Exception e) {
            throw new DAOException("Erreur d'accès à la liste des personnages "
                    + "menés : " + e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }

        return persos;
    }

    /**
     * Renvoie la liste des personnages menés
     * qui ne participent à aucune partie en cours.
     * 
     * @param mj Le MJ concerné
     * @return La liste des candidats potentiels
     * @throws DAOException 
     */
    public ArrayList<Personnage> getCandidats(Joueur mj) throws DAOException {
        ArrayList<Personnage> persos = new ArrayList<>();
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = getConnection();
            statement = link.prepareStatement("SELECT id, nom FROM Personnage p "
                    + "LEFT JOIN Participe r on r.personnage_id = p.id "
                    + "WHERE mj_id = ? and valide = 1 "
                    + "and NOT EXISTS (SELECT 1 FROM Aventure a "
                    + "WHERE r.aventure_id = a.id and finie = 0) "
                    + "ORDER BY nom ");
            
            statement.setInt(1, mj.getId());
            ResultSet res = statement.executeQuery();
            Personnage perso;

            while (res.next()) {
                perso = new Personnage();
                perso.setId(res.getInt("id"));
                perso.setNom(res.getString("nom"));
                
                persos.add(perso);
            }

        } catch (Exception e) {
            throw new DAOException("Erreur d'accès à la liste des candidats "
                    + e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }

        return persos;
    }

    @Override
    public void creer(Personnage p, String bio) throws DAOException {
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = initConnection();
            
            statement = link.prepareStatement("INSERT INTO Biographie (texte) VALUES (?)");
            statement.setString(1, bio);
            statement.executeUpdate();
            
            statement = link.prepareStatement("INSERT INTO Personnage "
                    + "(naissance, nom, portrait, profession, joueur_id, "
                    + "univers_id, biographie_id) VALUES (?, ?, ?, ?, ?, ?, "
                    + "bio_seq.currval)");

            statement.setString(1, p.getNaissance());
            statement.setString(2, p.getNom());
            statement.setString(3, p.getPortrait());
            statement.setString(4, p.getProfession());
            statement.setInt(5, p.getJoueur().getId());
            statement.setInt(6, p.getUnivers().getId());
            statement.executeUpdate();
            
            link.commit();

        } catch (Exception e) {
            rollback();
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }

    @Override
    public Personnage getPersonnage(int id) throws DAOException {
        Personnage perso = null;
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = getConnection();
            statement = link.prepareStatement("SELECT p.id, p.nom, naissance, "
                    + "profession, portrait, valide, biographie_id, mj_id, "
                    + "transfert_id, validateur_id, joueur_id, u.id as u_id, "
                    + "u.nom as u_nom, j.pseudo as meneur FROM Personnage p "
                    + "JOIN Univers u on p.univers_id = u.id "
                    + "LEFT JOIN Joueur j on j.id = mj_id WHERE p.id = ?");
            
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new Exception("Aucun personnage d'identifiant " + id);

            perso = new Personnage();
            perso.setId(rs.getInt("id"));
            perso.setNom(rs.getString("nom"));
            perso.setNaissance(rs.getString("naissance"));
            perso.setProfession(rs.getString("profession"));
            perso.setPortrait(rs.getString("portrait"));
            perso.setValide(rs.getBoolean("valide"));
            perso.setBiographie(new Biographie(rs.getInt("biographie_id")));
            perso.setMj(new Joueur(rs.getInt("mj_id"), rs.getString("meneur")));
            perso.setJoueur(new Joueur(rs.getInt("joueur_id")));
            perso.setTransfert(new Joueur(rs.getInt("transfert_id")));
            perso.setValidateur(new Joueur(rs.getInt("validateur_id")));

            Univers univers = new Univers(rs.getInt("u_id"),
                                          rs.getString("u_nom"));
            perso.setUnivers(univers);

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }

        return perso;
    }

    @Override
    public void requestValidation(int idPerso, int idMJ, int idUser) throws DAOException {
        Connection link = null;
        PreparedStatement statement = null;

        try {
            // Pas le droit de valider soi-même ses propres personnages
            if (idMJ == idUser)
                throw new DAOException("Accès refusé");

            
            link = initConnection();

            // On vérifie que l'utilisateur a le droit de demander validation
            statement = link.prepareStatement("SELECT 1 FROM Joueur j join Personnage p "
                    + "on p.joueur_id = j.id where p.id = ? and j.id = ? and p.valide = 0");

            statement.setInt(1, idPerso);
            statement.setInt(2, idUser);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new DAOException("Accès refusé");


            statement.close();
            statement = link.prepareStatement("UPDATE Personnage "
                    + "SET validateur_id = ? WHERE id = ?");

            statement.setInt(1, idMJ);
            statement.setInt(2, idPerso);
            statement.executeUpdate();

            link.commit();

        } catch (DAOException e) {
            throw e;

        } catch (SQLException e) {
            rollback();
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }

    @Override
    public void requestTransfer(int idPerso, int idMJ, int idUser) throws DAOException {
        Connection link = null;
        PreparedStatement statement = null;

        try {
            // Pas le droit de se transférer à soi-même ses propres personnages
            if (idMJ == idUser)
                throw new DAOException("Accès refusé");


            link = initConnection();

            // On vérifie que l'utilisateur a le droit de demander un transfert :
            // - le perso ne doit pas participer à une partie en cours
            // - doit être propriétaire
            statement = link.prepareStatement("SELECT 1 FROM Joueur j "
                    + "JOIN Personnage p on p.joueur_id = j.id "
                    + "WHERE p.id = ? and j.id = ? and p.valide = 1 "
                    + "AND NOT EXISTS (SELECT 1 FROM Participe r JOIN "
                    + "Aventure a on a.id = r.aventure_id "
                    + "WHERE p.id = r.personnage_id and finie = 0)");

            statement.setInt(1, idPerso);
            statement.setInt(2, idUser);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new DAOException("Accès refusé");

            statement.close();
            statement = link.prepareStatement("UPDATE Personnage "
                    + "SET transfert_id = ? WHERE id = ?");

            statement.setInt(1, idMJ);
            statement.setInt(2, idPerso);
            statement.executeUpdate();

            link.commit();

        } catch (DAOException e) {
            throw e;

        } catch (SQLException e) {
            rollback();
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }

    @Override
    public void acceptValidation(int idPerso, int idUser) throws DAOException {
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = initConnection();

            // On vérifie que l'utilisateur a le droit de valider
            statement = link.prepareStatement("SELECT 1 FROM Joueur j join "
                    + "Aventure a on j.id = a.mj_id join Personnage p "
                    + "on p.validateur_id = j.id where p.id = ? and j.id = ?");

            statement.setInt(1, idPerso);
            statement.setInt(2, idUser);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new DAOException("Accès refusé");

            statement.close();
            statement = link.prepareStatement("UPDATE Personnage "
                    + "SET valide = 1, mj_id = validateur_id WHERE id = ?");

            statement.setInt(1, idPerso);
            statement.executeUpdate();

            link.commit();

        } catch (DAOException e) {
            throw e;

        } catch (SQLException e) {
            rollback();
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }

    @Override
    public void acceptTransfer(int idPerso, int idUser) throws DAOException {
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = initConnection();

            // On vérifie que l'utilisateur a le droit de valider le transfert :
            // - le perso ne doit pas participer à une partie en cours
            // - doit être le MJ concerné
            statement = link.prepareStatement("SELECT 1 FROM Joueur j join "
                    + "Aventure a on j.id = a.mj_id join Personnage p "
                    + "on p.transfert_id = j.id where p.id = ? and j.id = ? "
                    + "AND NOT EXISTS (SELECT 1 FROM Participe r "
                    + "WHERE a.id = r.aventure_id and p.id = r.personnage_id "
                    + "and finie = 0)");

            statement.setInt(1, idPerso);
            statement.setInt(2, idUser);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new DAOException("Accès refusé");

            statement.close();
            statement = link.prepareStatement("UPDATE Personnage "
                    + "SET mj_id = transfert_id, transfert_id = NULL WHERE id = ?");

            statement.setInt(1, idPerso);
            statement.executeUpdate();

            link.commit();

        } catch (DAOException e) {
            throw e;

        } catch (SQLException e) {
            rollback();
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }

    @Override
    public void modifierPersonnage(Personnage p, int idUser) throws DAOException {
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = initConnection();

            // On vérifie que l'utilisateur est bien propriétaire du personnage
            statement = link.prepareStatement("SELECT 1 FROM Joueur j "
                    + "JOIN Personnage p on p.joueur_id = j.id "
                    + "WHERE p.id = ? and j.id = ?");

            statement.setInt(1, p.getId());
            statement.setInt(2, idUser);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new DAOException("Accès refusé");

            statement.close();
            statement = link.prepareStatement("UPDATE Personnage "
                    + "SET profession = ? WHERE id = ?");

            statement.setString(1, p.getProfession());
            statement.setInt(2, p.getId());
            statement.executeUpdate();

            link.commit();

        } catch (DAOException e) {
            throw e;

        } catch (SQLException e) {
            rollback();
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }

    @Override
    public void donnerPersonnage(int idPerso, int idDest, int idUser) throws DAOException {
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = initConnection();

            // On vérifie que l'utilisateur est bien propriétaire du personnage
            statement = link.prepareStatement("SELECT 1 FROM Joueur j "
                    + "JOIN Personnage p on p.joueur_id = j.id "
                    + "WHERE p.id = ? and j.id = ?");

            statement.setInt(1, idPerso);
            statement.setInt(2, idUser);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new DAOException("Accès refusé");

            statement.close();
            statement = link.prepareStatement("UPDATE Personnage "
                    + "SET joueur_id = ? WHERE id = ?");

            statement.setInt(1, idDest);
            statement.setInt(2, idPerso);
            statement.executeUpdate();

            link.commit();

        } catch (DAOException e) {
            throw e;

        } catch (SQLException e) {
            rollback();
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }

    public boolean dansPartieEnCours(int idPerso) throws DAOException {
        boolean result = true;
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = getConnection();
            statement = link.prepareStatement("SELECT 1 FROM Personnage p "
                    + "JOIN Participe r on p.id = r.personnage_id JOIN "
                    + "Aventure a on a.id = r.aventure_id WHERE finie = 0");

            ResultSet rs = statement.executeQuery();
            result = rs.next();

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }

        return result;
    }
}
