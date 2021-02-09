package be.vives.ti.ui.controller;

import be.vives.ti.databag.Fiets;
import be.vives.ti.databag.Lid;
import be.vives.ti.databag.Rit;
import be.vives.ti.datatype.Rijksregisternummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.FietsService;
import be.vives.ti.service.LidService;
import be.vives.ti.service.RitService;
import be.vives.ti.ui.VIVESbike;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;


public class RittenBeheerController {
    private VIVESbike parent;
    private RitService ritService;
    private LidService lidService;
    private FietsService fietsService;
    private Rit geselecteerdeRit;
    private List<Lid> leden;
    private List<Fiets> fietsen;
    @FXML
    private TableView<Rit> tvRitten;
    @FXML
    private TableColumn tcId;
    @FXML
    private TableColumn tcRijksregisternummer;
    @FXML
    private TableColumn tcRegistratienummer;
    @FXML
    private TableColumn tcStarttijd;
    @FXML
    private TableColumn tcEindtijd;
    @FXML
    private TableColumn tcPrijs;
    @FXML
    private Button btnToevoegen;
    @FXML
    private Button btnAfsluiten;
    @FXML
    private Button btnEersteRitVanLid;
    @FXML
    private Button btnActieveRitVanLid;
    @FXML
    private Button btnActiveRitVanFiets;
    @FXML
    private Label laError;
    @FXML
    private ComboBox<String> cbRijksregisternr;
    @FXML
    private ComboBox<Integer> cbFietsregistratienr;
    @FXML
    private TextField tfStarttijd;
    @FXML
    private TextField tfEindtijd;
    @FXML
    private TextField tfPrijs;

    public RittenBeheerController(RitService ritService, LidService lidService, FietsService fietsService) throws DBException {
        this.ritService = ritService;
        this.lidService = lidService;
        this.fietsService = fietsService;
        leden = lidService.zoekAlleLeden();
        fietsen = fietsService.zoekAlleFietsen();
    }

    public void openLedenBeheer() {
        parent.laadLedenbeheer();
    }

    public void openFietsenBeheer() {
        parent.laadFietsenBeheer();
    }

    public void setParent(VIVESbike p) {
        parent = p;
    }

    public void initialize() throws DBException {
        tcId.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        tcRijksregisternummer.setCellValueFactory(
                new PropertyValueFactory<>("lidRijksregisternummer"));
        tcRegistratienummer.setCellValueFactory(
                new PropertyValueFactory<>("fietsRegistratienummer"));
        tcStarttijd.setCellValueFactory(
                new PropertyValueFactory<>("starttijd"));
        tcEindtijd.setCellValueFactory(
                new PropertyValueFactory<>("eindtijd"));
        tcPrijs.setCellValueFactory(
                new PropertyValueFactory<>("prijs"));
        tfPrijs.setDisable(true);
        tfEindtijd.setDisable(true);
        tfStarttijd.setDisable(true);
        ObservableList<String> alleleden = FXCollections.
                observableArrayList(leden.stream().map(Lid::getRijksregisternummer).collect(Collectors.toList()));
        if (alleleden.size() != 0) {
            cbRijksregisternr.setItems(alleleden);
        }
        ObservableList<Integer> allefietsen = FXCollections.
                observableArrayList(fietsen.stream().map(Fiets::getRegistratienummer).collect(Collectors.toList()));
        if (allefietsen.size() != 0) {
            cbFietsregistratienr.setItems(allefietsen);
        }
        initialiseerTabel();
    }


    private void initialiseerTabel() {
        try {
            List<Rit> rittenLijst = ritService.zoekAlleRitten();

            ObservableList<Rit> ritten = FXCollections.
                    observableArrayList(rittenLijst);
            tvRitten.setItems(ritten);

        } catch (DBException ae) {
            laError.setText("Fout bij alleRitten halen uit DB: " + ae);
        }
    }

    public void SelecteerRit() {
        Rit rit = new Rit();
        rit = tvRitten.getSelectionModel().getSelectedItem();
        if (rit != geselecteerdeRit) {
            geselecteerdeRit = tvRitten.getSelectionModel().getSelectedItem();
            wisAlleVelden();
            setVelden(geselecteerdeRit);
        } else {
            tvRitten.getSelectionModel().select(null);
            geselecteerdeRit = null;
            wisAlleVelden();
        }
    }


    public void toevoegenRit() {
        try {
            System.out.println(cbFietsregistratienr.getValue());
            ritService.toevoegenRit(maakRit(new Rijksregisternummer(cbRijksregisternr.getValue()),
                    cbFietsregistratienr.getValue() == null ? 0 : cbFietsregistratienr.getValue()));
            initialiseerTabel();
            wisAlleVelden();
        } catch (ApplicationException | DBException ex) {
            laError.setText(ex.getMessage());
        }
    }

    public void afsluitenRit() {
        try {
            ritService.afsluitenRit(geselecteerdeRit.getId());
            initialiseerTabel();
            wisAlleVelden();
        } catch (ApplicationException | DBException ex) {
            laError.setText(ex.getMessage());
        }
    }

    public void zoekenEersteRit() {
        try {
            Rit rit = ritService.zoekEersteRitVanLid(cbRijksregisternr.getValue());
            if (rit != null) {
                setVelden(rit);
            } else {
                laError.setText(ApplicationExceptionType.RIT_GEEN_RITTEN.getMessage());
            }
        } catch (ApplicationException | DBException ex) {
            laError.setText(ex.getMessage());
        }
    }

    public void zoekActiefLid() {
        try {
            List<Rit> lijst = ritService.zoekActieveRittenVanLid(cbRijksregisternr.getValue());
            if (lijst.size() != 0) {
                setVelden(lijst.get(0));
            } else {
                laError.setText(ApplicationExceptionType.RIT_GEEN_ACTIEVE_RIT_LID.getMessage());
            }
        } catch (ApplicationException | DBException ex) {
            laError.setText(ex.getMessage());
        }
    }

    public void zoekActiefFiets() {
        try {
            List<Rit> lijst = ritService.zoekActieveRittenVanFiets(cbFietsregistratienr.getValue());
            if (lijst.size() != 0) {
                setVelden(lijst.get(0));
            } else {
                laError.setText(ApplicationExceptionType.RIT_GEEN_ACTIEVE_RIT_FIETS.getMessage());
            }
        } catch (ApplicationException | DBException ex) {
            laError.setText(ex.getMessage());
        }
    }

    private Rit maakRit(Rijksregisternummer rijksregisternummer, int fietsRegistratienummer) {
        Rit rit = new Rit();
        rit.setLidRijksregisternummer(rijksregisternummer);
        rit.setFietsRegistratienummer(fietsRegistratienummer);
        return rit;
    }

    private void wisAlleVelden() {
        cbFietsregistratienr.getSelectionModel().clearSelection();
        cbFietsregistratienr.setValue(null);
        cbRijksregisternr.getSelectionModel().clearSelection();
        cbRijksregisternr.setValue(null);
        tfStarttijd.setText("");
        tfEindtijd.setText("");
        tfPrijs.setText("");
        laError.setText("");
    }

    private void setVelden(Rit rit) {
        cbFietsregistratienr.getSelectionModel().select(rit.getFietsRegistratienummer());
        cbFietsregistratienr.setValue(rit.getFietsRegistratienummer());
        cbRijksregisternr.getSelectionModel().select(rit.getLidRijksregisternummer());
        cbRijksregisternr.setValue(rit.getLidRijksregisternummer());
        tfStarttijd.setText("" + rit.getStarttijd());
        if (rit.getEindtijd() != null) {
            tfEindtijd.setText("" + rit.getEindtijd());
            tfPrijs.setText("" + rit.getPrijs());
        } else {
            tfEindtijd.setText("Nog niet beëindigd");
            tfPrijs.setText("Nog niet beëindigd");
        }
        geselecteerdeRit = rit;
    }
}