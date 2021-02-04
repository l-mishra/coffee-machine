package coffee.machine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutletConf {

    public OutletConf() {
    }

    @JsonProperty(value="count_n")
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
