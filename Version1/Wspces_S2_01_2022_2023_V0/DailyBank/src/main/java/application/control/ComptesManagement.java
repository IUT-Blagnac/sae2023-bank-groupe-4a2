package application.control;

import java.util.ArrayList;



import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;


	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.cmcViewController = loader.getController();
			this.cmcViewController.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void modifierCompte(CompteCourant compte) {
	    CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
	    CompteCourant result = cep.doCompteEditorDialog(this.clientDesComptes, compte, EditionMode.MODIFICATION);
	    if (result != null) {
	        try {
	            Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
	            ac.updateCompteCourant(result);
	        } catch (DatabaseConnexionException e) {
	            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
	            ed.doExceptionDialog();
	            result = null;
	            this.primaryStage.close();
	        } catch (ApplicationException ae) {
	            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
	            ed.doExceptionDialog();
	            result = null;
	        }
	    }
	}

	

	public void doComptesManagementDialog() {
		this.cmcViewController.displayDialog();
	}

	public void gererOperationsDUnCompte(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dailyBankState,
				this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	public CompteCourant creerNouveauCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		if (compte != null) {
			try {
				if (Math.random() < -1) {
					throw new ApplicationException(Table.CompteCourant, Order.INSERT, "todo : test exceptions", null);
				}
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
		return compte;
	}

	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}
	


	public void supprimerCompte(CompteCourant cpt) {
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		CompteCourant result = cep.doCompteEditorDialog(this.clientDesComptes, cpt, EditionMode.SUPPRESSION);
		if (result != null) {
			try {
				Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
				cpt.setCloture("0");
				ac.deleteCompteCourant(cpt);
			} catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                result = null;
                this.primaryStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                result = null;
            }
		}
		
	}
}
