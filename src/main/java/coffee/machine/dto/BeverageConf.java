package coffee.machine.dto;

import java.util.Map;

public class BeverageConf {

    private Map<String, Map<String, Double>> beverageByName;

    public BeverageConf() {
    }

    public Map<String, Map<String, Double>> getBeverageByName() {
        return beverageByName;
    }

    public void setBeverageByName(Map<String, Map<String, Double>> beverageByName) {
        this.beverageByName = beverageByName;
    }



    @Override
    public String toString() {
        return "BeverageConf{" +
                "beverageByName=" + beverageByName +
                '}';
    }
}
