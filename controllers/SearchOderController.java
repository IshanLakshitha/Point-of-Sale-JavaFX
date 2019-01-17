package lk.ijse.fx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.fx.model.Order;
import lk.ijse.fx.model.OrderDetail;
import lk.ijse.fx.util.ManageCustomers;
import lk.ijse.fx.util.ManageOrders;
import lk.ijse.fx.view.util.OrderTM;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchOderController {

    @FXML
    private TableView<OrderTM> tblOrderDetails;
    @FXML
    private TextField txtSearch;
    @FXML
    private ObservableList<OrderTM> olOrders;

    public void initialize() {
        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderId"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("customerId"));

        ArrayList<Order> ordersDB = ManageOrders.getOrders();
        olOrders = FXCollections.observableArrayList();

        for (Order order : ordersDB) {
            olOrders.add(new OrderTM(order.getId(), order.getDate(), order.getCustomerId(),
                    ManageCustomers.findCustomer(order.getCustomerId()).getName(),
                    getOrderTotal(order.getOrderDetails())));
        }

        tblOrderDetails.setItems(olOrders);
    }

    private double getOrderTotal(ArrayList<OrderDetail> orderDetails) {
        double total = 0.0;
        for (OrderDetail orderDetail : orderDetails) {
            total += orderDetail.getQty() * orderDetail.getUnitPrice();
        }
        return total;
    }


    public void search(KeyEvent keyEvent) {
        ObservableList<OrderTM> tempList = FXCollections.observableArrayList();
        for (OrderTM olOrder : olOrders) {
            if (olOrder.getOrderId().startsWith(txtSearch.getText())) {
                tempList.add(olOrder);
            }
        }
        tblOrderDetails.setItems(tempList);
    }

    public void clickBack(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/MainPage.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) txtSearch.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void ClickRow(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {
            OrderTM selectedItem = tblOrderDetails.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/lk/ijse/fx/view/ShowOneOrder.fxml"));
            Parent parent = (Parent) fxmlLoader.load();
            ShowOneOrder controller = fxmlLoader.getController();
            controller.setInitData(selectedItem.getOrderId(), selectedItem.getTotal());
            Scene scene = new Scene(parent);
            ((Stage) tblOrderDetails.getScene().getWindow()).setScene(scene);
        }
    }

    public void clickReport(ActionEvent actionEvent) throws SQLException, JRException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
        File file = new File("table/viewAllOrders.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), connection);
        JasperViewer.viewReport(jasperPrint,false);


    }
}

