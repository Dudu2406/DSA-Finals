package com.example.dsafinals.controllers;

import com.example.dsafinals.model.JournalEntry;
import com.example.dsafinals.repository.JournalRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JournalController {

    @FXML private TextField searchField;
    @FXML private Button newEntryButton;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox entriesContainer;

    private final JournalRepository repository = new JournalRepository();

    @FXML
    public void initialize() {
        refreshEntries();
        newEntryButton.setOnAction(e -> createNewEntry());
        searchField.textProperty().addListener((obs, old, val) -> refreshEntries(val));
    }

    public void refreshEntries() {
        refreshEntries(searchField.getText());
    }

    private void refreshEntries(String query) {
        entriesContainer.getChildren().clear();
        List<JournalEntry> entries = repository.search(query);
        for (JournalEntry entry : entries) {
            entriesContainer.getChildren().add(buildCard(entry));
        }
        if (entries.isEmpty()) {
            Text empty = new Text("No journal entries yet. Click \"+ New Entry\" to start writing.");
            empty.getStyleClass().add("muted-text");
            empty.setStyle("-fx-font-size: 16;");
            VBox.setMargin(empty, new Insets(40, 0, 0, 0));
            entriesContainer.getChildren().add(empty);
        }
    }

    private VBox buildCard(JournalEntry entry) {
        VBox card = new VBox(8);
        card.getStyleClass().add("journal-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(16));
        card.setUserData(entry.getId());

        HBox headerRow = buildHeaderRow(entry, card);
        Text titleText = new Text(entry.getTitle() != null && !entry.getTitle().isEmpty()
            ? entry.getTitle() : "Untitled");
        titleText.getStyleClass().add("journal-card-title");
        titleText.setWrappingWidth(0);

        Text tagsText = new Text();
        if (entry.getTags() != null && !entry.getTags().isEmpty()) {
            tagsText.setText("Tags: " + entry.getTags());
            tagsText.getStyleClass().add("journal-tags");
        }

        Text contentText = new Text();
        String preview = entry.getContent();
        if (preview != null && !preview.isEmpty()) {
            preview = preview.length() > 150 ? preview.substring(0, 150) + "..." : preview;
            contentText.setText(preview);
            contentText.getStyleClass().add("journal-content-preview");
            contentText.setWrappingWidth(0);
        }

        card.getChildren().addAll(headerRow, titleText, tagsText, contentText);
        return card;
    }

    private HBox buildHeaderRow(JournalEntry entry, VBox card) {
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setMaxWidth(Double.MAX_VALUE);

        Text dateText = new Text(entry.getDate() != null
            ? entry.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            : "No date");
        dateText.getStyleClass().add("journal-date");
        if (entry.getDate() != null && entry.getDate().equals(LocalDate.now())) {
            Text todayBadge = new Text("  Today");
            todayBadge.getStyleClass().add("journal-today-badge");
            HBox.setMargin(todayBadge, new Insets(0, 0, 0, 8));
            dateText.setStyle("-fx-fill: -primary-color; -fx-font-weight: bold;");
            headerRow.getChildren().add(todayBadge);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = iconButton("mdi2p-pencil", 16, "Edit");
        Button deleteBtn = iconButton("mdi2d-delete", 16, "Delete");

        editBtn.setOnAction(e -> enterEditMode(card, entry));
        deleteBtn.setOnAction(e -> {
            repository.deleteEntry(entry.getId());
            refreshEntries();
        });

        headerRow.getChildren().addAll(dateText, spacer, editBtn, deleteBtn);
        return headerRow;
    }

    private void enterEditMode(VBox card, JournalEntry entry) {
        card.getChildren().clear();
        card.setPadding(new Insets(16));
        card.setSpacing(10);

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setMaxWidth(Double.MAX_VALUE);

        Text dateText = new Text(entry.getDate() != null
            ? entry.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            : LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateText.getStyleClass().add("journal-date");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button saveBtn = iconButton("mdi2c-check", 18, "Save");
        saveBtn.setStyle("-fx-icon-color: #22c55e;");
        Button cancelBtn = iconButton("mdi2c-close", 18, "Cancel");
        cancelBtn.setStyle("-fx-icon-color: #ef4444;");

        headerRow.getChildren().addAll(dateText, spacer, saveBtn, cancelBtn);

        TextField titleField = new TextField(entry.getTitle());
        titleField.getStyleClass().add("journal-field-title");
        titleField.setPromptText("Entry title");

        TextField tagsField = new TextField(entry.getTags());
        tagsField.getStyleClass().add("journal-field-tags");
        tagsField.setPromptText("Tags (comma-separated)");

        TextArea contentArea = new TextArea(entry.getContent());
        contentArea.getStyleClass().add("journal-field-content");
        contentArea.setPromptText("Write your entry here...");
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(6);

        card.getChildren().addAll(headerRow, titleField, tagsField, contentArea);

        saveBtn.setOnAction(e -> {
            entry.setTitle(titleField.getText().trim());
            entry.setTags(tagsField.getText().trim());
            entry.setContent(contentArea.getText().trim());
            if (entry.getDate() == null) {
                entry.setDate(LocalDate.now());
            }
            repository.saveEntry(entry);
            replaceCard(card, entry);
        });

        cancelBtn.setOnAction(e -> {
            replaceCard(card, entry);
        });

        Platform.runLater(titleField::requestFocus);
    }

    private void replaceCard(VBox oldCard, JournalEntry entry) {
        int idx = entriesContainer.getChildren().indexOf(oldCard);
        if (idx >= 0) {
            entriesContainer.getChildren().set(idx, buildCard(entry));
        }
    }

    public void createNewEntry() {
        JournalEntry entry = new JournalEntry("", "", "", LocalDate.now());
        repository.saveEntry(entry);
        refreshEntries();

        for (var node : entriesContainer.getChildren()) {
            if (node instanceof VBox card && entry.getId().equals(card.getUserData())) {
                enterEditMode(card, entry);
                break;
            }
        }

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private Button iconButton(String iconCode, int size, String tooltip) {
        Button btn = new Button();
        btn.setGraphic(createIcon(iconCode, size));
        btn.getStyleClass().addAll("journal-icon-btn", "transparent-button");
        btn.setPadding(new Insets(4));
        Tooltip tp = new Tooltip(tooltip);
        tp.setShowDelay(Duration.millis(300));
        btn.setTooltip(tp);
        return btn;
    }

    private FontIcon createIcon(String iconCode, int size) {
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(size);
        return icon;
    }
}
