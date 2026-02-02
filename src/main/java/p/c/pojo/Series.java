package p.c.pojo;

import java.util.List;

public class Series {
    public Series() {
    }

    public Series(List<String> data, String name, String stack, String type) {
        this.data = data;
        this.name = name;
        this.stack = stack;
        this.type = type;
    }

    private List<String> data;

    private String name;

    private String stack;

    private String type;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
