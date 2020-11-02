package com.smhu.helper;

import java.io.IOException;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;

public class PropertiesWithJavaConfig {

    private final Properties properties;

    public PropertiesWithJavaConfig(String path) throws IOException {
        properties = new Properties();
        properties.load(new ClassPathResource(path).getInputStream());
    }

    public Properties getProperties() {
        return properties;
    }
}
