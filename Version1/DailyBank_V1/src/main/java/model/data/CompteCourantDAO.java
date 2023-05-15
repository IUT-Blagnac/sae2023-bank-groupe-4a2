package model.data;

import model.orm.LogToDatabase;
import model.orm.exception.DatabaseConnexionException;

import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.SQLException;

public class CompteCourantDAO {
    /*
    * NB : J'ai utilisé le nom de classe "CompteCourantDAO" pour refléter le fait que cette classe agit comme un objet d'accès aux données
    * (Data Access Object) pour les comptes courants. Le suffixe "DAO" est couramment utilisé pour les classes qui encapsulent la logique
    *  d'accès et de manipulation des données.
    * Dans ce cas, la classe CompteCourantDAO fournit des méthodes pour effectuer des opérations de crédit et de débit sur les comptes
    * courants en interagissant avec une base de données. Elle isole la logique d'accès aux données des autres parties de l'application,
    * ce qui permet une meilleure séparation des préoccupations et une organisation plus claire du code
    *
    *
    * Notion de ORM : Une ORM (Object-Relational Mapping) est une technique qui permet de mapper les objets d'une application à des tables
    * d'une base de données relationnelle. Elle facilite l'interaction entre l'application et la base de données en fournissant une abstraction
    * entre les représentations objets et relationnelles des données
    *
    *
    * */
    private Connection connection;

    public CompteCourantDAO() throws DatabaseConnexionException {
        //connection = logToBd();
        try{
            connection= LogToDatabase.getConnexion(); // Injection de la dépendance connection pour avoir accès à la BD
        }catch (Exception ex){
            System.out.println("Erreur de connection à la base de données");
            ex.printStackTrace();
        }

    }

    public void crediterCompte(CompteCourant compte, double montant) {

        /*
                Explication de cette section : Une requête SQL paramétrée est préparée pour mettre à jour le solde du compte.
                 Le montant à créditer est défini comme paramètre à l'aide de setDouble(1, montant) et l'identifiant du
                 compte est défini comme paramètre à l'aide de setString(2, compte.getIdNumCompte()). La méthode executeUpdate()
                 est utilisée pour exécuter la requête et mettre à jour les données dans la base de données. Ensuite, une mise
                 à jour locale du compte est effectuée en ajoutant le montant crédité.
      * */
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

            System.out.println("Le compte a été crédité avec succès  et votre nouveau solde est !"+compte.solde);
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
            /*
                Explication de cette section : Une requête SQL paramétrée est préparée pour mettre à jour le solde du compte.
                 Le montant à créditer est défini comme paramètre à l'aide de setDouble(1, montant) et l'identifiant du
                 compte est défini comme paramètre à l'aide de setString(2, compte.getIdNumCompte()). La méthode executeUpdate()
                 est utilisée pour exécuter la requête et mettre à jour les données dans la base de données. Ensuite, une mise
                 à jour locale du compte est effectuée en retranchant le montant débiter.
      * */

            String query = "UPDATE COMPTECOURANT SET solde = solde - ? WHERE idNumCompte = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDouble(1, montant);
            statement.setInt(2,compte.idNumCompte);
            statement.executeUpdate();
            connection.setAutoCommit(true);
            compte.solde-=montant;

            System.out.println("Le compte a été débité avec succès et votre nouveau solde est ! "+compte.solde);
        } catch (SQLException e) {
            System.out.println("Une erreur s'est produite lors du débit du compte : " + e.getMessage());
        }
    }

}
