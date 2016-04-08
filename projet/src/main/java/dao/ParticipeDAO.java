/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static dao.AbstractDAO.CloseStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import modele.Aventure;
import modele.Participe;
import modele.Personnage;

/**
 *
 * @author faellan
 */
public class ParticipeDAO extends AbstractParticipeDAO {

    private static ParticipeDAO instance;
        
    private ParticipeDAO(DataSource ds) {
        super(ds);
    }
    
    public static ParticipeDAO Create(DataSource ds) {
        if(instance == null)
            instance = new ParticipeDAO(ds);
        
        return instance;
    }
    
    public static ParticipeDAO Get() {
        return instance;
    }

    
    @Override
    public void creerParticipe(Participe p) throws DAOException {
    
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = initConnection();
            
            statement = link.prepareStatement("INSERT INTO Participe "
                    + "(aventure_id, personnage_id) "
                    + " VALUES (?, ?)");

            statement.setInt(1, p.getAventure().getId());
            statement.setInt(2, p.getPersonnage().getId());
            statement.executeUpdate();
            
            link.commit();

        } catch (Exception e) {
            rollback();
            throw new DAOException("Erreur à l'ajout d'un participant : "
                    + e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }

    @Override
    public void supprimerParticipe(Aventure aventure, Personnage perso) throws DAOException {
        Connection link = null;
        PreparedStatement statement = null;

        try {
            link = initConnection();
            
            statement = link.prepareStatement("DELETE FROM Participe p"
                    + " WHERE p.aventure_id=? AND p.personnage_id=?");

            statement.setInt(1, aventure.getId());
            statement.setInt(2, perso.getId());
            statement.executeUpdate();
            
            link.commit();

        } catch (Exception e) {
            rollback();
            throw new DAOException("Erreur à la suppression d'un participant : "
                    + e.getMessage(), e);

        } finally {
            CloseStatement(statement);
            closeConnection(link);
        }
    }
    
}