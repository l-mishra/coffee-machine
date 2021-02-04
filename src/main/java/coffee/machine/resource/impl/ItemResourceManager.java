package coffee.machine.resource.impl;

import coffee.machine.dto.CoffeeMachineConf;

import java.util.*;
import java.util.concurrent.*;

import static coffee.machine.constant.IndicatorRule.GENERIC_INDICATOR_THRESHOLD;

/**
 * Ideally this class should be singleton
 */
public class ItemResourceManager {

    private static ItemResourceManager INSTANCE = null;
    private Map<String, Integer> inStoreItems;
    private CoffeeMachineConf coffeeMachineConf;
    private BlockingQueue<String> beverageIndicator;
    private ExecutorService beverageShortageIndicator;

    public ItemResourceManager(CoffeeMachineConf coffeeMachineConf) {
        this.coffeeMachineConf = coffeeMachineConf;
        beverageIndicator = new LinkedBlockingQueue<>();
        setup(coffeeMachineConf);
    }

    public void loadItem(String itemType, int volume) {
        if (inStoreItems.containsKey(itemType)) {
            inStoreItems.compute(itemType, (k, v) -> v + volume);
        } else {
            inStoreItems.put(itemType, volume);
        }
    }

    public List<String> getIndicators() throws InterruptedException {
        List<String> shortItems = new ArrayList<>();
        while (!beverageIndicator.isEmpty()) {
            shortItems.add(beverageIndicator.poll());
        }
        return shortItems;
    }

    public void setup(CoffeeMachineConf coffeeMachineConf) {
        inStoreItems = new ConcurrentHashMap<>();
        coffeeMachineConf.getMachineConfg().getItemConfig().forEach((k, v) -> this.inStoreItems.putIfAbsent(
                k, v));
        beverageShortageIndicator = Executors.newFixedThreadPool(1);
        beverageShortageIndicator.submit(() -> {
            while (true) {
                inStoreItems.entrySet().forEach(e -> {
                    if (e.getValue() < GENERIC_INDICATOR_THRESHOLD && !beverageIndicator.contains(e.getKey())) {
                        beverageIndicator.add(e.getKey());
                    }
                });
                Thread.sleep(100);
            }
        });
    }

    public boolean acquireItemByTypeAndQuantity(String itemType, int quantity) {
        if (!inStoreItems.containsKey(itemType) || inStoreItems.get(itemType).doubleValue() < quantity) {
            return false;
        }
        synchronized (itemType) {
            double availableQuantity = inStoreItems.get(itemType);
            if (availableQuantity < quantity) {
                return false;
            }
            inStoreItems.compute(itemType, (k, v) -> {
                return v - quantity;
            });
            return true;
        }
    }


}
