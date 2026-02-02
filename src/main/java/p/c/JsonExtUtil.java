package p.c;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class JsonExtUtil {

    /**
     * 从资源路径读取JSON文件并转换为指定类型的对象
     *
     * @param resourcePath 资源文件路径（相对于classpath）
     * @param clazz        目标对象类型
     * @param <T>          泛型类型参数
     * @return 指定类型的对象
     */
    public static <T> T readResourceJsonToObject(String resourcePath, Class<T> clazz) {
        try {
            InputStream inputStream = new JsonExtUtil().getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new RuntimeException("无法找到资源文件: " + resourcePath);
            }
            // 修正：使用IoUtil读取InputStream
            String jsonContent = IoUtil.read(inputStream, "utf8");

            T result = JSONUtil.toBean(jsonContent, clazz);
            inputStream.close(); // 记得关闭流

            return result;
        } catch (Exception e) {
            System.err.println("读取资源JSON文件失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void writeResourceJsonToObject(String resourcePath, T clazz) {
        try {
            URL resource = new JsonExtUtil().getClass().getClassLoader().getResource(resourcePath);
            if (resource == null) {
                throw new RuntimeException("无法找到资源文件: " + resourcePath);
            }
            FileUtil.writeBytes(JSONUtil.toJsonStr(clazz).getBytes(), resource.getPath());
        } catch (Exception e) {
            System.err.println("读取资源JSON文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static <T> void writeResource(String resourcePath, String str) {
        try {
            URL resource = new JsonExtUtil().getClass().getClassLoader().getResource(resourcePath);
            if (resource == null) {
                throw new RuntimeException("无法找到资源文件: " + resourcePath);
            }
            FileUtil.writeBytes(str.getBytes(), resource.getPath());
        } catch (Exception e) {
            System.err.println("读取资源JSON文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从资源路径读取JSON数组并转换为指定类型的List
     *
     * @param resourcePath 资源文件路径
     * @param clazz        列表元素类型
     * @param <T>          泛型类型参数
     * @return 指定类型的对象列表
     */
    public static <T> List<T> readResourceJsonToList(String resourcePath, Class<T> clazz) {
        try {
            InputStream inputStream = new JsonExtUtil().getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new RuntimeException("无法找到资源文件: " + resourcePath);
            }

            // 读取输入流为字符串
            String jsonContent = IoUtil.read(inputStream, "utf8");
            inputStream.close();

            // 解析JSON数组并转换为List
            JSONArray jsonArray = JSONUtil.parseArray(jsonContent);
            List<T> result = jsonArray.toList(clazz);

            return result;
        } catch (Exception e) {
            System.err.println("读取资源JSON列表文件失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从字符串读取JSON数组并转换为指定类型的List
     *
     * @param jsonString JSON字符串
     * @param clazz      列表元素类型
     * @param <T>        泛型类型参数
     * @return 指定类型的对象列表
     */
    public <T> List<T> readJsonToList(String jsonString, Class<T> clazz) {
        try {
            JSONArray jsonArray = JSONUtil.parseArray(jsonString);
            List<T> result = jsonArray.toList(clazz);

            return result;
        } catch (Exception e) {
            System.err.println("解析JSON列表失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
