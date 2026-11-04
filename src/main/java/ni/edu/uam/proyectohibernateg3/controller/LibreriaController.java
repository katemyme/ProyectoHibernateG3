package ni.edu.uam.proyectohibernateg3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LibreriaController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
