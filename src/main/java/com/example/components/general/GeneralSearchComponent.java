package com.example.components.general;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.function.Consumer;

public class GeneralSearchComponent extends VBox {
    private List<CustomSearchBox> searchBoxes;
    private Consumer<List<CustomSearchBox>> filterAction;

    public GeneralSearchComponent(List<CustomSearchBox> searchBoxes, Consumer<List<CustomSearchBox>> filterAction) {
        this.searchBoxes = searchBoxes;
        this.filterAction = filterAction;
        initializeSearchBoxes();
    }

    private void initializeSearchBoxes() {
        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setPadding(new Insets(20));
        searchBoxes.forEach(searchBox -> filters.getChildren().add(searchBox));
        Runnable filter = () -> filterAction.accept(searchBoxes);
        searchBoxes.forEach(searchBox -> {
            searchBox.getSearchField().textProperty().addListener((observable, oldValue, newValue) -> filter.run());
        });
        this.getChildren().add(filters);
    }
}
