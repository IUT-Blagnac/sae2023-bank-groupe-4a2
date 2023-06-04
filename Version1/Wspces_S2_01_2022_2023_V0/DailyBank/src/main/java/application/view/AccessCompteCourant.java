package application.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.data.CompteCourant;
import model.orm.LogToDatabase;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class AccessCompteCourant {

    public void cloturerCompteCourant(CompteCourant cc)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {

			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE CompteCourant SET solde = 0, estCloture = 'O' " + "WHERE idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, cc.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur accès", e);
		}
	}
    

}
