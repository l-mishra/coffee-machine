package coffee.machine.constant;

import coffee.machine.dto.CoffeeMachineConf;
import com.fasterxml.jackson.core.type.TypeReference;

public class IndicatorRule {
    public static final double GENERIC_INDICATOR_THRESHOLD = 50;
    public static final String INPUT_FILE = "coffee-machine-config";
    public static final TypeReference<CoffeeMachineConf>
            COFFEE_MACHINE_TYPE_REFERENCE = new TypeReference<CoffeeMachineConf>() {

    };
}
