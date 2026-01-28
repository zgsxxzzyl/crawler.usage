package p.b;

import p.a.DateUtil;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class HtmlD2rPipelineForm implements Pipeline {

    private final Object[] objects;
    private final String path;
    private final String keyword1;
    private final p.SearchPage searchPage; // 添加SearchPage引用

    /**
     * 构造函数
     *
     * @param path       文件保存路径
     * @param keyword1   关键词1
     * @param objects    对象数组
     * @param searchPage SearchPage引用
     */
    public HtmlD2rPipelineForm(String path, String keyword1, Object[] objects, p.SearchPage searchPage) {
        this.path = path;
        this.objects = objects;
        this.keyword1 = keyword1;
        this.searchPage = searchPage;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String url = resultItems.getRequest().getUrl();
        log("开始处理URL: " + url);

        // 生成文件名
        String fileName = Arrays.stream(objects)
                .map(Object::toString)
                .collect(Collectors.joining("_"))
                .replaceAll("\\|", "");

        String filePath = path + File.separator +
                DateUtil.startCurrentDate("yyyyMMdd") + "_" +
                keyword1 + "_" +
                fileName + ".html";

        log("生成文件路径: " + filePath);

        // 确保目录存在并创建文件
        ensureFileExists(filePath);

        // 写入数据
        writeDataToFile(resultItems, filePath);

        log("完成处理URL: " + url);
    }

    /**
     * 确保文件存在，如果不存在则创建
     *
     * @param filePath 文件路径
     */
    private void ensureFileExists(String filePath) {
        try {
            Path pathObj = Paths.get(filePath);
            Path parentDir = pathObj.getParent();

            log("检查目录是否存在: " + parentDir);

            // 创建父目录（如果不存在）
            if (parentDir != null && !Files.exists(parentDir)) {
                log("创建目录: " + parentDir);
                Files.createDirectories(parentDir);
            }

            // 创建文件（如果不存在）
            if (!Files.exists(pathObj)) {
                log("创建文件: " + pathObj);
                Files.createFile(pathObj);
            } else {
                log("文件已存在: " + pathObj);
            }

            // 设置结果路径到SearchPage的结果输入框
            if (searchPage != null) {
                String absolutePath = pathObj.toAbsolutePath().toString();
                log("设置结果路径到界面: " + absolutePath);
                searchPage.setResultText(absolutePath);
            }
        } catch (IOException e) {
            String errorMsg = "创建文件时出错: " + e.getMessage();
            System.err.println(getTimestamp() + " [Pipeline] " + errorMsg);
            e.printStackTrace();
        }
    }

    /**
     * 将数据写入文件
     *
     * @param resultItems 结果项
     * @param filePath    文件路径
     */
    private void writeDataToFile(ResultItems resultItems, String filePath) {
        try (PrintWriter printWriter = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath, true),
                        StandardCharsets.UTF_8))) {

            int itemCount = resultItems.getAll().size();
            log("开始写入数据，共 " + itemCount + " 项");

            int count = 0;
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                printWriter.println(entry.getValue());
                count++;
            }
            printWriter.flush();

            log("数据写入完成，实际写入 " + count + " 项");
        } catch (IOException e) {
            String errorMsg = "写入文件时出错: " + e.getMessage();
            System.err.println(getTimestamp() + " [Pipeline] " + errorMsg);
            e.printStackTrace();
        }
    }

    /**
     * 记录日志信息到控制台
     *
     * @param message 日志消息
     */
    private void log(String message) {
        System.out.println(getTimestamp() + " [Pipeline] " + message);

        // 如果SearchPage可用，同时输出到界面日志
        if (searchPage != null && false) {
            searchPage.log("[Pipeline] " + message);
        }
    }

    /**
     * 获取当前时间戳
     *
     * @return 格式化的时间戳字符串
     */
    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "[" + sdf.format(new Date()) + "]";
    }
}
