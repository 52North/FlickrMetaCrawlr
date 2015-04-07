package org.n52.crawlr.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;

public class PropertiesLoader {

    private static PropertiesLoader instance;

    private Properties properties;

    private PropertiesLoader() throws IOException {
        loadProperties();
    }
    
    public static synchronized PropertiesLoader getInstance() throws IOException {
        if (instance == null) {
            instance = new PropertiesLoader();
        }
        return instance;
    }

    private void loadProperties() throws IOException {
        properties = new Properties();
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("/setup.properties");

            if (in == null) {
                throw new MissingResourceException("Config file 'setup.properties' is missing.",
                        java.util.Properties.class.getName(), "");
            }

            properties.load(in);

        } catch (IOException ioExc) {
            throw ioExc;
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }

    public Properties getProperties() {
        return properties;

    }
}
