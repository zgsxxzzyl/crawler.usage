package p.b;

import org.apache.logging.log4j.util.Strings;
import p.SearchPage;
import p.a.CommonConfig;
import p.a.Good;
import p.a.GoodType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DD373_D2R_Search implements PageProcessor {
    // 添加对SearchPage的引用，用于日志输出
    private static SearchPage searchPage;

    public static String url = "https://www.dd373.com/s-1psrbm-u6w1hm-dkcdwk-0-0-0-5tgw08-0-0-0-0-0-%d-0-3-0.html";
    Html html;
    private Site site = Site.me().setRetryTimes(3).setSleepTime(2000);
    int num = 0;
    public static int start = 1;
    public static int pageSize = -1;
    public static boolean allMatch = true;
    public static boolean toLowerCase = true;
    public static Object[] keywords = new Object[]{GoodType.getGoodTypeArray(), "2刺客", "20施法", "20全抗"};

    public static void main(String[] args) {
        log("开始执行DD373_D2R_Search爬虫程序");
        String url = String.format(((GoodType) keywords[0]).getUrl(), start, keywords[1]);
        log("构建搜索URL: " + url);

        Spider spider = Spider.create(new DD373_D2R_Search()).addUrl(url)
                .addPipeline(new ConsolePipeline())
                .addPipeline(new HtmlD2rPipeline(CommonConfig.DATA_PATH, keywords))
                .thread(5);

        log("启动爬虫任务，线程数: " + 5);
        spider.run();
        log("爬虫任务执行完成");
    }

    public DD373_D2R_Search() {
        log("创建DD373_D2R_Search实例（默认构造函数）");
    }

    public DD373_D2R_Search(GoodType goodType, String keyword1, String[] keyword2) {
        log("创建DD373_D2R_Search实例，商品类型: " + goodType.getName() + ", 关键词1: " + keyword1);

        ArrayList<Object> objects = new ArrayList<>();
        objects.add(goodType);
        objects.add(keyword1);

        // 添加keyword2数组中的所有元素
        for (String keyword : keyword2) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                objects.add(keyword.trim());
                log("添加关键词2: " + keyword.trim());
            }
        }

        this.keywords = objects.toArray();
        log("初始化搜索关键词完成，总共" + this.keywords.length + "个关键词");
    }

    @Override
    public void process(Page page) {
        log("开始处理页面: " + page.getUrl().get());

        html = page.getHtml();
        String curPage = removeTags(html.xpath("//a[@class='ui-pagination-page-item active']").get());
        log("当前页面: " + curPage);

        // 控制最大页数
        if (pageSize > 0 && Integer.valueOf(curPage.trim()) > pageSize) {
            log("达到最大页数限制(" + pageSize + ")，停止爬取");
            return;
        }

        // 查询当前页面下所有商品条目
        List<Selectable> items = html.xpath("//div[@class='goods-list-item']").nodes();
        log("在当前页面找到" + items.size() + "个商品条目");

        List<Good> goods = items.stream().map(this::processGoods).filter(good -> {
            String title = good.getTitle();
            log("检查商品标题: " + title);

            // 跳过前两个
            if (keywords.length > 2) {
                if (allMatch) {
                    boolean matchResult = Arrays.stream(keywords).skip(2).map(String::valueOf).filter(Strings::isNotBlank).allMatch(keyword ->
                            Arrays.stream(keyword.split("\\|")).filter(Strings::isNotBlank).anyMatch(k -> title.contains(k)));
                    log("全匹配模式，商品匹配结果: " + matchResult);
                    return matchResult;
                } else {
                    boolean matchResult = Arrays.stream(keywords).skip(2).map(String::valueOf).filter(Strings::isNotBlank).anyMatch(keyword ->
                            Arrays.stream(keyword.split("\\|")).anyMatch(k -> title.contains(k)));
                    log("任一匹配模式，商品匹配结果: " + matchResult);
                    return matchResult;
                }
            } else {
                log("无额外关键词限制，商品默认通过");
                return true;
            }
        }).collect(Collectors.toList());

        if (goods.size() > 0) {
            log("获取到第" + curPage + "页数据");
            log("筛选后剩余" + goods.size() + "个符合条件的商品");

            goods.forEach(good -> {
                log("处理商品[" + num + "]: " + good.getTitle());
                page.putField(String.valueOf(num++), good.toString() + "\r\n");
            });

            log("==========================");
        } else {
            log("第" + curPage + "页未找到符合条件的商品");
        }

        List<String> list = page.getHtml().xpath("//a[@class='ui-pagination-page-item pre-next-btn']").links().all();
        log("找到" + list.size() + "个下一页链接");
        page.addTargetRequests(list);

        log("页面处理完成: " + page.getUrl().get());
    }

    /**
     * 解析商品条目
     *
     * @param item
     * @return
     */
    private Good processGoods(Selectable item) {
        String title = removeTags(item.xpath("//*[@class='game-account-flag']").get());
        title = toLowerCase ? title.toLowerCase() : title;
        String price = removeTags(item.xpath("//*[@class='goods-price']/span").get());
        String url = item.xpath("//*[contains(@class, 'goods-list-title')]").links().all().get(0);
        String number = removeTags(item.xpath("//p[@class='game-reputation']/span/span[contains(@class, 'bold')]").get());
        Good good = new Good(title, price, url, number);
        log("解析商品信息 - 标题: " + title + ", 价格: " + price + ", 数量: " + number);
        return good;
    }

    /**
     * 清除HTML标签
     *
     * @param content
     * @return
     */
    private String removeTags(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        // 去除HTML/XML标签
        String result = content.replaceAll("<[^>]*>", "");
        // 去除多余的空白字符（包括HTML实体）
        result = result.replaceAll("&nbsp;", " ").replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\s+", " ").trim();
        return result;
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 设置SearchPage引用，用于日志输出
     *
     * @param page SearchPage实例
     */
    public static void setSearchPage(SearchPage page) {
        searchPage = page;
    }

    /**
     * 输出日志到SearchPage的日志区域
     *
     * @param message 日志消息
     */
    private static void log(String message) {
        if (searchPage != null) {
            searchPage.log("[爬虫] " + message);
        } else {
            System.out.println("[爬虫] " + message);
        }
    }
}
