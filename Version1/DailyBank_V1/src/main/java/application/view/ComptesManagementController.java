package application.view;

import java.util.ArrayList;
import java.util.Optional;

import application.DailyBankState;
import application.control.ComptesManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class ComptesManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private ComptesManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> oListCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
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
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			this.cmDialogController.gererOperationsDUnCompte(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}

	@FXML
	private void doModifierCompte() {

	}

	@FXML
	private void doSupprimerCompte() throws RowNotFoundOrTooManyRowsException, DataAccessException,
	DatabaseConnexionException, ManagementRuleViolation {
		btnSupprCompte.setDisable(false);
		CompteCourant compteS = lvComptes.getSelectionModel().getSelectedItem();
		if (compteS.solde != 0 || compteS == null) {//si le compte n'a pas un solde égal à 0€ ou n'existe pas, il ne peut pas être supprimé
			Alert confirmBox = new Alert(AlertType.ERROR);
			confirmBox.setTitle("Cloturer Compte");
			confirmBox.setHeaderText("Impossible de cloturer le compte avec un solde différent de 0");
			confirmBox.showAndWait();

		} else {

			if (compteS.estCloture.equals("N")) {
				Alert confirmBox = new Alert(AlertType.CONFIRMATION);//on demande à l'utilisateur une confirmation
				confirmBox.setTitle("Cloturer Compte");
				confirmBox.setHeaderText("Supprimer Compte");
				Optional<ButtonType> reponse = confirmBox.showAndWait();
				
				if (reponse.orElse(null) == ButtonType.OK) {//si l'utilisateur clique sur "OK", cela veut dire qu'il cloture le compte.
					compteS.solde = 0;
					compteS.estCloture = "O";
					lvComptes.refresh();
					AccessCompteCourant acC = new AccessCompteCourant();
					acC.cloturerCompteCourant(compteS);//on cloture donc le compte
					btnSupprCompte.setDisable(true);//on ne peut plus cliquer sur le bouton pour supprimer le compte
				}
			}
		}
	}




	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cmDialogController.creerNouveauCompte();
		if (compte != null) {
			this.oListCompteCourant.add(compte);
		}
	}

	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cmDialogController.getComptesDunClient();
		this.oListCompteCourant.clear();
		this.oListCompteCourant.addAll(listeCpt);
	}

	private void validateComponentState() {
		// Non implémenté => désactivé
		this.btnModifierCompte.setDisable(true);
		this.btnSupprCompte.setDisable(true);

		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnVoirOpes.setDisable(false);
			this.btnModifierCompte.setDisable(false);
			this.btnSupprCompte.setDisable(false);
		} else {
			this.btnVoirOpes.setDisable(true);
			this.btnModifierCompte.setDisable(true);
			this.btnSupprCompte.setDisable(true);
		}
	}
}
