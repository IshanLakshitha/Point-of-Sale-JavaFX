package lk.ijse.fx.util;

import lk.ijse.fx.model.Order;
import lk.ijse.fx.model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;


public class ManageOrders {

    // private static ArrayList<Order> ordersDB = new ArrayList<>();

    public static ArrayList<Order> getOrders() {
        ArrayList<Order> alOrders = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM oder");
            ResultSet rst = pstm.executeQuery();
            while (rst.next()) {
                String orderId = rst.getString(1);
                Date orderDate = rst.getDate(2);

                String customerId = rst.getString(3);

                PreparedStatement pstm2 = connection.prepareStatement("SELECT * FROM orderdetails WHERE oID = ?");
                pstm2.setObject(1, orderId);
                ResultSet rst2 = pstm2.executeQuery();

                ArrayList<OrderDetail> alOrderDetailList = new ArrayList<>();

                while (rst2.next()) {
                    String itemCode = rst2.getString(2);
                    int qty = rst2.getInt(3);
                    double unitPrice = rst2.getDouble(4);
                    String description = ManageItems.findItem(itemCode).getDescription();
                    OrderDetail orderDetails = new OrderDetail(itemCode, description, qty, unitPrice);
                    alOrderDetailList.add(orderDetails);
                }

                Order order = new Order(orderId, orderDate.toLocalDate(), customerId, alOrderDetailList);
                alOrders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alOrders;
    }

    public static String generateOrderId() {
        return getOrders().size() + 1 + "";
    }

    public static void createOrder(Order order) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
            //connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO oder VALUES (?,?,?)");
            pstm.setObject(1, order.getId());
            pstm.setObject(2, order.getDate());
            pstm.setObject(3, order.getCustomerId());
            int affrectedRows = pstm.executeUpdate();

            if (affrectedRows == 0) {
                return;
            }
            PreparedStatement pstm2 = connection.prepareStatement("INSERT INTO orderdetails VALUES (?,?,?,?)");
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                pstm2.setObject(1, order.getId());
                pstm2.setObject(2, orderDetail.getCode());
                pstm2.setObject(3, orderDetail.getQty());
                pstm2.setObject(4, orderDetail.getUnitPrice());
                affrectedRows = pstm2.executeUpdate();

                if (affrectedRows == 0) {
                    connection.rollback();
                    return;

                }
                int qtyOnHand = ManageItems.findItem(orderDetail.getCode()).getQtyOnHand();
                qtyOnHand -= orderDetail.getQty();

                PreparedStatement pstm3 = connection.prepareStatement("UPDATE itemsdata SET qty_on_hand=? WHERE item_id=?");
                pstm3.setObject(1, qtyOnHand);
                pstm3.setObject(2, orderDetail.getCode());

                affrectedRows = pstm3.executeUpdate();

                if (affrectedRows == 0) {
                    connection.rollback();
                    return;
                }

            }
            connection.commit();

        } catch (Exception e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Order findOrder(String orderId) {
        for (Order order : getOrders()) {
            if (order.getId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
}
