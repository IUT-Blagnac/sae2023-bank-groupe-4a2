package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class OperationsManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private OperationsManagementController omcViewController;
	private Client clientDuCompte;
	private CompteCourant compteConcerne;

	public OperationsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("operationsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des opérations");
			this.primaryStage.setResizable(false);

			this.omcViewController = loader.getController();
			this.omcViewController.initContext(this.primaryStage, this, _dbstate, client, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doOperationsManagementDialog() {
		this.omcViewController.displayDialog();
	}

	public Operation enregistrerDebit() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.DEBIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	
	public Operation enregistrerCredit() {
	    OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
	    Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.CREDIT);
	    if (op != null) {
	        try {
	            Access_BD_Operation ao = new Access_BD_Operation();
	            ao.insertCredit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);
	  
	        } catch (DatabaseConnexionException e) {
	            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
	            ed.doExceptionDialog();
	            this.primaryStage.close();
	            op = null;
	        } catch (ApplicationException ae) {
	            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
	            ed.doExceptionDialog();
	            op = null;
	        }
	    }
	    return op;
	}
	
	public Operation enregistrerDebitExecptionnel() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.CREDIT); //Operation nulle des que solde depassé
		if(op!=null){
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				//ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);
				ao.insertDebitExcep(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);
				

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				System.out.println("Exception= "+e.getMessage());
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				System.out.println("Exception= "+ ae.getMessage());
				ed.doExceptionDialog();
				op = null;
			}
		}


		return op;
	}
	
	public Operation enregistrerVirement(int compteDestinataireId) {
	    Operation debit = enregistrerDebit();
	    Operation credit = null;

	    if (debit != null) {
	        try {
	            Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
	            CompteCourant compteSource = acc.getCompteCourant(this.compteConcerne.idNumCompte);

	            if (compteSource != null) {
	                double montant = debit.montant;
	                credit = enregistrerCredit();
	                CompteCourant compteDestinataire = acc.getCompteCourant(compteDestinataireId); // Remplacez compteDestinataireId par l'ID du compte destinataire choisi

	                if (credit != null && compteDestinataire != null) {
	                    // Mise à jour du solde des comptes
	                    compteSource.solde -= montant;
	                    compteDestinataire.solde += montant;
	                    acc.updateCompteCourant(compteSource);
	                    acc.updateCompteCourant(compteDestinataire);
	                }
	            }
	        } catch (DatabaseConnexionException e) {
	            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
	            ed.doExceptionDialog();
	            this.primaryStage.close();
	            credit = null;
	        } catch (ApplicationException ae) {
	            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
	            ed.doExceptionDialog();
	            credit = null;
	        }
	    }
	    return credit;
	}



	public PairsOfValue<CompteCourant, ArrayList<Operation>> operationsEtSoldeDunCompte() {
		ArrayList<Operation> listeOP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des opérations du compte de l'utilisateur
			Access_BD_Operation ao = new Access_BD_Operation();
			listeOP = ao.getOperations(this.compteConcerne.idNumCompte);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeOP = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeOP = new ArrayList<>();
		}
		System.out.println(this.compteConcerne.solde);
		return new PairsOfValue<>(this.compteConcerne, listeOP);
	}
}
