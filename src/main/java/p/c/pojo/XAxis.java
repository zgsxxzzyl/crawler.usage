package p.c.pojo;

import java.util.List;

public class XAxis {
    public XAxis(Boolean boundaryGap, List<String> data, String type) {
        this.boundaryGap = boundaryGap;
        this.data = data;
        this.type = type;
    }

    private Boolean boundaryGap;

    private List<String> data;

    private String type;

    public Boolean getBoundaryGap() {
        return boundaryGap;
    }

    public void setBoundaryGap(Boolean boundaryGap) {
        this.boundaryGap = boundaryGap;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
