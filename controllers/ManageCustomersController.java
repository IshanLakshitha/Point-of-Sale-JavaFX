package lk.ijse.fx.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lk.ijse.fx.model.Customer;
import lk.ijse.fx.util.ManageCustomers;
import lk.ijse.fx.util.ManageItems;
import lk.ijse.fx.view.util.CustomerTM;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ManageCustomersController {

    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private TextField txtName;

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtAddress;

    @FXML
    private TableView<CustomerTM> tblCustomerTable;

    public void initialize() {

        tblCustomerTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomerTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomerTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        txtID.setEditable(false);
        txtName.setEditable(false);
        txtAddress.setEditable(false);

        ArrayList<Customer> customersDB = ManageCustomers.getCustomers();
        ObservableList<Customer> customers = FXCollections.observableArrayList(customersDB);
        ObservableList<CustomerTM> tblItems = FXCollections.observableArrayList();
        for (Customer customer : customers) {
            tblItems.add(new CustomerTM(customer.getId(), customer.getName(), customer.getAddress()));
        }
        tblCustomerTable.setItems(tblItems);

        tblCustomerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM selectedCustomer) {

                if (selectedCustomer == null) {
                    // Clear Selection
                    return;
                }

                txtID.setText(selectedCustomer.getId());
                txtName.setText(selectedCustomer.getName());
                txtAddress.setText(selectedCustomer.getAddress());

                txtID.setEditable(false);

                btnSave.setDisable(false);
                btnDelete.setDisable(false);

            }
        });
    }

    @FXML
    void ClickBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/MainPage.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) txtID.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Main Page");
    }


    @FXML
    void ClickDelete(ActionEvent event) {

        Alert confirmMsg = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete this customer?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = confirmMsg.showAndWait();

        if (buttonType.get() == ButtonType.YES) {
            String  selectedCustomer = tblCustomerTable.getSelectionModel().getSelectedItem().getId();
            ManageCustomers.deleteCustomer(selectedCustomer);
            tblCustomerTable.getItems().remove(tblCustomerTable.getSelectionModel().getSelectedItem());
           // ManageCustomers.deleteCustomer(selectedCustomer);

            reset();
        }
    }

    @FXML
    void ClickNewCustomer(ActionEvent event) {
        reset();
    }

    @FXML
    void ClickSave(ActionEvent event) {

        if (txtID.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer ID is Empty", ButtonType.OK).showAndWait();
            txtID.requestFocus();
            return;
        } else if (txtName.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer Name is Empty", ButtonType.OK).showAndWait();
            txtName.requestFocus();
            return;
        } else if (txtAddress.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Customer Address is Empty", ButtonType.OK).showAndWait();
            txtAddress.requestFocus();
            return;
        }

        if (tblCustomerTable.getSelectionModel().isEmpty()) {
            ObservableList<CustomerTM> items = tblCustomerTable.getItems();
            for (CustomerTM customerTM : items) {
                if (customerTM.getId().equals(txtID.getText())) {
                    new Alert(Alert.AlertType.ERROR, "Duplicate Customer IDs are not allowed", ButtonType.OK).showAndWait();
                    txtID.requestFocus();
                    return;
                }
            }

            CustomerTM customerTM = new CustomerTM(txtID.getText(), txtName.getText(), txtName.getText());
            tblCustomerTable.getItems().add(customerTM);
            Customer customer = new Customer(txtID.getText(), txtName.getText(), txtAddress.getText());
            ManageCustomers.createCustomer(customer); //*

            new Alert(Alert.AlertType.CONFIRMATION, "Customer has been saved successfully", ButtonType.OK).showAndWait();
            txtID.clear();
            txtName.clear();
            txtAddress.clear();
            txtID.requestFocus();
            tblCustomerTable.scrollTo(customerTM);

        }
        else{
            // Update

            CustomerTM selectedItem = tblCustomerTable.getSelectionModel().getSelectedItem();
            selectedItem.setName(txtName.getText());
            selectedItem.setAddress(txtAddress.getText());
            tblCustomerTable.refresh();

            int selectedRow = tblCustomerTable.getSelectionModel().getSelectedIndex();

            ManageCustomers.updateCustomer(selectedRow,new Customer(txtID.getText(),
                    txtName.getText(),
                    txtAddress.getText()));

            new Alert(Alert.AlertType.INFORMATION,"Customer has been updated successfully", ButtonType.OK).showAndWait();
        }
        reset();
    }

    private void reset() {
        txtID.clear();
        txtName.clear();
        txtAddress.clear();
        txtID.requestFocus();
        txtID.setEditable(true);
        txtName.setEditable(true);
        txtAddress.setEditable(true);
        btnSave.setDisable(false);
        btnDelete.setDisable(true);
        tblCustomerTable.getSelectionModel().clearSelection();
    }


    public void clickPrint(ActionEvent actionEvent) throws JRException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
        File file = new File("table/customer_report.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), connection);

        JasperViewer.viewReport(jasperPrint,false);
    }
}
