package com.example.dsafinals.controllers;

import com.example.dsafinals.util.SettingsManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class SettingsDialogController {

    @FXML private TextField directoryField;

    @FXML private Button browseButton;

    @FXML private Button closeButton;

    @FXML
    public void initialize() {
        directoryField.setText(SettingsManager.loadDirectoryPath());
        directoryField.setEditable(false);
    }

    @FXML
    private void handleBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");

        String currentPath = directoryField.getText();
        if (!currentPath.isEmpty()) {
            File currentDir = new File(currentPath);
            if (currentDir.exists()) {
                directoryChooser.setInitialDirectory(currentDir);
            }
        }

        // When directory gets selected
        File selectedDirectory = directoryChooser.showDialog(browseButton.getScene().getWindow());
        if (selectedDirectory != null) {
            String path = selectedDirectory.getAbsolutePath();
            directoryField.setText(path);
            SettingsManager.saveDirectoryPath(path);
            System.out.println(path);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
