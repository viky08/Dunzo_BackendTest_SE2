package app.dunzo.backendtest;

//Base class for ingredients. Each InventoryItem is different but has common attributes it extends Ingredient
public class Ingredient {
    protected String name;
    protected int quantity;

    public Ingredient(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

}
