package org.ieselgrao.hibernatepractica.view;

import com.google.gson.Gson;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import org.ieselgrao.hibernatepractica.UniGraoVerse;
import org.ieselgrao.hibernatepractica.controller.UniGraoVerseController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for the play view.
 */
public class PlayViewController {

    private UniGraoVerseController controller;

    @FXML
    private TableView<List<String>> mainTableView;

    @FXML
    private Button backButton;
    @FXML
    private Button addButton;

    private String currentLevel = "planets"; // Tracks the current table level
    private int selectedSolarSystem;

    @FXML
    public void initialize() {
        controller = new UniGraoVerseController();
        loadSolarSystemsTable();
        setupBackButton();
    }

    private void loadSolarSystemsTable()
    {
        currentLevel = "solarSystems";
        List<String> solarSystems = controller.getSolarSystemsData();

        setupTable(
                List.of("ID", "Solar System", "Main Star", "Distance to the Sun", "Radius of the solar system"),
                List.of("id", "name", "star", "distance", "radius"),
                solarSystems,
                selection -> {
                    selectedSolarSystem = Integer.parseInt(selection.getFirst());
                    loadPlanetsTable();
                }
        );
    }
    private void loadPlanetsTable() {
        currentLevel = "planets";
        List<String> planets = controller.getPlanetsData(selectedSolarSystem);

        setupTable(
                List.of("ID", "Planets from " + selectedSolarSystem, "Mass", "Radius", "Gravity", "Last albedo measurement date"),
                List.of("id", "name", "mass", "radius", "gravity", "date"),
                planets,
                null
        );

        // Make fields editable
        // Note that loops starts on 1. This leaves first column (id) as non-editable
        for (int i = 1; i < mainTableView.getColumns().size(); i++) {
            final int colIndex = i;
            TableColumn<List<String>, String> column = (TableColumn<List<String>, String>) mainTableView.getColumns().get(i);

            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> {
                List<String> row = event.getRowValue();
                row.set(colIndex, event.getNewValue());

                updatePlanetInController(row);
            });
        }

        mainTableView.setEditable(true);
        mainTableView.getSelectionModel().setCellSelectionEnabled(false);
        mainTableView.getSelectionModel().clearSelection();
        mainTableView.setOnMouseClicked(null);
    }

    private void addNewEmptyRow() {
        mainTableView.getItems().add(new ArrayList<>(List.of("", "", "", "", "")));
    }
    private void updatePlanetInController(List<String> row) {

        String name = row.get(1); // Name is a string, no parsing errors possible

        double mass;
        try {
            mass = Double.parseDouble(row.get(2)); // Parse the data to a double
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid input", "The mass must be a valid number.");
            row.set(2, ""); // Clear the mass if it's invalid
            mainTableView.refresh(); // Refresh the table to show the cleared value
            return;
        }

        double radius;
        try {
            radius = Double.parseDouble(row.get(3)); // Parse the data to a double
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid input", "The radius must be a valid number.");
            row.set(3, ""); // Clear the radius if it's invalid
            mainTableView.refresh(); // Refresh the table
            return;
        }

        double gravity;
        try {
            gravity = Double.parseDouble(row.get(4)); // Parse the data to a double
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid input", "The gravity must be a valid number.");
            row.set(4, ""); // Clear the gravity if it's invalid
            mainTableView.refresh(); // Refresh the table
            return;
        }

        LocalDate date;
        try {
            String dateString = row.get(5);
            date = (dateString == null || dateString.isEmpty()) ? null : LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            showErrorDialog("Invalid input", "The date must be in a valid format (YYYY-MM-DD).");
            row.set(5, ""); // Clear the date if it's invalid
            mainTableView.refresh(); // Refresh the table
            return;
        }

        try {
            controller.updatePlanet(selectedSolarSystem, name, mass, radius, gravity, date);
        } catch (Exception e) {
            showErrorDialog("Invalid planet update", e.getMessage());
        }

        mainTableView.refresh(); // Sincroniza la vista con los cambios en la lista 'row'
    }
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupTable(List<String> headers, List<String> keys, List<String> data, TableRowClickListener listener) {
        mainTableView.getColumns().clear();

        // Crear columnas dinámicamente
        for (int i = 0; i < headers.size(); i++) {
            int columnIndex = i;
            TableColumn<List<String>, String> column = new TableColumn<>(headers.get(i));
            column.setCellValueFactory(param -> {
                List<String> row = param.getValue();
                if (row != null && row.size() > columnIndex) {
                    String value = row.get(columnIndex);
                    return new SimpleStringProperty(value != null ? value : ""); // Convertir null a ""
                }
                return new SimpleStringProperty("");
            });
            mainTableView.getColumns().add(column);
        }

        List<List<String>> rows = new ArrayList<>();
        for (String rowJson : data) {
            rows.add(parseJsonToList(rowJson, keys)); // Convertir JSON a lista de valores
        }

        mainTableView.setItems(FXCollections.observableArrayList(rows));
        mainTableView.setVisible(true);

        if (listener != null) {
            mainTableView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && mainTableView.getSelectionModel().getSelectedItem() != null) {
                    listener.onRowClick(mainTableView.getSelectionModel().getSelectedItem());
                }
            });
        }
    }

    // Parses a Json, with a specificed list of strings to a list of the values
    private List<String> parseJsonToList(String json, List<String> keys) {
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(json, Map.class);
        List<String> values = new ArrayList<>();

        for (String key : keys) {
            Object value = map.get(key);
            if (value == null && map.containsKey("details")) {
                Map<String, Object> detailsMap = (Map<String, Object>) map.get("details");
                value = detailsMap != null ? detailsMap.get(key) : null;
            }

            if (value instanceof Double) {
                values.add(String.valueOf(((Double) value).intValue()));
            } else if (value == null || "null".equals(value.toString())) {
                values.add("");
            } else {
                values.add(value.toString());
            }

        }
        return values;
    }

    @FXML
    private void setupBackButton() {
        backButton.setOnAction(e -> {
            try {
                if ("planets".equals(currentLevel)) {
                    loadSolarSystemsTable();
                } else if ("solarSystem".equals(currentLevel)) {
                    UniGraoVerse.main.goScene("main");
                }
            } catch (IOException ex) {
                System.exit(1);
            }
        });
    }

    // Add planet/solar system logic
    @FXML
    private void setupAddButton() {
        addButton.setOnAction(e -> showAddDialog());
    }

    private void showAddDialog() {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Add New " + (currentLevel.equals("planets") ? "Planet" : "Solar System"));
        dialog.setHeaderText("Enter the details for the new " + currentLevel);

        // Botones de confirmación
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        List<TextField> fields = new ArrayList<>();
        List<String> labels = currentLevel.equals("planets")
                ? List.of("Name:", "Mass:", "Radius:", "Gravity:", "Date (YYYY-MM-DD):")
                : List.of("Name:", "Star:", "Distance:", "Radius:");

        for (int i = 0; i < labels.size(); i++) {
            grid.add(new Label(labels.get(i)), 0, i);
            TextField tf = new TextField();
            fields.add(tf);
            grid.add(tf, 1, i);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return fields.stream().map(TextField::getText).toList();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::processNewEntry);
    }
    private void processNewEntry(List<String> data) {
        try {
            if (currentLevel.equals("planets")) {
                // Validar y parsear igual que en updatePlanetInController
                String name = data.get(0);
                double mass = Double.parseDouble(data.get(1));
                double radius = Double.parseDouble(data.get(2));
                double gravity = Double.parseDouble(data.get(3));
                LocalDate date = data.get(4).isEmpty() ? null : LocalDate.parse(data.get(4));

                controller.addPlanet(selectedSolarSystem, name, mass, radius, gravity, date);
                loadPlanetsTable(); // Recargar tabla
            } else {
                // Lógica para Solar System
                String name = data.get(0);
                String star = data.get(1);
                double distance = Double.parseDouble(data.get(2));
                double radius = Double.parseDouble(data.get(3));

                controller.addSolarSystem(name, star, distance, radius);
                loadSolarSystemsTable(); // Recargar tabla
            }
        } catch (Exception e) {
            showErrorDialog("Input Error", "Check your data formats: " + e.getMessage());
        }
    }
    @FunctionalInterface
    private interface TableRowClickListener {
        void onRowClick(List<String> row);
    }
}
