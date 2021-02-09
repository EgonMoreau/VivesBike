package be.vives.ti.ui.controller;

import be.vives.ti.databag.Lid;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.LidService;
import be.vives.ti.ui.VIVESbike;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class LedenBeheerController {

    private LidService lidService;

    private VIVESbike parent;

    private String geselecteerdeLid;

    @FXML
    private TableView<Lid> tvLeden;
    @FXML
    private TableColumn tcVoornaam;
    @FXML
    private TableColumn tcNaam;
    @FXML
    private TableColumn tcRijksreg;
    @FXML
    private Label laErrorLeden;
    @FXML
    private TextField tfRijksregisternummer;
    @FXML
    private TextField tfVoornaam;
    @FXML
    private TextField tfNaam;
    @FXML
    private TextField tfEmail;
    @FXML
    private DatePicker dpStartdatum;
    @FXML
    private CheckBox cbUitgeschreven;
    @FXML
    private TextArea taOpmerking;
    @FXML
    private Button btnAnnuleren;
    @FXML
    private Button btnOpslaan;





    public LedenBeheerController(LidService lidService){
        this.lidService = lidService;
    }

    public void initialize() {
        tcRijksreg.setCellValueFactory(
                new PropertyValueFactory<>("rijksregisternummer"));
        tcNaam.setCellValueFactory(
                new PropertyValueFactory<>("naam"));
        tcVoornaam.setCellValueFactory(
                new PropertyValueFactory<>("voornaam"));
        disableVelden();
        initialiseerTabel();
    }

    public void SelecteerLid(){
        disableVelden();
        Lid GeselecteerdeLid = tvLeden.getSelectionModel().getSelectedItem();
        if(GeselecteerdeLid != null){
            vulAlleVeldenIn();
        }
    }

    public void openFietsenBeheer(){
        parent.laadFietsenBeheer();
    }

    public void openRittenBeheer(){
        parent.laadRittenBeheer();
    }

    public void toevoegenLid(){
        wisAlleVelden();
        enableAlleVelden();
    }

    //Button wijzigen
    public void wijzigenLid() {
        Lid GeselecteerdeLid = tvLeden.getSelectionModel().getSelectedItem();
        if(GeselecteerdeLid == null) return;
        tfVoornaam.setDisable(false);
        tfNaam.setDisable(false);
        tfEmail.setDisable(false);
        taOpmerking.setDisable(false);
        btnOpslaan.setDisable(false);
        btnAnnuleren.setDisable(false);
    }

    //Button StartDatum wijzigen
    public void wijzigenStartdatum() {
        Lid GeselecteerdeLid = tvLeden.getSelectionModel().getSelectedItem();
        if(GeselecteerdeLid == null) return;

        dpStartdatum.setDisable(false);
        btnOpslaan.setDisable(false);
        btnAnnuleren.setDisable(false);
    }

    //Button Uitschrijven
    public void uitschrijvenLid() {
        Lid GeselecteerdeLid = tvLeden.getSelectionModel().getSelectedItem();
        if(GeselecteerdeLid == null) return;
        try{
            lidService.uitschrijvenLid(GeselecteerdeLid.getRijksregisternummer());
        }catch(ApplicationException | DBException ex){
            laErrorLeden.setText(ex.getMessage());
        }
        tvLeden.getSelectionModel().getSelectedItem().setEinde_lidmaatschap(LocalDate.now());
        cbUitgeschreven.setSelected(true);
    }

    //Button annuleren
    public void annuleren() {
        vulAlleVeldenIn();
        disableVelden();
    }

    //Button Ok
    public void opslaan() {
            if(!tfRijksregisternummer.isDisabled() && !tfEmail.isDisabled() && dpStartdatum.isDisabled()){
                // toevoegen van een lid
                try{
                    System.out.println("test");
                    lidService.toevoegenLid(maakLid(new Rijksregisternummer(tfRijksregisternummer.getText()), tfVoornaam.getText(), tfNaam.getText(), tfEmail.getText(), taOpmerking.getText()));
                    initialiseerTabel();
                }catch(ApplicationException | DBException ex){
                    laErrorLeden.setText(ex.getMessage());
                }
            }else if(tfRijksregisternummer.isDisabled() && !tfEmail.isDisabled() && dpStartdatum.isDisabled()){
                // wijzigen van een lid
                Lid teWijzigenLid = tvLeden.getSelectionModel().getSelectedItem();
                teWijzigenLid.setEmailadres(tfEmail.getText());
                teWijzigenLid.setNaam(tfNaam.getText());
                teWijzigenLid.setOpmerking(taOpmerking.getText());
                teWijzigenLid.setVoornaam(tfVoornaam.getText());
                try{
                    lidService.wijzigenLid(teWijzigenLid);
                    disableVelden();
                    tvLeden.getItems().set(tvLeden.getSelectionModel().getSelectedIndex(), teWijzigenLid); // lid updaten in lijst
                }catch(ApplicationException | DBException ex){
                    laErrorLeden.setText(ex.getMessage());
                }
            }else if(tfRijksregisternummer.isDisabled() && tfEmail.isDisabled() && !dpStartdatum.isDisabled()){
                // veranderen startdatum van een lid
                try{
                    lidService.wijzigStartDatumVanLid(tvLeden.getSelectionModel().getSelectedItem().getRijksregisternummer(), dpStartdatum.getValue());
                    tvLeden.getSelectionModel().getSelectedItem().setStart_lidmaatschap(dpStartdatum.getValue());
                    disableVelden();
                }catch(ApplicationException | DBException ex){
                    laErrorLeden.setText(ex.getMessage());
                }
            }
    }

    /**
     * De foutboodschap op het scherm verwijderen
     */
    private void resetErrorMessage() {
        laErrorLeden.setText("");
    }

    /**
     * Een methode om te testen
     */
    private Lid maakLid(Rijksregisternummer rijksregisternummer, String voornaam, String naam, String emailadres, String opmerking) {
        Lid lid = new Lid();
        lid.setRijksregisternummer(rijksregisternummer);
        lid.setVoornaam(voornaam);
        lid.setNaam(naam);
        lid.setEmailadres(emailadres);
        lid.setOpmerking(opmerking);
        return lid;
    }

    private void initialiseerTabel() {
        resetErrorMessage();
        /*
        try{
            Lid lid1 = maakLid(new Rijksregisternummer("64101612335"), "Andres", "Sabbe", "sabbeandres@outlook.com", "dit is een opmerking");
            Lid lid2 = maakLid(new Rijksregisternummer("93051822361"), "Pieter", "Post", "pieterpost@outlook.com", "dit is een opmerking");
            Lid lid3 = maakLid(new Rijksregisternummer("75120513714"), "Franky", "Testman", "frankytestman@outlook.com", "dit is een opmerking");
            lidService.toevoegenLid(lid1);
            lidService.toevoegenLid(lid2);
            lidService.toevoegenLid(lid3);
        }catch(ApplicationException ex){
            System.out.println(ex);
        }catch (DBException db){
            System.out.println(db);
        }
         */
        try {
            // alle rekeningen van de geselecteerde klant ophalen
            List<Lid> lidlijst = lidService.zoekAlleLeden();

            ObservableList<Lid> rekeningen = FXCollections.
                    observableArrayList(lidlijst);
            tvLeden.setItems(rekeningen);

        } catch (DBException ae) {
            laErrorLeden.setText("onherstelbare fout: " + ae.
                    getMessage());
        }
    }

    private void vulAlleVeldenIn(){
        Lid GeselecteerdeLid = tvLeden.getSelectionModel().getSelectedItem();
        if(GeselecteerdeLid == null) return;
        tfRijksregisternummer.setText(GeselecteerdeLid.getRijksregisternummer());
        tfEmail.setText(GeselecteerdeLid.getEmailadres());
        tfNaam.setText(GeselecteerdeLid.getNaam());
        tfVoornaam.setText(GeselecteerdeLid.getVoornaam());
        dpStartdatum.setValue(GeselecteerdeLid.getStart_lidmaatschap());
        cbUitgeschreven.setSelected(GeselecteerdeLid.getEinde_lidmaatschap() != null ? true : false);
        taOpmerking.setText(GeselecteerdeLid.getOpmerking());
    }
    private void wisAlleVelden(){
        tfRijksregisternummer.setText("");
        tfEmail.setText("");
        tfNaam.setText("");
        tfVoornaam.setText("");
        dpStartdatum.setValue(LocalDate.now());
        cbUitgeschreven.setSelected(false);
        taOpmerking.setText("");
    }
    private void disableVelden(){
        tfRijksregisternummer.setDisable(true);
        tfEmail.setDisable(true);
        tfNaam.setDisable(true);
        tfVoornaam.setDisable(true);
        dpStartdatum.setDisable(true);
        cbUitgeschreven.setDisable(true);
        taOpmerking.setDisable(true);
        btnAnnuleren.setDisable(true);
        btnOpslaan.setDisable(true);
    }
    private void enableAlleVelden(){
        tfRijksregisternummer.setDisable(false);
        tfEmail.setDisable(false);
        tfNaam.setDisable(false);
        tfVoornaam.setDisable(false);
        dpStartdatum.setDisable(true);
        cbUitgeschreven.setDisable(true);
        taOpmerking.setDisable(false);
        btnAnnuleren.setDisable(false);
        btnOpslaan.setDisable(false);
    }

    public void setParent(VIVESbike p) {
        parent = p;
    }
}

