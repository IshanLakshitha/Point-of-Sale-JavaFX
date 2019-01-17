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
import lk.ijse.fx.model.Item;
import lk.ijse.fx.util.ManageItems;
import lk.ijse.fx.view.util.CustomerTM;
import lk.ijse.fx.view.util.ItemTM;
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

public class ManageItemsController {

    @FXML
    private Button btnNewItem;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnDelete;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtCode;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TableView<ItemTM> tblItemsTable;

    @FXML
    private TextField txtPrice;


    public void initialize() {
        boolean result = isInt("55");
        System.out.println(result);

        tblItemsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItemsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItemsTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItemsTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        btnSave.setDisable(true);
        btnDelete.setDisable(true);

//        ArrayList<Item> itemsDB = ManageItems.getItems();
        ArrayList<Item> itemsDB = ManageItems.getItems();
        ObservableList<Item> items = FXCollections.observableArrayList(itemsDB);
        ObservableList<ItemTM> itemTMS = FXCollections.observableArrayList();
        for (Item item : items) {
            itemTMS.add(new ItemTM(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));
        }
        tblItemsTable.setItems(itemTMS);

        tblItemsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM selectedItem) {

                if (selectedItem == null) {
                    // Clear Selection
                    return;
                }

                txtCode.setText(selectedItem.getCode());
                txtDescription.setText(selectedItem.getDescription());
                txtPrice.setText(selectedItem.getUnitPrice() + "");
                txtQuantity.setText(selectedItem.getQtyOnHand() + "");

                txtCode.setEditable(false);

                btnSave.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    @FXML
    void ClickBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/fx/view/MainPage.fxml"));
        Scene mainScene = new Scene(root);
        Stage primaryStage = (Stage) txtCode.getScene().getWindow();
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Main Page");
    }

    @FXML
    void ClickDelete(ActionEvent event) {
        Alert confirmMsg = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete this item?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = confirmMsg.showAndWait();

        if (buttonType.get() == ButtonType.YES) {
            String selectedRow = tblItemsTable.getSelectionModel().getSelectedItem().getCode();
            tblItemsTable.getItems().remove(tblItemsTable.getSelectionModel().getSelectedItem());
            ManageItems.deleteItem(selectedRow);
            reset();
        }
    }

    @FXML
    void ClickNewItems(ActionEvent event) {
        reset();
    }

    @FXML
    void ClickSave(ActionEvent event) {
        if (txtCode.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Item Code is empty", ButtonType.OK).showAndWait();
            txtCode.requestFocus();
            return;
        } else if (txtDescription.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Item Description is empty", ButtonType.OK).showAndWait();
            txtDescription.requestFocus();
            return;
        } else if (txtPrice.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Unit Price is empty", ButtonType.OK).showAndWait();
            txtPrice.requestFocus();
            return;
        } else if (txtQuantity.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Qty On Hand is empty", ButtonType.OK).showAndWait();
            txtQuantity.requestFocus();
            return;
        }
        else if(!isDouble(txtPrice.getText()) || Double.parseDouble(txtPrice.getText())< 0){
            new Alert(Alert.AlertType.ERROR,"Invalid Unit Price",ButtonType.OK).showAndWait();
            txtPrice.requestFocus();
            return;
        }
        else if (!isInt(txtQuantity.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty", ButtonType.OK).showAndWait();
            txtQuantity.requestFocus();
            return;
        }

        if (tblItemsTable.getSelectionModel().isEmpty()) {
            // New

            ObservableList<ItemTM> items = tblItemsTable.getItems();
            for (ItemTM itemTM : items) {
                if (itemTM.getCode().equals(txtCode.getText())) {
                    new Alert(Alert.AlertType.ERROR, "Duplicate Item Codes are not allowed").showAndWait();
                    txtCode.requestFocus();
                    return;
                }
            }

            ItemTM itemTM = new ItemTM(txtCode.getText(), txtDescription.getText(),
                    Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQuantity.getText()));
            tblItemsTable.getItems().add(itemTM);
            Item item = new Item(txtCode.getText(), txtDescription.getText(),
                    Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQuantity.getText()));
            ManageItems.createItem(item);

            new Alert(Alert.AlertType.INFORMATION, "Item has been saved successfully", ButtonType.OK).showAndWait();
            tblItemsTable.scrollTo(itemTM);

        } else {
            // Update

            ItemTM selectedItem = tblItemsTable.getSelectionModel().getSelectedItem();
            selectedItem.setDescription(txtDescription.getText());
            selectedItem.setUnitPrice(Double.parseDouble(txtPrice.getText()));
            selectedItem.setQtyOnHand(Integer.parseInt(txtQuantity.getText()));
            tblItemsTable.refresh();

            String selectedRow = tblItemsTable.getSelectionModel().getSelectedItem().getCode();

            ManageItems.updateItem(selectedRow, new Item(txtCode.getText(), txtDescription.getText(),
                    Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQuantity.getText())));

            new Alert(Alert.AlertType.INFORMATION, "Item has been updated successfully", ButtonType.OK).showAndWait();
        }

        reset();

    }

    private boolean isInt(String number) {
        char[] chars = number.toCharArray();
        for (char aChar : chars) {
            if (!Character.isDigit(aChar)) {
                return false;
            }
        }
        return true;
    }

    private void reset() {
        txtCode.clear();
        txtDescription.clear();
        txtPrice.clear();
        txtQuantity.clear();
        txtCode.requestFocus();
        txtCode.setEditable(true);
        btnSave.setDisable(false);
        btnDelete.setDisable(true);
        tblItemsTable.getSelectionModel().clearSelection();
    }

    private boolean isDouble(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void clickReport(ActionEvent actionEvent) throws JRException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
        File file = new File("table/items_report.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(),connection );
        JasperViewer.viewReport(jasperPrint,false);


    }
}
