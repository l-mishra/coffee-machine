package coffee.machine.beverage.manager;

import coffee.machine.dto.CoffeeMachineConf;
import coffee.machine.resource.impl.ItemResourceManager;

import java.util.Map;
import java.util.concurrent.*;

public class BeverageManager {

    private static BeverageManager INSTANCE = null;
    ItemResourceManager itemResourceManager;
    private CoffeeMachineConf coffeeMachineConf;
    private ExecutorService beverageTaskExecutor;

    public BeverageManager(ItemResourceManager itemResourceManager, CoffeeMachineConf coffeeMachineConf) {
        this.itemResourceManager = itemResourceManager;
        this.coffeeMachineConf = coffeeMachineConf;
        setup();
    }

    private String createTask(String beverageName) {
        Map<String, Integer> itemToQuantityMap =
                coffeeMachineConf.getMachineConfg().getBeverageConfMap().get(beverageName);
        for (Map.Entry<String, Integer> itemToQuantityEntry : itemToQuantityMap.entrySet()) {
            if (!itemResourceManager.acquireItemByTypeAndQuantity(itemToQuantityEntry.getKey(),
                    itemToQuantityEntry.getValue())) {
                return beverageName + " cannot be prepared because " + itemToQuantityEntry.getKey() + " is not " +
                        "available";
            }
        }
        return beverageName + " is prepared";
    }

    public String prepareBeverage(String beverageName) throws ExecutionException, InterruptedException {
        return beverageTaskExecutor.submit(() -> createTask(beverageName)).get();
    }

    public void setup() {
        beverageTaskExecutor =
                Executors.newFixedThreadPool(coffeeMachineConf.getMachineConfg().getOutletConf().getCount());
    }

    public ItemResourceManager getItemResourceManager() {
        return itemResourceManager;
    }

    public void setItemResourceManager(ItemResourceManager itemResourceManager) {
        this.itemResourceManager = itemResourceManager;
    }
}
