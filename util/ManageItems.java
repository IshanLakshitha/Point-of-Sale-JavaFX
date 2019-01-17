package lk.ijse.fx.util;

import lk.ijse.fx.db.DBConnection;
import lk.ijse.fx.model.Customer;
import lk.ijse.fx.model.Item;
import lk.ijse.fx.view.util.ItemTM;

import java.sql.*;
import java.util.ArrayList;

public class ManageItems {


    public static ArrayList<Item> getItems(){
        ArrayList<Item> allItems = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
           // Connection connection = DBConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM itemsdata");
            ResultSet rst = pstm.executeQuery();
            while(rst.next()){
                String code = rst.getString(1);
                String description = rst.getString(2);
                double unitPrice = rst.getDouble(3);
                int qty = rst.getInt(4);
                Item item = new Item(code, description, unitPrice, qty);
                allItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allItems;
    }

    public static void createItem(Item item){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
          //  Connection connection = DBConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO itemsdata VALUES (?,?,?,?)");
            pstm.setObject(1,item.getCode());
            pstm.setObject(2,item.getDescription());
            pstm.setObject(3,item.getUnitPrice());
            pstm.setObject(4,item.getQtyOnHand());
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateItem(String code, Item item){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
            //Connection connection = DBConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE itemsdata SET des = ? , unit_price = ? , qty_on_hand = ? WHERE item_id = ?");
            pstm.setObject(1,item.getDescription());
            pstm.setObject(2,item.getUnitPrice());
            pstm.setObject(3,item.getQtyOnHand());
            pstm.setObject(4,code);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//    public static void updateCustomer(int index, Customer customer){
//        try {
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
//            // Connection connection = DBConnection.getConnection();
//            PreparedStatement pstm = connection.prepareStatement("UPDATE customerdata SET cus_name=?, address=? WHERE cus_id = ?");
//            pstm.setObject(1,customer.getName());
//            pstm.setObject(2,customer.getAddress());
//            pstm.setObject(3,customer.getId());
//            pstm.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    public static void deleteItem(String customerID){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
            //Connection connection = DBConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM itemsdata WHERE item_id=?");
            pstm.setObject(1,customerID);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Item findItem(String itemCode) {
        for (Item item : getItems()) {
            if (item.getCode().equals(itemCode)){
                return item;
            }
        }
        return null;
    }
}
