package carShopCompany;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Field;
import java.util.List;

public class TableViewBuilder<T> {

    /**
     * Builds a TableView for the given data list and class type.
     * @param data List of objects to populate the TableView.
     * @param clazz The class of the data objects.
     * @return A TableView populated with the data.
     */
    public TableView<T> buildTable(List<T> data, Class<T> clazz) {
        TableView<T> tableView = new TableView<>();
        ObservableList<T> observableData = FXCollections.observableArrayList(data);

        // Create columns based on the fields of the class
        for (Field field : clazz.getDeclaredFields()) {
            TableColumn<T, Object> column = new TableColumn<>(capitalize(field.getName()));
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tableView.getColumns().add(column);
        }

        // Set items for the TableView
        tableView.setItems(observableData);

        // Apply styles using setStyle
        applyTableStyles(tableView);

        return tableView;
    }

    /**
     * Capitalizes the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Applies styling to the TableView directly in the code.
     */
    private void applyTableStyles(TableView<T> tableView) {
        // Apply table background color, text color, and border color directly
        tableView.setStyle("-fx-background-color: black;" +
                           "-fx-text-fill: white;" +
                           "-fx-font-size: 14px;" +
                           "-fx-font-family: Arial;" +
                           "-fx-border-color: red;" +
                           "-fx-border-width: 0 0 2px 0;");  // Only horizontal borders

        // Style the table rows
        tableView.setRowFactory(tv -> {
            javafx.scene.control.TableRow<T> row = new javafx.scene.control.TableRow<>();
            row.setStyle("-fx-border-color: red;" +
                         "-fx-border-width: 0 0 1px 0;"); // Only horizontal borders for rows
            return row;
        });

        // Style the header
        tableView.lookup(".column-header").setStyle("-fx-background-color: #444444;" +
                                                     "-fx-text-fill: white;" +
                                                     "-fx-border-color: red;" +
                                                     "-fx-border-width: 0 0 2px 0;");  // Horizontal border for header
    }
}
