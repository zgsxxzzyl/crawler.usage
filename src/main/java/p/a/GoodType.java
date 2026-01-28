package p.a;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GoodType {
    private String code;
    private String name;
    private String url;
    // 使用LinkedHashMap保持插入顺序，并用Collections.synchronizedMap保证线程安全
    private static final Map<String, GoodType> goodTypes = Collections.synchronizedMap(new LinkedHashMap<String, GoodType>());
    private static boolean loaded = false;

    public GoodType(String code, String name, String url) {
        this.code = code;
        this.name = name;
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static synchronized void loadGoodTypes() {
        if (loaded) {
            System.out.println("[GoodType] 商品类型配置已加载，共 " + goodTypes.size() + " 个类型");
            return;
        }

        System.out.println("[GoodType] 开始加载商品类型配置...");

        // 尝试加载配置文件
        try (InputStream input = getConfigInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {

            if (input == null) {
                System.err.println("[GoodType] 错误: 无法找到配置文件 goodtypes.properties");
                throw new RuntimeException("无法找到配置文件 goodtypes.properties");
            }

            // 按顺序读取配置文件，保持顺序
            Map<String, Map<String, String>> typeData = parseConfigFile(reader);

            // 处理每个商品类型，保持顺序
            loadGoodTypeData(typeData);

            loaded = true;
            System.out.println("[GoodType] 配置加载完成，成功加载 " + goodTypes.size() + " 个商品类型");
        } catch (IOException e) {
            System.err.println("[GoodType] 加载商品类型配置文件失败: " + e.getMessage());
            throw new RuntimeException("加载商品类型配置文件失败", e);
        }
    }

    /**
     * 获取配置文件输入流
     * @return 配置文件输入流
     * @throws IOException IO异常
     */
    private static InputStream getConfigInputStream() throws IOException {
        // 首先尝试从JAR文件同级目录加载配置文件
        File externalConfig = new File("goodtypes.properties");
        if (externalConfig.exists()) {
            System.out.println("[GoodType] 从外部路径加载配置文件: " + externalConfig.getAbsolutePath());
            return new FileInputStream(externalConfig);
        } else {
            // 如果外部文件不存在，则从classpath加载
            System.out.println("[GoodType] 外部配置文件不存在，从classpath加载配置文件");
            return GoodType.class.getClassLoader().getResourceAsStream("goodtypes.properties");
        }
    }

    /**
     * 解析配置文件
     * @param reader 配置文件读取器
     * @return 解析后的类型数据
     * @throws IOException IO异常
     */
    private static Map<String, Map<String, String>> parseConfigFile(BufferedReader reader) throws IOException {
        Map<String, Map<String, String>> typeData = new LinkedHashMap<>();
        String line;
        int lineCount = 0;

        while ((line = reader.readLine()) != null) {
            lineCount++;
            line = line.trim();

            // 跳过空行和注释行
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // 解析键值对
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    // 解析商品类型配置键
                    String[] keyParts = key.split("\\.");
                    if (keyParts.length == 2) {
                        String typeCode = keyParts[0];
                        String property = keyParts[1];

                        // 按顺序添加到typeData中
                        typeData.computeIfAbsent(typeCode, k -> new LinkedHashMap<>()).put(property, value);
                        System.out.println("[GoodType] 解析配置项: " + key + " = " + value);
                    }
                }
            }
        }

        System.out.println("[GoodType] 成功加载配置文件，共解析 " + lineCount + " 行，识别出 " + typeData.size() + " 个商品类型");
        return typeData;
    }

    /**
     * 加载商品类型数据
     * @param typeData 类型数据
     */
    private static void loadGoodTypeData(Map<String, Map<String, String>> typeData) {
        int loadedCount = 0;
        for (Map.Entry<String, Map<String, String>> entry : typeData.entrySet()) {
            String typeCode = entry.getKey();
            Map<String, String> properties = entry.getValue();
            String name = properties.get("name");
            String url = properties.get("url");

            if (name != null && url != null) {
                url = processUrl(url);
                goodTypes.put(typeCode, new GoodType(typeCode, name, url));
                loadedCount++;
                System.out.println("[GoodType] 已加载 - " + typeCode + ": " + name);
            } else {
                System.out.println("[GoodType] 已跳过 - " + typeCode + " (缺少必要信息: 名称=" + (name != null) + ", URL=" + (url != null) + ")");
            }
        }

        System.out.println("[GoodType] 成功加载 " + loadedCount + "/" + typeData.size() + " 个商品类型");
    }

    private static String processUrl(String url) {
        System.out.println("[GoodType] >>>>> 开始处理URL <<<<<");
        System.out.println("[GoodType] 原始URL: " + url);

        if (url == null) {
            System.out.println("[GoodType] 警告: 输入URL为null，无法处理");
            return null;
        }

        String processedUrl = url.replace("0-0-0-0-1-0", "0-0-0-0-%d-0") + "?KeyWord=%s";

        System.out.println("[GoodType] <<<<< URL处理完成 >>>>>");
        System.out.println("[GoodType] 最终URL: " + processedUrl);

        return processedUrl;
    }

    public static GoodType valueOf(String typeCode) {
        if (!loaded) {
            loadGoodTypes();
        }

        GoodType type = goodTypes.get(typeCode);
        if (type == null) {
            System.out.println("[GoodType] 未找到商品类型: " + typeCode);
        }
        return type;
    }

    public static Collection<GoodType> values() {
        if (!loaded) {
            loadGoodTypes();
        }
        return goodTypes.values();
    }

    public static Set<String> keySet() {
        if (!loaded) {
            loadGoodTypes();
        }
        return goodTypes.keySet();
    }

    public static GoodType[] getGoodTypeArray() {
        if (!loaded) {
            loadGoodTypes();
        }
        GoodType[] array = goodTypes.values().toArray(new GoodType[0]);
        System.out.println("[GoodType] 返回 " + array.length + " 个商品类型");
        return array;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GoodType goodType = (GoodType) obj;
        return Objects.equals(code, goodType.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}