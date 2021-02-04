package coffee.machine;

import coffee.machine.beverage.manager.BeverageManager;
import coffee.machine.dto.CoffeeMachineConf;
import coffee.machine.resource.impl.ItemResourceManager;
import coffee.machine.service.CoffeeMachineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class TestCoffeeMachine {

    @Test
    public void shouldBeAbleToPrepareCofee() throws ExecutionException, InterruptedException, IOException {
        String input = "{\"machine\":{\"outlets\":{\"count_n\":3}," +
                "\"total_items_quantity\":{\"hot_water\":500,\"hot_milk\":500,\"ginger_syrup\":100," +
                "\"sugar_syrup\":100,\"tea_leaves_syrup\":100},\"beverages\":{\"hot_tea\":{\"hot_water\":200," +
                "\"hot_milk\":100,\"ginger_syrup\":10,\"sugar_syrup\":10,\"tea_leaves_syrup\":30}," +
                "\"hot_coffee\":{\"hot_water\":100,\"ginger_syrup\":30,\"hot_milk\":400,\"sugar_syrup\":50," +
                "\"tea_leaves_syrup\":30},\"black_tea\":{\"hot_water\":300,\"ginger_syrup\":30,\"sugar_syrup\":50," +
                "\"tea_leaves_syrup\":30},\"green_tea\":{\"hot_water\":100,\"ginger_syrup\":30,\"sugar_syrup\":50," +
                "\"green_mixture\":30}}}}";

        CoffeeMachineConf coffeeMachineConf = new ObjectMapper().readValue(input, CoffeeMachineConf.class);
        ItemResourceManager itemResourceManager = new ItemResourceManager(coffeeMachineConf);
        BeverageManager beverageManager = new BeverageManager(itemResourceManager, coffeeMachineConf);
        CoffeeMachineService coffeeMachineService = new CoffeeMachineService(beverageManager);
        assertEquals("hot_coffee is prepared", coffeeMachineService.getBeverage("hot_coffee"));
    }

    @Test
    public void shouldBeAbleToParallellyProcessBreverageAndGreenTeaShouldNotBePrepared() throws ExecutionException,
            InterruptedException, IOException {
        String input = "{\"machine\":{\"outlets\":{\"count_n\":3}," +
                "\"total_items_quantity\":{\"hot_water\":1000,\"hot_milk\":1000,\"ginger_syrup\":500," +
                "\"sugar_syrup\":500,\"tea_leaves_syrup\":500},\"beverages\":{\"hot_tea\":{\"hot_water\":200," +
                "\"hot_milk\":100,\"ginger_syrup\":10,\"sugar_syrup\":10,\"tea_leaves_syrup\":30}," +
                "\"hot_coffee\":{\"hot_water\":10,\"ginger_syrup\":10,\"hot_milk\":100,\"sugar_syrup\":10," +
                "\"tea_leaves_syrup\":10},\"black_tea\":{\"hot_water\":10,\"ginger_syrup\":10,\"sugar_syrup\":10," +
                "\"tea_leaves_syrup\":10},\"green_tea\":{\"hot_water\":30,\"ginger_syrup\":10,\"sugar_syrup\":10," +
                "\"green_mixture\":30}}}}";
        CoffeeMachineConf coffeeMachineConf = new ObjectMapper().readValue(input, CoffeeMachineConf.class);
        List<String> expected = new ArrayList<>();
        expected.add("hot_coffee is prepared");
        expected.add("hot_tea is prepared");
        expected.add("green_tea cannot be prepared because green_mixture is not available");

        ItemResourceManager itemResourceManager = new ItemResourceManager(coffeeMachineConf);
        BeverageManager beverageManager = new BeverageManager(itemResourceManager, coffeeMachineConf);
        CoffeeMachineService coffeeMachineService = new CoffeeMachineService(beverageManager);

        Callable<String> greenTeaRequeest = () -> {
            String output = coffeeMachineService.getBeverage("green_tea");
            Thread.sleep(1000);
            return output;
        };
        Callable<String> hotCoffee = () -> {
            String output = coffeeMachineService.getBeverage("hot_coffee");
            Thread.sleep(1500);
            return output;
        };
        Callable<String> hotTea = () -> {
            String output = coffeeMachineService.getBeverage("hot_tea");
            Thread.sleep(2000);
            return output;
        };
        ExecutorService es = Executors.newFixedThreadPool(3);
        List<String> output = new ArrayList<>();
        output.add(es.submit(hotCoffee).get());
        output.add(es.submit(hotTea).get());
        output.add(es.submit(greenTeaRequeest).get());
        assertEquals(expected, output);
    }

    @Test
    public void shouldBeAbleToProcessHotTeaHotCoffeeButNotHotTeaAgainParallely() throws ExecutionException,
            InterruptedException, IOException {
        String input = "{\"machine\":{\"outlets\":{\"count_n\":3}," +
                "\"total_items_quantity\":{\"hot_water\":500,\"hot_milk\":500,\"ginger_syrup\":100," +
                "\"sugar_syrup\":100,\"tea_leaves_syrup\":100},\"beverages\":{\"hot_tea\":{\"hot_water\":200," +
                "\"hot_milk\":100,\"ginger_syrup\":10,\"sugar_syrup\":50,\"tea_leaves_syrup\":30}," +
                "\"hot_coffee\":{\"hot_water\":100,\"ginger_syrup\":30,\"hot_milk\":400,\"sugar_syrup\":50," +
                "\"tea_leaves_syrup\":30},\"black_tea\":{\"hot_water\":300,\"ginger_syrup\":30,\"sugar_syrup\":50," +
                "\"tea_leaves_syrup\":30},\"green_tea\":{\"hot_water\":100,\"ginger_syrup\":30,\"sugar_syrup\":50," +
                "\"green_mixture\":30}}}}";
        CoffeeMachineConf coffeeMachineConf = new ObjectMapper().readValue(input, CoffeeMachineConf.class);

        ItemResourceManager itemResourceManager = new ItemResourceManager(coffeeMachineConf);
        BeverageManager beverageManager = new BeverageManager(itemResourceManager, coffeeMachineConf);
        CoffeeMachineService coffeeMachineService = new CoffeeMachineService(beverageManager);

        List<String> expected = new ArrayList<>();
        expected.add("hot_tea is prepared");
        expected.add("hot_coffee is prepared");
        expected.add("hot_tea cannot be prepared because hot_milk is not available");

        Callable<String> hotTeaRequest1 = () -> {
            String output = coffeeMachineService.getBeverage("hot_tea");
            return output;
        };
        Callable<String> hotCoffeeRequest1 = () -> {
            Thread.sleep(500);
            String output = coffeeMachineService.getBeverage("hot_coffee");
            return output;
        };
        Callable<String> hotTeaRequest2 = () -> {
            Thread.sleep(1000);
            String output = coffeeMachineService.getBeverage("hot_tea");
            return output;
        };
        ExecutorService es = Executors.newFixedThreadPool(3);
        List<String> output = new ArrayList<>();
        output.add(es.submit(hotTeaRequest1).get());
        output.add(es.submit(hotCoffeeRequest1).get());
        output.add(es.submit(hotTeaRequest2).get());
        assertEquals(expected, output);
    }

    @Test
    public void shouldTestItemShortIndicator() throws ExecutionException, InterruptedException, IOException {
        String input = "{\"machine\":{\"outlets\":{\"count_n\":3}," +
                "\"total_items_quantity\":{\"hot_water\":500,\"hot_milk\":500,\"ginger_syrup\":100," +
                "\"sugar_syrup\":100,\"tea_leaves_syrup\":100},\"beverages\":{\"hot_tea\":{\"hot_water\":200," +
                "\"hot_milk\":100,\"ginger_syrup\":10,\"sugar_syrup\":10,\"tea_leaves_syrup\":30}," +
                "\"hot_coffee\":{\"hot_water\":470,\"ginger_syrup\":30,\"hot_milk\":400,\"sugar_syrup\":50," +
                "\"tea_leaves_syrup\":30},\"black_tea\":{\"hot_water\":300,\"ginger_syrup\":30,\"sugar_syrup\":50," +
                "\"tea_leaves_syrup\":30},\"green_tea\":{\"hot_water\":100,\"ginger_syrup\":30,\"sugar_syrup\":50," +
                "\"green_mixture\":30}}}}";

        CoffeeMachineConf coffeeMachineConf = new ObjectMapper().readValue(input, CoffeeMachineConf.class);
        ItemResourceManager itemResourceManager = new ItemResourceManager(coffeeMachineConf);
        BeverageManager beverageManager = new BeverageManager(itemResourceManager, coffeeMachineConf);
        CoffeeMachineService coffeeMachineService = new CoffeeMachineService(beverageManager);
        coffeeMachineService.getBeverage("hot_coffee");
        Thread.sleep(1000);
        List<String> expectedShortItemList = new ArrayList<>();
        expectedShortItemList.add("hot_water");
        Thread.sleep(1000);
        assertEquals(expectedShortItemList,coffeeMachineService.getShortIndicator());
    }

}
