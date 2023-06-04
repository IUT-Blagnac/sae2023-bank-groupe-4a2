package application.view;

import java.util.ArrayList;
import java.util.Optional;

import application.DailyBankState;
import application.control.EmployeManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

public class EmployeManagamentController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à EmployeesManagementController
    private EmployeManagement emDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage primaryStage;

    // Données de la fenêtre
    private ObservableList<Employe> oListEmployes;

    // Manipulation de la fenêtre
    public void initContext(Stage _containingStage, EmployeManagement _em, DailyBankState _dbstate) {
        this.emDialogController = _em;
        this.primaryStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.oListEmployes = FXCollections.observableArrayList();
        this.lvEmployes.setItems(this.oListEmployes);
        this.lvEmployes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvEmployes.getFocusModel().focus(-1);
        this.lvEmployes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        this.validateComponentState();
    }

    public void displayDialog() {
        this.primaryStage.showAndWait();
    }

    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scene + actions

    @FXML
    private TextField txtEmployeeId;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private ListView<Employe> lvEmployes;
    @FXML
    private Button btnEditEmploye;
    @FXML
    private Button btnCreateEmploye;
    @FXML
    private Button btnDeleteEmploye;

    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }

    @FXML
    private void doSearch() {
        int employeeId;
        try {
            String eid = this.txtEmployeeId.getText();
            if (eid.equals("")) {
                employeeId = -1;
            } else {
                employeeId = Integer.parseInt(eid);
                if (employeeId < 0) {
                    this.txtEmployeeId.setText("");
                    employeeId = -1;
                }
            }
        } catch (NumberFormatException nfe) {
            this.txtEmployeeId.setText("");
            employeeId = -1;
        }

        String firstName = this.txtFirstName.getText();
        String lastName = this.txtLastName.getText();

        if (employeeId != -1) {
            this.txtFirstName.setText("");
            this.txtLastName.setText("");
        } else {
            if (firstName.equals("") && !lastName.equals("")) {
                this.txtLastName.setText("");
            }
        }

        // Recherche des employés en BD. cf. EmployeManagement > getListeEmployes(.)
        // employeeId != -1 => recherche sur employeeId
        // employeeId == -1 et firstName non vide => recherche nom/prénom
        // employeeId == -1 et firstName vide => recherche tous les employés
        ArrayList<Employe> listeEmployes;
        listeEmployes = this.emDialogController.getListeEmployes(employeeId, firstName, lastName);

        this.oListEmployes.clear();
        this.oListEmployes.addAll(listeEmployes);
        this.validateComponentState();
    }

    @FXML
    private void doCreateEmploye() {
        Employe employe;
        employe = this.emDialogController.creerEmploye();
        if (employe != null) {
            this.oListEmployes.add(employe);
        }
    }

    @FXML
    private void doEditEmploye() {
        int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            Employe empMod = this.oListEmployes.get(selectedIndice);
            Employe result = this.emDialogController.modifierEmploye(empMod);
            if (result != null) {
                this.oListEmployes.set(selectedIndice, result);
            }
        }
    }
    


    
    @FXML
    private void doDeleteEmploye() {
        // Récupérer l'employé sélectionné dans la liste
        Employe selectedEmploye = lvEmployes.getSelectionModel().getSelectedItem();
        
        // Vérifier si un employé est sélectionné
        if (selectedEmploye != null) {
            // Afficher une boîte de dialogue de confirmation
            
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer l'employé");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer l'employé sélectionné ?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Appeler la méthode de suppression de l'employé
            	this.emDialogController.deleteEmploye(selectedEmploye);
            	this.oListEmployes.remove(selectedEmploye);
               
            }
        }
    }




    private void validateComponentState() {
        // Non implémenté => désactivé
        this.btnEditEmploye.setDisable(true);
        int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnEditEmploye.setDisable(false);
        }
        
    }
    
}
    