package com.lealpoints.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyManager {
    private static final Logger logger = LogManager.getLogger(PropertyManager.class.getName());
    private static final java.lang.String PROPERTY_FILE_NAME = "leal.properties";
    private static Properties _properties = null;

    private PropertyManager() {
    }

    public static void loadConfiguration() {
        if (_properties == null) {
            reloadConfiguration();
        }
    }

    public static void reloadConfiguration() {
        if (_properties == null) {
            _properties = new Properties();
        }
        InputStream inputStream = PropertyManager.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME);
        if (inputStream != null) {
            try {
                _properties.load(inputStream);
            } catch (IOException e) {
                final String error = "Could not load property file: " + PROPERTY_FILE_NAME;
                logger.info(error);
                throw new RuntimeException(error);
            }
        } else {
            final String error = "Could not find property file: " + PROPERTY_FILE_NAME;
            logger.info(error);
            throw new RuntimeException(error);
        }
    }

    public static Properties getProperties() {
        if (_properties == null) {
            PropertyManager.loadConfiguration();
        }
        return _properties;
    }

    public static String getProperty(String key) {
        return getProperties().getProperty(key);
    }
}
