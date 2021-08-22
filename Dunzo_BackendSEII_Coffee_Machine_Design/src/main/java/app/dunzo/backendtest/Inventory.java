package app.dunzo.backendtest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//Class to maintain inventory related tasks
public class Inventory {
    private final Map<String, InventoryItem> items;


    public Inventory(Map<String, InventoryItem> items) {
        this.items = items;
        markItemsUnavailable();
    }
    
    //Mark items that are unavailable
    public void markItemsUnavailable() {
        for (Map.Entry<String, InventoryItem> e : items.entrySet()) {
            if (e.getValue().quantity == 0)
                e.getValue().lowInStock = true;
        }
    }
    
    //Get the quantity of a particular items
    public int getItemQuantity(String name) {
        if (!items.containsKey(name))
            throw new IllegalArgumentException("This Item does not exist");
        return items.get(name).quantity;
    }

    // Returns if quantity is zero or has no entry
    public boolean itemUnavailable(String name) { 
    	// adding an item required to make the beverage but not mentioned in the inventory
        if (!items.containsKey(name))
            addItem(name, 0);
        
        return items.get(name).quantity == 0;
    }
    
    // Returns if the item is less than required
    public boolean itemLowInStock(String name) {
    	// adding an item required to make the beverage but not mentioned in the inventory
        if (!items.containsKey(name))
            addItem(name, 0);
        return items.get(name).lowInStock;
    }

    // Returns unavailable and low in stock items
    public Map<String, Integer> getItemsToRefill() {
        Map<String, Integer> itemsToRefill = new HashMap<>();
        items.keySet().forEach(itemName -> {
            if (itemUnavailable(itemName) || itemLowInStock(itemName))
                itemsToRefill.put(itemName, getItemQuantity(itemName));
        });
        return itemsToRefill;
    }

    public boolean consumeItem(String name, int amount) {
        return items.get(name).consume(amount);
    }
    
    //Adding an item
    public void addItem(String name, int amount) {
    	/* Checking if the item is already present or not
    	   If Yes, refill the item.
    	   If No, add the item name as well as amount*/
    	
        if (items.containsKey(name)) 
            items.get(name).add(amount); // refill item in this case
        else
            items.put(name, new InventoryItem(name, amount)); 
    }
    
    //Returns the summary of the Inventory
    public Set<String> inventorySummary() {
        Set<String> itemsSet = new HashSet<>();
        for (Map.Entry<String, InventoryItem> e : items.entrySet()) {
            itemsSet.add(e.getKey() + " : " + e.getValue().quantity);
        }
        return itemsSet;
    }
}
