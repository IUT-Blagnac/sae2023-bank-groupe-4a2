package model.data;

import model.orm.LogToDatabase;
import model.orm.exception.DatabaseConnexionException;

import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.SQLException;

public class CompteCourantDAO {

    private Connection connection;

    public CompteCourantDAO() throws DatabaseConnexionException {
 
        try{
            connection= LogToDatabase.getConnexion(); // Injection de la dépendance connection pour avoir accès à la BD
        }catch (Exception ex){
            System.out.println("Erreur de connection à la base de données");
            ex.printStackTrace();
        }

    }

    public void crediterCompte(CompteCourant compte, double montant) {

        try {
            //Mise à jour sur le serveur
            String query = "UPDATE COMPTECOURANT SET solde = solde + ? WHERE idNumCompte = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDouble(1, montant);
            statement.setInt(2, compte.idNumCompte);
            statement.executeUpdate();
            connection.setAutoCommit(true);

            // Mettre à jour le solde local du compte
            compte.solde+=montant;

            System.out.println("Le compte a été crédité avec succès  et votre nouveau solde est : "+compte.solde);
        } catch (SQLException ex) {
            System.out.println("Une erreur s'est produite lors de la créditation du compte : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void debiterCompte(CompteCourant compte, double montant) {
        if(montant > compte.solde - compte.debitAutorise) {
            System.out.println("Le montant demandé dépasse le solde disponible et le découvert autorisé.");
            return;
        }

        try {

            String query = "UPDATE COMPTECOURANT SET solde = solde - ? WHERE idNumCompte = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDouble(1, montant);
            statement.setInt(2,compte.idNumCompte);
            statement.executeUpdate();
            connection.setAutoCommit(true);
            compte.solde-=montant;

            System.out.println("Le compte a été débité avec succès et votre nouveau solde est : "+compte.solde);
        } catch (SQLException e) {
            System.out.println("Une erreur s'est produite lors du débit du compte : " + e.getMessage());
        }
    }

}
