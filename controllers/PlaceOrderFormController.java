package lk.ijse.fx.controllers;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import lk.ijse.fx.model.Item;
import lk.ijse.fx.model.Order;
import lk.ijse.fx.model.OrderDetail;
import lk.ijse.fx.util.ManageCustomers;
import lk.ijse.fx.util.ManageItems;
import lk.ijse.fx.util.ManageOrders;
import lk.ijse.fx.view.util.OrderDetailTM;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class PlaceOrderFormController {

    @FXML
    private DatePicker DatePickerBox;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnPlaceOder;
    @FXML
    private TableView<OrderDetailTM> tblOrderDetails;
    @FXML
    private Button btnRemove;
    @FXML
    private TextField txtCustomerID;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField txtOrderID;
    @FXML
    private TextField txtDate;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private TextField txtItemCode;
    @FXML
    private TextField txtItemDescription;
    @FXML
    private TextField txtUnitPrice;
    @FXML
    private TextField txtQuantityInHand;
    @FXML
    private TextField txtOrderQuantity;

    private ObservableList<Item> tempItemsDB = FXCollections.observableArrayList();

    public void initialize() {

        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        ArrayList<Item> itemsDB = ManageItems.getItems();
        for (Item item : itemsDB) {
            tempItemsDB.add(new Item(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));
        }

        txtOrderID.setEditable(false);

        txtOrderID.setText(ManageOrders.generateOrderId());
        DatePickerBox.setValue(LocalDate.now());


        btnRemove.setDisable(true);
        btnPlaceOder.setDisable(true);
        calculateTotal();

        Platform.runLater(() -> {
            txtCustomerID.requestFocus();
        });

        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderDetailTM>() {
            @Override
            public void changed(ObservableValue<? extends OrderDetailTM> observable, OrderDetailTM oldValue, OrderDetailTM selectedOrderDetail) {

                if (selectedOrderDetail == null) {
                    // Clear Selection
                    return;
                }

                txtItemCode.setText(selectedOrderDetail.getCode());
                txtItemDescription.setText(selectedOrderDetail.getDescription());
                txtUnitPrice.setText(selectedOrderDetail.getUnitPrice() + "");
                txtOrderQuantity.setText(selectedOrderDetail.getQty() + "");
                txtQuantityInHand.setText(getItemFromTempDB(txtItemCode.getText()).getQtyOnHand() + "");

                txtItemCode.setEditable(false);
                btnRemove.setDisable(false);

            }
        });

        tblOrderDetails.getItems().addListener(new ListChangeListener<OrderDetailTM>() {
            @Override
            public void onChanged(Change<? extends OrderDetailTM> c) {
                calculateTotal();

                btnPlaceOder.setDisable(tblOrderDetails.getItems().size() == 0);
            }
        });

    }


    public void ClickBack(ActionEvent actionEvent) throws IOException {

        Parent parent = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/MainPage.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) lblTotal.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void ClickAddButton(ActionEvent actionEvent) {

        if (validateItemCode() == null) {
            return;
        }

        String qty = txtOrderQuantity.getText();
        if (!isInt(qty)) {
            showInvalidateMsgBox("Qty should be a number");
            return;
        } else if (Integer.parseInt(qty) == 0) {
            showInvalidateMsgBox("Qty can't be zero");
            return;
        } else if (Integer.parseInt(qty) > Integer.parseInt(txtQuantityInHand.getText())) {
            showInvalidateMsgBox("Invalid Qty");
            return;
        }

        if (tblOrderDetails.getSelectionModel().isEmpty()) {
            // New

            OrderDetailTM orderDetailTM = null;

            if ((orderDetailTM = isItemExist(txtItemCode.getText())) == null) {

                OrderDetailTM newOrderDetailTM = new OrderDetailTM(txtItemCode.getText(),
                        txtItemDescription.getText(),
                        Integer.parseInt(qty),
                        Double.parseDouble(txtUnitPrice.getText()),
                        Integer.parseInt(qty) * Double.parseDouble(txtUnitPrice.getText()));

                tblOrderDetails.getItems().add(newOrderDetailTM);

            } else {
                orderDetailTM.setQty(orderDetailTM.getQty() + Integer.parseInt(qty));
            }


        } else {
            // Update
            OrderDetailTM selectedItem = tblOrderDetails.getSelectionModel().getSelectedItem();
            synchronizeQty(selectedItem.getCode());
            selectedItem.setQty(Integer.parseInt(qty));
        }

        setTempQty(txtItemCode.getText(), Integer.parseInt(qty));
        tblOrderDetails.refresh();
        reset();

//        calculateTotal();

    }


    public void ClickRemoveButton(ActionEvent actionEvent) {
        OrderDetailTM selectedItem = tblOrderDetails.getSelectionModel().getSelectedItem();
        tblOrderDetails.getItems().remove(selectedItem);

        synchronizeQty(selectedItem.getCode());
        reset();

//        calculateTotal();
    }

    public void EnterCustomerIDtxt(ActionEvent actionEvent) {
        String customerID = txtCustomerID.getText();

        Customer customer = ManageCustomers.findCustomer(customerID);

        if (customer == null) {
            new Alert(Alert.AlertType.ERROR, "Invalid Customer ID", ButtonType.OK).showAndWait();
            txtCustomerName.clear();
            txtCustomerID.requestFocus();
            txtCustomerID.selectAll();
        } else {
            txtCustomerName.setText(customer.getName());
            txtItemCode.requestFocus();
        }
    }

    public void EnterItemCodetxt(ActionEvent actionEvent) {
        Item item = validateItemCode();

        if (item != null) {

            txtItemDescription.setText(item.getDescription());
            txtQuantityInHand.setText(getItemFromTempDB(item.getCode()).getQtyOnHand() + "");
            txtUnitPrice.setText(item.getUnitPrice() + "");
            txtOrderQuantity.requestFocus();
        }
    }

    public void pressEnterOnOrderquantity(ActionEvent actionEvent) {
    }

    public void ClickPlaceOrder(ActionEvent actionEvent) throws JRException {
        if (txtCustomerID.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Can't place a order without a customer Id", ButtonType.OK).showAndWait();
            txtCustomerID.requestFocus();
            return;
        }

        ObservableList<OrderDetailTM> items = tblOrderDetails.getItems();
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderDetailTM item : items) {
            orderDetails.add(new OrderDetail(item.getCode(), item.getDescription(), item.getQty(), item.getUnitPrice()));
        }
        ManageOrders.createOrder(new Order(txtOrderID.getText(), DatePickerBox.getValue(), txtCustomerID.getText(), orderDetails));

        new Alert(Alert.AlertType.CONFIRMATION, "Order has been placed successfully", ButtonType.OK).showAndWait();

        String Oid = txtOrderID.getText();
        String Cus_id = txtCustomerID.getText();
        String Cus_Name = txtCustomerName.getText();

        File file = new File("table/place_order.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);

        HashMap<String, Object> parems = new HashMap<>();
        parems.put("oid", Oid);
        parems.put("cus_id", Cus_id);
        parems.put("cus_name", Cus_Name);

        DefaultTableModel dtm = new DefaultTableModel(new Object[]{"item_code", "item_desc", "item_qty", "item_unitPrice", "total"}, 0);
        ObservableList<OrderDetailTM> itmes = tblOrderDetails.getItems();

        for (OrderDetailTM item : itmes) {
            Object[] rowDate = {item.getCode(), item.getDescription(), item.getQty(), item.getUnitPrice(), item.getTotal()};
            dtm.addRow(rowDate);
        }
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parems, new JRTableModelDataSource(dtm));
        JasperViewer.viewReport(jasperPrint,false);

        hardReset();
    }

    private void hardReset() {
        reset();
        tblOrderDetails.getItems().removeAll(tblOrderDetails.getItems());
        txtCustomerID.clear();
        txtCustomerName.clear();
        txtOrderID.setText(ManageOrders.generateOrderId());
        txtCustomerID.requestFocus();
    }

    public void calculateTotal() {
        ObservableList<OrderDetailTM> items = tblOrderDetails.getItems();

        double total = 0.0;

        for (OrderDetailTM item : items) {
            total += item.getTotal();
        }

        lblTotal.setText("Total : " + total + "");
    }

    private Item validateItemCode() {
        String itemCode = txtItemCode.getText();

        Item item = ManageItems.findItem(itemCode);

        if (item == null) {
            new Alert(Alert.AlertType.ERROR, "Invalid Item Code", ButtonType.OK).showAndWait();
            txtItemDescription.clear();
            txtQuantityInHand.clear();
            txtUnitPrice.clear();
            txtOrderQuantity.clear();
            txtItemCode.requestFocus();
            txtItemCode.selectAll();
        }
        return item;
    }

    public boolean isInt(String number) {
        char[] chars = number.toCharArray();
        for (char aChar : chars) {
            if (!Character.isDigit(aChar)) {
                return false;
            }
        }
        return true;
    }

    public Item getItemFromTempDB(String itemCode) {
        for (Item item : tempItemsDB) {
            if (item.getCode().equals(itemCode)) {
                return item;
            }
        }
        return null;
    }

    private void showInvalidateMsgBox(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
        txtOrderQuantity.requestFocus();
        txtOrderQuantity.selectAll();
    }

    private OrderDetailTM isItemExist(String itemCode) {
        ObservableList<OrderDetailTM> items = tblOrderDetails.getItems();
        for (OrderDetailTM item : items) {
            if (item.getCode().equals(itemCode)) {
                return item;
            }
        }
        return null;
    }

    public void reset() {
        tblOrderDetails.refresh();
        txtItemCode.clear();
        txtItemDescription.clear();
        txtOrderQuantity.clear();
        txtQuantityInHand.clear();
        txtUnitPrice.clear();
        txtItemCode.setEditable(true);
        btnRemove.setDisable(true);
        tblOrderDetails.getSelectionModel().clearSelection();
        txtItemCode.requestFocus();
    }

    private void setTempQty(String itemCode, int qty) {
        for (Item item : tempItemsDB) {
            if (item.getCode().equals(itemCode)) {
                item.setQtyOnHand(item.getQtyOnHand() - qty);
                break;
            }
        }
    }

    private void synchronizeQty(String itemCode) {
        int qtyOnHand = ManageItems.findItem(itemCode).getQtyOnHand();
        for (Item item : tempItemsDB) {
            if (item.getCode().equals(itemCode)) {
                item.setQtyOnHand(qtyOnHand);
                return;
            }
        }
    }
}
