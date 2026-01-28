package p.b;

import p.a.DateUtil;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HtmlD2rPipeline implements Pipeline {

    private Object[] objects;
    Pattern p = Pattern.compile("\\-(?<page>\\d+)\\.html");
    private String path;

    public HtmlD2rPipeline(String path, Object[] objects) {
        this.path = path;
        this.objects = objects;
    }

    public void process(ResultItems resultItems, Task task) {
        String fileName = Arrays.stream(objects).skip(1).map(o -> o.toString()).collect(Collectors.joining("_")).replaceAll("\\|","");
        String repath = path + File.separator + DateUtil.startCurrentDate("yyyyMMdd") + "_" + fileName + ".html";
        checkAndMakeFile(repath);
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(repath, true), "UTF-8"));
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                printWriter.println(entry.getValue());
                printWriter.flush();
            }
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkAndMakeFile(String fullName) {
        File file = new File(fullName);
        File par = file.getParentFile();
        if (!par.exists()) {
            par.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
