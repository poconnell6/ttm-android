package edu.cvtc.ttm.tabletopmanager;

public class ItemObject {

    private int itemId;
    private String itemName;
    private int itemWeight;
    private int itemCost;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(int itemWeight) {
        this.itemWeight = itemWeight;
    }

    public int getItemCost() {
        return itemCost;
    }

    public void setItemCost(int itemCost) {
        this.itemCost = itemCost;
    }

    public ItemObject(int itemId, String itemName, int itemWeight, int itemCost) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemWeight = itemWeight;
        this.itemCost = itemCost;
    }

    public ItemObject() {

    }


}
