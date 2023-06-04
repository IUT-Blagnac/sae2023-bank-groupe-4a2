package application.view;

import java.util.regex.Pattern;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class EmployeEditorPaneController {

    private DailyBankState dailyBankState;
    private Stage primaryStage;
    private Employe employeEdite;
    private EditionMode editionMode;
    private Employe employeResultat;

    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.primaryStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
    }

    public Employe displayDialog(Employe employe, EditionMode mode) {
        this.editionMode = mode;
        if (employe == null) {
            this.employeEdite = new Employe(0, "", "", "", "", "",0);
        } else {
            this.employeEdite = new Employe(employe);
        }
        this.employeResultat = null;
        switch (mode) {
            case CREATION:
                this.txtIdEmp.setDisable(true);
                this.txtNom.setDisable(false);
                this.txtPrenom.setDisable(false);
                this.txtDroitsAccess.setDisable(false);
                this.lblMessage.setText("Informations sur le nouvel employé");
                this.butOk.setText("Ajouter");
                this.butCancel.setText("Annuler");
                break;
            case MODIFICATION:
                this.txtIdEmp.setDisable(true);
                this.txtNom.setDisable(false);
                this.txtPrenom.setDisable(false);
                this.txtDroitsAccess.setDisable(false);
                this.lblMessage.setText("Informations employé");
                this.butOk.setText("Modifier");
                this.butCancel.setText("Annuler");
                break;
            case SUPPRESSION:
                ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION EMPLOYE NON PREVUE", null);
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                break;
        }
        // Initialisation des champs
        this.txtIdEmp.setText("" + this.employeEdite.idEmploye);
        this.txtNom.setText(this.employeEdite.nom);
        this.txtPrenom.setText(this.employeEdite.prenom);
        this.txtDroitsAccess.setText(this.employeEdite.droitsAccess);

        this.primaryStage.showAndWait();
        return this.employeResultat;
    }

    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    @FXML
    private Label lblMessage;
    @FXML
    private TextField txtIdEmp;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtDroitsAccess;
    @FXML
    private TextField txtlogin;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button butOk;
    @FXML
    private Button butCancel;

    @FXML
    private void doCancel() {
        this.employeResultat = null;
        this.primaryStage.close();
    }

    @FXML
    private void doAjouter() {
        switch (this.editionMode) {
            case CREATION:
                if (this.isSaisieValide()) {
                    this.employeResultat = this.employeEdite;
                    this.primaryStage.close();
                }
                break;
            case MODIFICATION:
                if (this.isSaisieValide()) {
                    this.employeResultat = this.employeEdite;
                    this.primaryStage.close();
                }
                break;
            case SUPPRESSION:
                this.employeResultat = this.employeEdite;
                this.primaryStage.close();
                break;
        }
    }

    private boolean isSaisieValide() {
        this.employeEdite.nom = this.txtNom.getText().trim();
        this.employeEdite.prenom = this.txtPrenom.getText().trim();
        this.employeEdite.droitsAccess = this.txtDroitsAccess.getText().trim();
        this.employeEdite.login = this.txtlogin.getText().trim();
        this.employeEdite.motPasse = this.passwordField.getText().trim();

        if (this.employeEdite.nom.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide", AlertType.WARNING);
            this.txtNom.requestFocus();
            return false;
        }
        if (this.employeEdite.prenom.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide", AlertType.WARNING);
            this.txtPrenom.requestFocus();
            return false;
        }
        if (this.employeEdite.droitsAccess.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Les droits d'accès ne doivent pas être vides", AlertType.WARNING);
            this.txtDroitsAccess.requestFocus();
            return false;
        }
        if (!this.employeEdite.droitsAccess.equalsIgnoreCase("guichetier") && !this.employeEdite.droitsAccess.equalsIgnoreCase("chefAgence")) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Les droits d'accès doivent être 'guichetier' ou 'chefAgence'", AlertType.WARNING);
            this.txtDroitsAccess.requestFocus();
            return false;
        }
        if (this.employeEdite.login.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login ne doit pas être vide", AlertType.WARNING);
            this.txtlogin.requestFocus();
            return false;
        }
        if (this.employeEdite.motPasse.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mot de passe ne doit pas être vide", AlertType.WARNING);
            this.passwordField.requestFocus();
            return false;
        }

        return true;
    }

}
