package com.example.AppStructure.aside.menu;

import com.example.AppStructure.AppStage;
import com.example.AppStructure.LoginStage;
import com.example.AppStructure.main.Main;
import com.example.session.SessionManager;
import com.example.views.CarsView;
import com.example.views.CustomersView;
import com.example.views.EmployeesView;
import com.example.views.OrdersView;
import com.example.views.PaymentsView;
import com.example.views.ReportsView;
import com.example.views.ServicesView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class Menu extends VBox {

    public Menu() {
        setSpacing(20);
        setAlignment(Pos.TOP_LEFT);

        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("car.png", "Cars", false));
        items.add(new MenuItem("customer.png", "Customers", false));
        items.add(new MenuItem("emp.png", "Employees", false));
        items.add(new MenuItem("order.png", "Orders", false));
        items.add(new MenuItem("payment.png", "Payments", false));
        items.add(new MenuItem("service.png", "Services", false));
        items.add(new MenuItem("service.png", "Reports", false));

        items.forEach(item -> {
            item.setOnMouseClicked(e -> {
                items.forEach(i -> i.setSelected(false));
                item.setSelected(true);

                changeView(item.getText());
            });
            getChildren().add(item);
        });
    }

    private void changeView(String menuItemText) {
        Main mainInstance = Main.getInstance();
        if (mainInstance != null) {
            Node view;
            switch (menuItemText.toLowerCase()) {
                case "cars":
                    view = new CarsView();
                    break;
                case "customers":
                    view = new CustomersView();
                    break;
                case "employees":
                    view = new EmployeesView();
                    break;
                case "orders":
                    view = new OrdersView();
                    break;
                case "payments":
                    view = new PaymentsView();
                    break;
                case "services":
                    view = new ServicesView();
                    break;
                case "reports":
                    view = new ReportsView();
                    break;
                // case "log out":
                // // view = new ReportsView();
                // break;
                default:
                    view = new Label("View not implemented: " + menuItemText);
                    break;
            }
            mainInstance.changeView(view);
        }
    }
}
