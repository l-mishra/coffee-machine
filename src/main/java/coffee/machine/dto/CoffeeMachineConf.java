package coffee.machine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoffeeMachineConf {

    @JsonProperty(value = "machine")
    private MachineConfg machineConfg;

    public CoffeeMachineConf() {
    }

    public MachineConfg getMachineConfg() {
        return machineConfg;
    }

    public void setMachineConfg(MachineConfg machineConfg) {
        this.machineConfg = machineConfg;
    }

    @Override
    public String toString() {
        return "CoffeeMachineConf{" +
                "machineConfg=" + machineConfg +
                '}';
    }
}
