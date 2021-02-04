package coffee.machine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class MachineConfg {

    @JsonProperty(value= "outlets")
    private OutletConf outletConf;

    @JsonProperty(value="beverages")
    private Map<String, Map<String, Integer>> beverageConfMap;

    @JsonProperty(value= "total_items_quantity")
    private Map<String,Integer> itemConfig;

    public MachineConfg() {
    }

    public OutletConf getOutletConf() {
        return outletConf;
    }

    public void setOutletConf(OutletConf outletConf) {
        this.outletConf = outletConf;
    }

    public Map<String, Map<String, Integer>> getBeverageConfMap() {
        return beverageConfMap;
    }

    public void setBeverageConfMap(Map<String, Map<String, Integer>> beverageConfMap) {
        this.beverageConfMap = beverageConfMap;
    }

    public Map<String, Integer> getItemConfig() {
        return itemConfig;
    }

    public void setItemConfig(Map<String, Integer> itemConfig) {
        this.itemConfig = itemConfig;
    }

    @Override
    public String toString() {
        return "MachineConfg{" +
                "outletConf=" + outletConf +
                ", beverageConfMap=" + beverageConfMap +
                ", itemConfig=" + itemConfig +
                '}';
    }
}
