package app.dunzo.backendtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

//Class: CoffeeMachine
public class CoffeeMachine {
    private final int numOutlets;
    private final Logger log = LoggerFactory.getLogger(CoffeeMachine.class.getSimpleName());
    private final List<Beverage> beverages;
    private final Inventory inventory;

    //constructor initialized with initial parameter-values
    public CoffeeMachine(int numOutlets, List<Beverage> beverages, Inventory inventory) {
        this.numOutlets = numOutlets;
        this.beverages = beverages;
        this.inventory = inventory;
    }
    
    //The Coffee-Machine starts preparing beverages(Called when coffee-machine is started)
    public Set<String> startPreparing() {
        int numBeveragesToPrepare = Math.min(numOutlets, beverages.size());
        Set<String> output = new HashSet<>();
        
        /*ExecutorService is a JDK API that automatically provides a pool of threads 
          and an API for assigning tasks to it.*/
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(numOutlets, Runtime.getRuntime().availableProcessors() - 1));
        
        List<Callable<String>> callableTasks = new ArrayList<>();
        
    	/*For each beverage first check if the requirements are met. 
  	      If Yes, start preparing the beverage else throw error*/
        for (Beverage beverage : beverages) {
        	
            String status = checkRequirements(beverage);
            if (status.isEmpty()) {
                callableTasks.add(new PrepareBeverage(beverage, inventory));
                numBeveragesToPrepare--;
            } else
                output.add(status);
            if (numBeveragesToPrepare == 0)
                break;
        }

        try {
        	//Asynchronously run all the callableTasks added 
            List<Future<String>> futures = executorService.invokeAll(callableTasks);
            for (Future<String> future : futures) {
                String status = future.get();
                output.add(status);
            }
        } catch (ExecutionException e) {
            log.error("Something went wrong");
        } catch (InterruptedException e) {
            log.error("Thread running the task was interrupted");
        }

        executorService.shutdown();
        return output;
    }
    
    //Requirements check according to available ingredients
    public String checkRequirements(Beverage beverage) {
        for (Ingredient ingredient : beverage.getIngredients()) {
            if (inventory.itemUnavailable(ingredient.name))
                return beverage.getName() + " cannot be prepared because " + ingredient.name + " is not available";

            if (inventory.itemLowInStock(ingredient.name))
                return beverage.getName() + " cannot be prepared because item " + ingredient.name + " is not sufficient";
        }
        return "";
    }
    
    //Add a new beverage to the system with duplicate check
    public void addNewBeverage(Beverage beverage) {
        for (Beverage b : beverages) {
            if (b.getName().equals(beverage.getName()))
                throw new IllegalArgumentException("A beverage with name " + beverage.getName() + " already exists.");
        }
        beverages.add(beverage);
    }
    
    //Get info of items that need to be refilled
    public Map<String, Integer> getItemsToRefill() {
        return inventory.getItemsToRefill();
    }
    
    //Add an Inventory-item
    public void addInventoryItem(String name, int amount) {
        inventory.addItem(name, amount);
    }
    
    //Get full inventory summary
    public Set<String> getInventorySummary() {
        return inventory.inventorySummary();
    }

}
