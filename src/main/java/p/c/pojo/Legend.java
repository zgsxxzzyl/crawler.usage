package p.c.pojo;

import java.util.List;

public class Legend {
    public Legend(List<String> data) {
        this.data = data;
    }

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
