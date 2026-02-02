package p.b;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import p.a.CommonConfig;
import p.a.Good;
import p.c.JsonExtUtil;
import p.c.pojo.Legend;
import p.c.pojo.Option;
import p.c.pojo.Series;
import p.c.pojo.XAxis;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DD373_D2R_Rune implements PageProcessor {
    private static String option_data = "runes\\option_data.json";
    // 日期
    private static String xAxis_data = "runes\\xAxis_data.json";
    Html html;
    public static Map<String, Good> map = new ConcurrentHashMap<>();
    private static Object readLock = new Object();

    public static void main(String[] args) {
        String url = "https://www.dd373.com/s-1psrbm-u6w1hm-dkcdwk-0-0-0-sndbsb-0kax50-0-0-0-0-0-0-0-0.html";
        Spider spider = Spider.create(new DD373_D2R_Rune()).addUrl(url)
//                .addPipeline(new ConsolePipeline())
                .addPipeline(new HtmlD2rPipeline(CommonConfig.DATA_PATH, new String[]{"符文"}))
                .thread(5);
        spider.run();
        List<Good> collect = map.values().stream().sorted((a, b) ->
                        StrUtil.compare(a.getTitle(), b.getTitle(), true))
                .filter(g -> Arrays.asList("29号符文", "30号符文", "31号符文").contains(g.getTitle()))
                .collect(Collectors.toList());
        if (spider.isExitWhenComplete()) {
            Option option = JsonExtUtil.readResourceJsonToObject(option_data, Option.class);
            Legend legend = new Legend(collect.stream().map(g -> g.getTitle()).collect(Collectors.toList()));
            XAxis xAxis = option.getXAxis();
            xAxis.getData().add(DateUtil.now());
            List<Series> seriesList = option.getSeries();
            for (int i = 0; i < seriesList.size(); i++) {
                seriesList.get(i).getData().add(collect.get(i).getPrice());
                seriesList.get(i).setStack(seriesList.get(i).getName());
            }
            option.setLegend(legend);
            option.setXAxis(xAxis);
            option.setSeries(seriesList);
            System.out.println("option = " + JSONUtil.toJsonStr(option));
            FileUtil.writeBytes(JSONUtil.toJsonStr(option).getBytes(), "E:\\github.com\\crawler.usage\\src\\main\\resources\\runes\\option_data.json");
        }
    }

    private static Series convertGood(Good g) {
        Series s = new Series();
        s.setName(g.getTitle());
        s.setType("line");
        s.setStack(g.getTitle());
//        s.setData(Arrays.asList(g.getPrice()));
        s.getData().add(g.getPrice());
        return s;
    }

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        Good good = new Good();
        synchronized (readLock) {
            if (map.containsKey(url)) {
                return;
            }
            map.put(url, good);
        }
        html = page.getHtml();
        String name = html.xpath("//div[@class='goods-select-value  select-the-goodtypechild-box']//*[@class='active']/text()").all().get(0);
        String price = html.xpath("//div[@class='kucun']//p[@class='font12 color666 m-t5']/text()").all().get(0);
        price = doPrice(price);
        /*List<Selectable> nodes = html.xpath("//div[@class='select-the-goodtypechild-box']/a").nodes();
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i).$("a", "text") + " = " + nodes.get(i).$("a", "href"));
        }
        System.out.println(name + " = " + (price = doPrice(price)));*/
        good.setTitle(name);
        good.setPrice(price);
        good.setUrl(url);
        good.setNumber("0");
        page.putField(name, good);
        System.out.println(url + " : " + name);
        List<String> all = html.xpath("//div[@class='select-the-goodtypechild-box']/a").links().regex("https\\:\\/\\/www\\.dd373\\.com\\/s\\-1psrbm\\-u6w1hm\\-dkcdwk\\-0\\-0\\-0\\-sndbsb\\-.*\\-0\\-0\\-0\\-0\\-0\\-0\\-0\\-0\\.html").all();
        page.addTargetRequests(all);
    }

    @Override
    public Site getSite() {
        return PageProcessor.super.getSite();
    }

    private String doPrice(String price) {
        return price.split("=")[1].replaceAll("元", "");
    }
}
