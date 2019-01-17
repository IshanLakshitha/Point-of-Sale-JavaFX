package lk.ijse.fx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lk.ijse.fx.util.*;


import java.io.IOException;

public class MainPageController {

    @FXML
    private Button btnManageCustomers;


    public void clickManageCustomers(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/ManageCustomersPage.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) btnManageCustomers.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Manage Customers");
    }

    public void ClickManageitems(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/ManageItemPage2.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) btnManageCustomers.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Manage Items");
    }

    public void ClickPlaceOrder(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/PlaceOrderPage.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) btnManageCustomers.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Place Order");
    }

    public void clickViewOrders(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/ShowOrdersPage.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) btnManageCustomers.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Show Orders");
    }

    public void clickLogOut(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/LoginPage.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) btnManageCustomers.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Login Page");

        ManageUser.loginUser = null; // Clear Log in user

    }

    public void clickOnSettingButton(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/SystemSettingPage.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) btnManageCustomers.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Settings");


    }
}
