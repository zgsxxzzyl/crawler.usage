package p.b;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

public class DD373_D2R_Rune implements PageProcessor {
    Html html;

    public static void main(String[] args) {
        String url = "https://www.dd373.com/s-1psrbm-u6w1hm-dkcdwk-0-0-0-sndbsb-0kax50-0-0-0-0-0-0-0-0.html";
        Spider spider = Spider.create(new DD373_D2R_Rune()).addUrl(url)
                .addPipeline(new ConsolePipeline())
                .thread(5);
        spider.run();
        if (spider.isExitWhenComplete()) {

        }
    }

    @Override
    public void process(Page page) {
        html = page.getHtml();
        String name = html.xpath("//div[@class='goods-select-value  select-the-goodtypechild-box']//*[@class='active']/text()").all().get(0);
        String price = html.xpath("//div[@class='kucun']//p[@class='font12 color666 m-t5']/text()").all().get(0);

        Selectable xpath = html.xpath("//div[@class='select-the-goodtypechild-box']/a");
        List<Selectable> nodes = html.xpath("//div[@class='select-the-goodtypechild-box']/a").nodes();
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i).$("a", "text") + " = " + nodes.get(i).$("a", "href"));
        }
        System.out.println(name + " = " + doPrice(price));
//        page.putField(name,doPrice(price));
//        List<String> all = html.xpath("//div[@class='select-the-goodtypechild-box']/a").links().regex("https\\:\\/\\/www\\.dd373\\.com\\/s\\-1psrbm\\-u6w1hm\\-dkcdwk\\-0\\-0\\-0\\-sndbsb-.*").all();
//        page.addTargetRequests(all);
    }

    @Override
    public Site getSite() {
        return PageProcessor.super.getSite();
    }

    private String doPrice(String price) {
        return price.split("=")[1].replaceAll("元", "");
    }
}
