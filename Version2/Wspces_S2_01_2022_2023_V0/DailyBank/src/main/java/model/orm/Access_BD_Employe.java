package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 * Classe d'accès aux Employe en BD Oracle.
 */
public class Access_BD_Employe {

	public Access_BD_Employe() {
	}

	/**
	 * Recherche d'un employé par son login / mot de passe.
	 *
	 * @param login    login de l'employé recherché
	 * @param password mot de passe donné
	 * @return un Employe ou null si non trouvé
	 * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
	 * @throws DataAccessException               Erreur d'accès aux données (requête
	 *                                           mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 */
	public Employe getEmploye(String login, String password)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {

		Employe employeTrouve;

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Employe WHERE" + " login = ?" + " AND motPasse = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, login);
			pst.setString(2, password);

			ResultSet rs = pst.executeQuery();

			System.err.println(query);

			if (rs.next()) {
				int idEmployeTrouve = rs.getInt("idEmploye");
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				String droitsAccess = rs.getString("droitsAccess");
				String loginTROUVE = rs.getString("login");
				String motPasseTROUVE = rs.getString("motPasse");
				int idAgEmploye = rs.getInt("idAg");

				employeTrouve = new Employe(idEmployeTrouve, nom, prenom, droitsAccess, loginTROUVE, motPasseTROUVE,
						idAgEmploye);
			} else {
				rs.close();
				pst.close();
				// Non trouvé
				return null;
			}

			if (rs.next()) {
				// Trouvé plus de 1 ... bizarre ...
				rs.close();
				pst.close();
				throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return employeTrouve;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Employe, Order.SELECT, "Erreur accès", e);
		}
	}
	
	/**
	 * Insertion d'un employé.
	 *
	 * @param employe IN/OUT Tous les attributs IN sauf idEmploye en OUT
	 * @throws RowNotFoundOrTooManyRowsException La requête insère 0 ou plus de 1 ligne
	 * @throws DataAccessException               Erreur d'accès aux données (requête mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 */
	public void insertEmploye(Employe employe)
	        throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
	    try {
	        Connection con = LogToDatabase.getConnexion();

	        String query = "INSERT INTO Employe VALUES (" + "seq_id_employe.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
	                + "?" + ", " + "?" + ", " + "?" + ", " + "?" + ")";
	        PreparedStatement pst = con.prepareStatement(query);
	        pst.setString(1, employe.nom);
	        pst.setString(2, employe.prenom);
	        pst.setString(3, employe.droitsAccess);
	        pst.setString(4, employe.login);
	        pst.setString(5, employe.motPasse);
	        pst.setInt(6, employe.idAg);

	        System.err.println(query);

	        int result = pst.executeUpdate();
	        pst.close();

	        if (result != 1) {
	            con.rollback();
	            throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.INSERT,
	                    "Insert anormal (insert de moins ou plus d'une ligne)", null, result);
	        }

	        query = "SELECT seq_id_employe.CURRVAL from DUAL";

	        System.err.println(query);
	        PreparedStatement pst2 = con.prepareStatement(query);

	        ResultSet rs = pst2.executeQuery();
	        rs.next();
	        int idEmploye = rs.getInt(1);

	        con.commit();
	        rs.close();
	        pst2.close();

	        employe.idEmploye = idEmploye;
	    } catch (SQLException e) {
	        throw new DataAccessException(Table.Employe, Order.INSERT, "Erreur accès", e);
	    }
	}
	
	public void deleteEmploye(Employe employe) throws DatabaseConnexionException {
	    try (Connection con = LogToDatabase.getConnexion();
	         PreparedStatement pst = con.prepareStatement("DELETE FROM Employe WHERE id = ?")) {
	        pst.setInt(1, employe.idEmploye);
	        pst.executeUpdate();
	    } catch (SQLException e) {
	       
	    }
	}


	/**
	 * Mise à jour d'un employé.
	 *
	 * employe.idEmploye est la clé primaire et doit exister, tous les autres champs sont des mises à jour.
	 *
	 * @param employe IN employe.idEmploye (clé primaire) doit exister
	 * @throws RowNotFoundOrTooManyRowsException La requête modifie 0 ou plus de 1 ligne
	 * @throws DataAccessException               Erreur d'accès aux données (requête mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 */
	public void updateEmploye(Employe employe)
	        throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
	    try {
	        Connection con = LogToDatabase.getConnexion();

	        String query = "UPDATE Employe SET " + "nom = " + "? , " + "prenom = " + "? , " + "droitsAccess = "
	                + "? , " + "login = " + "? , " + "motPasse = " + "? " + " "
	                + "WHERE idEmploye = ? ";

	        PreparedStatement pst = con.prepareStatement(query);
	        pst.setString(1, employe.nom);
	        pst.setString(2, employe.prenom);
	        pst.setString(3, employe.droitsAccess);
	        pst.setString(4, employe.login);
	        pst.setString(5, employe.motPasse);
	        pst.setInt(6, employe.idEmploye);

	        System.err.println(query);

	        int result = pst.executeUpdate();
	        pst.close();
	        if (result != 1) {
	            con.rollback();
	            throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.UPDATE,
	                    "Update anormal (update de moins ou plus d'une ligne)", null, result);
	        }
	        con.commit();
	    } catch (SQLException e) {
	        throw new DataAccessException(Table.Employe, Order.UPDATE, "Erreur accès", e);
	    }
	}
}
