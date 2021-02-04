package coffee.machine.service;

import coffee.machine.beverage.manager.BeverageManager;
import coffee.machine.dto.CoffeeMachineConf;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CoffeeMachineService {

    private static CoffeeMachineService INSTANCE = null;
    private static CoffeeMachineConf coffeeMachineConf;
    private BeverageManager beverageManager;

    public CoffeeMachineService(BeverageManager beverageManager) {
        this.beverageManager = beverageManager;
    }

    public String getBeverage(String type) throws ExecutionException, InterruptedException {
        return beverageManager.prepareBeverage(type);
    }

    public List<String> getShortIndicator() throws InterruptedException {
        return beverageManager.getItemResourceManager().getIndicators();
    }
}
