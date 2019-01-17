package lk.ijse.fx.util;


public class OrderDetails {
    private String itemCode;
    private String description;
    private int quantity;
    private double unitPrice;

    public OrderDetails(String itemCode, String description, int quantity, double unitPrice) {
        this.setItemCode(itemCode);
        this.setDescription(description);
        this.setQuantity(quantity);
        this.setUnitPrice(unitPrice);
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }


    //   private int total;
 //   int quantityInHand;



}
