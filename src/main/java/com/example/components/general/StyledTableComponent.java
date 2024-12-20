package com.example.components.general;

import com.example.components.Theme;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;

public abstract class StyledTableComponent<T> extends VBox {

    protected TableView<T> table;

    public StyledTableComponent(String title) {
        setSpacing(15);
        setPadding(new Insets(20));
        setBackground(new Background(new BackgroundFill(Theme.WHITE_COLOR, new CornerRadii(15), Insets.EMPTY)));
        setEffect(Theme.createShadow());
        setMinHeight(620);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getStyleClass().add("table-container");
        table.setMinHeight(550);

        CustomButton addButton = new CustomButton("Insert", "fa-plus");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox headerBox = new HBox( spacer, addButton);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        addButton.setOnAction(event -> onInsert());

        getChildren().addAll(headerBox, table);
    }

    protected <V> TableColumn<T, V> createColumn(String title, String property, int minWidth) {
        TableColumn<T, V> column = new TableColumn<>(title);
        column.setMinWidth(minWidth);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    protected TableColumn<T, Void> createActionColumn() {
        TableColumn<T, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setMinWidth(200);

        Callback<TableColumn<T, Void>, TableCell<T, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<T, Void> call(final TableColumn<T, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("edit");
                    private final Button detailsButton = new Button("Report");

                    {

                        editButton.setOnAction(event -> {
                            T item = getTableView().getItems().get(getIndex());
                            onEdit(item);
                        });

                        detailsButton.setOnAction(event -> {
                            T item = getTableView().getItems().get(getIndex());
                            onDetails(item);
                        });

                        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        detailsButton.setStyle("-fx-background-color: #616161; -fx-text-fill: white;");
                        editButton.setMinWidth(50);
                        detailsButton.setMinWidth(50);
                        editButton.setCursor(Cursor.HAND);
                        detailsButton.setCursor(Cursor.HAND);
                    }

                    private final HBox buttonsBox = new HBox(10, editButton, detailsButton);

                    {
                        buttonsBox.setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttonsBox);
                        }
                    }
                };
            }
        };

        actionColumn.setCellFactory(cellFactory);
        return actionColumn;
    }

    public void setData(ObservableList<T> dataList) {
        table.setItems(dataList);
    }

    public TableView<T> getTableView() {
        return table;
    }

    protected abstract void initializeColumns();

    protected abstract void onEdit(T item);

    protected abstract void onDetails(T item);

    protected abstract void onInsert();
}
