package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeManagamentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeManagement {

    private Stage primaryStage;
    private DailyBankState dailyBankState;
    private EmployeManagamentController emcViewController;

    public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(EmployeManagamentController.class.getResource("employemanagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des employés");
            this.primaryStage.setResizable(false);

            this.emcViewController = loader.getController();
            this.emcViewController.initContext(this.primaryStage, this, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doEmployeManagementDialog() {
        this.emcViewController.displayDialog();
    }

    public Employe modifierEmploye(Employe emp) {
        EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
        Employe result = eep.doEmployeEditorDialog(emp, EditionMode.MODIFICATION);
        if (result != null) {
            try {
            	
                Access_BD_Employe ae = new Access_BD_Employe();
                ae.updateEmploye(result);
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
        return result;
    }

    public Employe creerEmploye() {
        Employe employe;
        EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
        employe = eep.doEmployeEditorDialog(null, EditionMode.CREATION);
        employe.idAg=this.dailyBankState.getAgenceActuelle().idAg;
        if (employe != null) {
            try {
                Access_BD_Employe ae = new Access_BD_Employe();
                ae.insertEmploye(employe);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                employe = null;
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                employe = null;
            }
        }
        return employe;
    }
    
    public void deleteEmploye(Employe employe) {
        try {
            Access_BD_Employe accessBD = new Access_BD_Employe();
            accessBD.deleteEmploye(employe);
            
            // Afficher un message de confirmation de suppression
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Suppression réussie");
            alert.setHeaderText(null);
            alert.setContentText("L'employé a été supprimé avec succès.");
            alert.showAndWait();
            
        } catch (DatabaseConnexionException e) {
            // Gérer les exceptions de connexion à la base de données
            // Afficher un message d'erreur approprié
            e.printStackTrace();
        } catch (ApplicationException e) {
            // Gérer les exceptions d'application
            // Afficher un message d'erreur approprié
            e.printStackTrace();
        }
    }


    public ArrayList<Employe> getListeEmployes(int _employeeId, String _firstName, String _lastName) {
        ArrayList<Employe> listeEmployes = new ArrayList<>();
        try {
            Access_BD_Employe ae = new Access_BD_Employe();
            listeEmployes.add(ae.getEmploye(this.dailyBankState.getEmployeActuel().login ,this.dailyBankState.getEmployeActuel().motPasse));

        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.primaryStage.close();
            listeEmployes = new ArrayList<>();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
            ed.doExceptionDialog();
            listeEmployes = new ArrayList<>();
        }
        return listeEmployes;
    }

}