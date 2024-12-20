package com.example.views;

import java.util.List;
import java.util.function.Consumer;

import com.example.components.general.CustomButton;
import com.example.components.general.CustomSearchBox;
import com.example.components.general.GeneralSearchComponent;
import com.example.controller.PaymentController;
import com.example.model.Payment;
import com.example.views.Tables.PaymentTableComponent;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PaymentsView extends ScrollPane {
    private PaymentController paymentController;
    private PaymentTableComponent paymentTableComponent;
    private GeneralSearchComponent paymentSearchComponent;

    public PaymentsView() {
        this.paymentController = new PaymentController();
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

        paymentTableComponent = new PaymentTableComponent(paymentController);

        // Load all payments
        ObservableList<Payment> paymentList = paymentController.loadAllPayments();
        paymentTableComponent.setData(paymentList);

        List<CustomSearchBox> searchBoxes = paymentController.getSearchBoxes();

        Consumer<List<CustomSearchBox>> filterAction = searchBoxesList -> paymentController
                .filterPayments(searchBoxesList);

        paymentSearchComponent = new GeneralSearchComponent(searchBoxes, filterAction);

        export.setOnAction(event -> {
            paymentController.handleExport();
        });

        contentBox.getChildren().addAll(paymentSearchComponent, paymentTableComponent);
    }
}
