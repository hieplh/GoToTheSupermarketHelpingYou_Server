package com.smhu.helper;

import java.io.IOException;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;

public class PropertiesWithJavaConfig {

    public static final Properties PROPERTIES = new Properties();

    public static Properties getProperties(String path) throws IOException {
        Properties prop = new Properties();
        prop.load(new ClassPathResource(path).getInputStream());
        return prop;
    }
}
