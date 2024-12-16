package carShopCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLException;
import java.util.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import org.controlsfx.control.RangeSlider;
import java.util.Set;
import java.util.stream.Collectors;

public class CarTableView {
    // Color constants
    private static final String PRIMARY_RED = "#B22222";
    private static final String DARKER_RED = "#8B0000";
    private static final String BG_DARK = "#1A1A1A";
    private static final String SURFACE_DARK = "#2A2A2A";
    private static final String ACCENT_GREY = "#3C3C3C";
    private static final String TEXT_LIGHT = "#FFFFFF";
    
    // Data
    private static ObservableList<Car> carData;
    private static FilteredList<Car> filteredData;
    private static TableView<Car> tableView;

    // Filter components
    private static RangeSlider yearSlider;
    private static RangeSlider priceSlider;
    private static ComboBox<String> makeComboBox;
    private static ComboBox<String> modelComboBox;
    private static Spinner<Integer> stockSpinner;
    private static TextField idFilter;
    private static TextField vinFilter;
    private static Label resultCountLabel;

    public static BorderPane buildCarTableView() {
        // Initialize main container
        BorderPane mainContent = new BorderPane();
        mainContent.getStyleClass().add("main-content");
        
        // Initialize data
        carData = FXCollections.observableArrayList(CarsASU.getAllCars());
        filteredData = new FilteredList<>(carData, p -> true);
        
        // Create components
        VBox headerSection = createHeaderSection();
        tableView = buildTable();
        
        // Layout setup
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.getChildren().add(tableView);
        
        // Set layout
        mainContent.setTop(headerSection);
        mainContent.setCenter(contentArea);
        
        return mainContent;
    }
    
    private static VBox createHeaderSection() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: " + SURFACE_DARK + ";");

        // Top Section: Breadcrumb and Title
        Label breadcrumb = new Label("Dashboard > Cars");
        breadcrumb.getStyleClass().add("breadcrumb");

        // Title Row with Add Button
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Cars Management");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = createAddButton();
        titleRow.getChildren().addAll(title, spacer, addButton);

        // Filters Container
        VBox filtersContainer = createFiltersContainer();

        header.getChildren().addAll(breadcrumb, titleRow, filtersContainer);
        return header;
    }

    private static VBox createFiltersContainer() {
        VBox container = new VBox(15);
        container.getStyleClass().add("filter-box");

        // Create filter grid
        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(20);
        filterGrid.setVgap(15);

        // ID Filter
        idFilter = createStyledTextField("Enter ID");
        addFilterToGrid(filterGrid, "ID:", idFilter, 0, 0);

        // Make and Model Filters
        makeComboBox = new ComboBox<>();
        modelComboBox = new ComboBox<>();
        setupMakeModelComboBoxes();
        addFilterToGrid(filterGrid, "Make:", makeComboBox, 0, 1);
        addFilterToGrid(filterGrid, "Model:", modelComboBox, 0, 2);

        // Year Range Slider
        VBox yearBox = createYearFilter();
        addFilterToGrid(filterGrid, "Year Range:", yearBox, 1, 0, 1, 2);

        // Price Range Slider
        VBox priceBox = createPriceFilter();
        addFilterToGrid(filterGrid, "Price Range:", priceBox, 2, 0, 1, 2);

        // Stock Spinner
        stockSpinner = createStockSpinner();
        addFilterToGrid(filterGrid, "Min Stock:", stockSpinner, 1, 2);

        // VIN Filter
        vinFilter = createStyledTextField("Enter VIN");
        addFilterToGrid(filterGrid, "VIN:", vinFilter, 2, 2);

        // Results count label
        resultCountLabel = new Label("Showing all cars");
        resultCountLabel.getStyleClass().add("result-count");

        // Action buttons
        HBox actionButtons = createFilterActionButtons();

        container.getChildren().addAll(filterGrid, actionButtons, resultCountLabel);
        return container;
    }

    private static void setupMakeModelComboBoxes() {
        // Setup Make ComboBox
        Set<String> makes = carData.stream()
            .map(Car::getMake)
            .collect(Collectors.toSet());
        
        ObservableList<String> makesList = FXCollections.observableArrayList();
        makesList.add("All Makes");
        makesList.addAll(makes);
        
        makeComboBox.setItems(makesList);
        makeComboBox.setValue("All Makes");
        makeComboBox.getStyleClass().add("filter-combo-box");

        // Setup Model ComboBox
        modelComboBox.setPromptText("Select Model");
        modelComboBox.getStyleClass().add("filter-combo-box");

        // Make-Model relationship
        makeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("All Makes")) {
                Set<String> models = carData.stream()
                    .filter(car -> car.getMake().equals(newVal))
                    .map(Car::getModel)
                    .collect(Collectors.toSet());
                modelComboBox.setItems(FXCollections.observableArrayList(models));
            } else {
                modelComboBox.getItems().clear();
                modelComboBox.setPromptText("Select Model");
            }
        });
    }

    private static VBox createYearFilter() {
        int minYear = carData.stream().mapToInt(Car::getYear).min().orElse(2000);
        int maxYear = carData.stream().mapToInt(Car::getYear).max().orElse(2024);

        Label yearRangeLabel = new Label(String.format("%d - %d", minYear, maxYear));
        yearRangeLabel.getStyleClass().add("range-label");

        yearSlider = new RangeSlider(minYear, maxYear, minYear, maxYear);
        yearSlider.getStyleClass().add("range-slider");
        
        yearSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> 
            yearRangeLabel.setText(String.format("%d - %d", 
                newVal.intValue(), (int)yearSlider.getHighValue())));
                
        yearSlider.highValueProperty().addListener((obs, oldVal, newVal) -> 
            yearRangeLabel.setText(String.format("%d - %d", 
                (int)yearSlider.getLowValue(), newVal.intValue())));

        VBox yearBox = new VBox(5);
        yearBox.getChildren().addAll(yearRangeLabel, yearSlider);
        return yearBox;
    }

    private static VBox createPriceFilter() {
        double minPrice = carData.stream().mapToDouble(Car::getPrice).min().orElse(0);
        double maxPrice = carData.stream().mapToDouble(Car::getPrice).max().orElse(200000);

        Label priceRangeLabel = new Label(String.format("$%.2f - $%.2f", minPrice, maxPrice));
        priceRangeLabel.getStyleClass().add("range-label");

        priceSlider = new RangeSlider(minPrice, maxPrice, minPrice, maxPrice);
        priceSlider.getStyleClass().add("range-slider");
        
        priceSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> 
            priceRangeLabel.setText(String.format("$%.2f - $%.2f", 
                newVal.doubleValue(), priceSlider.getHighValue())));
                
        priceSlider.highValueProperty().addListener((obs, oldVal, newVal) -> 
            priceRangeLabel.setText(String.format("$%.2f - $%.2f", 
                priceSlider.getLowValue(), newVal.doubleValue())));

        VBox priceBox = new VBox(5);
        priceBox.getChildren().addAll(priceRangeLabel, priceSlider);
        return priceBox;
    }

    private static Spinner<Integer> createStockSpinner() {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
        Spinner<Integer> spinner = new Spinner<>(factory);
        spinner.setEditable(true);
        spinner.getStyleClass().add("filter-spinner");
        return spinner;
    }

    private static HBox createFilterActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button resetButton = new Button("Reset Filters");
        resetButton.getStyleClass().addAll("action-button", "reset-button");
        resetButton.setOnAction(e -> resetFilters());

        buttonBox.getChildren().add(resetButton);
        return buttonBox;
    }

    private static void addFilterToGrid(GridPane grid, String labelText, 
        Node control, int col, int row, int colSpan, int rowSpan) {
        
        Label label = new Label(labelText);
        label.getStyleClass().add("filter-label");
        
        grid.add(label, col * 2, row, 1, rowSpan);
        grid.add(control, col * 2 + 1, row, colSpan, rowSpan);
        GridPane.setHgrow(control, Priority.ALWAYS);
    }

    private static void addFilterToGrid(GridPane grid, String labelText, 
        Node control, int col, int row) {
        addFilterToGrid(grid, labelText, control, col, row, 1, 1);
    }
    private static void setupFilterListeners() {
        // Create basic filter predicates
        idFilter.textProperty().addListener((obs, old, newVal) -> applyFilters());
        makeComboBox.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        modelComboBox.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        yearSlider.lowValueProperty().addListener((obs, old, newVal) -> applyFilters());
        yearSlider.highValueProperty().addListener((obs, old, newVal) -> applyFilters());
        priceSlider.lowValueProperty().addListener((obs, old, newVal) -> applyFilters());
        priceSlider.highValueProperty().addListener((obs, old, newVal) -> applyFilters());
        stockSpinner.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        vinFilter.textProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private static void applyFilters() {
        filteredData.setPredicate(car -> {
            // ID Filter
            if (!idFilter.getText().isEmpty() &&
                !String.valueOf(car.getCarID()).contains(idFilter.getText())) {
                return false;
            }

            // Make Filter
            if (makeComboBox.getValue() != null && 
                !makeComboBox.getValue().equals("All Makes") &&
                !car.getMake().equals(makeComboBox.getValue())) {
                return false;
            }

            // Model Filter
            if (modelComboBox.getValue() != null && 
                !modelComboBox.getValue().isEmpty() &&
                !car.getModel().equals(modelComboBox.getValue())) {
                return false;
            }

            // Year Range Filter
            if (car.getYear() < yearSlider.getLowValue() || 
                car.getYear() > yearSlider.getHighValue()) {
                return false;
            }

            // Price Range Filter
            if (car.getPrice() < priceSlider.getLowValue() || 
                car.getPrice() > priceSlider.getHighValue()) {
                return false;
            }

            // Stock Filter
            if (car.getStock() < stockSpinner.getValue()) {
                return false;
            }

            // VIN Filter
            if (!vinFilter.getText().isEmpty() && 
                !car.getVin().toLowerCase().contains(vinFilter.getText().toLowerCase())) {
                return false;
            }

            return true;
        });

        updateResultCount();
    }

    private static void resetFilters() {
        // Reset ID filter
        idFilter.clear();

        // Reset Make/Model filters
        makeComboBox.setValue("All Makes");
        modelComboBox.getItems().clear();
        modelComboBox.setPromptText("Select Model");

        // Reset Year slider
        yearSlider.setLowValue(yearSlider.getMin());
        yearSlider.setHighValue(yearSlider.getMax());

        // Reset Price slider
        priceSlider.setLowValue(priceSlider.getMin());
        priceSlider.setHighValue(priceSlider.getMax());

        // Reset Stock spinner
        stockSpinner.getValueFactory().setValue(0);

        // Reset VIN filter
        vinFilter.clear();

        // Apply reset filters
        applyFilters();
    }

    private static void updateResultCount() {
        int filteredCount = filteredData.size();
        int totalCount = carData.size();
        resultCountLabel.setText(
            String.format("Showing %d of %d cars", filteredCount, totalCount)
        );
    }

    private static TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("filter-field");
        return field;
    }

    private static Button createAddButton() {
        Button addBtn = new Button("Add New Car");
        addBtn.getStyleClass().addAll("action-button", "add-button");
        
        // Add hover effect
        addBtn.setOnMouseEntered(e -> 
            addBtn.setStyle("-fx-background-color: " + DARKER_RED + ";"));
        addBtn.setOnMouseExited(e -> 
            addBtn.setStyle("-fx-background-color: " + PRIMARY_RED + ";"));
        
        // Add button click handler
        addBtn.setOnAction(e -> openAddDialog());
        
        return addBtn;
    }

    private static void refreshTable() {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        
        carData.clear();
        carData.addAll(CarsASU.getAllCars());
        
        applyFilters();
        
        // Restore selection if possible
        if (selectedIndex >= 0 && selectedIndex < tableView.getItems().size()) {
            tableView.getSelectionModel().select(selectedIndex);
        }
    }
    private static TableView<Car> buildTable() {
        TableView<Car> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Create columns
        TableColumn<Car, Integer> carIDColumn = createColumn("Car ID", "carID", 82);
        TableColumn<Car, String> makeColumn = createColumn("Make", "make", 115);
        TableColumn<Car, String> modelColumn = createColumn("Model", "model", 115);
        TableColumn<Car, Integer> yearColumn = createColumn("Year", "year", 82);
        
        // Price column with custom formatting
        TableColumn<Car, Double> priceColumn = createColumn("Price", "price", 100);
        priceColumn.setCellFactory(column -> new TableCell<Car, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        TableColumn<Car, Integer> stockColumn = createColumn("Stock", "stock", 82);
        TableColumn<Car, String> vinColumn = createColumn("VIN", "vin", 163);

        // Create Edit column
        TableColumn<Car, Void> editColumn = createEditColumn();

        // Add all columns to table
        tableView.getColumns().addAll(
            carIDColumn, makeColumn, modelColumn,
            yearColumn, priceColumn, stockColumn,
            vinColumn, editColumn
        );

        // Style rows
        tableView.setRowFactory(tv -> {
            TableRow<Car> row = new TableRow<>();
            
            // Style based on row state (normal, hover, selected)
            row.styleProperty().bind(
                javafx.beans.binding.Bindings.when(row.selectedProperty())
                    .then("-fx-background-color: " + DARKER_RED + ";")
                    .otherwise(
                        javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then("")
                            .otherwise(row.getIndex() % 2 == 0 
                                ? "-fx-background-color: " + SURFACE_DARK + ";"
                                : "-fx-background-color: derive(" + SURFACE_DARK + ", 5%);"
                            )
                    )
            );

            // Hover effect
            row.setOnMouseEntered(event -> {
                if (!row.isSelected()) {
                    row.setStyle("-fx-background-color: " + PRIMARY_RED + ";");
                }
            });
            
            row.setOnMouseExited(event -> {
                if (!row.isSelected()) {
                    if (!row.isEmpty()) {
                        row.setStyle(row.getIndex() % 2 == 0 
                            ? "-fx-background-color: " + SURFACE_DARK + ";"
                            : "-fx-background-color: derive(" + SURFACE_DARK + ", 5%);"
                        );
                    }
                }
            });

            return row;
        });

        // Set table properties
        tableView.setItems(filteredData);
        tableView.getStyleClass().add("car-table");
        tableView.setFixedCellSize(40); // Consistent row height

        return tableView;
    }

    private static <T> TableColumn<Car, T> createColumn(String title, String property, double width) {
        TableColumn<Car, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        column.setStyle("-fx-alignment: CENTER;");
        return column;
    }

    private static TableColumn<Car, Void> createEditColumn() {
        TableColumn<Car, Void> editColumn = new TableColumn<>("Edit");
        editColumn.setPrefWidth(70);
        editColumn.setStyle("-fx-alignment: CENTER;");

        editColumn.setCellFactory(column -> new TableCell<Car, Void>() {
            private final Button editButton = new Button();

            {
                // Load edit icon
                try {
                    Image editImage = new Image(getClass().getResourceAsStream("/icons8-edit-96.png"));
                    ImageView editIcon = new ImageView(editImage);
                    editIcon.setFitWidth(16);
                    editIcon.setFitHeight(16);
                    editButton.setGraphic(editIcon);
                    editButton.getStyleClass().add("edit-button");

                    editButton.setOnAction(event -> {
                        Car car = getTableView().getItems().get(getIndex());
                        openEditDialog(car);
                    });
                } catch (Exception e) {
                    // Fallback if icon fails to load
                    editButton.setText("Edit");
                    System.err.println("Could not load edit icon: " + e.getMessage());
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
                setAlignment(Pos.CENTER);
            }
        });

        return editColumn;
    }
//    private static void openEditDialog(Car car) {
//        Dialog<Car> dialog = new Dialog<>();
//        dialog.setTitle("Edit Car");
//        dialog.setHeaderText("Edit Car Details");
//        Node headerPanel = dialog.getDialogPane().lookup(".header-panel");
//        if (headerPanel != null) {
//            headerPanel.setStyle("-fx-background-color: red;");
//        }
//        Node headerText = dialog.getDialogPane().lookup(".header-panel .label");
//        if (headerText != null) {
//            headerText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
//        }
//        // Style dialog
//        DialogPane dialogPane = dialog.getDialogPane();
//        dialogPane.setStyle(
//                "-fx-background-color: " + SURFACE_DARK + ";" +
//                "-fx-border-color: " + PRIMARY_RED + ";" +
//                "-fx-border-width: 1px;"
//            );
//
//        
//        
//        // Set the button types
//        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
//        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//        dialogPane.getButtonTypes().addAll(saveButtonType, cancelButtonType);
//
//        // Create form grid
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20));
//        grid.setStyle("-fx-background-color: " + SURFACE_DARK + ";");
//
//        // Create and populate fields
//        TextField makeField = createDialogTextField(car.getMake(), "Make");
//        TextField modelField = createDialogTextField(car.getModel(), "Model");
//        TextField yearField = createDialogTextField(String.valueOf(car.getYear()), "Year");
//        TextField priceField = createDialogTextField(String.format("%.2f", car.getPrice()), "Price");
//        TextField stockField = createDialogTextField(String.valueOf(car.getStock()), "Stock");
//        TextField vinField = createDialogTextField(car.getVin(), "VIN");
//
//        // Add fields to grid
//        addFormField(grid, "Make:", makeField, 0);
//        addFormField(grid, "Model:", modelField, 1);
//        addFormField(grid, "Year:", yearField, 2);
//        addFormField(grid, "Price:", priceField, 3);
//        addFormField(grid, "Stock:", stockField, 4);
//        addFormField(grid, "VIN:", vinField, 5);
//
//        // Add validation
//        addValidationListeners(makeField, modelField, yearField, 
//                              priceField, stockField, vinField);
//
//        dialogPane.setContent(grid);
//
//        // Enable/Disable save button based on validation
//        Node saveButton = dialogPane.lookupButton(saveButtonType);
//        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
//        saveButton.setStyle(
//                "-fx-background-color: " + PRIMARY_RED + ";" +
//                "-fx-text-fill: white;" +
//                "-fx-font-weight: bold;"
//            );
//
//        cancelButton.setStyle(
//                "-fx-background-color: transparent;" +
//                "-fx-border-color: #808080;" +
//                "-fx-border-width: 1px;" +
//                "-fx-text-fill: white;"
//            );
//        // Convert the result
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == saveButtonType) {
//                try {
//                    validateInputs(makeField.getText(), modelField.getText(),
//                                 yearField.getText(), priceField.getText(),
//                                 stockField.getText(), vinField.getText());
//
//                    Car updatedCar = new Car(
//                        car.getCarID(),
//                        makeField.getText().trim(),
//                        modelField.getText().trim(),
//                        Integer.parseInt(yearField.getText().trim()),
//                        Double.parseDouble(priceField.getText().trim()),
//                        Integer.parseInt(stockField.getText().trim()),
//                        vinField.getText().trim()
//                    );
//
//                    // Check VIN uniqueness
//                    if (!vinField.getText().equals(car.getVin()) && 
//                        CarsASU.isVinExists(vinField.getText())) {
//                        showError("Validation Error", "This VIN is already registered");
//                        return null;
//                    }
//
//                    return updatedCar;
//                } catch (ValidationException e) {
//                    showError("Validation Error", e.getMessage());
//                    return null;
//                } catch (Exception e) {
//                    showError("Error", "An unexpected error occurred");
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//            return null;
//        });
    
    private static void openEditDialog(Car car) {
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Edit Car");
        dialog.setHeaderText("Edit Car Details");
        
        // Style dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("custom-dialog");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Create form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.getStyleClass().add("form-grid");

        // Create and populate fields
        TextField makeField = createDialogTextField(car.getMake(), "Make");
        TextField modelField = createDialogTextField(car.getModel(), "Model");
        TextField yearField = createDialogTextField(String.valueOf(car.getYear()), "Year");
        TextField priceField = createDialogTextField(String.format("%.2f", car.getPrice()), "Price");
        TextField stockField = createDialogTextField(String.valueOf(car.getStock()), "Stock");
        TextField vinField = createDialogTextField(car.getVin(), "VIN");

        // Add fields to grid
        addFormField(grid, "Make:", makeField, 0);
        addFormField(grid, "Model:", modelField, 1);
        addFormField(grid, "Year:", yearField, 2);
        addFormField(grid, "Price:", priceField, 3);
        addFormField(grid, "Stock:", stockField, 4);
        addFormField(grid, "VIN:", vinField, 5);

        // Add validation
        addValidationListeners(makeField, modelField, yearField, 
                              priceField, stockField, vinField);

        dialogPane.setContent(grid);

        // Enable/Disable save button based on validation
        Node saveButton = dialogPane.lookupButton(saveButtonType);
        saveButton.getStyleClass().add("save-button");
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    validateInputs(makeField.getText(), modelField.getText(),
                                 yearField.getText(), priceField.getText(),
                                 stockField.getText(), vinField.getText());

                    Car updatedCar = new Car(
                        car.getCarID(),
                        makeField.getText().trim(),
                        modelField.getText().trim(),
                        Integer.parseInt(yearField.getText().trim()),
                        Double.parseDouble(priceField.getText().trim()),
                        Integer.parseInt(stockField.getText().trim()),
                        vinField.getText().trim()
                    );

                    // Check VIN uniqueness
                    if (!vinField.getText().equals(car.getVin()) && 
                        CarsASU.isVinExists(vinField.getText())) {
                        showError("Validation Error", "This VIN is already registered");
                        return null;
                    }

                    return updatedCar;
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                    return null;
                } catch (Exception e) {
                    showError("Error", "An unexpected error occurred");
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });

        // Handle the result
        dialog.showAndWait().ifPresent(updatedCar -> {
            try {
                if (CarsASU.updateCar(updatedCar)) {
                    int index = carData.indexOf(car);
                    if (index != -1) {
                        carData.set(index, updatedCar);
                        showSuccess("Success", "Car updated successfully");
                    }
                } else {
                    throw new Exception("Failed to update database");
                }
            } catch (Exception e) {
                showError("Database Error", "Failed to update car details");
                e.printStackTrace();
                refreshTable();
            }
        });
    }

        
//        // Handle the result
//        dialog.showAndWait().ifPresent(updatedCar -> {
//            try {
//                if (CarsASU.updateCar(updatedCar)) {
//                    int index = carData.indexOf(car);
//                    if (index != -1) {
//                        carData.set(index, updatedCar);
//                        showSuccess("Success", "Car updated successfully");
//                    }
//                } else {
//                    throw new Exception("Failed to update database");
//                }
//            } catch (Exception e) {
//                showError("Database Error", "Failed to update car details");
//                e.printStackTrace();
//                refreshTable();
//            }
//        });
//    }
    
    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private static void validateInputs(String make, String model, String year, 
                                     String price, String stock, String vin) 
                                     throws ValidationException {
        // Make validation
        if (make == null || make.trim().isEmpty()) {
            throw new ValidationException("Make cannot be empty");
        }
        if (make.length() > 50) {
            throw new ValidationException("Make cannot be longer than 50 characters");
        }

        // Model validation
        if (model == null || model.trim().isEmpty()) {
            throw new ValidationException("Model cannot be empty");
        }
        if (model.length() > 50) {
            throw new ValidationException("Model cannot be longer than 50 characters");
        }

        // Year validation
        try {
            int yearValue = Integer.parseInt(year);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (yearValue < 1900 || yearValue > currentYear + 1) {
                throw new ValidationException(
                    "Year must be between 1900 and " + (currentYear + 1)
                );
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Year must be a valid number");
        }

        // Price validation
        try {
            double priceValue = Double.parseDouble(price);
            if (priceValue < 0) {
                throw new ValidationException("Price cannot be negative");
            }
            if (priceValue > 1000000000) {
                throw new ValidationException("Price seems unreasonably high");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Price must be a valid number");
        }

        // Stock validation
        try {
            int stockValue = Integer.parseInt(stock);
            if (stockValue < 0) {
                throw new ValidationException("Stock cannot be negative");
            }
            if (stockValue > 1000) {
                throw new ValidationException("Stock value seems unreasonably high");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Stock must be a valid number");
        }

        // VIN validation
        if (vin == null || vin.trim().isEmpty()) {
            throw new ValidationException("VIN cannot be empty");
        }
        if (!vin.matches("\\d{17}")) {
            throw new ValidationException("VIN must be exactly 17 digits");
        }
    }

    private static void openAddDialog() {
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Add New Car");
        dialog.setHeaderText("Enter New Car Details");
        
        // Style dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("custom-dialog");
        
        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(addButtonType, cancelButtonType);

        // Create form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.getStyleClass().add("form-grid");

        // Create empty fields
        TextField makeField = createDialogTextField("", "Make");
        TextField modelField = createDialogTextField("", "Model");
        TextField yearField = createDialogTextField("", "Year");
        TextField priceField = createDialogTextField("", "Price");
        TextField stockField = createDialogTextField("", "Stock");
        TextField vinField = createDialogTextField("", "VIN");

        // Add fields to grid
        addFormField(grid, "Make:", makeField, 0);
        addFormField(grid, "Model:", modelField, 1);
        addFormField(grid, "Year:", yearField, 2);
        addFormField(grid, "Price:", priceField, 3);
        addFormField(grid, "Stock:", stockField, 4);
        addFormField(grid, "VIN:", vinField, 5);

        // Add validation
        addValidationListeners(makeField, modelField, yearField, 
                              priceField, stockField, vinField);

        dialogPane.setContent(grid);

        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    validateInputs(makeField.getText(), modelField.getText(),
                                 yearField.getText(), priceField.getText(),
                                 stockField.getText(), vinField.getText());

                    // Check VIN uniqueness
                    if (CarsASU.isVinExists(vinField.getText())) {
                        showError("Validation Error", "This VIN is already registered");
                        return null;
                    }

                    return new Car(
                        0, // ID will be set by database
                        makeField.getText().trim(),
                        modelField.getText().trim(),
                        Integer.parseInt(yearField.getText().trim()),
                        Double.parseDouble(priceField.getText().trim()),
                        Integer.parseInt(stockField.getText().trim()),
                        vinField.getText().trim()
                    );
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                    return null;
                } catch (Exception e) {
                    showError("Error", "An unexpected error occurred");
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });

        // Handle the result
        dialog.showAndWait().ifPresent(newCar -> {
            try {
                addCarToTable(newCar);
            } catch (Exception e) {
                showError("Database Error", "Failed to add car to database");
                e.printStackTrace();
                refreshTable();
            }
        });
    }
    
    private static TextField createDialogTextField(String initialValue, String promptText) {
        TextField field = new TextField(initialValue);
        field.setPromptText(promptText);
        field.setStyle(
                "-fx-background-color: #3C3C3C;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #808080;" +
                "-fx-padding: 8;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 4;" +
                "-fx-min-width: 200px;"
            );
        
        // Add specific input restrictions based on field type
        switch (promptText) {
            case "Year":
                field.textProperty().addListener((obs, old, newVal) -> {
                    if (!newVal.matches("\\d{0,4}")) {
                        field.setText(old);
                    }
                });
                break;
            case "Price":
                field.textProperty().addListener((obs, old, newVal) -> {
                    if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                        field.setText(old);
                    }
                });
                break;
            case "Stock":
                field.textProperty().addListener((obs, old, newVal) -> {
                    if (!newVal.matches("\\d*")) {
                        field.setText(old);
                    }
                });
                break;
            case "VIN":
                field.textProperty().addListener((obs, old, newVal) -> {
                    if (!newVal.matches("\\d{0,17}")) {
                        field.setText(old);
                    }
                });
                break;
        }
        
        return field;
    }

    private static void addFormField(GridPane grid, String labelText, TextField field, int row) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white;");
        grid.add(label, 0, row);
        grid.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private static void addValidationListeners(TextField makeField, TextField modelField,
                                             TextField yearField, TextField priceField,
                                             TextField stockField, TextField vinField) {
        // Make validation
        makeField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal.trim().isEmpty()) {
                setFieldError(makeField, "Make cannot be empty");
            } else if (newVal.length() > 50) {
                setFieldError(makeField, "Make is too long");
            } else {
                setFieldValid(makeField);
            }
        });

        // Model validation
        modelField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal.trim().isEmpty()) {
                setFieldError(modelField, "Model cannot be empty");
            } else if (newVal.length() > 50) {
                setFieldError(modelField, "Model is too long");
            } else {
                setFieldValid(modelField);
            }
        });

        // Year validation
        yearField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d+")) {
                setFieldError(yearField, "Year must be a number");
                return;
            }
            try {
                int year = Integer.parseInt(newVal);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (year < 1900 || year > currentYear + 1) {
                    setFieldError(yearField, "Invalid year range");
                } else {
                    setFieldValid(yearField);
                }
            } catch (NumberFormatException e) {
                setFieldError(yearField, "Invalid year");
            }
        });

        // Price validation
        priceField.textProperty().addListener((obs, old, newVal) -> {
            try {
                if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                    setFieldError(priceField, "Invalid price format");
                    return;
                }
                double price = Double.parseDouble(newVal);
                if (price < 0) {
                    setFieldError(priceField, "Price cannot be negative");
                } else if (price > 1000000000) {
                    setFieldError(priceField, "Price too high");
                } else {
                    setFieldValid(priceField);
                }
            } catch (NumberFormatException e) {
                setFieldError(priceField, "Invalid price");
            }
        });

        // Stock validation
        stockField.textProperty().addListener((obs, old, newVal) -> {
            try {
                if (!newVal.matches("\\d+")) {
                    setFieldError(stockField, "Stock must be a number");
                    return;
                }
                int stock = Integer.parseInt(newVal);
                if (stock < 0) {
                    setFieldError(stockField, "Stock cannot be negative");
                } else if (stock > 1000) {
                    setFieldError(stockField, "Stock too high");
                } else {
                    setFieldValid(stockField);
                }
            } catch (NumberFormatException e) {
                setFieldError(stockField, "Invalid stock value");
            }
        });

        // VIN validation
        vinField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d{0,17}")) {
                setFieldError(vinField, "VIN must be 17 digits");
            } else if (newVal.length() != 17) {
                setFieldError(vinField, "VIN must be exactly 17 digits");
            } else {
                setFieldValid(vinField);
            }
        });
    }

    private static void setFieldError(TextField field, String message) {
        field.getStyleClass().remove("valid-field");
        field.getStyleClass().add("error-field");
        field.setTooltip(new Tooltip(message));
    }

    private static void setFieldValid(TextField field) {
        field.getStyleClass().remove("error-field");
        field.getStyleClass().add("valid-field");
        field.setTooltip(null);
    }

    private static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        styleAlert(alert, title, content, PRIMARY_RED);
    }

    private static void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        styleAlert(alert, title, content, "#4CAF50");
    }

    private static void styleAlert(Alert alert, String title, String content, String color) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("custom-alert");
        dialogPane.setStyle(
            "-fx-background-color: " + SURFACE_DARK + ";" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2px;"
        );
        
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Style the button
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );
        
        // Style the content
        dialogPane.lookup(".content.label").setStyle(
            "-fx-text-fill: white; -fx-font-size: 14px;"
        );
        
        alert.showAndWait();
    }
    
    private static void addCarToTable(Car car) {
        try {
            // Add to database first
            int newId = CarsASU.addCar(car);
            
            if (newId != -1) {
                // Set the generated ID from database
                car.setCarID(newId);
                
                // Add to observable list
                carData.add(car);
                
                // Refresh filters to show new data
                applyFilters();
                
                // Show success message
                showSuccess("Success", "Car added successfully!");
                
                // Select the new car in the table
                tableView.getSelectionModel().select(car);
                tableView.scrollTo(car);
            } else {
                showError("Error", "Failed to add car to database.");
            }
        } catch (Exception e) {
            showError("Error", 
                "An unexpected error occurred while adding the car: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Refresh the table to ensure consistency
            refreshTable();
        }
    }
}