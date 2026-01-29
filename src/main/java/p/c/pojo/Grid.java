package p.c.pojo;

public class Grid {
    public Grid(String bottom, Boolean containLabel, String left, String right) {
        this.bottom = bottom;
        this.containLabel = containLabel;
        this.left = left;
        this.right = right;
    }

    private String bottom;

    private Boolean containLabel;

    private String left;

    private String right;

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public Boolean getContainLabel() {
        return containLabel;
    }

    public void setContainLabel(Boolean containLabel) {
        this.containLabel = containLabel;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
