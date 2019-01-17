package lk.ijse.fx.util;
import lk.ijse.fx.db.DBConnection;
import lk.ijse.fx.model.Customer;

import java.sql.*;
import java.util.ArrayList;

public class ManageCustomers {

    // Database
   // private static ArrayList<Customer> customersDB = new ArrayList<>();

    public static ArrayList<Customer> getCustomers(){

        ArrayList<Customer> AllCustomer = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
           // Connection connection = DBConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM customerdata");
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);
                Customer customer = new Customer(id, name, address);
                AllCustomer.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return AllCustomer;
    }


    public static void createCustomer(Customer customer){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
          //  Connection connection = DBConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO customerdata VALUES (?,?,?)");
            pstm.setObject(1,customer.getId());
            pstm.setObject(2,customer.getName());
            pstm.setObject(3,customer.getAddress());
            pstm.executeUpdate(); //*
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomer(int index, Customer customer){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
           // Connection connection = DBConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE customerdata SET cus_name=?, address=? WHERE cus_id = ?");
            pstm.setObject(1,customer.getName());
            pstm.setObject(2,customer.getAddress());
            pstm.setObject(3,customer.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCustomer(String customerID){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
            //Connection connection = DBConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM customerdata WHERE  cus_id =?");
            pstm.setObject(1,customerID);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Customer findCustomer(String id){
        for (Customer customer : getCustomers()) {
            if (customer.getId().equals(id)){
                return customer;
            }
        }
        return null;
    }


}
