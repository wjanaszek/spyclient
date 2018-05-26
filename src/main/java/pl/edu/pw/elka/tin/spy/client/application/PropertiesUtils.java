package pl.edu.pw.elka.tin.spy.client.application;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.io.IOException;

@Slf4j
public class PropertiesUtils {

	static void createPropertiesFile() {
		String propertiesPath = getPropertiesDirectoryPath() + File.separator + "client.properties";
		File propertiesFile = new File(propertiesPath);
		try {
			propertiesFile.createNewFile();
		} catch (IOException e) {
			log.error("Problem accessing properties file");
		}
	}

	static void saveRegisteredInfo(int clientId, String regValue) {
		if (clientId > 0)
			setProperty("clientId", Integer.toString(clientId));
		setProperty("registered", regValue);
	}

	static boolean isRegistered() {
		String property = getProperty("registered");
		if (property == null)
			return false;
		return property.equals("true");
	}

	static int clientId() {
		return Integer.parseInt(getProperty("clientId"));
	}

	static String getPropertiesDirectoryPath() {
		String homeDirectory = System.getProperty("user.dir");
		String propertiesDirectory = homeDirectory + File.separator + "SpyClient";
		File newDirectory = new File(propertiesDirectory);
		newDirectory.mkdir();

		return propertiesDirectory;
	}

	static String getProperty(String propertyName) {
		String propertiesPath = getPropertiesDirectoryPath() + File.separator + "client.properties";
		String property = null;

		try {
			PropertiesConfiguration config = new PropertiesConfiguration(propertiesPath);
			property = config.getString(propertyName);
		} catch (ConfigurationException e) {
			log.error("Cannot set property in properties file");
		}
		return property;
	}

	static void setProperty(String name, String value) {
		String propertiesPath = getPropertiesDirectoryPath() + File.separator + "client.properties";

		try {
			PropertiesConfiguration config = new PropertiesConfiguration(propertiesPath);
			config.setProperty(name, value);
			config.save();
		} catch (ConfigurationException e) {
			log.error("Cannot set property in properties file");
		}
	}
}
