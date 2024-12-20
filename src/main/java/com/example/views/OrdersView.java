package com.example.views;

import java.util.List;
import java.util.function.Consumer;

import com.example.components.general.CustomButton;
import com.example.components.general.CustomSearchBox;
import com.example.components.general.GeneralSearchComponent;
import com.example.controller.OrderController;
import com.example.model.Order;
import com.example.views.Tables.OrderTableComponent;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class OrdersView extends ScrollPane {
    private OrderController orderController;
    private OrderTableComponent orderTableComponent;
    private GeneralSearchComponent orderSearchComponent;

    public OrdersView() {
        this.orderController = new OrderController();
        VBox contentBox = new VBox();
        contentBox.setPadding(new Insets(20));
        contentBox.setSpacing(20);
        contentBox.getStyleClass().add("primary-bg");
        CustomButton export = new CustomButton("Export", "export.png");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        setContent(contentBox);
        setFitToWidth(true);
        setPannable(true);
        getStyleClass().add("primary-bg");
        setPadding(new Insets(20));

        orderTableComponent = new OrderTableComponent(orderController);

        // Load all orders
        ObservableList<Order> orderList = orderController.loadAllOrders();
        orderTableComponent.setData(orderList);

        List<CustomSearchBox> searchBoxes = orderController.getSearchBoxes();

        Consumer<List<CustomSearchBox>> filterAction = searchBoxesList -> orderController
                .filterOrders(searchBoxesList);

        orderSearchComponent = new GeneralSearchComponent(searchBoxes, filterAction);

        export.setOnAction(event -> {
            orderController.handleExport();
        });

        contentBox.getChildren().addAll(orderSearchComponent, orderTableComponent);
    }
}
