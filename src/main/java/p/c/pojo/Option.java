package p.c.pojo;

import java.util.List;
import java.util.stream.Collectors;

public class Option {
    public Option(Grid grid, Legend legend, List<Series> series, Title title, Toolbox toolbox, Tooltip tooltip, XAxis xAxis, YAxis yAxis) {
        this.grid = grid;
        this.legend = legend;
        this.series = series;
        this.title = title;
        this.toolbox = toolbox;
        this.tooltip = tooltip;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    private Grid grid;

    private Legend legend;

    private List<Series> series;

    private Title title;

    private Toolbox toolbox;

    private Tooltip tooltip;

    private XAxis xAxis;

    private YAxis yAxis;

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Legend getLegend() {
        return legend;
    }

    public void setLegend(Legend legend) {
        this.legend = legend;
    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
        List<String> legend = series.stream().map(s -> s.getName()).collect(Collectors.toList());
        this.legend = new Legend(legend);
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Toolbox getToolbox() {
        return toolbox;
    }

    public void setToolbox(Toolbox toolbox) {
        this.toolbox = toolbox;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public void setTooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    public XAxis getXAxis() {
        return xAxis;
    }

    public void setXAxis(XAxis xAxis) {
        this.xAxis = xAxis;
    }

    public YAxis getYAxis() {
        return yAxis;
    }

    public void setYAxis(YAxis yAxis) {
        this.yAxis = yAxis;
    }
}
