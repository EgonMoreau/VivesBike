package be.vives.ti.ui.controller;

import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Standplaats;
import be.vives.ti.datatype.Status;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.FietsService;
import be.vives.ti.ui.VIVESbike;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;


public class FietsenBeheerController {

    @FXML
    private TextField tfRegistratienummer;
    @FXML
    private ChoiceBox<Status> cbStatus;
    @FXML
    private ChoiceBox<Standplaats> cbStandplaats;
    @FXML
    private TextArea taOpmerkingen;
    @FXML
    private TableColumn tcRegistratienummer;
    @FXML
    private TableColumn tcStatus;
    @FXML
    private TableColumn tcStandplaats;
    @FXML
    private Label laErrorFietsen;
    @FXML
    private Button btnAnnuleer;
    @FXML
    private Button btnOK;
    @FXML
    private TableView<Fiets> tvFietsen;

    private FietsService fietsService;
    private VIVESbike parent;

    public FietsenBeheerController(FietsService fietsService){
        this.fietsService = fietsService;
    }

    public void initialize() {
        tcRegistratienummer.setCellValueFactory(
                new PropertyValueFactory<>("registratienummer"));
        tcStatus.setCellValueFactory(
                new PropertyValueFactory<>("status"));
        tcStandplaats.setCellValueFactory(
                new PropertyValueFactory<>("standplaats"));
        disableVelden();
        try {
            List<Fiets> fietsenLijst = fietsService.zoekAlleFietsen();

            ObservableList<Fiets> fietsen = FXCollections.
                    observableArrayList(fietsenLijst);
            tvFietsen.setItems(fietsen);

        } catch (DBException ae) {
            laErrorFietsen.setText("onherstelbare fout: " + ae.
                    getMessage());
        }
    }

    /**
     * De foutboodschap op het scherm verwijderen
     */
    private void resetErrorMessage() {
        laErrorFietsen.setText("");
    }
    public void SelecteerFiets(){
        disableVelden();
        Fiets GeselecteerdeLid = tvFietsen.getSelectionModel().getSelectedItem();
        if(GeselecteerdeLid != null){
            vulAlleVeldenIn();
        }
    }
    private void disableVelden(){
        tfRegistratienummer.setDisable(true);
        cbStatus.setDisable(true);
        cbStandplaats.setDisable(true);
        taOpmerkingen.setDisable(true);
        btnAnnuleer.setDisable(true);
        btnOK.setDisable(true);
    }
    private void wisAlleVelden(){
        tfRegistratienummer.setText("");
        taOpmerkingen.setText("");
        cbStatus.setValue(null);
        cbStandplaats.getItems().setAll(Standplaats.values());
    }
    private void vulAlleVeldenIn(){
        Fiets GeselecteerdeFiets = tvFietsen.getSelectionModel().getSelectedItem();
        if(GeselecteerdeFiets == null) return;
        tfRegistratienummer.setText(GeselecteerdeFiets.getRegistratienummer().toString());
        cbStatus.getItems().setAll(Status.values());
        cbStatus.setValue(GeselecteerdeFiets.getStatus());
        cbStandplaats.getItems().setAll(Standplaats.values());
        cbStandplaats.setValue(GeselecteerdeFiets.getStandplaats());
        taOpmerkingen.setText(GeselecteerdeFiets.getOpmerking());
    }

    public void toevoegenFiets(){
        disableVelden();
        wisAlleVelden();

        cbStandplaats.getItems().setAll(Standplaats.values());

        cbStatus.getItems().setAll(Status.values());
        cbStatus.setValue(Status.actief);

        cbStandplaats.setDisable(false);
        btnAnnuleer.setDisable(false);
        btnOK.setDisable(false);
    }

    public void wijzigenStatusFiets(){
        disableVelden();
        wisAlleVelden();
        Fiets GeselecteerdeFiets = tvFietsen.getSelectionModel().getSelectedItem();
        if(GeselecteerdeFiets == null) return;
        cbStatus.setDisable(false);
        btnAnnuleer.setDisable(false);
        btnOK.setDisable(false);
        vulAlleVeldenIn();
    }

    public void wijzigenOpmerkingFiets(){
        disableVelden();
        wisAlleVelden();
        Fiets GeselecteerdeFiets = tvFietsen.getSelectionModel().getSelectedItem();
        if(GeselecteerdeFiets == null) return;
        taOpmerkingen.setDisable(false);
        btnAnnuleer.setDisable(false);
        btnOK.setDisable(false);
        vulAlleVeldenIn();
    }

    public void resetVelden(){
        resetErrorMessage();
        wisAlleVelden();
        disableVelden();
    }

    public void opslaan(){
        Fiets GeselecteerdeFiets = tvFietsen.getSelectionModel().getSelectedItem();
        if(cbStatus.isDisabled() && taOpmerkingen.isDisabled() && !cbStandplaats.isDisabled() && tfRegistratienummer.isDisabled()){
            Fiets nieuweFiets = new Fiets();
            nieuweFiets.setStandplaats(cbStandplaats.getValue());
            nieuweFiets.setStatus(Status.actief);
            try{
                nieuweFiets.setRegistratienummer(fietsService.toevoegenFiets(nieuweFiets));

                tvFietsen.getItems().add(nieuweFiets);
                resetVelden();
            }catch(ApplicationException | DBException ex){
                laErrorFietsen.setText(ex.getMessage());
            }
        }else if(!cbStatus.isDisabled() && taOpmerkingen.isDisabled() && cbStandplaats.isDisabled() && tfRegistratienummer.isDisabled()){
            GeselecteerdeFiets.setStatus(cbStatus.getValue());
            try{
                switch (GeselecteerdeFiets.getStatus()) {
                    case actief: fietsService.wijzigenStatusNaarActief(GeselecteerdeFiets.getRegistratienummer());
                    break;
                    case herstel: fietsService.wijzigenStatusNaarHerstel(GeselecteerdeFiets.getRegistratienummer());
                    break;
                    case uit_omloop: fietsService.wijzigenStatusNaarUitOmloop(GeselecteerdeFiets.getRegistratienummer());
                    break;
                }
                tvFietsen.getItems().set(tvFietsen.getSelectionModel().getSelectedIndex(), GeselecteerdeFiets);
                resetVelden();
            }catch(ApplicationException | DBException ex){
                laErrorFietsen.setText(ex.getMessage());
            }
        }else if(cbStatus.isDisabled() && !taOpmerkingen.isDisabled() && cbStandplaats.isDisabled() && tfRegistratienummer.isDisabled()){
            GeselecteerdeFiets.setOpmerking(taOpmerkingen.getText());
            try{
                fietsService.wijzigenOpmerkingFiets(GeselecteerdeFiets.getRegistratienummer(), taOpmerkingen.getText());
                tvFietsen.getItems().set(tvFietsen.getSelectionModel().getSelectedIndex(), GeselecteerdeFiets);
                resetVelden();
            }catch(ApplicationException | DBException ex){
                laErrorFietsen.setText(ex.getMessage());
            }
        }
    }

    public void openLedenBeheer(){
        parent.laadLedenbeheer();
    }
    public void openRittenBeheer(){
        parent.laadRittenBeheer();
    }

    public void setParent(VIVESbike p) {
        parent = p;
    }
}
