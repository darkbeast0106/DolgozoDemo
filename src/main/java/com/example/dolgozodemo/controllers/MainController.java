package com.example.dolgozodemo.controllers;

import com.example.dolgozodemo.DolgozoApp;
import com.example.dolgozodemo.core.Controller;
import com.example.dolgozodemo.models.Dolgozo;
import com.example.dolgozodemo.models.DolgozoDB;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainController extends Controller {
    @FXML private ListView<Dolgozo> dolgozoList;
    private DolgozoDB db;

    @FXML public void initialize() {
        try {
            db = new DolgozoDB();
            listaFeltolt();
        } catch (SQLException e) {
            Platform.runLater(() -> hibaKiir(e));
        }
    }

    @FXML private void hozzaadClick(){
        try {
            Controller controller = newWindow("views/hozzaad.fxml", "Dolgozó hozzáadása", 400, 240);
            Stage stage = controller.getStage();
            stage.setOnCloseRequest(event -> listaFeltolt());
            stage.show();
        } catch (IOException e) {
            hibaKiir(e);
        }
    }

    @FXML public void modositClick(){
        try {
            int selected = dolgozoList.getSelectionModel().getSelectedIndex();
            if (selected == -1){
                throw new Exception("Dolgozó kiválasztása kötelező");
            }
            Dolgozo d = dolgozoList.getSelectionModel().getSelectedItem();
            ModositController controller = (ModositController)newWindow("views/modosit.fxml", "Dolgozó módosítása", 400, 240);
            controller.setModositando(d);
            Stage stage = controller.getStage();
            stage.setOnHiding(event -> {
                dolgozoList.refresh();
            });
            stage.show();
        } catch (Exception e) {
            hibaKiir(e);
        }
    }

    @FXML public void torolClick(){
        try {
            int selected = dolgozoList.getSelectionModel().getSelectedIndex();
            if (selected == -1){
                throw new Exception("Dolgozó kiválasztása kötelező");
            }
            Dolgozo d = dolgozoList.getSelectionModel().getSelectedItem();
            int erintettSorok = db.deleteDolgozo(d.getId());
            if (erintettSorok != 1) {
                throw new SQLException("Hiba történt a dolgozó törlése során");
            }
            dolgozoList.getItems().remove(d);
        } catch (Exception e) {
            hibaKiir(e);
        }
    }

    private void listaFeltolt(){
        try {
            dolgozoList.getItems().clear();
            dolgozoList.getItems().addAll(db.getDolgozok());
        } catch (SQLException e) {
            Platform.runLater(() -> hibaKiir(e));
        }
    }

}