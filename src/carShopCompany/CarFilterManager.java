package carShopCompany;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import org.controlsfx.control.RangeSlider;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CarFilterManager {
    private VBox filterContainer;
    private TableView<Car> carTableView;
    private ObservableList<Car> originalCarData;
    private FilteredList<Car> filteredCarData;

    // Filter Components
    private TextField idFilter;
    private ComboBox<String> makesComboBox;
    private ComboBox<String> modelsComboBox;
    private RangeSlider yearRangeSlider;
    private RangeSlider priceRangeSlider;
    private Spinner<Integer> stockSpinner;
    private TextField vinFilter;
    private Label resultCountLabel;

    public VBox createAdvancedFilterPane(TableView<Car> tableView) {
        this.carTableView = tableView;
        this.originalCarData = FXCollections.observableArrayList(tableView.getItems());
        this.filteredCarData = new FilteredList<>(originalCarData);
        tableView.setItems(filteredCarData);

        filterContainer = new VBox(15);
        filterContainer.setPadding(new Insets(20));
        filterContainer.setStyle("-fx-background-color: #2A2A2A; -fx-border-color: #404040; -fx-border-width: 0 0 1 0;");

        // Filter title with count
        HBox headerBox = createHeaderSection();
        
        // Create filter sections in tabs for better organization
        TabPane filterTabs = createFilterTabs();
        
        // Action buttons
        HBox actionRow = createActionRow();

        filterContainer.getChildren().addAll(headerBox, filterTabs, actionRow);
        setupFilterLogic();

        return filterContainer;
    }

    private HBox createHeaderSection() {
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label filterTitle = new Label("Advanced Filters");
        filterTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        filterTitle.setTextFill(Color.WHITE);

        resultCountLabel = new Label("Showing all cars");
        resultCountLabel.setTextFill(Color.LIGHTGRAY);
        resultCountLabel.setFont(Font.font("Arial", 12));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(filterTitle, spacer, resultCountLabel);
        return headerBox;
    }

    private TabPane createFilterTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");

        // Basic Filters Tab
        Tab basicTab = new Tab("Basic Filters", createBasicFiltersGrid());
        
        // Advanced Filters Tab
        Tab advancedTab = new Tab("Advanced Filters", createAdvancedFiltersGrid());

        tabPane.getTabs().addAll(basicTab, advancedTab);
        return tabPane;
    }

    private GridPane createBasicFiltersGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.setStyle("-fx-background-color: #2A2A2A;");

        // ID Filter
        idFilter = createStyledTextField("Enter ID...");
        addFilterRow(grid, "Car ID:", idFilter, 0);

        // Make Filter
        makesComboBox = new ComboBox<>();
        makesComboBox.setPromptText("Select Make");
        makesComboBox.setMaxWidth(Double.MAX_VALUE);
        setupMakesComboBox();
        addFilterRow(grid, "Make:", makesComboBox, 1);

        // Model Filter
        modelsComboBox = new ComboBox<>();
        modelsComboBox.setPromptText("Select Model");
        modelsComboBox.setMaxWidth(Double.MAX_VALUE);
        addFilterRow(grid, "Model:", modelsComboBox, 2);

        return grid;
    }

    private GridPane createAdvancedFiltersGrid() {
    	  GridPane grid = new GridPane();
    	    grid.setHgap(15);
    	    grid.setVgap(10);
    	    grid.setPadding(new Insets(15));
    	    grid.setStyle("-fx-background-color: #2A2A2A;");

    	    // Year Range Slider
    	    int minYearDB = originalCarData.stream()
    	        .mapToInt(Car::getYear)
    	        .min()
    	        .orElse(2000);
    	    int maxYearDB = originalCarData.stream()
    	        .mapToInt(Car::getYear)
    	        .max()
    	        .orElse(2024);

    	    VBox yearBox = new VBox(5);
    	    Label yearRangeLabel = new Label(String.format("Selected: %d - %d", minYearDB, maxYearDB));
    	    yearRangeLabel.setTextFill(Color.WHITE);

    	    yearRangeSlider = new RangeSlider(minYearDB, maxYearDB, minYearDB, maxYearDB);
    	    yearRangeSlider.setShowTickLabels(true);
    	    yearRangeSlider.setShowTickMarks(true);
    	    yearRangeSlider.setBlockIncrement(1);
    	    yearRangeSlider.setMajorTickUnit((maxYearDB - minYearDB) / 5);
    	    yearRangeSlider.setPrefWidth(300);

    	    // Update label when sliders move
    	    yearRangeSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> {
    	        yearRangeLabel.setText(String.format("Selected: %d - %d", 
    	            newVal.intValue(), (int)yearRangeSlider.getHighValue()));
    	        applyFilters();
    	    });
    	    yearRangeSlider.highValueProperty().addListener((obs, oldVal, newVal) -> {
    	        yearRangeLabel.setText(String.format("Selected: %d - %d", 
    	            (int)yearRangeSlider.getLowValue(), newVal.intValue()));
    	        applyFilters();
    	    });

    	    yearBox.getChildren().addAll(yearRangeLabel, yearRangeSlider);
    	    addFilterRow(grid, "Year Range:", yearBox, 0);

    	    // Price Range Slider
    	    double minPriceDB = originalCarData.stream()
    	        .mapToDouble(Car::getPrice)
    	        .min()
    	        .orElse(0);
    	    double maxPriceDB = originalCarData.stream()
    	        .mapToDouble(Car::getPrice)
    	        .max()
    	        .orElse(200000);

    	    VBox priceBox = new VBox(5);
    	    Label priceRangeLabel = new Label(String.format("Selected: $%.2f - $%.2f", minPriceDB, maxPriceDB));
    	    priceRangeLabel.setTextFill(Color.WHITE);

    	    priceRangeSlider = new RangeSlider(minPriceDB, maxPriceDB, minPriceDB, maxPriceDB);
    	    priceRangeSlider.setShowTickLabels(true);
    	    priceRangeSlider.setShowTickMarks(true);
    	    priceRangeSlider.setMajorTickUnit((maxPriceDB - minPriceDB) / 5);
    	    priceRangeSlider.setPrefWidth(300);

    	    // Update label when sliders move
    	    priceRangeSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> {
    	        priceRangeLabel.setText(String.format("Selected: $%.2f - $%.2f", 
    	            newVal.doubleValue(), priceRangeSlider.getHighValue()));
    	        applyFilters();
    	    });
    	    priceRangeSlider.highValueProperty().addListener((obs, oldVal, newVal) -> {
    	        priceRangeLabel.setText(String.format("Selected: $%.2f - $%.2f", 
    	            priceRangeSlider.getLowValue(), newVal.doubleValue()));
    	        applyFilters();
    	    });

    	    priceBox.getChildren().addAll(priceRangeLabel, priceRangeSlider);
    	    addFilterRow(grid, "Price Range:", priceBox, 1);
        // Rest of the components remain the same
        stockSpinner = new Spinner<>(0, 100, 0);
        stockSpinner.setEditable(true);
        addFilterRow(grid, "Min Stock:", stockSpinner, 2);

        vinFilter = createStyledTextField("Enter VIN...");
        addFilterRow(grid, "VIN:", vinFilter, 3);

        return grid;
    }
    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private Slider createRangeSlider(String name, double min, double max) {
        Slider slider = new Slider(min, max, min);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 5);
        slider.setBlockIncrement((max - min) / 10);
        slider.setMaxWidth(Double.MAX_VALUE);
        return slider;
    }

    private void addFilterRow(GridPane grid, String labelText, Node control, int row) {
        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        grid.add(label, 0, row);
        grid.add(control, 1, row);
        GridPane.setHgrow(control, Priority.ALWAYS);
    }

    private HBox createActionRow() {
        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(10, 0, 0, 0));

        Button applyButton = createStyledButton("Apply Filters", "#4CAF50");
        Button resetButton = createStyledButton("Reset Filters", "#F44336");

        applyButton.setOnAction(e -> applyFilters());
        resetButton.setOnAction(e -> resetFilters());

        actionRow.getChildren().addAll(applyButton, resetButton);
        return actionRow;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 15 8 15;" +
            "-fx-background-radius: 5;"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: derive(" + color + ", -20%);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 15 8 15;" +
                "-fx-background-radius: 5;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 15 8 15;" +
                "-fx-background-radius: 5;"
            )
        );
        
        return button;
    }

    private void setupMakesComboBox() {
        // Populate Makes Dropdown with "All Makes" option
        Set<String> makes = originalCarData.stream()
            .map(Car::getMake)
            .collect(Collectors.toSet());
        
        ObservableList<String> makesList = FXCollections.observableArrayList();
        makesList.add("All Makes"); // Add "All Makes" as first option
        makesList.addAll(makes);
        makesComboBox.setItems(makesList);
        makesComboBox.setValue("All Makes"); // Set as default selection

        // Update models when make is selected
        makesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("All Makes")) {
                Set<String> models = originalCarData.stream()
                    .filter(car -> car.getMake().equals(newVal))
                    .map(Car::getModel)
                    .collect(Collectors.toSet());
                modelsComboBox.setItems(FXCollections.observableArrayList(models));
            } else {
                modelsComboBox.getItems().clear();
                modelsComboBox.setPromptText("Select Model");
            }
        });
    }

    private void setupFilterLogic() {
        // Add listeners to all filter components
        idFilter.textProperty().addListener((obs, old, newVal) -> applyFilters());
        makesComboBox.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        modelsComboBox.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        yearRangeSlider.setLowValue(yearRangeSlider.getMin());
        yearRangeSlider.setHighValue(yearRangeSlider.getMax());
        priceRangeSlider.setLowValue(priceRangeSlider.getMin());
        priceRangeSlider.setHighValue(priceRangeSlider.getMax());
        stockSpinner.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        vinFilter.textProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void applyFilters() {
        filteredCarData.setPredicate(car -> {
            boolean idMatch = idFilter.getText().isEmpty() || 
                String.valueOf(car.getCarID()).contains(idFilter.getText());

            boolean makeMatch = makesComboBox.getValue() == null || 
                    makesComboBox.getValue().equals("All Makes") ||
                    car.getMake().equals(makesComboBox.getValue());

            boolean modelMatch = modelsComboBox.getValue() == null || 
                car.getModel().equals(modelsComboBox.getValue());

            boolean yearMatch = car.getYear() >= yearRangeSlider.getLowValue() && 
                    car.getYear() <= yearRangeSlider.getHighValue();

            
            boolean priceMatch = car.getPrice() >= priceRangeSlider.getLowValue() && 
                    car.getPrice() <= priceRangeSlider.getHighValue();
            boolean stockMatch = car.getStock() >= stockSpinner.getValue();

            boolean vinMatch = vinFilter.getText().isEmpty() || 
                car.getVin().toLowerCase().contains(vinFilter.getText().toLowerCase());

            return idMatch && makeMatch && modelMatch && yearMatch && 
                   priceMatch && stockMatch && vinMatch;
        });

        updateResultCount();
    }

    private void updateResultCount() {
        int count = filteredCarData.size();
        int total = originalCarData.size();
        resultCountLabel.setText(
            String.format("Showing %d of %d cars", count, total)
        );
    }

    private void resetFilters() {
        // Reset all filter components
        idFilter.clear();
        makesComboBox.setValue("All Makes");
        modelsComboBox.setValue(null);
        yearRangeSlider.setLowValue(yearRangeSlider.getMin());
        yearRangeSlider.setHighValue(yearRangeSlider.getMax());
        priceRangeSlider.setLowValue(priceRangeSlider.getMin());
        priceRangeSlider.setHighValue(priceRangeSlider.getMax());
        stockSpinner.getValueFactory().setValue(0);
        vinFilter.clear();

        // Clear filters
        filteredCarData.setPredicate(p -> true);
        updateResultCount();
    }

    // Add this method to get filtered data
    public FilteredList<Car> getFilteredData() {
        return filteredCarData;
    }
}