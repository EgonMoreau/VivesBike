package be.vives.ti.ui;

import be.vives.ti.dao.FietsDAO;
import be.vives.ti.dao.LidDAO;
import be.vives.ti.dao.RitDAO;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.FietsService;
import be.vives.ti.service.LidService;
import be.vives.ti.service.RitService;
import be.vives.ti.ui.controller.FietsenBeheerController;
import be.vives.ti.ui.controller.LedenBeheerController;
import be.vives.ti.ui.controller.RittenBeheerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VIVESbike extends Application {

    private final Stage stage = new Stage();

    private FietsService fietsService;
    private LidService lidService;
    private RitService ritService;
    private FietsDAO fietsDAO;
    private LidDAO lidDAO;
    private RitDAO ritDAO;

    private FietsService createFietsService() {
        if (fietsService == null) {
            this.fietsService = new FietsService(createFietsDAO());
        }
        return fietsService;
    }
    private FietsDAO createFietsDAO() {
        if (fietsDAO == null) {
            this.fietsDAO = new FietsDAO();
        }
        return fietsDAO;
    }
    private LidService createLidService() {
        if (lidService == null) {
            this.lidService = new LidService(createLidDAO(), createRitService());
        }
        return lidService;
    }
    private LidDAO createLidDAO(){
        if (lidDAO == null) {
            this.lidDAO = new LidDAO();
        }
        return lidDAO;
    }
    private RitService createRitService() {
        if (ritService == null) {
            this.ritService = new RitService(createRitDAO(), createFietsService(), createLidDAO());
        }
        return ritService;
    }
    private RitDAO createRitDAO() {
        if (ritDAO == null) {
            this.ritDAO = new RitDAO();
        }
        return ritDAO;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        laadLedenbeheer();
        stage.show();
    }


    public void laadLedenbeheer() {
        try {
            String fxmlFile = "/fxml/LedenBeheer.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // controller instellen
            LedenBeheerController controller = new LedenBeheerController(createLidService());
            loader.setController(controller);

            Parent root = loader.load();
            controller.setParent(this);
            Scene scene = new Scene(root);
            stage.setTitle("Leden beheren");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("SYSTEEMFOUT bij laden ledenbeheer: " + e.getMessage());
        }
    }

    public void laadRittenBeheer(){
        try {
            String fxmlFile = "/fxml/RittenBeheer.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // controller instellen
            RittenBeheerController controller = new RittenBeheerController(createRitService(), createLidService(), createFietsService());
            loader.setController(controller);

            Parent root = loader.load();
            controller.setParent(this);
            Scene scene = new Scene(root);
            stage.setTitle("Ritten beheren");
            stage.setScene(scene);

        } catch (IOException | DBException e) {
            System.out.println("SYSTEEMFOUT bij laden rittenbeheer: " + e.getMessage());
        }
    }

    public void laadFietsenBeheer(){
        try {
            String fxmlFile = "/fxml/FietsenBeheer.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // controller instellen
            FietsenBeheerController controller = new FietsenBeheerController(createFietsService());
            loader.setController(controller);

            Parent root = loader.load();
            controller.setParent(this);
            Scene scene = new Scene(root);
            stage.setTitle("Fietsen beheer");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("SYSTEEMFOUT bij laden rittenbeheer: " + e.getMessage());
        }
    }

    public Stage getPrimaryStage() {
        return stage;
    }
}
