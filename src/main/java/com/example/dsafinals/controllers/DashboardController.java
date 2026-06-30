package com.example.dsafinals.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;

public class DashboardController {
    @FXML
    private StackPane entriesIconBox;

    @FXML
    private StackPane photosIconBox;

    @FXML
    private StackPane albumsIconBox;

    @FXML
    public void initialize() {
        entriesIconBox.getChildren().add(createIcon("mdi2b-book-open-page-variant"));
        photosIconBox.getChildren().add(createIcon("mdi2i-image"));
        albumsIconBox.getChildren().add(createIcon("mdi2f-folder"));
    }

    private FontIcon createIcon(String iconCode) {
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(24);
        icon.getStyleClass().add("sidebar-icon");
        return icon;
    }
}
