package com.genitus.channel.tracker.util.audio;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

    /** 资源文件中配置的信息 */
    private static Map<String, String> properties = new HashMap<String, String>();

    /**
     * 设置资源文件中的配置信息
     *
     * @param properties
     *            配置信息
     */
    public static void setProperties(Map<String, String> properties) {
        PropertiesUtils.properties.putAll(properties);
    }

    public static Map<String, String> getProperties() {
        return properties;
    }

    /**
     * 根据KEY取得资源配置信息
     *
     * @param key
     *            key，资源文件中配置的key
     * @return 资源配置信息
     */
    public static String getProperty(String key) {

        String result = properties.get(key);
        if (StringUtils.isEmpty(result)) {
            result = "";
        }
        return result.trim();
    }

    public static String getProperty(String key, String defVal) {
        String result = properties.get(key);
        if (StringUtils.isEmpty(result)) {
            return defVal;
        }
        return result.trim();
    }

    /**
     * 根据KEY删除资源文件中的配置信息
     *
     * @param key
     *            key，资源文件中配置的key
     */
    public static void removeProperty(String key) {
        properties.remove(key);
    }

    /**
     * 从配置文件读取字典数据,形如:0-新建,1-待审批,2-待生效...
     *
     * @param key
     * @return
     */
    public static Map<String, String> getPropertyMap(String key) {
        return getPropertyMap(key, ",", "-");
    }

    public static Map<String, String> getPropertyMap(String key, String joiner, String pair) {
        Map<String, String> map = Maps.newTreeMap();
        if (StringUtils.isEmpty(key)) {
            return map;
        }
        String result = properties.get(key);
        if (StringUtils.isEmpty(result)) {
            return map;
        }
        String[] pairs = result.split(joiner);
        for (String _pair : pairs) {
            if (StringUtils.isEmpty(_pair)) {
                continue;
            }
            String[] kvs = _pair.split(pair);
            if (kvs.length == 2) {
                map.put(StringUtils.trim(kvs[0]), StringUtils.trim(kvs[1]));
            }
        }
        return map;
    }

    public static Properties getProperties(String filename) {
        FileInputStream in = null;
        InputStreamReader streamReader = null;
        Properties properties = new Properties();
        try {
            in = new FileInputStream(filename);
            streamReader = new InputStreamReader(in);
            properties.load(streamReader);
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            try {
                if (streamReader != null) {
                    streamReader.close();
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return properties;
    }
}
