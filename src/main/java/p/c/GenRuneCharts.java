package p.c;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import p.c.pojo.Legend;
import p.c.pojo.Option;
import p.c.pojo.Series;
import p.c.pojo.XAxis;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GenRuneCharts {
    private static String option_data = "runes\\option_data.json";
    // 符文条目
    private static String legend_data = "runes\\legend_data.json";
    // 日期
    private static String xAxis_data = "runes\\xAxis_data.json";
    // 符文明细
    private static String series_data = "runes\\series_data_%d.json";

    public static void main(String[] args) {
        // 1. read runes.xml
        Document document = XmlUtil.readXML(ResourceUtil.getStream("runes.xml"));
        NodeList a = document.getElementsByTagName("a");
        List<Series> series = new ArrayList<>();
        for (int i = 0; i < a.getLength(); i++) {
//            series.add(new Series());
            System.out.println(a.item(i).getTextContent() + " = https://www.dd373.com" + a.item(i).getAttributes().getNamedItem("href").getTextContent());
        }
        // 2. parse content to json
        


        // 1、根据json生成配置对象
//        Option option = JsonExtUtil.readResourceJsonToObject(option_data, Option.class);
//        XAxis xAxis = JsonExtUtil.readResourceJsonToObject(xAxis_data, XAxis.class);
//        List<Series> series = JsonExtUtil.readResourceJsonToList(series_data, Series.class);
//        option.setXAxis(xAxis); // todo
//        option.setSeries(series);
        // 2、根据配置对象生成前端配置
        // 3、生成折线图

    }
}
