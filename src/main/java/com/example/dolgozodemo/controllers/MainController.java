package com.example.dolgozodemo.controllers;

import com.example.dolgozodemo.core.Controller;
import com.example.dolgozodemo.models.Dolgozo;
import com.example.dolgozodemo.models.DolgozoDB;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;

public class MainController extends Controller {
    @FXML private ListView<Dolgozo> dolgozoList;
    private DolgozoDB db;
    private int szempont;
    private int irany;
    @FXML
    private RadioButton radioKor;
    @FXML
    private ToggleGroup rendezesSzempont;
    @FXML
    private RadioButton radioNev;
    @FXML
    private RadioButton radioFizetes;
    @FXML
    private RadioButton radioCsokkeno;
    @FXML
    private ToggleGroup rendezesIranya;
    @FXML
    private RadioButton radioNovekvo;

    public void initialize() {
        szempont = 0;
        irany = 0;
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
            listaRendez();
        } catch (SQLException e) {
            Platform.runLater(() -> hibaKiir(e));
        }
    }

    private void listaRendez() {
        switch (szempont){
            case 0:
                if (irany == 0){
                    dolgozoList.getItems().sort(Comparator.comparing(Dolgozo::getNev));
                } else {
                    dolgozoList.getItems().sort(Comparator.comparing(Dolgozo::getNev).reversed());
                }
                break;
            case 1:
                if (irany == 0){
                    dolgozoList.getItems().sort(Comparator.comparing(Dolgozo::getEletkor));
                } else {
                    dolgozoList.getItems().sort(Comparator.comparing(Dolgozo::getEletkor).reversed());
                }
                break;
            case 2:
                if (irany == 0){
                    dolgozoList.getItems().sort(Comparator.comparing(Dolgozo::getFizetes));
                } else {
                    dolgozoList.getItems().sort(Comparator.comparing(Dolgozo::getFizetes).reversed());
                }
                break;
        }
    }

    @FXML
    public void radioKorClick(ActionEvent actionEvent) {
        szempont = 1;
        listaRendez();
    }

    @FXML
    public void radioCsokkenoClick(ActionEvent actionEvent) {
        irany = 1;
        listaRendez();
    }

    @FXML
    public void radioFizetesClick(ActionEvent actionEvent) {
        szempont = 2;
        listaRendez();
    }

    @FXML
    public void radioNovekvoClick(ActionEvent actionEvent) {
        irany = 0;
        listaRendez();
    }

    @FXML
    public void radioNevClick(ActionEvent actionEvent) {
        szempont = 0;
        listaRendez();
    }
}