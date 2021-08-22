package app.dunzo.backendtest;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;


public class Main {
    public static int testNumber = 0;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();

        try {
        	//Reading all the test-cases locally 
            Object obj = parser.parse(new FileReader("src/test/resources/TestAll.json"));

            JSONArray tests = (JSONArray) obj;
            
            //Call the function to parse the test-object into Coffee-Machine instructions
            tests.forEach(element -> parseMachineObject((JSONObject) element));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    //function to parse the test-object into Coffee-Machine attributes & instructions
    private static void parseMachineObject(JSONObject element) {
    	
    	// Reading the attributes of Coffee-Machine
        JSONObject machineObject = (JSONObject) element.get("machine");
        JSONObject outletsObject = (JSONObject) machineObject.get("outlets");
        int numOutlets = (int) (long) outletsObject.get("count_n");
        
        List<Beverage> beverages = new ArrayList<>();
        Map<String, InventoryItem> items = new HashMap<>();
        
        //Reading the beverages name and composition(Ingredients & their amount for specific drink) 
        JSONObject beveragesObject = (JSONObject) machineObject.get("beverages");
        beveragesObject.forEach((K, V) -> {
            String beverageName = (String) K;
            List<Ingredient> ingredients = new ArrayList<>();
            
            ((JSONArray) V).forEach(ingredient -> {
                JSONObject ingredientObject = (JSONObject) ingredient;
                ingredientObject.forEach((k, v) -> ingredients.add(new Ingredient((String) k, (int) (long) v)));
            });
            //Adding each beverage and it's composition to the beverage-list
            beverages.add(new Beverage(beverageName, ingredients));
        });
        
        //Total initial quantity of each ingredient 
        JSONObject itemsQuantityObject = (JSONObject) machineObject.get("total_items_quantity");
        itemsQuantityObject.forEach((K, V) -> items.put((String) K, new InventoryItem((String) K, (int) (long) V)));

        //Creating an instance of Coffee-Machine for this test-case with above acquired info
        CoffeeMachine coffeeMachine = new CoffeeMachine(numOutlets, beverages, new Inventory(items));
        System.out.println("\n<--- Test #" + ++testNumber + " --->");
        
        Set<String> output = coffeeMachine.startPreparing();
        for (String s : output)
            System.out.println(s);
    }

}
